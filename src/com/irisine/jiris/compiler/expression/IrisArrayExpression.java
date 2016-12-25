package com.irisine.jiris.compiler.expression;

import java.util.LinkedList;

import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public class IrisArrayExpression extends IrisExpression {

	private LinkedList<IrisExpression> m_expressions = null;
	
	public IrisArrayExpression(LinkedList<IrisExpression> expressions) {
		m_expressions = expressions;
	}
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder,
			MethodVisitor visitor) {
		if(m_expressions != null) {
			for(IrisExpression elem : m_expressions) {
				if(!elem.Generate(currentCompiler, currentBuilder, visitor)) {
					return false;
				}
				visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
				visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
				visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false);
			}
			
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
			visitor.visitLdcInsn(new Integer(m_expressions.size()));
			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "getPartPrameterListOf", "(I)Ljava/util/ArrayList;", false);	
		} else {
			visitor.visitInsn(Opcodes.ACONST_NULL);
		}
		
		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "CreateArray", "(Ljava/util/ArrayList;)Lorg/irislang/jiris/core/IrisValue;", false);
		visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
		
		if(m_expressions != null) {
			visitor.visitVarInsn(Opcodes.ALOAD, 2);
			visitor.visitLdcInsn(new Integer(m_expressions.size()));
			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopParameter", "(I)V", false);
		}
		
		return true;
	}

}
