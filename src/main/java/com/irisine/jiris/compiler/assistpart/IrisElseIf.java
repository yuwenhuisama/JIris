package com.irisine.jiris.compiler.assistpart;

import com.irisine.jiris.compiler.expression.IrisExpression;

public class IrisElseIf {
	private IrisExpression m_condition = null;
	private IrisBlock m_block = null;
	
	public IrisElseIf(IrisExpression condition, IrisBlock block) {
		m_condition = condition;
		m_block = block;
	}
	
	public IrisBlock getBlock() {
		return m_block;
	}
	
	public IrisExpression getCondition() {
		return m_condition;
	}
}
