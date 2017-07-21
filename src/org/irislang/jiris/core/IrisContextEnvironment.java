package org.irislang.jiris.core;

import java.util.HashMap;

public class IrisContextEnvironment {
	
	public enum RunTimeType {
		ClassDefineTime,
		ModuleDefineTime,
		InterfaceDefineTime,
		RunTime,
	}
	
	private IrisRunningObject m_runningType = null;
	private RunTimeType m_runTimeType = RunTimeType.RunTime; 
	
	private HashMap<String, IrisValue> m_localVariableMap = new HashMap<String, IrisValue>();
	private IrisContextEnvironment m_upperContext = null;
	
	private IrisMethod m_currentMethod = null;

	private IrisObject m_closureBlockObj = null;

    public IrisObject getClosureBlockObj() {
        return m_closureBlockObj;
    }
    
    public void setClosureBlockObj(IrisObject closureBlockObj) {
        this.m_closureBlockObj = closureBlockObj;
    }

    public IrisMethod getCurrentMethod() {
		return m_currentMethod;
	}
	
	public void setCurrentMethod(IrisMethod currentMethod) {
		m_currentMethod = currentMethod;
	}
	
	public IrisValue GetLocalVariableWithinChain(String localName) {
		IrisContextEnvironment tmp = this;
		IrisValue value = null;
		while(tmp != null) {
			value = tmp.GetLocalVariable(localName);
			if(value != null) {
				break;
			}
			tmp = tmp.m_upperContext;
		}
		return value;
	}
		
	public IrisValue GetLocalVariable(String localName) {
		return m_localVariableMap.get(localName);
	}
	
	public void AddLocalVariable(String localName, IrisValue value) {
		m_localVariableMap.put(localName, value);
	}
	
	public IrisRunningObject getRunningType() {
		return m_runningType;
	}
	
	public void setRunningType(IrisRunningObject runningType) {
		m_runningType = runningType;
	}

	public RunTimeType getRunTimeType() {
		return m_runTimeType;
	}

	public void setRunTimeType(RunTimeType runTymeType) {
		m_runTimeType = runTymeType;
	}
	
	public void setUpperContext(IrisContextEnvironment upperContext) {
		m_upperContext = upperContext;
	}
	
	public IrisContextEnvironment getUpperContext() {
		return m_upperContext;
	}
}
