package com.irisine.jiris.compiler.expression

import com.irisine.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.IrisNativeJavaClass

import com.irisine.jiris.compiler.IrisCompiler

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes

class IrisInstantValueExpression(val type: InstantValueType) : IrisExpression() {

    enum class InstantValueType {
        Nil,
        True,
        False
    }

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        when (type) {
            IrisInstantValueExpression.InstantValueType.Nil -> GenerateInstant(visitor, "Nil", currentCompiler)
            IrisInstantValueExpression.InstantValueType.True -> GenerateInstant(visitor, "True", currentCompiler)
            IrisInstantValueExpression.InstantValueType.False -> GenerateInstant(visitor, "False", currentCompiler)
        }

        return true
    }

    private fun GenerateInstant(visitor: MethodVisitor, obj: String, currentCompiler: IrisCompiler) {
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", obj,
                "()Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
    }
}
