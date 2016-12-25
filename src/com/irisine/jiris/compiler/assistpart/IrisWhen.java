package com.irisine.jiris.compiler.assistpart;

import java.util.LinkedList;

import com.irisine.jiris.compiler.expression.IrisExpression;

public class IrisWhen {
	private LinkedList<IrisExpression> m_conditions = null;
	private IrisBlock m_whenBlock = null;
	
	public IrisWhen(LinkedList<IrisExpression> conditions, IrisBlock whenBlock) {
		m_conditions = conditions;
		m_whenBlock = whenBlock;
	}
	
	public LinkedList<IrisExpression> getConditions() {
		return m_conditions;
	}
	
	public IrisBlock getWhenBlock() {
		return m_whenBlock;
	}
}
