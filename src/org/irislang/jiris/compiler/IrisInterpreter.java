package org.irislang.jiris.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.irisine.jiris.compiler.IrisCompiler;

import org.irislang.jiris.core.IrisClass;
import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisInterface;
import org.irislang.jiris.core.IrisMethod;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;
import org.irislang.jiris.dev.IrisModuleRoot;
import org.irislang.jiris.irisclass.*;
import org.irislang.jiris.irisclass.IrisModuleBase.IrisModuleBaseTag;
import org.irislang.jiris.irisclass.IrisClassBase.IrisClassBaseTag;
import org.irislang.jiris.irismodule.IrisKernel;

public class IrisInterpreter {
	
	public static final IrisInterpreter INSTANCE = new IrisInterpreter();
	private IrisInterpreter() {}
	
	private HashMap<String, IrisValue> m_constances = new HashMap<String, IrisValue>();
	private HashMap<String, IrisValue> m_globalValues = new HashMap<String, IrisValue>();
	
	private HashMap<String, IrisMethod> m_mainMethods = new HashMap<String, IrisMethod>(); 
	
	private IrisValue m_Nil = null;
	private IrisValue m_True = null;
	private IrisValue m_False = null;
	
	private int m_generatedJavaClassFileNumber = 0;
	
	private IrisCompiler m_currentCompiler = null;
	
	public void InceamJavaClassFileNumber() {
		++m_generatedJavaClassFileNumber;
	}
	
	public int getJavaClassFileNumber() {
		return m_generatedJavaClassFileNumber;
	}
	
	public IrisValue Nil() {
		return m_Nil;
	}
	
	public IrisValue True() {
		return m_True;
	}
	
	public IrisValue False() {
		return m_False;
	}
	
	public void AddMainMethod(IrisMethod method) {
		m_mainMethods.put(method.getMethodName(), method);
	}
	
	public IrisMethod GetMainMethod(String methodName) {
		return m_mainMethods.get(methodName);
	}
	
	public IrisModule GetModule(LinkedList<String> fullPath) throws Throwable {
		
		if(fullPath.isEmpty()) {
			throw new Exception("Path is empty!");
		}
		
		IrisModule tmpCur = null;
		IrisValue tmpValue = null;
 		String firstModuleName = fullPath.pop();
		
		tmpValue = GetConstance(firstModuleName);
 		if(tmpValue != null) {
			if(IrisDevUtil.IsModuleObject(tmpValue)) {
				tmpCur = ((IrisModuleBaseTag)IrisDevUtil.GetNativeObjectRef(tmpValue)).getModule();
			}
			else {
				return null;
			}
		} else {
			return null;
		}
		
		for(String moduleName : fullPath){
			tmpValue = tmpCur.GetConstance(moduleName);
			if(tmpValue != null) {
				if(IrisDevUtil.IsModuleObject(tmpValue)) {
					tmpCur = ((IrisModuleBaseTag)IrisDevUtil.GetNativeObjectRef(tmpValue)).getModule();
				}
				else {
					break;
				}
			}
		}
		return tmpCur;
	}
	
