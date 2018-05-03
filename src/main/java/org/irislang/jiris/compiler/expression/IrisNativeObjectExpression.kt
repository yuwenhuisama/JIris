package org.irislang.jiris.compiler.expression

import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.IrisNativeJavaClass

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.expression.IrisNativeObjectExpression.NativeObjectType.*

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes

class IrisNativeObjectExpression : IrisExpression {

    private var m_string = ""
    private var m_integer = 0
    private var m_float = 0.0

    private var m_type = NativeObjectType.String

    enum class NativeObjectType {
        String,
        Integer,
        Float,
        UniqueString
    }

    constructor(type: NativeObjectType, string: String) : super() {
        m_type = type
        m_string = string
    }

    constructor(integer: Int) : super() {
        m_integer = integer
        m_type = Integer
    }

    constructor(irfloat: Double) : super() {
        m_float = irfloat
        m_type = NativeObjectType.Float
    }

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        when (m_type) {
            NativeObjectType.String -> {
                visitor.visitLdcInsn(m_string)
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "CreateString", "(Ljava/lang/String;)Lorg/irislang/jiris/core/IrisValue;", false)
                visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
            }
            Integer -> {
                visitor.visitLdcInsn(m_integer)
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "CreateInt", "(I)Lorg/irislang/jiris/core/IrisValue;", false)
                visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
            }
            NativeObjectType.Float -> {
                visitor.visitLdcInsn(m_float)
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "CreateFloat", "(D)Lorg/irislang/jiris/core/IrisValue;", false)
                visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
            }
            UniqueString -> {
                IrisCompiler.INSTANCE.AddUniqueString(m_string)
                val index = IrisCompiler.INSTANCE.GetUinqueIndex(m_string)

                visitor.visitFieldInsn(Opcodes.GETSTATIC, currentCompiler.currentClassName, "sm_uniqueStringObjects", "Ljava/util/ArrayList;")
                visitor.visitLdcInsn(index)
                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;", false)
                visitor.visitTypeInsn(Opcodes.CHECKCAST, "org/irislang/jiris/core/IrisValue")
                visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
            }
        }

        return true
    }

}
