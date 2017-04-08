package org.irislang.jiris.compiler;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Arrays;

import org.irislang.jiris.core.IrisClass;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.IrisObject;
import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisMethod;
import org.irislang.jiris.core.IrisMethod.IrisUserMethod;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.core.IrisContextEnvironment.RunTimeType;
import org.irislang.jiris.dev.IrisDevUtil;
import org.irislang.jiris.irisclass.IrisInteger.IrisIntegerTag;

public abstract class IrisNativeJavaClass {
	
	public static CallSite BootstrapMethod(Class<?> classObj, MethodHandles.Lookup lookup, String name, MethodType mt) throws Throwable {
        return new ConstantCallSite(lookup.findStatic(classObj, name, mt));
    }
	
	protected static IrisValue CallMethod(IrisValue object, String methodName, IrisThreadInfo threadInfo, IrisContextEnvironment context, int parameterCount) throws Throwable {
		IrisValue result = null;
		// hide call
		if(object == null) {
			// main
			if(context.getRunTimeType() != RunTimeType.RunTime) {
				IrisMethod method = IrisInterpreter.INSTANCE.GetMainMethod(methodName);
				if(method == null) {
  					result = IrisDevUtil.GetModule("Kernel").getModuleObject().CallInstanceMethod(methodName, threadInfo.getPartPrameterListOf(parameterCount), context, threadInfo, IrisMethod.CallSide.Outeside);
				} else {
					result = method.CallMain(threadInfo.getPartPrameterListOf(parameterCount), context, threadInfo);
				}
			} else {
				if(context.getRunningType() != null) {
					result = ((IrisObject)context.getRunningType()).CallInstanceMethod(methodName, threadInfo.getPartPrameterListOf(parameterCount), context, threadInfo, IrisMethod.CallSide.Inside);
				} else {
					IrisMethod method = IrisInterpreter.INSTANCE.GetMainMethod(methodName);
					if(method == null) {
						result = IrisDevUtil.GetModule("Kernel").getModuleObject().CallInstanceMethod(methodName, threadInfo.getPartPrameterListOf(parameterCount), context, threadInfo, IrisMethod.CallSide.Outeside);
					} else {
						result = method.CallMain(threadInfo.getPartPrameterListOf(parameterCount), context, threadInfo);
					}
				}
			} 
		} else {
			// normal call
			result = object.getObject().CallInstanceMethod(methodName, threadInfo.getPartPrameterListOf(parameterCount), context, threadInfo, IrisMethod.CallSide.Outeside);
		}
		return result; 
	}
	
	protected static IrisValue GetLocalVariable(String variableName, IrisThreadInfo threadInfo, IrisContextEnvironment context) {
		IrisValue value = context.GetLocalVariable(variableName);
		if(value == null) {
			value = IrisValue.CloneValue(IrisDevUtil.Nil());
			context.AddLocalVariable(variableName, value);
		}
		else {
			value = IrisValue.CloneValue(value);
		}
		return IrisValue.CloneValue(value);
	}
	
	protected static IrisValue SetLocalVariable(String variableName, IrisValue value, IrisThreadInfo threadInfo, IrisContextEnvironment context) {
		IrisValue testValue = context.GetLocalVariable(variableName);
		if(testValue == null) {
			context.AddLocalVariable(variableName, IrisValue.CloneValue(value));
		} else {
			testValue.setObject(value.getObject());
		}
		return value;
	}
	
	protected static IrisValue GetClassVariable(String variableName, IrisThreadInfo threadInfo, IrisContextEnvironment context) {
		IrisValue value = null;
		// Main context
		if(context.getRunningType() == null) {
			value = context.GetLocalVariable(variableName);
			if(value == null) {
				value = IrisValue.CloneValue(IrisDevUtil.Nil());
				context.AddLocalVariable(variableName, value);
			}
			else {
				value = IrisValue.CloneValue(value);
			}
		}
		// Class Type
		else {
			switch(context.getRunTimeType()) {
			case ClassDefineTime :
				value = ((IrisClass)context.getRunningType()).SearchClassVariable(variableName);
				if(value == null) {
					value = IrisValue.CloneValue(IrisDevUtil.Nil());
					((IrisClass)context.getRunningType()).AddClassVariable(variableName, value);
				}
				else {
					value = IrisValue.CloneValue(value);
				}
				break;
			case ModuleDefineTime :
				value = ((IrisModule)context.getRunningType()).SearchClassVariable(variableName);
				if(value == null) {
					value = IrisValue.CloneValue(IrisDevUtil.Nil());
					((IrisModule)context.getRunningType()).AddClassVariable(variableName, value);
				}
				else {
					value = IrisValue.CloneValue(value);
				}
				break;
			case InterfaceDefineTime :
				/* Error */
				return null;
			case RunTime :
				value = ((IrisClass)context.getRunningType()).SearchClassVariable(variableName);
				if(value == null) {
					value = IrisValue.CloneValue(IrisDevUtil.Nil());
					((IrisClass)context.getRunningType()).AddClassVariable(variableName, value);
				}
				else {
					value = IrisValue.CloneValue(value);
				}
				break;
			}
		}
		return value;
	}
	
