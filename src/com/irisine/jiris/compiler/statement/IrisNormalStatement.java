package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisGenerateHelper;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.expression.IrisExpression;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;




public class IrisNormalStatement extends IrisStatement {

	private IrisExpression m_expression = null;
	
	public IrisNormalStatement(IrisExpression expression) {
		m_expression = expression;
	}
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder,
			MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());
		return m_expression.Generate(currentCompiler, currentBuilder, visitor);
	}

}
