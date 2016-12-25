package org.irislang.jiris.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.irislang.jiris.core.IrisMethod.CallSide;
import org.irislang.jiris.core.IrisMethod.MethodAuthority;
import org.irislang.jiris.dev.IrisDevUtil;

public class IrisObject implements IrisRunningObject {
	private IrisClass m_class = null;
	private HashMap<String, IrisMethod> m_methods = new HashMap<String, IrisMethod>();
	private HashMap<String, IrisValue> m_instanceValues = new HashMap<String, IrisValue>();
	private Object m_nativeObject = null;
	private static int sm_objectNumber = 0;
	private int m_objectID = 0;
	
	public IrisObject() {
		m_objectID = ++sm_objectNumber;
	}
	
	public int getObjectID() {
		return m_objectID;
	}
	
	public IrisValue CallInstanceMethod(String methodName, ArrayList<IrisValue> parameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo, IrisMethod.CallSide callSide) throws Throwable {
		IrisMethod method = null;
		boolean isCurrentMethod = false;
		
		// Object's instance method
		if(m_methods.containsKey(methodName)) {
			method = m_methods.get(methodName);
			isCurrentMethod = true;
		// instance method in class
		} else {
 			IrisClass.SearchResult result = new IrisClass.SearchResult();
			m_class.GetMethod(methodName, result);
 			method = result.getMethod();
			isCurrentMethod = result.isCurrentClassMethod();
		}
		
		if(method == null) {
			/* Error */
			//return CallInstanceMethod("missing_method", parameterList, context, callSide);
			return IrisDevUtil.Nil();
		}
		
		// Inside call
		IrisValue callResult = null;
		IrisValue caller = IrisValue.WrapObject(this);
		if(callSide == CallSide.Inside) {
			
			if(isCurrentMethod) {
				callResult = method.Call(caller, parameterList, context, threadInfo);
			}
			else {
				if(method.getAuthority() == MethodAuthority.Personal) {
					/* Error */
					callResult = IrisDevUtil.Nil();
				}
				else {
					callResult = method.Call(caller, parameterList, context, threadInfo);
				}
			}
		}
		// Outside call
		else {
			if(method.getAuthority() != MethodAuthority.Everyone) {
				/* Error */
				callResult = IrisDevUtil.Nil();
			}
			else {
				callResult = method.Call(caller, parameterList, context, threadInfo);
			}
		}
		
		return callResult;
	}

	public void AddInstanceMethod(IrisMethod method) {
		m_methods.put(method.getMethodName(), method);
	}
	
	public void AddInstanceVariable(String name, IrisValue value) {
		m_instanceValues.put(name, value);
	}
	
	public IrisValue GetInstanceVariable(String name) {
		return m_instanceValues.get(name);
	}
	
	public IrisClass getObjectClass() {
		return m_class;
	}

	public void setObjectClass(IrisClass classObj) {
		m_class = classObj;
	}

	public Object getNativeObject() {
		return m_nativeObject;
	}

	public void setNativeObject(Object nativeObject) {
		m_nativeObject = nativeObject;
	}
	
	public void ResetAllMethodsObject() throws Throwable {
		Iterator<?> iterator = m_methods.entrySet().iterator();
		while(iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, IrisMethod> entry = (Entry<String, IrisMethod>) iterator.next();
			IrisMethod method = entry.getValue();
			method.ResetMethodObject();
		}
	}

	
}
 