	protected static IrisValue SetClassVariable(String variableName, IrisValue value, IrisThreadInfo threadInfo, IrisContextEnvironment context) {
		IrisValue testValue = null;
		// Main context
		if(context.getRunningType() == null) {
			testValue = context.GetLocalVariable(variableName);
			if(testValue == null) {
				context.AddLocalVariable(variableName, IrisValue.CloneValue(value));
			} else {
				testValue.setObject(value.getObject());
			}
		}
		else {
			switch(context.getRunTimeType()) {
			case ClassDefineTime :
				testValue = ((IrisClass)context.getRunningType()).SearchClassVariable(variableName);
				if(testValue == null) {
					((IrisClass)context.getRunningType()).AddClassVariable(variableName, IrisValue.CloneValue(value));
				} else {
					testValue.setObject(value.getObject());
				}
				break;
			case ModuleDefineTime :
				testValue = ((IrisModule)context.getRunningType()).SearchClassVariable(variableName);
				if(testValue == null) {
					((IrisModule)context.getRunningType()).AddClassVariable(variableName, IrisValue.CloneValue(value));
				} else {
					testValue.setObject(value.getObject());
				}
				break;
			case InterfaceDefineTime :
				/* Error */
				return null;
			case RunTime :
				testValue = ((IrisClass)context.getRunningType()).SearchClassVariable(variableName);
				if(testValue == null) {
					((IrisClass)context.getRunningType()).AddClassVariable(variableName, IrisValue.CloneValue(value));
				} else {
					testValue.setObject(value.getObject());
				}
				break;
			}
		}
		
		return value;
	}
	
	protected static IrisValue GetConstance(String variableName, IrisThreadInfo threadInfo, IrisContextEnvironment context) {
		IrisValue value = null;
		if(context.getRunningType() == null) {
			value = IrisInterpreter.INSTANCE.GetConstance(variableName);
			if(value == null) {
				value = IrisValue.CloneValue(IrisDevUtil.Nil());
				IrisInterpreter.INSTANCE.AddConstance(variableName, value);
			}
			else {
				value = IrisValue.CloneValue(value);
			}
		}
		else {
			switch(context.getRunTimeType()) {
				case ClassDefineTime :
					value = ((IrisClass)context.getRunningType()).SearchConstance(variableName);
					if(value == null) {
						value = IrisValue.CloneValue(IrisDevUtil.Nil());
						((IrisClass)context.getRunningType()).AddConstance(variableName, value);
					}
					else {
						value = IrisValue.CloneValue(value);
					}
					break;
				case ModuleDefineTime :
					value = ((IrisModule)context.getRunningType()).SearchConstance(variableName);
					if(value == null) {
						value = IrisValue.CloneValue(IrisDevUtil.Nil());
						((IrisModule)context.getRunningType()).AddConstance(variableName, value);
					}
					else {
						value = IrisValue.CloneValue(value);
					}
					break;
				case InterfaceDefineTime :
				/* Error */
					return null;
				case RunTime :
					value = ((IrisObject)context.getRunningType()).getObjectClass().SearchConstance(variableName);
					if(value == null) {
						value = IrisValue.CloneValue(IrisDevUtil.Nil());
						((IrisObject)context.getRunningType()).getObjectClass().AddConstance(variableName, value);
					}
					else {
						value = IrisValue.CloneValue(value);
					}
					break;
			}
		}
		return value;
	}
	
	protected static IrisValue SetConstance(String variableName, IrisValue value, IrisThreadInfo threadInfo, IrisContextEnvironment context) {
		IrisValue testValue = null;
		if(context.getRunningType() == null) {
			testValue = IrisInterpreter.INSTANCE.GetConstance(variableName);
			if(testValue == null) {
				IrisInterpreter.INSTANCE.AddConstance(variableName, IrisValue.CloneValue(value));
			} else {
				/* Error */
				return null;
			}
		}
		else {
			switch(context.getRunTimeType()) {
			case ClassDefineTime :
				testValue = ((IrisClass)context.getRunningType()).SearchConstance(variableName);
				if(testValue == null) {
					((IrisClass)context.getRunningType()).AddConstance(variableName, IrisValue.CloneValue(value));
				} else {
					/* Error */
					return null;
				}
				break;
			case ModuleDefineTime :
				testValue = ((IrisModule)context.getRunningType()).SearchConstance(variableName);
				if(testValue == null) {
					((IrisModule)context.getRunningType()).AddConstance(variableName, IrisValue.CloneValue(value));
				} else {
					/* Error */
					return null;
				}
				break;
			case InterfaceDefineTime :
				/* Error */
				return null;
			case RunTime :
				testValue = ((IrisObject)context.getRunningType()).getObjectClass().SearchConstance(variableName);
				if(testValue == null) {
					((IrisObject)context.getRunningType()).getObjectClass().AddConstance(variableName, IrisValue.CloneValue(value));
				} else {
					/* Error */
					return null;
				}
				break;
			}
		}
		return value;
	}
	
