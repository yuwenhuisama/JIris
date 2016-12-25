package org.irislang.jiris.core;

import java.util.HashMap;
import java.util.HashSet;

import org.irislang.jiris.dev.IrisDevUtil;
import org.irislang.jiris.dev.IrisModuleRoot;
import org.irislang.jiris.irisclass.IrisModuleBase.IrisModuleBaseTag;

public class IrisModule implements IrisRunningObject {
	private String m_moduleName; 
	private IrisObject m_moduleObject = null;
	private IrisModule m_upperModule = null;
	private HashSet<IrisClass> m_subClasses = new HashSet<IrisClass>();
	private HashSet<IrisModule> m_subModules = new HashSet<IrisModule>();
	private HashSet<IrisModule> m_involvedModules = new HashSet<IrisModule>();
	private HashMap<String, IrisValue> m_constances = new HashMap<String, IrisValue>();
	private HashMap<String, IrisValue> m_classVariables = new HashMap<String, IrisValue>();
	private HashMap<String, IrisMethod> m_instanceMethods = new HashMap<String, IrisMethod>();
	
	public IrisModule(IrisModuleRoot upperModule) throws Throwable {
		setModuleName(upperModule.NativeModuleNameDefine());
		setUpperModule(upperModule.NativeUpperModuleDefine());
		
		IrisValue obj = IrisDevUtil.GetClass("Module").CreateNewInstance(null, null, IrisDevUtil.GetCurrentThreadInfo());
		((IrisModuleBaseTag)IrisDevUtil.GetNativeObjectRef(obj)).setModule(this);
		m_moduleObject = obj.getObject();
		
		upperModule.NativeModuleDefine(this);
	}
	
	public void AddSubClass(IrisClass subClass) {
		m_subClasses.add(subClass);
	}
	
	public void AddSubModule(IrisModule subModule) {
		m_subModules.add(subModule);
	}
	
	public IrisMethod GetMethod(String methodName) {
		return _SearchMethod(this, methodName);
	}
	
	public IrisMethod _SearchMethod(IrisModule curModule, String methodName) {
		IrisMethod method = curModule.m_instanceMethods.get(methodName);
		if(method != null) {
			return method;
		}
		
		for(IrisModule module : m_involvedModules) {
			method = _SearchMethod(module, methodName);
			if(method != null) {
				return method;
			}
		}
		return null;
	}
	
	public IrisObject getModuleObject() {
		return m_moduleObject;
	}

	public void setModuleObject(IrisObject moduleObject) {
		m_moduleObject = moduleObject;
	}
	
	public void AddConstance(String name, IrisValue value) {
		m_constances.put(name, value);
	}
	
	public IrisValue GetConstance(String name) {
		return m_constances.get(name);
	}

	public IrisValue SearchConstance(String name) {
		return _SearchConstance(this, name);
	}
	
	private IrisValue _SearchConstance(IrisModule curModule, String name) {
		IrisValue result = curModule.GetConstance(name);
		
		if(result != null) {
			return result;
		}
		
		for(IrisModule module : curModule.m_involvedModules){
			result = _SearchConstance(module, name);
			if(result != null) {
				return result;
			}
		}
		return null;
	}
	
	public IrisValue GetClassVariable(String name) {
		return m_classVariables.get(name);
	}
	
	public IrisValue SearchClassVariable(String name) {
		return _SearchClassVariable(this, name);
	}
	
	private IrisValue _SearchClassVariable(IrisModule curModule, String name) {
		IrisValue result = curModule.GetConstance(name);
		
		if(result != null) {
			return result;
		}
		
		for(IrisModule module : curModule.m_involvedModules){
			result = _SearchClassVariable(module, name);
			if(result != null) {
				return result;
			}
		}
		return null;
	}
	
	public IrisModule getUpperModule() {
		return m_upperModule;
	}

	public void setUpperModule(IrisModule upperModule) {
		m_upperModule = upperModule;
	}

	public String getModuleName() {
		return m_moduleName;
	}

	public void setModuleName(String moduleName) {
		m_moduleName = moduleName;
	}
	
	public void AddClassMethod(Class<?> nativeClass, String nativeName, String methodName, int parameterAmount, boolean isWithVariableParameter, IrisMethod.MethodAuthority authority) throws Throwable {
		IrisMethod method = new IrisMethod(methodName,
				parameterAmount,
				isWithVariableParameter,
				authority,
				IrisDevUtil.GetIrisNativeMethodHandle(nativeClass, nativeName));
		AddClassMethod(method);
	}

	public void AddInstanceMethod(Class<?> nativeClass, String nativeName, String methodName, int parameterAmount, boolean isWithVariableParameter, IrisMethod.MethodAuthority authority) throws Throwable {
		IrisMethod method = new IrisMethod(methodName,
				parameterAmount,
				isWithVariableParameter,
				authority,
				IrisDevUtil.GetIrisNativeMethodHandle(nativeClass, nativeName));
		AddInstanceMethod(method);
	}
	
	private void AddClassMethod(IrisMethod method) {
		m_moduleObject.AddInstanceMethod(method);
	}
	
	private void AddInstanceMethod(IrisMethod method) {
		m_instanceMethods.put(method.getMethodName(), method);
	}

	public void AddClassVariable(String variableName, IrisValue value) {
		m_classVariables.put(variableName, value);
	}
}
