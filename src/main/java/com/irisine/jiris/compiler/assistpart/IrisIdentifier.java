package com.irisine.jiris.compiler.assistpart;

import com.irisine.jiris.compiler.IrisSyntaxUnit;

public class IrisIdentifier extends IrisSyntaxUnit {
	
	public enum IdentifierType {
		Constance,
		LocalVariable,
		GlobalVariable,
		InstanceVariable,
		ClassVariable,
	}
	
	private String m_identifier = "";
	private IdentifierType m_type = IdentifierType.Constance;
	private int m_lineNumber = 0;
	
	public IrisIdentifier(IdentifierType type, String identifier) {
		m_type = type;
		m_identifier = identifier;
	}
	
	public String getIdentifier() {
		return m_identifier;
	}
	
	public IdentifierType getType() {
		return m_type;
	}

	public int getLineNumber() {
		return m_lineNumber;
	}

	public void setLineNumber(int m_lineNumber) {
		this.m_lineNumber = m_lineNumber;
	}
}
