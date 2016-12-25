package com.irisine.jiris.compiler.assistpart;


public class IrisDeferredBlock {
	private IrisBlock m_block = null;
	private String m_generateName = null;
	
	public IrisDeferredBlock(IrisBlock block, String generateName) {
		m_block = block;
		m_generateName = generateName;
	}
	
	public String getGenerateName() {
		return m_generateName;
	}
	
	public IrisBlock getStatement() {
		return m_block;
	}
}
