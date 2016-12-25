package com.irisine.jiris.compiler.statement;

import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.assistpart.IrisSwitchBlock;
import com.irisine.jiris.compiler.expression.IrisExpression;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;

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
		// TODO Auto-generated method stub
		return false;
	}

}
