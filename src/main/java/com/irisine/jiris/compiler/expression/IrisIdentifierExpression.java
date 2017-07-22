package com.irisine.jiris.compiler.expression;

import com.irisine.jiris.compiler.IrisGenerateHelper;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public class IrisIdentifierExpression extends IrisExpression {

	private IrisIdentifier m_identifier = null; 
	
	public String getIdentifierString() {
		return m_identifier.getIdentifier();
	}
	
	public IrisIdentifierExpression(IrisIdentifier identifier) {
		super();
		m_identifier = identifier;
	}
	
	@Override
	public LeftValueResult LeftValue(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
		LeftValueResult result = new LeftValueResult();
		result.setIdentifier(m_identifier.getIdentifier());
		result.setResult(true);
		
		switch(m_identifier.getType()) {
		case ClassVariable:
			result.setType(LeftValueType.ClassVariable);
			break;
		case Constance:
			result.setType(LeftValueType.Constance);
			break;
		case GlobalVariable:
			result.setType(LeftValueType.GlobalVariable);
			break;
		case InstanceVariable:
			result.setType(LeftValueType.InstanceVariable);
			break;
		case LocalVariable:
			result.setType(LeftValueType.LocalVariable);
			break;
		}
		
		return result;
	}
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());

		visitor.visitLdcInsn(m_identifier.getIdentifier());
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
		
		switch (m_identifier.getType()) {
		case LocalVariable:
			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, 
					currentCompiler.getCurrentClassName(),
					"GetLocalVariable", 
					"(Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;",
					false);
			break;
		case ClassVariable:
			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, 
					currentCompiler.getCurrentClassName(),
					"GetClassVariable", 
					"(Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;",
					false);
			break;
		case InstanceVariable:
			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, 
					currentCompiler.getCurrentClassName(),
					"GetInstanceVariable", 
					"(Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;",
					false);
			break;
		case GlobalVariable:
			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, 
					currentCompiler.getCurrentClassName(),
					"GetGlobalVariable", 
					"(Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;",
					false);
			break;
		case Constance:
			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, 
					currentCompiler.getCurrentClassName(),
					"GetConstance", 
					"(Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;",
					false);
			break;
		}
		
		visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
		
		return true;
	}

}
