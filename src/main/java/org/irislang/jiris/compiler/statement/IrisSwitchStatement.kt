package org.irislang.jiris.compiler.statement

import org.irislang.jiris.compiler.IrisGenerateHelper
import net.bytebuddy.jar.asm.Label
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.assistpart.IrisSwitchBlock
import org.irislang.jiris.compiler.expression.IrisExpression

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor

class IrisSwitchStatement(val condition: IrisExpression,
                          val switchBlock: IrisSwitchBlock) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                          visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        // generate condition
        if (!condition.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        // record result
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "PushComparedObject", "(Lorg/irislang/jiris/core/IrisValue;)V", false)

        val whens = switchBlock.whenList

        val switchEnd = Label()

//        val stackFrameGenerated = false

        for (`when` in whens) {
            val conditions = `when`.conditions

            var firstFlag = true
            var nextSection: Label? = null
            val blockLabel = Label()
            for (tar in conditions) {

                if (!firstFlag) {
                    visitor.visitLabel(nextSection)
                }
                firstFlag = false

                nextSection = Label()

                if (!tar.Generate(currentCompiler, currentBuilder, visitor)) {
                    return false
                }
                IrisGenerateHelper.AddParameter(visitor, currentCompiler)

                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                        "GetTopComparedObject", "()Lorg/irislang/jiris/core/IrisValue;", false)
                visitor.visitLdcInsn("==")
                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
                visitor.visitInsn(Opcodes.ICONST_1)
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "CallMethod",
                        "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;I)Lorg/irislang/jiris/core/IrisValue;", false)
                visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
                IrisGenerateHelper.PopParameter(visitor, currentCompiler, 1)

                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil",
                        "NotFalseOrNil", "(Lorg/irislang/jiris/core/IrisValue;)Z", false)

                // last false condition will jump to next block
                visitor.visitJumpInsn(Opcodes.IFEQ, nextSection)
                IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)
                // if true
                visitor.visitJumpInsn(Opcodes.GOTO, blockLabel)
                IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)

            }

            visitor.visitLabel(blockLabel)
            if (!`when`.whenBlock.Generate(currentCompiler, currentBuilder, visitor)) {
                return false
            }
            visitor.visitJumpInsn(Opcodes.GOTO, switchEnd)
            visitor.visitLabel(nextSection)
            IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)
        }


        if (switchBlock.elseBlock != null) {
            if (!switchBlock.elseBlock.Generate(currentCompiler, currentBuilder, visitor)) {
                return false
            }
        }

        visitor.visitLabel(switchEnd)

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "org/irislang/jiris/core/IrisThreadInfo",
                "PopCompareadObject", "()V", false)

        return true
    }
}
