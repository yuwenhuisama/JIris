package com.irisine.jiris.compiler.statement;

import java.util.LinkedList;

import com.irisine.jiris.compiler.IrisGenerateHelper;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.assistpart.IrisBlock;
import com.irisine.jiris.compiler.assistpart.IrisElseIf;
import com.irisine.jiris.compiler.expression.IrisExpression;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public class IrisConditionIfStatement extends IrisStatement {

	private IrisExpression m_condition = null;
	private IrisBlock m_block = null;
	private LinkedList<IrisElseIf> m_elseIfList = null;
	private IrisBlock m_elseBlock = null;
	
	public IrisConditionIfStatement(IrisExpression condition, IrisBlock block, LinkedList<IrisElseIf> elseIfList, IrisBlock elseBlock) {
		m_condition = condition;
		m_block = block;
		m_elseIfList = elseIfList;
		m_elseBlock = elseBlock;
	}
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder,
			MethodVisitor visitor) {
		
		Label nextLabel = null;
		Label endLabel = new Label(); 
		
		if(!m_condition.Generate(currentCompiler, currentBuilder, visitor)) {
			return false;
		}
		
		// CMP
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "NotFalseOrNil", "(Lorg/irislang/jiris/core/IrisValue;)Z", false);
		nextLabel = new Label();
		// if false or nil
		visitor.visitJumpInsn(Opcodes.IFEQ, nextLabel);
		IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler);

		if(!m_block.Generate(currentCompiler, currentBuilder, visitor)){
			return false;
		}
		
		visitor.visitLabel(nextLabel);
		
		visitor.visitJumpInsn(Opcodes.GOTO, endLabel);
		visitor.visitLabel(nextLabel);
//		if(!currentCompiler.isFirstStackFrameGenerated()) {
//			visitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {"org/irislang/jiris/core/IrisValue"}, 0, null);
//			currentCompiler.setFirstStackFrameGenerated(true);
//		} else {
//			visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//		}

		IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler);

		if(m_elseIfList != null) {
			for(IrisElseIf elseIf : m_elseIfList) {
				if(!elseIf.getCondition().Generate(currentCompiler, currentBuilder, visitor)) {
					return false;
				}
				
				visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
				visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "NotFalseOrNil", "(Lorg/irislang/jiris/core/IrisValue;)Z", false);
				
				nextLabel = new Label();
				visitor.visitJumpInsn(Opcodes.IFEQ, nextLabel);
				IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler);

				if(!m_block.Generate(currentCompiler, currentBuilder, visitor)) {
					return false;
				}
				visitor.visitJumpInsn(Opcodes.GOTO, endLabel);
				visitor.visitLabel(nextLabel);
				IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler);
			}
		}
		
		if(m_elseBlock != null) {
			if(!m_elseBlock.Generate(currentCompiler, currentBuilder, visitor)) {
				return false;
			}
		}
		visitor.visitLabel(endLabel);
		
		return true;
	}
}
