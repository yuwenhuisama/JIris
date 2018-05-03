package com.irisine.jiris.compiler.statement

import com.irisine.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.IrisNativeJavaClass

import com.irisine.jiris.compiler.IrisCompiler
import com.irisine.jiris.compiler.expression.IrisExpression

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes

class IrisReturnStatement(private val returnExpression: IrisExpression?) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                          visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        if (returnExpression != null) {
            if (!returnExpression.Generate(currentCompiler, currentBuilder, visitor)) {
                return false
            }
        } else {
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "Nil",
                    "()Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
        }
        visitor.visitJumpInsn(Opcodes.GOTO, currentCompiler.currentEndLable)
        return true
    }

}