	protected static IrisValue GetGlobalVariable(String variableName, IrisThreadInfo threadInfo, IrisContextEnvironment context) {
		IrisValue value = null;
		value = IrisInterpreter.INSTANCE.GetGlobalValue(variableName);
		if(value == null) {
			value = IrisValue.CloneValue(IrisDevUtil.Nil());
			IrisInterpreter.INSTANCE.AddGlobalValue(variableName, value);
		}
		return value;
	}
	
	protected static IrisValue SetGlobalVariable(String variableName, IrisValue value, IrisThreadInfo threadInfo, IrisContextEnvironment context) {
		IrisValue testValue = IrisInterpreter.INSTANCE.GetGlobalValue(variableName);
		if(testValue == null) {
			IrisInterpreter.INSTANCE.AddGlobalValue(variableName, IrisValue.CloneValue(value));
		} else {
			testValue.setObject(value.getObject());
		}
		return value;
	}
	
	protected static IrisValue GetInstanceVariable(String variableName, IrisThreadInfo threadInfo, IrisContextEnvironment context) {
		IrisValue value = null;
		IrisObject obj = (IrisObject) context.getRunningType();
		
		if(obj != null) {
			value = obj.GetInstanceVariable(variableName);
			if(value == null) {
				value = IrisValue.CloneValue(IrisDevUtil.Nil());
				obj.AddInstanceVariable(variableName, value);
			}
			else {
				value = IrisValue.CloneValue(value);
			}
		} else {
			value = context.GetLocalVariable(variableName);
			if(value == null) {
				value = IrisValue.CloneValue(IrisDevUtil.Nil());
				context.AddLocalVariable(variableName, IrisValue.CloneValue(value));
			}
			else {
				value = IrisValue.CloneValue(value);
			}
		}
		
		return value;
	}
	
	protected static IrisValue SetInstanceVariable(String variableName, IrisValue value, IrisThreadInfo threadInfo, IrisContextEnvironment context) {
		
		IrisValue testValue = null;
		IrisObject obj = (IrisObject) context.getRunningType();
		
		if(obj != null) {
			testValue = obj.GetInstanceVariable(variableName);
			if(testValue == null) {
				obj.AddInstanceVariable(variableName, IrisValue.CloneValue(value));
			} else {
				testValue.setObject(value.getObject());
			}
		} else {
			testValue = context.GetLocalVariable(variableName);
			if(testValue == null) {
				context.AddLocalVariable(variableName, IrisValue.CloneValue(value));
			} else {
				testValue.setObject(value.getObject());
			}
		}
		
		return value;
	}
	
	protected static boolean CompareCounterLess(IrisValue org, IrisValue tar, IrisThreadInfo threadInfo, IrisContextEnvironment context) throws Throwable {
//		int b = IrisDevUtil.GetInt(target);
//		return a < b;
		//int a = IrisDevUtil.GetInt(org);
		threadInfo.AddParameter(tar);
		IrisValue result = CallMethod(org, "<", threadInfo, context, 1);
		threadInfo.PopParameter(1);
		return result == IrisDevUtil.True();
	}
	
	protected static void DefineInstanceMethod(Class<?> nativeClass, 
			String nativeName, 
			String methodName, 
			String[] parameters, 
			String variableParameter, 
			String withBlockName, 
			String withoutBlockName,
			IrisMethod.MethodAuthority authority,
			IrisContextEnvironment context,
			IrisThreadInfo threadInfo) throws Throwable {
		
		IrisUserMethod userMethod = new IrisUserMethod();
		if(parameters != null) {
			userMethod.setParameterList(new ArrayList<String>(Arrays.asList(parameters)));	
		} else {
			userMethod.setParameterList(null);
		}
		
		if(variableParameter != null) {
			userMethod.setVariableParameterName(variableParameter);
		}
		
		if(context.getRunTimeType() == IrisContextEnvironment.RunTimeType.RunTime) {
			IrisInterpreter.INSTANCE.AddMainMethod(
					new IrisMethod(
							methodName, 
							userMethod, 
							authority,
							IrisDevUtil.GetIrisNativeUserMethodHandle(nativeClass, nativeName)));
		} else {
			;
		}
	}
}
