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
	//private LinkedList<IrisStatement> m_statements = null;
    private IrisBlock m_block = null;

    public IrisBlock getBlock() {
        return m_block;
    }

    public void setBlock(IrisBlock block) {
        m_block = block;
    }

    public IrisClosureBlockLiteral(LinkedList<IrisIdentifier> parameters, IrisIdentifier variableParameter, IrisBlock block){
		m_parameters = parameters;
		m_variableParameter = variableParameter;
		//m_statements = statements;
        m_block = block;
	}

    public LinkedList<IrisIdentifier> getParameters() {
        return m_parameters;
    }

    public void setParameters(LinkedList<IrisIdentifier> parameters) {
        m_parameters = parameters;
    }

    public IrisIdentifier getVariableParameter() {
        return m_variableParameter;
    }

    public void setVariableParameter(IrisIdentifier variableParameter) {
        m_variableParameter = variableParameter;
    }
}
