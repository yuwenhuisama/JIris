package org.irislang.jiris.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.irislang.jiris.compiler.IrisInterpreter;
import org.irislang.jiris.core.IrisMethod.IrisUserMethod;
import org.irislang.jiris.core.IrisMethod.MethodAuthority;
import org.irislang.jiris.core.exceptions.IrisExceptionBase;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;
import org.irislang.jiris.irisclass.IrisClassBase;

public class IrisClass implements IrisRunningObject {
	
	public static class SearchResult {
		private IrisMethod m_method = null;
		private boolean m_isCurrentClassMethod = false;
		
		public IrisMethod getMethod() {
			return m_method;
		}
		public void setMethod(IrisMethod method) {
			m_method = method;
		}
		public boolean isCurrentClassMethod() {
			return m_isCurrentClassMethod;
		}
		public void setCurrentClassMethod(boolean isCurrentClassMethod) {
			m_isCurrentClassMethod = isCurrentClassMethod;
		}
	}
	
	private IrisClass m_superClass = null;
	private IrisObject m_classObject = null;
	private IrisModule m_upperModule = null;
	private String m_className = ""; 
	private IrisClassRoot m_externClass = null;
	
	private HashSet<IrisModule> m_involvedModules = new HashSet<IrisModule>();
	private HashSet<IrisInterface> m_involvedInteraces = new HashSet<IrisInterface>();
	
	private HashMap<String, IrisValue> m_classVariables = new HashMap<String, IrisValue>();
	private HashMap<String, IrisValue> m_constances = new HashMap<String, IrisValue>();
	private HashMap<String, IrisMethod> m_instanceMethods = new HashMap<String, IrisMethod>();
	
	public IrisClass(IrisClassRoot externClass) throws IrisExceptionBase {
		m_className = externClass.NativeClassNameDefine();
		m_superClass = externClass.NativeSuperClassDefine();
		m_upperModule = externClass.NativeUpperModuleDefine();
		m_externClass = externClass;
		
		IrisClass classObj = IrisDevUtil.GetClass("Class");
		
		if(classObj != null) {
			m_classObject = classObj.CreateNewInstance(null, null, IrisDevUtil.GetCurrentThreadInfo()).getObject();
			((IrisClassBase.IrisClassBaseTag)m_classObject.getNativeObject()).setClassObj(this);
		}
		else {
			m_classObject = new IrisObject();
			m_classObject.setObjectClass(this);
			m_classObject.setNativeObject(externClass.NativeAlloc());
			((IrisClassBase.IrisClassBaseTag)m_classObject.getNativeObject()).setClassObj(this);
		}
		
		m_externClass.NativeClassDefine(this);

	}

	public IrisClass(String className, IrisModule upperModule, IrisClass superClass) throws IrisExceptionBase {
	    m_className = className;
	    m_upperModule = upperModule;
	    m_superClass = superClass;

        IrisClass classObj = IrisDevUtil.GetClass("Class");
        m_classObject = classObj.CreateNewInstance(null, null, IrisDevUtil.GetCurrentThreadInfo()).getObject();
        ((IrisClassBase.IrisClassBaseTag)IrisDevUtil.GetNativeObjectRef(m_classObject)).setClassObj(this);;
	}
	
	public void ResetAllMethodsObject() throws IrisExceptionBase {

		m_classObject.ResetAllMethodsObject();
		
		Iterator<?> iterator = m_instanceMethods.entrySet().iterator();
		while(iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, IrisMethod> entry = (Map.Entry<String, IrisMethod>) iterator.next();
			IrisMethod method = entry.getValue();
			method.ResetMethodObject();
		}
	}
	
	public void GetMethod(String methodName, SearchResult result) {
		// this class
		IrisMethod method = null;
		result.setCurrentClassMethod(false);
		result.setMethod(null);
		
		method = _GetMethod(this, methodName);
		
		if(method != null) {
			result.setCurrentClassMethod(true);
			result.setMethod(method);
			return;
		}
		
		IrisClass curClass = m_superClass;
		
		while(curClass != null) {
			method = _GetMethod(curClass, methodName);
			if(method != null) {
				result.setCurrentClassMethod(false);
				result.setMethod(method);
				return;
			}
			curClass = curClass.getSuperClass();
		}
		
	}
	
	private IrisMethod _SearchClassModuleMethod(IrisClass searchClass, String methodName) {
		IrisMethod method = null;
		for(IrisModule module : searchClass.m_involvedModules) {
			method = module.GetMethod(methodName);
			if(method != null) {
				break;
			}
		}
		return method;
	}
	
	private IrisMethod _GetMethod(IrisClass searchClass, String methodName){
		IrisMethod method = null;
		method = searchClass.m_instanceMethods.get(methodName);
		if(method == null) {
			// involved module
			method = _SearchClassModuleMethod(searchClass, methodName);
		}
		
		return method;
	}
	
	public IrisValue CreateNewInstance(ArrayList<IrisValue> parameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) throws IrisExceptionBase {
		// new object
		IrisObject object = new IrisObject();
		object.setObjectClass(this);
		if(m_externClass == null) {
            object.setNativeObject(null);
        }
        else {
            Object nativeObj =  m_externClass.NativeAlloc();
            object.setNativeObject(nativeObj);
        }

        if(IrisInterpreter.INSTANCE.getObjectClass() != null) {
            object.CallInstanceMethod("__format", parameterList, context, threadInfo, IrisMethod.CallSide.Outeside);
        }

		return IrisValue.WrapObject(object);
	}
	
