package com.irisine.jiris.compiler.assistpart;

import java.util.LinkedList;

import com.irisine.jiris.compiler.IrisSyntaxUnit;
import com.irisine.jiris.compiler.statement.IrisStatement;

public class IrisClosureBlockLiteral extends IrisSyntaxUnit {
	@SuppressWarnings("unused")
	private LinkedList<IrisIdentifier> m_parameters = null;
	@SuppressWarnings("unused")
	private IrisIdentifier m_variableParameter = null;
	@SuppressWarnings("unused")
	private LinkedList<IrisStatement> m_statements = null;
	
	public boolean Generate() {
		return false;
	}
	
	public IrisClosureBlockLiteral(LinkedList<IrisIdentifier> parameters, IrisIdentifier variableParameter, LinkedList<IrisStatement> statements){
		m_parameters = parameters;
		m_variableParameter = variableParameter;
		m_statements = statements;
	}
}
