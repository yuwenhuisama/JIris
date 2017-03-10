package com.irisine.jiris.compiler.statement;


import com.irisine.jiris.compiler.IrisGenerateHelper;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.assistpart.IrisBlock;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;
import com.irisine.jiris.compiler.expression.IrisExpression;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import sun.security.krb5.internal.CredentialsUtil;

public class IrisLoopIfStatement extends IrisStatement{

	private IrisExpression m_condition = null;
	private IrisExpression m_loopTime = null;
	private IrisIdentifier m_logVariable = null;
	private IrisBlock m_block = null;
	
	public IrisLoopIfStatement(IrisExpression condition, IrisExpression loopTime, IrisIdentifier logVariable, IrisBlock block) {
		m_condition = condition;
		m_loopTime = loopTime;
		m_logVariable = logVariable;
		m_block = block;
	}
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder,
			MethodVisitor visitor) {
		
		// set counter => 0
//		visitor.visitVarInsn(Opcodes.ALOAD, 2);
//		visitor.visitInsn(Opcodes.ICONST_0);
//		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "setCounter", "(I)V", false);

		// threadInfo.pushCounter(IrisDevUtil.CreateInt(0))
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitInsn(Opcodes.ICONST_0);
		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "CreateInt", "(I)Lorg/irislang/jiris/core/IrisValue;", false);
		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "pushCounter", "(Lorg/irislang/jiris/core/IrisValue;)V", false);

		// Unlimited Loop ?
		if(!m_loopTime.Generate(currentCompiler, currentBuilder, visitor)) {
			return false;
		}

		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PushLoopTime", "(Lorg/irislang/jiris/core/IrisValue;)V", false);

		// Compare Loop Time to 0
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitInsn(Opcodes.ICONST_0);
		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "CreateInt", "(I)Lorg/irislang/jiris/core/IrisValue;", false);
		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false);

//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
//		visitor.visitLdcInsn(">");
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
//		visitor.visitInsn(Opcodes.ICONST_1);
//		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "CallMethod", "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;I)Lorg/irislang/jiris/core/IrisValue;", false);
//		visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
		IrisGenerateHelper.CallMethod(visitor, currentCompiler, ">", 1, false);

//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//		visitor.visitInsn(Opcodes.ICONST_1);
//		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopParameter", "(I)V", false);
		IrisGenerateHelper.PopParameter(visitor, currentCompiler, 1);

		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "True", "()Lorg/irislang/jiris/core/IrisValue;", false);

		Label unimitedLable = new Label();
		Label elseLable = new Label();

		visitor.visitJumpInsn(Opcodes.IF_ACMPNE, unimitedLable);

		if(!GenerateLoopBody(false, currentCompiler, currentBuilder, visitor)){
			return false;
		}

		visitor.visitJumpInsn(Opcodes.GOTO, elseLable);

		visitor.visitLabel(unimitedLable);
//		if(!currentCompiler.isFirstStackFrameGenerated()){
//			visitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {"org/irislang/jiris/core/IrisValue"}, 0, null);
//			currentCompiler.setFirstStackFrameGenerated(true);
//		} else {
//			visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//		}

		IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler);

		if(!GenerateLoopBody(true, currentCompiler, currentBuilder, visitor)){
			return false;
		}

		visitor.visitLabel(elseLable);

		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopLoopTime", "()V", false);

		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopCounter", "()V", false);
		return true;
	}
	
	private boolean GenerateLoopBody(boolean unlimitedFlag, IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder,
			MethodVisitor visitor) {
		if(m_logVariable != null) {
			visitor.visitLdcInsn(m_logVariable.getIdentifier());
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "getCounter", "()Lorg/irislang/jiris/core/IrisValue;", false);
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetLocalVariable", "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false);
			visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
		}

		Label continueLabel = new Label();
		Label endLable = new Label();
		
		visitor.visitLabel(continueLabel);
		if(!m_condition.Generate(currentCompiler, currentBuilder, visitor)) {
			return false;
		}
		
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "NotFalseOrNil", "(Lorg/irislang/jiris/core/IrisValue;)Z", false);
		visitor.visitJumpInsn(Opcodes.IFEQ, endLable);
		
		if(!unlimitedFlag) {
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "GetTopLoopTime", "()Lorg/irislang/jiris/core/IrisValue;", false);
			visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());

//			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "getCounter", "()I", false);
//			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
//			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "CompareCounterLess", "(ILorg/irislang/jiris/core/IrisValue;)Z", false);

			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "getCounter", "()Lorg/irislang/jiris/core/IrisValue;", false);
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "CompareCounterLess", "(Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Z", false);

			visitor.visitJumpInsn(Opcodes.IFEQ, endLable);
		}

		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "increamCounter", "()V", false);

		if(m_logVariable != null) {
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "getCounter", "()Lorg/irislang/jiris/core/IrisValue;", false);
			visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());

			visitor.visitLdcInsn(m_logVariable.getIdentifier());
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetLocalVariable", "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false);
			visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
		}

		if(!m_block.Generate(currentCompiler, currentBuilder, visitor)) {
			return false;
		}
		
		visitor.visitJumpInsn(Opcodes.GOTO, continueLabel);
		visitor.visitLabel(endLable);
		visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		
		return true;
	}
	
}
