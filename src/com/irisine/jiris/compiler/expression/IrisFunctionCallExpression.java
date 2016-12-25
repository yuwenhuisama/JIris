package com.irisine.jiris.compiler.expression;

import java.util.LinkedList;

import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.assistpart.IrisClosureBlockLiteral;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public class IrisFunctionCallExpression extends IrisExpression {
	
	private IrisExpression m_object = null;
	private IrisIdentifier m_functionName = null;
	private LinkedList<IrisExpression> m_parameters = null;
	@SuppressWarnings("unused")
	private IrisClosureBlockLiteral m_closureBlock = null;

	public IrisFunctionCallExpression(IrisExpression object, IrisIdentifier functionName, LinkedList<IrisExpression> parameters, IrisClosureBlockLiteral closureBlock) {
		m_object = object;
		m_functionName = functionName;
		m_parameters = parameters;
		m_closureBlock = closureBlock;
	}
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder,
			MethodVisitor visitor) {
		
		int pushedCount = 0;
		
		if(m_parameters != null) {
			for(IrisExpression expression : m_parameters) {
				if(!expression.Generate(currentCompiler, currentBuilder, visitor)) {
					return false;
				}
				visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
				visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
				visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false);
			}
			pushedCount = m_parameters.size();
		}
		
		if(m_object == null) {
			visitor.visitInsn(Opcodes.ACONST_NULL);
		} else {
			if(!m_object.Generate(currentCompiler, currentBuilder, visitor)) {
				return false;
			}
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
		}
		
		visitor.visitLdcInsn(m_functionName.getIdentifier());
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
		visitor.visitLdcInsn(new Integer(pushedCount));
		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "CallMethod", "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;I)Lorg/irislang/jiris/core/IrisValue;", false);
		visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
		
		if(pushedCount > 0) {
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
			visitor.visitLdcInsn(new Integer(pushedCount));
			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopParameter", "(I)V", false);
		}
		
		return true;
	}

}
