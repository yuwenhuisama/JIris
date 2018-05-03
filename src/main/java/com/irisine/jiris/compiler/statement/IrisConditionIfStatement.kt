package com.irisine.jiris.compiler.statement

import java.util.LinkedList

import com.irisine.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.IrisNativeJavaClass

import com.irisine.jiris.compiler.IrisCompiler
import com.irisine.jiris.compiler.assistpart.IrisBlock
import com.irisine.jiris.compiler.assistpart.IrisElseIf
import com.irisine.jiris.compiler.expression.IrisExpression

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.Label
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes

class IrisConditionIfStatement(val condition: IrisExpression,
                               val block: IrisBlock,
                               val elseIfList: LinkedList<IrisElseIf>?,
                               val elseBlock: IrisBlock?) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                          visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        var nextLabel: Label? = null
        val endLabel = Label()

        if (!condition.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        // CMP
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "NotFalseOrNil", "(Lorg/irislang/jiris/core/IrisValue;)Z", false)
        nextLabel = Label()
        // if false or nil
        visitor.visitJumpInsn(Opcodes.IFEQ, nextLabel)
        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)

        if (!block.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        visitor.visitLabel(nextLabel)

        visitor.visitJumpInsn(Opcodes.GOTO, endLabel)
        visitor.visitLabel(nextLabel)

        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)

        if (elseIfList != null) {
            for (elseIf in elseIfList) {
                if (!elseIf.condition.Generate(currentCompiler, currentBuilder, visitor)) {
                    return false
                }

                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "NotFalseOrNil", "(Lorg/irislang/jiris/core/IrisValue;)Z", false)

                nextLabel = Label()
                visitor.visitJumpInsn(Opcodes.IFEQ, nextLabel)
                IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)

                if (!block.Generate(currentCompiler, currentBuilder, visitor)) {
                    return false
                }
                visitor.visitJumpInsn(Opcodes.GOTO, endLabel)
                visitor.visitLabel(nextLabel)
                IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)
            }
        }

        if (elseBlock != null) {
            if (!elseBlock.Generate(currentCompiler, currentBuilder, visitor)) {
                return false
            }
        }
        visitor.visitLabel(endLabel)

        return true
    }
}
