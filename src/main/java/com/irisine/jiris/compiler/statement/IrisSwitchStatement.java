package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisGenerateHelper;
import com.irisine.jiris.compiler.assistpart.IrisWhen;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.assistpart.IrisSwitchBlock;
import com.irisine.jiris.compiler.expression.IrisExpression;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;

import java.util.LinkedList;

public class IrisSwitchStatement extends IrisStatement {
	
	private IrisExpression m_condition = null;
	private IrisSwitchBlock m_switchBlock = null;
	
	public IrisSwitchStatement(IrisExpression condition, IrisSwitchBlock switchBlock) {
		m_condition = condition;
		m_switchBlock = switchBlock;
	}
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder,
			MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());
	    // generate condition
	    if(!m_condition.Generate(currentCompiler, currentBuilder, visitor)){
	        return false;
        }

	    // record result
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PushComparedObject", "(Lorg/irislang/jiris/core/IrisValue;)V", false);

        LinkedList<IrisWhen> whens =  m_switchBlock.getWhenList();

        Label switchEnd = new Label();

        boolean stackFrameGenerated = false;

        for(IrisWhen when : whens) {
            LinkedList<IrisExpression> conditions = when.getConditions();

            boolean firstFlag = true;
            Label nextSection = null;
            Label blockLabel = new Label();
            for(IrisExpression tar : conditions) {

                if(!firstFlag) {
                    visitor.visitLabel(nextSection);
                }
                firstFlag = false;

                nextSection = new Label();

                if(!tar.Generate(currentCompiler, currentBuilder, visitor)) {
                    return false;
                }
//                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
//                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false);
                IrisGenerateHelper.AddParameter(visitor, currentCompiler);

                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "GetTopComparedObject", "()Lorg/irislang/jiris/core/IrisValue;", false);
                visitor.visitLdcInsn("==");
                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
                visitor.visitInsn(Opcodes.ICONST_1);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "CallMethod", "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;I)Lorg/irislang/jiris/core/IrisValue;", false);
                visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
//                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//                visitor.visitInsn(Opcodes.ICONST_1);
//                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopParameter", "(I)V", false);
                IrisGenerateHelper.PopParameter(visitor, currentCompiler, 1);

                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "NotFalseOrNil", "(Lorg/irislang/jiris/core/IrisValue;)Z", false);
                // last false condition will jump to next block
                visitor.visitJumpInsn(Opcodes.IFEQ, nextSection);
                IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler);
                // if true
                visitor.visitJumpInsn(Opcodes.GOTO, blockLabel);
                IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler);

            }

            visitor.visitLabel(blockLabel);
            if(!when.getWhenBlock().Generate(currentCompiler, currentBuilder, visitor)){
                return false;
            }
            visitor.visitJumpInsn(Opcodes.GOTO, switchEnd);
            visitor.visitLabel(nextSection);
            IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler);
        }


        if(m_switchBlock.getElseBlock() != null){
            if(!m_switchBlock.getElseBlock().Generate(currentCompiler, currentBuilder, visitor)) {
                return false;
            }
        }

        visitor.visitLabel(switchEnd);

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopCompareadObject", "()V", false);

        return true;
	}
}
