package org.irislang.jiris.compiler.expression

import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.IrisNativeJavaClass

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.expression.IrisUnaryExpression.UnaryExpressionType.*

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor

class IrisUnaryExpression(val type: UnaryExpressionType, val expression: IrisExpression) : IrisExpression() {

    enum class UnaryExpressionType {
        LogicNot,
        BitNot,
        Minus,
        Plus
    }

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        if (!expression.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        when (type) {
            LogicNot -> GenerateUnary(currentCompiler, visitor, "!")
            BitNot -> GenerateUnary(currentCompiler, visitor, "~")
            Minus -> GenerateUnary(currentCompiler, visitor, "__minus")
            Plus -> GenerateUnary(currentCompiler, visitor, "__plus")
        }

        return true
    }

    private fun GenerateUnary(currentCompiler: IrisCompiler, visitor: MethodVisitor, op: String) {
        IrisGenerateHelper.CallMethod(visitor, currentCompiler, op, 0, false)
    }

}
