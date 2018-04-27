package com.irisine.jiris.compiler.expression;

import java.util.LinkedList;

import com.irisine.jiris.compiler.IrisGenerateHelper;
import org.irislang.jiris.IrisNativeJavaClass;

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

	    IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());

		if(m_expressions != null) {
			for(IrisExpression elem : m_expressions) {
				if(!elem.Generate(currentCompiler, currentBuilder, visitor)) {
					return false;
				}
				IrisGenerateHelper.AddParameter(visitor, currentCompiler);
			}

			IrisGenerateHelper.GetPartPrametersOf(visitor, currentCompiler, m_expressions.size());
			} else {
			visitor.visitInsn(Opcodes.ACONST_NULL);
		}
		
		IrisGenerateHelper.CreateArray(visitor, currentCompiler);

		if(m_expressions != null) {
			IrisGenerateHelper.PopParameter(visitor, currentCompiler, m_expressions.size());
		}
		
		return true;
	}

}
