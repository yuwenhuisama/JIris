package org.irislang.jiris.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

import com.irisine.jiris.compiler.IrisCompiler;

import org.irislang.jiris.core.*;
import org.irislang.jiris.core.exceptions.IrisExceptionBase;
import org.irislang.jiris.core.exceptions.IrisRuntimeException;
import org.irislang.jiris.core.exceptions.fatal.*;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;
import org.irislang.jiris.dev.IrisModuleRoot;
import org.irislang.jiris.irisclass.*;
import org.irislang.jiris.irisclass.IrisModuleBase.IrisModuleBaseTag;
import org.irislang.jiris.irisclass.IrisClassBase.IrisClassBaseTag;
import org.irislang.jiris.irismodule.IrisKernel;
import org.apache.logging.log4j.*;

public class IrisInterpreter {

    private IrisClass m_classClass = null;
    private IrisClass m_moduleClass = null;
    private IrisClass m_interfaceClass = null;
    private IrisClass m_objectClass = null;
    private IrisClass m_methodClass = null;

    public IrisClass getMethodClass() {
        return m_methodClass;
    }

    public void setMethodClass(IrisClass methodClass) {
        m_methodClass = methodClass;
    }

    public IrisClass getObjectClass() {
        return m_objectClass;
    }

    public void setObjectClass(IrisClass objectClass) {
        m_objectClass = objectClass;
    }

    public IrisClass getClassClass() {
        return m_classClass;
    }

    public void setClassClass(IrisClass classClass) {
        m_classClass = classClass;
    }

    public IrisClass getModuleClass() {
        return m_moduleClass;
    }

    public void setModuleClass(IrisClass moduleClass) {
        m_moduleClass = moduleClass;
    }

    public IrisClass getInterfaceClass() {
        return m_interfaceClass;
    }

    public void setInterfaceClass(IrisClass interfaceClass) {
        m_interfaceClass = interfaceClass;
    }

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
	
	public IrisModule GetModule(LinkedList<String> fullPath) throws IrisExceptionBase {
		
		if(fullPath.isEmpty()) {
			throw new IrisUnkownFatalException(IrisDevUtil.GetCurrentThreadInfo().getCurrentFileName(),
                    IrisDevUtil.GetCurrentThreadInfo().getCurrentLineNumber(),
                    "Oh, shit! An UNKNOWN ERROR has been lead to by YOU to Iris! What a SHIT unlucky man you are! " +
                            "Please don't approach Iris ANYMORE ! - The interface CANNOT be registed to Iris.");
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
	
	public boolean RegistClass(IrisClassRoot classObj) throws IrisExceptionBase {
		
		IrisModule upperModule = classObj.NativeUpperModuleDefine();
		String className = classObj.NativeClassNameDefine();
		
		if(upperModule == null) {
			if(GetConstance(className) != null) {
			    throw new IrisClassExistsException(IrisDevUtil.GetCurrentThreadInfo()
                        .getCurrentFileName(),
                        IrisDevUtil.GetCurrentThreadInfo().getCurrentLineNumber(),
                        "Class " + className +  " has been already registered.");
			}
		} else {
			if(upperModule.GetConstance(className) != null) {
                throw new IrisClassExistsException(IrisDevUtil.GetCurrentThreadInfo()
                        .getCurrentFileName(),
                        IrisDevUtil.GetCurrentThreadInfo().getCurrentLineNumber(),
                        "Class " + className +  " has been already registered.");
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
	
	public boolean RegistModule(IrisModuleRoot moduleObj) throws IrisExceptionBase {
		
		IrisModule upperModule = moduleObj.NativeUpperModuleDefine();
		String moduleName = moduleObj.NativeModuleNameDefine();
		
		if(upperModule == null) {
			if(GetConstance(moduleName) != null) {
                throw new IrisModuleExistsException(IrisDevUtil.GetCurrentThreadInfo()
                        .getCurrentFileName(),
                        IrisDevUtil.GetCurrentThreadInfo().getCurrentLineNumber(),
                        "Module " + moduleName +  " has been already registered.");
			}
		} else {
			if(upperModule.GetConstance(moduleName) != null) {
                throw new IrisModuleExistsException(IrisDevUtil.GetCurrentThreadInfo()
                        .getCurrentFileName(),
                        IrisDevUtil.GetCurrentThreadInfo().getCurrentLineNumber(),
                        "Module " + moduleName +  " has been already registered.");
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
		
	public boolean Initialize() throws IrisExceptionBase {
		
		IrisThreadInfo.SetMainThreadID(Thread.currentThread().getId());
		IrisThreadInfo mainThreadInfo = new IrisThreadInfo();		
		IrisThreadInfo.SetMainThreedInfo(mainThreadInfo);

		RegistClass(new IrisClassBase());
		setClassClass(GetClass("Class"));

		RegistClass(new IrisModuleBase());
		setModuleClass(GetClass("Module"));
		
		RegistModule(new IrisKernel());	
		
		RegistClass(new IrisObjectBase());
		setObjectClass(GetClass("Object"));
		getObjectClass().AddInvolvedModule(IrisDevUtil.GetModule("Kernel"));

		getClassClass().setSuperClass(getObjectClass());
        getModuleClass().setSuperClass(getObjectClass());
		
		RegistClass(new IrisMethodBase());
		setMethodClass(GetClass("Method"));
		
		getClassClass().ResetAllMethodsObject();
		getObjectClass().ResetAllMethodsObject();
        getModuleClass().ResetAllMethodsObject();
		getMethodClass().ResetAllMethodsObject();
        IrisDevUtil.GetModule("Kernel").ResetAllMethodsObject();
		
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
		}
		catch (IllegalAccessException | IllegalArgumentException |
                NoSuchMethodException | SecurityException | InstantiationException e) {
            e.printStackTrace();
            return false;
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if(t instanceof IrisFatalException) {
                //System.out.(((IrisFatalException)t).GetReportString());
                //Logger logger = Logger.getLogger("Iris FatalException");
                org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("jiris");
                logger.error("\n" + ((IrisFatalException)t).GetReportString());
            }
            else if(t instanceof IrisRuntimeException) {
                IrisRuntimeException runtimeException = (IrisRuntimeException)t;
                IrisValue irregularObject = runtimeException.getExceptionObject();
                IrisValue stringResult;
                try {
                    stringResult = IrisDevUtil.CallMethod(irregularObject, "to_string", null, null,
                            IrisDevUtil.GetCurrentThreadInfo());
                }
                catch (IrisExceptionBase e2) {
                    e2.printStackTrace();
                    return false;
                }

                String outString = IrisDevUtil.GetString(stringResult);
                System.out.print(new IrisIrregularNotDealedException(IrisDevUtil.GetCurrentThreadInfo()
                        .getCurrentFileName(),
                        IrisDevUtil.GetCurrentThreadInfo().getCurrentLineNumber(), outString).GetReportString()
                );
            }
            else {
                e.printStackTrace();
            }
            return false;
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
