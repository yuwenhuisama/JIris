package com.irisine.jiris.compiler.assistpart;

import java.util.LinkedList;

import com.irisine.jiris.compiler.IrisSyntaxUnit;

public class IrisFunctionHeader extends IrisSyntaxUnit {
	private IrisIdentifier m_functionName = null;
	private LinkedList<IrisIdentifier> m_parameters = null;
	private IrisIdentifier m_variableParameter = null;
	private boolean m_isClassMethod = false;
	private int m_lineNumber = -1;
	
	public IrisFunctionHeader(IrisIdentifier functionName, LinkedList<IrisIdentifier> parameters, IrisIdentifier variableParameter, boolean isClassMethod) {
		m_functionName = functionName;
		m_parameters = parameters;
		m_variableParameter = variableParameter;
		m_isClassMethod = isClassMethod;
	}

    public int getLineNumber() {
        return m_lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        m_lineNumber = lineNumber;
    }

    public IrisIdentifier getFunctionName() {
		return m_functionName;
	}
	
	public LinkedList<IrisIdentifier> getParameters() {
		return m_parameters;
	}
	
	public IrisIdentifier getVariableParameter() {
		return m_variableParameter;
	}
	
	public boolean isClassMethod() {
		return m_isClassMethod;
	}
	
}
