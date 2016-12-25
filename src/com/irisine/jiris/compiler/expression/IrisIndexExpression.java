package com.irisine.jiris.compiler.expression;

import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public class IrisIndexExpression extends IrisExpression {
	
	private IrisExpression m_target = null;
	private IrisExpression m_indexer = null;
	
	public IrisIndexExpression(IrisExpression target, IrisExpression indexer) {
		m_target = target;
		m_indexer = indexer;
	}
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder,
			MethodVisitor visitor) {
		
		if(!m_indexer.Generate(currentCompiler, currentBuilder, visitor)) {
			return false;
		}
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false);
		
		if(!m_target.Generate(currentCompiler, currentBuilder, visitor)) {
			return false;
		}
		
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
		visitor.visitLdcInsn("[]");
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
		visitor.visitInsn(Opcodes.ICONST_1);
		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "CallMethod", "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;I)Lorg/irislang/jiris/core/IrisValue;", false);
		visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
		
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitInsn(Opcodes.ICONST_1);
		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopParameter", "(I)V", false);
		
		return true;
	}
	
	@Override
	public LeftValueResult LeftValue(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
		LeftValueResult result = new LeftValueResult();
		
		if(!m_indexer.Generate(currentCompiler, currentBuilder, visitor)) {
			result.setResult(false);
			return result;
		}
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false);
		
		if(!m_target.Generate(currentCompiler, currentBuilder, visitor)) {
			result.setResult(false);
			return result;
		}
		
		result.setType(LeftValueType.IndexVariable);
		result.setResult(true);
		
		return result;
	}

}
