package org.irislang.jiris.core;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Iterator;

import org.irislang.jiris.core.IrisContextEnvironment.RunTimeType;
import org.irislang.jiris.dev.IrisDevUtil;
import org.irislang.jiris.irisclass.IrisMethodBase;

public class IrisMethod {
	
	public enum MethodAuthority {
		Everyone,
		Relative,
		Personal,
	}
	
	public enum CallSide {
		Outeside,
		Inside,
	}
	
	private MethodAuthority m_authority;
	private String m_methodName = "";
	private int m_parameterCount = 0;
	private boolean m_isWithVariableParameter = false;
	private IrisUserMethod m_userMethod = null;
	private IrisObject m_methodObject = null;
	private MethodHandle m_methodHanlde = null;
	
	public IrisObject getMethodObject() {
		return m_methodObject;
	}
	
	public IrisMethod(String methodName, int parameterCount, boolean isWithVariableParameter, MethodAuthority authority, MethodHandle methodHandle) throws Throwable {
		m_methodName = methodName;
		m_parameterCount = parameterCount;
		m_isWithVariableParameter = isWithVariableParameter;
		m_authority = authority;
		m_userMethod = null;
		m_methodHanlde = methodHandle;
		IrisClass methodClass = IrisDevUtil.GetClass("Method");
		if(methodClass != null) {
			CreateMethodObject(methodClass);
		}
	}
	
	public IrisMethod(String methodName, IrisUserMethod userMethod, MethodAuthority authority, MethodHandle methodHandle) throws Throwable {
		m_methodName = methodName;
		m_parameterCount = userMethod.getParameterList() == null ? 0 : userMethod.getParameterList().size();
		m_isWithVariableParameter = userMethod.getVariableParameterName().equals("");
		m_userMethod = userMethod;
		m_authority = authority;
		m_methodHanlde = methodHandle;
		
		CreateMethodObject(IrisDevUtil.GetClass("Method"));
	}
	
	private IrisContextEnvironment CreateNewContext(IrisObject caller, ArrayList<IrisValue> parameterList, IrisContextEnvironment currentContext, IrisThreadInfo threadInfo) throws Throwable {
		IrisContextEnvironment newContex = new IrisContextEnvironment();
		
		newContex.setRunTimeType(RunTimeType.RunTime);
		newContex.setRunningType(caller);
		
		newContex.setUpperContext(currentContext);
		
		newContex.setCurrentMethod(this);
	
		if(m_userMethod != null) {
			// with/without block
		
			// parameter -> local variable && variable parameter process
			if(parameterList != null && parameterList.size() != 0) {
				int counter = 0;
				Iterator<IrisValue> iterator = parameterList.iterator();
				for(String value : m_userMethod.getParameterList()) {
					newContex.AddLocalVariable(value, iterator.next());
					++counter;
				}
				
				if(m_isWithVariableParameter) {
					ArrayList<IrisValue> variables = new ArrayList<IrisValue>(parameterList.subList(counter, parameterList.size()));
					IrisClass arrayClass = IrisDevUtil.GetClass("Array");
					IrisValue arrayValue = arrayClass.CreateNewInstance(variables, null, threadInfo);
					newContex.AddLocalVariable(m_userMethod.m_variableParameterName, arrayValue);
				}
			}
		}
		
		return newContex;
	}
	
	private boolean ParameterCheck(ArrayList<IrisValue> parameterList) {
		if(parameterList != null && parameterList.size() > 0) {
			if(m_isWithVariableParameter) {
				return parameterList.size() >= m_parameterCount;
			} else {
				return parameterList.size() == m_parameterCount;
			}
		} else {
			return m_parameterCount == 0;
		}
	}
	
	private void CreateMethodObject(IrisClass methodClass) throws Throwable {
		IrisValue methodObj = methodClass.CreateNewInstance(null, null, IrisDevUtil.GetCurrentThreadInfo());
		((IrisMethodBase.IrisMethodBaseTag)(IrisDevUtil.GetNativeObjectRef(methodObj))).setMethodObj(this);
		m_methodObject = methodObj.getObject();
	}
	
	public void ResetMethodObject() throws Throwable {
		CreateMethodObject(IrisDevUtil.GetClass("Method"));
	}
	
	@SuppressWarnings("rawtypes")
	public IrisValue Call(IrisValue caller, ArrayList<IrisValue> parameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) throws Throwable {
		IrisValue result = null;
		
		if(!ParameterCheck(parameterList)) {
			/* Error */
			return IrisDevUtil.Nil();
		}
		
		// Getter Setter
		
		IrisContextEnvironment newContext = CreateNewContext(caller.getObject(), parameterList, context, threadInfo);
		// Call
		if(parameterList == null || parameterList.size() == 0) {
			if(m_userMethod == null) {
				result = (IrisValue) m_methodHanlde.invokeExact(caller, (ArrayList)null, (ArrayList)null, newContext, threadInfo);
			} else {
				result = (IrisValue) m_methodHanlde.invokeExact(newContext, threadInfo);
			}
		} else {
			if(m_userMethod == null) {
				// Variable Parameters
				ArrayList<IrisValue> variableValues = null; 
				ArrayList<IrisValue> normalParameters = null;
				if(parameterList.size() > m_parameterCount) {
					variableValues = new ArrayList<IrisValue>(parameterList.subList(m_parameterCount, parameterList.size()));
				}
				if(m_parameterCount > 0) {
					normalParameters = new ArrayList<IrisValue>(parameterList.subList(0, m_parameterCount));
				}
				result = (IrisValue) m_methodHanlde.invokeExact(caller, normalParameters, variableValues, newContext, threadInfo);
			} else {
				result = (IrisValue) m_methodHanlde.invokeExact(newContext, threadInfo);
			}
		}
		
		return result;
	}
	
	public IrisValue CallMain(ArrayList<IrisValue> arrayList, IrisContextEnvironment context, IrisThreadInfo threadInfo) throws Throwable{
		if(!ParameterCheck(arrayList)) {
			/* Error */
			return IrisDevUtil.Nil();
		}
		
		IrisContextEnvironment newContext = CreateNewContext(null, arrayList, context, threadInfo);
		
		return (IrisValue) m_methodHanlde.invokeExact(newContext, threadInfo);
	}
	
	public String getMethodName() {
		return m_methodName;
	}
	public void setMethodName(String methodName) {
		m_methodName = methodName;
	}
	
	public MethodAuthority getAuthority() {
		return m_authority;
	}

	public void setAuthority(MethodAuthority authority) {
		m_authority = authority;
	}

	static public class IrisUserMethod {
		private ArrayList<String> m_parameterList = null;
		private String m_variableParameterName = "";
		private MethodHandle m_withBlockHandle = null;
		private MethodHandle m_withoutBlockHandle = null;
		
		public ArrayList<String> getParameterList() {
			return m_parameterList;
		}
		public void setParameterList(ArrayList<String> parameterList) {
			m_parameterList = parameterList;
		}
		public String getVariableParameterName() {
			return m_variableParameterName;
		}
		public void setVariableParameterName(String variableParameterName) {
			m_variableParameterName = variableParameterName;
		}
		public MethodHandle getWithBlockHandle() {
			return m_withBlockHandle;
		}
		public void setWithBlockHandle(MethodHandle withBlockHandle) {
			m_withBlockHandle = withBlockHandle;
		}
		public MethodHandle getWithoutBlockHandle() {
			return m_withoutBlockHandle;
		}
		public void setWithoutBlockHandle(MethodHandle withoutBlockHandle) {
			m_withoutBlockHandle = withoutBlockHandle;
		}
	}
}
