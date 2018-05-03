package org.irislang.jiris.compiler.statement

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.compiler.assistpart.IrisBlock
import org.irislang.jiris.compiler.assistpart.IrisIdentifier
import org.irislang.jiris.compiler.expression.IrisExpression
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.Label
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

/**
 * Created by Huisama on 2017/4/8 0008.
 */
class IrisForStatement(val iter1: IrisIdentifier, val iter2: IrisIdentifier?,
                       val source: IrisExpression, val block: IrisBlock) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        val oldLoopContinueLable = currentCompiler.currentLoopContinueLable
        val oldLoopEndLable = currentCompiler.currentLoopEndLable

        val jumpBackLabel = Label()
        val jumpToEndLable = Label()

        currentCompiler.currentLoopContinueLable = jumpBackLabel
        currentCompiler.currentLoopEndLable = jumpToEndLable

        if (!source.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        // push new vessel
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PushVessel",
                "(Lorg/irislang/jiris/core/IrisValue;)V", false)

        // push(new iterator)
        IrisGenerateHelper.CallMethod(visitor, currentCompiler, "get_iterator", 0, false)

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PushIterator",
                "(Lorg/irislang/jiris/core/IrisValue;)V", false)

        visitor.visitLabel(jumpBackLabel)
        // next
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "GetIterator",
                "()Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
        IrisGenerateHelper.CallMethod(visitor, currentCompiler, "next", 0, false)

        // assign iter
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "GetIterator",
                "()Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())

        if (iter2 == null) {
            IrisGenerateHelper.CallMethod(visitor, currentCompiler, "get_value", 0, false)

            // assign iter1
            visitor.visitLdcInsn(iter1.identifier)
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "SetLocalVariable", "" +
                    "(Ljava/lang/String;" +
                    "Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
        } else {
            IrisGenerateHelper.CallMethod(visitor, currentCompiler, "get_key", 0, false)

            // assign iter1
            visitor.visitLdcInsn(iter1.identifier)
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "SetLocalVariable",
                    "(Ljava/lang/String;" +
                    "Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())

            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "GetIterator",
                    "()Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())

            IrisGenerateHelper.CallMethod(visitor, currentCompiler, "get_value", 0, false)
            // assign iter2
            visitor.visitLdcInsn(iter2.identifier)
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "SetLocalVariable",
                    "(Ljava/lang/String;" +
                    "Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
        }

        if (!block.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        // is_end
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "GetIterator",
                "()Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
        IrisGenerateHelper.CallMethod(visitor, currentCompiler, "is_end", 0, false)

        // if end
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "NotFalseOrNil",
                "(Lorg/irislang/jiris/core/IrisValue;)Z", false)
        visitor.visitJumpInsn(Opcodes.IFNE, jumpToEndLable)
        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)

        visitor.visitJumpInsn(Opcodes.GOTO, jumpBackLabel)
        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)

        visitor.visitLabel(jumpToEndLable)

        // pop
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "PopIterator", "()V", false)

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "PopVessel", "()V", false)

        currentCompiler.currentLoopContinueLable = oldLoopContinueLable
        currentCompiler.currentLoopEndLable = oldLoopEndLable
        return true
    }
}
