package org.irislang.jiris.compiler.expression

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.IrisGenerateHelper
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

import java.util.LinkedList

/**
 * Created by Huisama on 2017/4/12 0012.
 */
class IrisFieldExpression(val list: LinkedList<IrisIdentifierExpression>, val identifier: IrisIdentifierExpression, val isTopField: Boolean) : IrisExpression() {


    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        val firstIdentifier = list.removeFirst()

        if (!isTopField) {
            if (!firstIdentifier.Generate(currentCompiler, currentBuilder, visitor)) {
                return false
            }
        } else {
            visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/compiler/IrisInterpreter",
                    "INSTANCE",
                    "Lorg/irislang/jiris/compiler/IrisInterpreter;")
            visitor.visitLdcInsn(firstIdentifier.identifierString)
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/compiler/IrisInterpreter",
                    "GetConstance",
                    "(Ljava/lang/String;)Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
        }

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())

        if (list.isEmpty()) {
            visitor.visitInsn(Opcodes.ACONST_NULL)
        } else {
            visitor.visitIntInsn(Opcodes.BIPUSH, list.size)
            visitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String")
            var index = 0
            for (identifierExpression in list) {
                visitor.visitInsn(Opcodes.DUP)
                visitor.visitInsn(index++)
                visitor.visitLdcInsn(identifierExpression.identifierString)
                visitor.visitInsn(Opcodes.AASTORE)
            }
        }

        visitor.visitLdcInsn(identifier.identifierString)
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "GetFieldValue", "" + "(Lorg/irislang/jiris/core/IrisValue;[Ljava/lang/String;Ljava/lang/String;)Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())

        return true
    }
}
