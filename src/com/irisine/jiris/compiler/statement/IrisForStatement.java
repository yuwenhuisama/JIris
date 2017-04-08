package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisGenerateHelper;
import com.irisine.jiris.compiler.assistpart.IrisBlock;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;
import com.irisine.jiris.compiler.expression.IrisExpression;
import com.sun.org.apache.xpath.internal.compiler.OpCodes;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.compiler.IrisNativeJavaClass;
import org.irislang.jiris.irisclass.IrisInteger;

/**
 * Created by Huisama on 2017/4/8 0008.
 */
public class IrisForStatement extends  IrisStatement {
    IrisIdentifier m_iter1 = null;
    IrisIdentifier m_iter2 = null;
    IrisExpression m_source = null;
    IrisBlock m_block = null;

    public IrisForStatement(IrisIdentifier iter1, IrisIdentifier iter2, IrisExpression source, IrisBlock block) {
        m_iter1 = iter1;
        m_iter2 = iter2;
        m_source = source;
        m_block = block;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {

        Label oldLoopContinueLable = currentCompiler.getCurrentLoopContinueLable();
        Label oldLoopEndLable = currentCompiler.getCurrentLoopEndLable();

        Label jumpBackLabel = new Label();
        Label jumpToEndLable = new Label();

        currentCompiler.setCurrentLoopContinueLable(jumpBackLabel);
        currentCompiler.setCurrentLoopEndLable(jumpToEndLable);

        if(!m_source.Generate(currentCompiler, currentBuilder, visitor)) {
            return false;
        }

        // push new vessel
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PushVessel", "(Lorg/irislang/jiris/core/IrisValue;)V", false);

        // push(new iterator)
        IrisGenerateHelper.CallMethod(visitor, currentCompiler, "get_iterator", 0, false);

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PushIterator", "(Lorg/irislang/jiris/core/IrisValue;)V", false);

        visitor.visitLabel(jumpBackLabel);
        // next
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "GetIterator", "()Lorg/irislang/jiris/core/IrisValue;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
        IrisGenerateHelper.CallMethod(visitor, currentCompiler, "next", 0, false);

        // assign iter
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "GetIterator", "()Lorg/irislang/jiris/core/IrisValue;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());

        if(m_iter2 == null) {
            IrisGenerateHelper.CallMethod(visitor, currentCompiler, "get_value", 0, false);

            // assign iter1
            visitor.visitLdcInsn(m_iter1.getIdentifier());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetLocalVariable", "" +
                    "(Ljava/lang/String;" +
                    "Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false);
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
        }
        else {
            IrisGenerateHelper.CallMethod(visitor, currentCompiler, "get_key", 0, false);

            // assign iter1
            visitor.visitLdcInsn(m_iter1.getIdentifier());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetLocalVariable", "" +
                    "(Ljava/lang/String;" +
                    "Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false);
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());

            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "GetIterator", "()Lorg/irislang/jiris/core/IrisValue;", false);
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());

            IrisGenerateHelper.CallMethod(visitor, currentCompiler, "get_value", 0, false);
            // assign iter2
            visitor.visitLdcInsn(m_iter2.getIdentifier());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetLocalVariable", "" +
                    "(Ljava/lang/String;" +
                    "Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false);
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
        }

        if(!m_block.Generate(currentCompiler, currentBuilder, visitor)) {
            return false;
        }

        // is_end
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "GetIterator", "()Lorg/irislang/jiris/core/IrisValue;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
        IrisGenerateHelper.CallMethod(visitor, currentCompiler, "is_end", 0, false);

        // if end
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "NotFalseOrNil", "(Lorg/irislang/jiris/core/IrisValue;)Z", false);
        visitor.visitJumpInsn(Opcodes.IFNE, jumpToEndLable);
        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler);

        visitor.visitJumpInsn(Opcodes.GOTO, jumpBackLabel);
        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler);

        visitor.visitLabel(jumpToEndLable);

        // pop
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopIterator", "()V", false);

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopVessel", "()V", false);

        currentCompiler.setCurrentLoopContinueLable(oldLoopContinueLable);
        currentCompiler.setCurrentLoopEndLable(oldLoopEndLable);
        return true;
    }
}