	public void AddClassMethod(Class<?> nativeClass, String nativeName, String methodName, int parameterAmount, boolean isWithVariableParameter, IrisMethod.MethodAuthority authority) throws IrisExceptionBase {
		IrisMethod method = new IrisMethod(methodName,
				parameterAmount,
				isWithVariableParameter,
				authority,
				IrisDevUtil.GetIrisNativeMethodHandle(nativeClass, nativeName));
		AddClassMethod(method);
	}

	public void AddClassMethod(Class<?> nativeClass, String nativeName, String methodName, IrisUserMethod userMethod, MethodAuthority authority) throws IrisExceptionBase {
		IrisMethod method = new IrisMethod(methodName, userMethod, authority, IrisDevUtil.GetIrisNativeUserMethodHandle(nativeClass, nativeName));
		AddClassMethod(method);
	}
	
	public void AddInstanceMethod(Class<?> nativeClass, String nativeName, String methodName, int parameterAmount, boolean isWithVariableParameter, IrisMethod.MethodAuthority authority) throws IrisExceptionBase {
		IrisMethod method = new IrisMethod(methodName,
				parameterAmount,
				isWithVariableParameter,
				authority,
				IrisDevUtil.GetIrisNativeMethodHandle(nativeClass, nativeName));
		AddInstanceMethod(method);
	}
	
	public void AddInstanceMethod(Class<?> nativeClass, String nativeName, String methodName, IrisUserMethod userMethod, MethodAuthority authority) throws IrisExceptionBase {
		IrisMethod method = new IrisMethod(methodName, userMethod, authority, IrisDevUtil.GetIrisNativeUserMethodHandle(nativeClass, nativeName));
		AddInstanceMethod(method);
	}

	public void AddClassMethod(IrisMethod method) {
		m_classObject.AddInstanceMethod(method);
	}
	
	public void AddInstanceMethod(IrisMethod method) {
		m_instanceMethods.put(method.getMethodName(), method);
	}
	
	public void AddInvolvedModule(IrisModule moduleObj) {
		m_involvedModules.add(moduleObj);
	}
	
	public void AddInvolvedInterface(IrisInterface interfaceObj) {
		m_involvedInteraces.add(interfaceObj);
	}
	
	public void AddConstance(String name, IrisValue value) {
		m_constances.put(name, value);
	}
		
	public IrisValue GetConstance(String name) {
		return m_constances.get(name);
	}
	
	public IrisValue SearchConstance(String name) {
		// current class 、 involved module、super class
		IrisClass curClass = this;
		IrisValue result = null;
		
		while(curClass != null) {
			result = _GetConstance(curClass, name);
			if(result != null) {
				break;
			}
			curClass = curClass.getSuperClass();
		}
		return result;
	}
	
	private IrisValue _GetConstance(IrisClass curClass, String name) {
		IrisValue result = curClass.GetConstance(name);
		if(result == null) {
			result = _SearchClassModuleConstance(curClass, name);
		}
		
		return result;
	}
	
	private IrisValue _SearchClassModuleConstance(IrisClass curClass, String name) {
		IrisValue result = null;
		for(IrisModule module : curClass.m_involvedModules) {
			result = module.SearchConstance(name);
			if(result != null) {
				break;
			}
		}
		return result;
	}
	
	public void AddClassVariable(String name, IrisValue value) {
		m_classVariables.put(name, value);
	}
	
	public IrisValue GetClassVariable(String name) {
		return m_classVariables.get(name);
	}
	
	public IrisValue SearchClassVariable(String name) {
		// current class 、 involved module、super class
		IrisClass curClass = this;
		IrisValue result = null;
		
		while(curClass != null) {
			result = _GetClassVariable(curClass, name);
			if(result != null) {
				break;
			}
			curClass = curClass.getSuperClass();
		}
		return result;
	}
	
	private IrisValue _GetClassVariable(IrisClass curClass, String name) {
		IrisValue result = curClass.GetClassVariable(name);
		if(result == null) {
			result = _SearchClassModuleClassVariable(curClass, name);
		}
		
		return result;
	}
	
	private IrisValue _SearchClassModuleClassVariable(IrisClass curClass, String name) {
		IrisValue result = null;
		for(IrisModule module : curClass.m_involvedModules) {
			result = module.SearchClassVariable(name);
			if(result != null) {
				break;
			}
		}
		return result;
	}
	
	public IrisClass getSuperClass() {
		return m_superClass;
	}

	public void setSuperClass(IrisClass superClass) {
		m_superClass = superClass;
	}

	public IrisObject getClassObject() {
		return m_classObject;
	}

	public void setClassObject(IrisObject classObject) {
		m_classObject = classObject;
	}
	
	public IrisModule getUpperModule() {
		return m_upperModule;
	}

	public void setUpperModule(IrisModule upperModule) {
		m_upperModule = upperModule;
	}

	public String getClassName() {
		return m_className;
	}

	public void setClassName(String className) {
		m_className = className;
	}
}