	public IrisClass GetClass(LinkedList<String> fullPath) {
		String className = fullPath.removeLast();
		
		IrisModule tmpUpperModule = null;
		IrisValue tmpValue = null;
		
		if(fullPath.isEmpty()) {
			tmpValue = GetConstance(className);
			if(tmpValue == null) {
				return null;
			}
			if(IrisDevUtil.IsClassObject(tmpValue)) {
				return ((IrisClassBaseTag)IrisDevUtil.GetNativeObjectRef(tmpValue)).getClassObj();
			} else {
				return null;
			}
		} else {
			try {
				tmpUpperModule = GetModule(fullPath);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			if(tmpUpperModule != null) {
				tmpValue = tmpUpperModule.GetConstance(className);
				if(tmpValue != null) {
					if(IrisDevUtil.IsClassObject(tmpValue)) {
						return ((IrisClassBaseTag)IrisDevUtil.GetNativeObjectRef(tmpValue)).getClassObj();
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}
	
	public IrisInterface GetInterface(LinkedList<String> fullPath) {
		String className = fullPath.removeLast();
		
		IrisModule tmpUpperModule = null;
		IrisValue tmpValue = null;
		
		if(fullPath.isEmpty()) {
			tmpValue = GetConstance(className);
			if(tmpValue == null) {
				return null;
			}
			if(IrisDevUtil.IsInterfaceObject(tmpValue)) {
				return IrisDevUtil.GetNativeObjectRef(tmpValue);
			} else {
				return null;
			}
		} else {
			try {
				tmpUpperModule = GetModule(fullPath);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			if(tmpUpperModule != null) {
				tmpValue = tmpUpperModule.GetConstance(className);
				if(tmpValue != null) {
					if(IrisDevUtil.IsInterfaceObject(tmpValue)) {
						return IrisDevUtil.GetNativeObjectRef(tmpValue);
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	public IrisModule GetModule(String fullPath) {
		String[] result = fullPath.split("::");
		LinkedList<String> stringList = new LinkedList<String>(Arrays.asList(result));
		IrisModule tmpMoudule = null;
		try {
			tmpMoudule = GetModule(stringList);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return tmpMoudule;
	}
	
	public IrisClass GetClass(String fullPath) {
		String[] result = fullPath.split("::");
		LinkedList<String> stringList = new LinkedList<String>(Arrays.asList(result));
		return GetClass(stringList);
	}

	public IrisInterface GetInterface(String fullPath) {
		String[] result = fullPath.split("::");
		LinkedList<String> stringList = new LinkedList<String>(Arrays.asList(result));
		return GetInterface(stringList);
	}
	
	public boolean RegistClass(IrisClassRoot classObj) throws Throwable {
		
		IrisModule upperModule = classObj.NativeUpperModuleDefine();
		String className = classObj.NativeClassNameDefine();
		
		if(upperModule == null) {
			if(GetConstance(className) != null) {
				return false;
			}
		} else {
			if(upperModule.GetConstance(className) != null) {
				return false;
			}
		}
		
		IrisClass classInternObj = new IrisClass(classObj);
		IrisValue classValue = IrisValue.WrapObject(classInternObj.getClassObject());
		
		if(upperModule == null) {
			AddConstance(className, classValue);
		} else {
			upperModule.AddConstance(className, classValue);
			upperModule.AddSubClass(classInternObj);
		}
		
		return true;
	}
	
	public boolean RegistModule(IrisModuleRoot moduleObj) throws Throwable {
		
		IrisModule upperModule = moduleObj.NativeUpperModuleDefine();
		String moduleName = moduleObj.NativeModuleNameDefine();
		
		if(upperModule == null) {
			if(GetConstance(moduleName) != null) {
				return false;
			}
		} else {
			if(upperModule.GetConstance(moduleName) != null) {
				return false;
			}
		}
		
		IrisModule moduleInternObj = new IrisModule(moduleObj);
		IrisValue moduleValue = IrisValue.WrapObject(moduleInternObj.getModuleObject());
		
		if(upperModule == null) {
			AddConstance(moduleName, moduleValue);
		} else {
			upperModule.AddConstance(moduleName, moduleValue);
			upperModule.AddSubModule(moduleInternObj);
		}
		
		return true;
	}
	
/*	public boolean RegistClass(IrisClass classObj) {
		if(classObj.getUpperModule() == null) {
			if(GetConstance(classObj.getClassName()) != null) {
				return false;
			} else {
				AddConstance(classObj.getClassName(), IrisValue.WrapObject(classObj.getClassObject()));
			}
		} else {
			IrisModule upperModule = classObj.getUpperModule();
			if(upperModule.GetConstance(classObj.getClassName()) != null) {
				return false;
			} else {
				upperModule.AddConstance(classObj.getClassName(), IrisValue.WrapObject(classObj.getClassObject()));
			}
		}
		return true;
	}
	
	public boolean RegistInterface(IrisInterface interfaceObj) {
		return true;
	}
*/			
	public void AddConstance(String name, IrisValue value) {
		m_constances.put(name, value);
	}
	
	public IrisValue GetConstance(String name) {
		return m_constances.get(name);
	}
	
	public void AddGlobalValue(String name, IrisValue value) {
		m_globalValues.put(name, value);
	}
	
	public IrisValue GetGlobalValue(String name) {
		return m_globalValues.get(name);
	}
		
	public boolean Initialize() throws Throwable {
		
		IrisThreadInfo.SetMainThreadID(Thread.currentThread().getId());
		IrisThreadInfo mainThreadInfo = new IrisThreadInfo();		
		IrisThreadInfo.SetMainThreedInfo(mainThreadInfo);
		
		RegistClass(new IrisClassBase());
		RegistClass(new IrisModuleBase());
		
		RegistModule(new IrisKernel());	
		
		RegistClass(new IrisObjectBase());
				
		IrisDevUtil.GetClass("Class").setSuperClass(IrisDevUtil.GetClass("Object"));
		
		RegistClass(new IrisMethodBase());
		
		IrisDevUtil.GetClass("Class").ResetAllMethodsObject();
		IrisDevUtil.GetClass("Object").ResetAllMethodsObject();
		IrisDevUtil.GetClass("Method").ResetAllMethodsObject();
		
		RegistClass(new IrisInteger());
		RegistClass(new IrisFloat());
		RegistClass(new IrisString());
		RegistClass(new IrisUniqueString());
		
		RegistClass(new IrisTrueClass());
		RegistClass(new IrisFalseClass());
		RegistClass(new IrisNilClass());
		
		RegistClass(new IrisArray());
		RegistClass(new IrisArrayIterator());
		RegistClass(new IrisHash());
		RegistClass(new IrisHashIterator());
		
		m_True = IrisDevUtil.GetClass("TrueClass").CreateNewInstance(null, null, IrisDevUtil.GetCurrentThreadInfo());
		m_False = IrisDevUtil.GetClass("FalseClass").CreateNewInstance(null, null, IrisDevUtil.GetCurrentThreadInfo());
		m_Nil = IrisDevUtil.GetClass("NilClass").CreateNewInstance(null, null, IrisDevUtil.GetCurrentThreadInfo());
		
		return true;
	}
	
	public boolean Run() {
		if(m_currentCompiler == null) {
			return false;
		}
		
		Class<?> runClass = m_currentCompiler.getJavaClass();
		IrisContextEnvironment mainEnv = new IrisContextEnvironment();
		
		try {
			Object instance = runClass.newInstance();
			Method method = runClass.getMethod("run", IrisContextEnvironment.class, IrisThreadInfo.class);
 			method.invoke(instance, mainEnv, IrisDevUtil.GetCurrentThreadInfo());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean ShutDown() {
		return true;
	}

	public IrisCompiler getCurrentCompiler() {
		return m_currentCompiler;
	}

	public void setCurrentCompiler(IrisCompiler currentCompiler) {
		m_currentCompiler = currentCompiler;
	}
}
