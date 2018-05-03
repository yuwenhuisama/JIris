package org.irislang.jiris.compiler.statement


import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.IrisNativeJavaClass

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.assistpart.IrisBlock
import org.irislang.jiris.compiler.assistpart.IrisIdentifier
import org.irislang.jiris.compiler.expression.IrisExpression

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.Label
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes

class IrisLoopIfStatement(val condition: IrisExpression,
                          val loopTime: IrisExpression,
                          val logVariable: IrisIdentifier?,
                          val block: IrisBlock) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                          visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        // set counter => 0

        val oldLoopContinueLable = currentCompiler.currentLoopContinueLable
        val oldLoopEndLable = currentCompiler.currentLoopEndLable

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitInsn(Opcodes.ICONST_0)
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil",
                "CreateInt", "(I)Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "pushCounter", "(Lorg/irislang/jiris/core/IrisValue;)V", false)

        // Unlimited Loop ?
        if (!loopTime.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "PushLoopTime", "(Lorg/irislang/jiris/core/IrisValue;)V", false)

        // Compare Loop Time to 0
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitInsn(Opcodes.ICONST_0)
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil",
                "CreateInt", "(I)Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false)

        IrisGenerateHelper.CallMethod(visitor, currentCompiler, ">", 1, false)
        IrisGenerateHelper.PopParameter(visitor, currentCompiler, 1)

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "True", "()Lorg/irislang/jiris/core/IrisValue;", false)

        val unimitedLable = Label()
        val elseLable = Label()

        visitor.visitJumpInsn(Opcodes.IF_ACMPNE, unimitedLable)
        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)

        if (!GenerateLoopBody(false, currentCompiler, currentBuilder, visitor)) {
            return false
        }

        visitor.visitJumpInsn(Opcodes.GOTO, elseLable)
        visitor.visitLabel(unimitedLable)
        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)

        if (!GenerateLoopBody(true, currentCompiler, currentBuilder, visitor)) {
            return false
        }

        visitor.visitLabel(elseLable)

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "PopLoopTime", "()V", false)

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "PopCounter", "()V", false)

        currentCompiler.currentLoopEndLable = oldLoopEndLable
        currentCompiler.currentLoopContinueLable = oldLoopContinueLable

        return true
    }

    private fun GenerateLoopBody(unlimitedFlag: Boolean, currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                                 visitor: MethodVisitor): Boolean {
        if (logVariable != null) {
            visitor.visitLdcInsn(logVariable.identifier)
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                    "getCounter", "()Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                    "SetLocalVariable",
                    "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
        }

        val continueLabel = Label()
        val endLable = Label()

        currentCompiler.currentLoopContinueLable = continueLabel
        currentCompiler.currentLoopEndLable = endLable

        visitor.visitLabel(continueLabel)
        if (!condition.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil",
                "NotFalseOrNil", "(Lorg/irislang/jiris/core/IrisValue;)Z", false)
        visitor.visitJumpInsn(Opcodes.IFEQ, endLable)
        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)

        if (!unlimitedFlag) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                    "GetTopLoopTime", "()Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                    "getCounter", "()Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                    "CompareCounterLess",
                    "(Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Z", false)

            visitor.visitJumpInsn(Opcodes.IFEQ, endLable)
            IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)
        }

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "increamCounter", "()V", false)

        if (logVariable != null) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                    "getCounter", "()Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())

            visitor.visitLdcInsn(logVariable.identifier)
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                    "SetLocalVariable",
                    "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
        }

        if (!block.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        visitor.visitJumpInsn(Opcodes.GOTO, continueLabel)
        visitor.visitLabel(endLable)
        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)

        return true
    }

}
