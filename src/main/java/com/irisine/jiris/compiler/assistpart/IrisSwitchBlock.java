package com.irisine.jiris.compiler.assistpart;

import java.util.LinkedList;

import com.irisine.jiris.compiler.expression.IrisExpression;

public class IrisSwitchBlock {
	private LinkedList<IrisWhen> m_whenList = null;
	private IrisBlock m_elseBlock = null;
	
	public IrisSwitchBlock(LinkedList<IrisWhen> whenList, IrisBlock elseBlock) {
		m_whenList = whenList;
		m_elseBlock = elseBlock;
	}
	
	public LinkedList<IrisWhen> getWhenList() {
		return m_whenList;
	}
	
	public IrisBlock getElseBlock() {
		return m_elseBlock;
	}
	
}
