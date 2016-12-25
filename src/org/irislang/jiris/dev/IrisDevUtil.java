package org.irislang.jiris.dev;

import static java.lang.invoke.MethodHandles.lookup;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.irislang.jiris.compiler.IrisInterpreter;
import org.irislang.jiris.core.IrisClass;
import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisInterface;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.IrisObject;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.irisclass.IrisUniqueString;
import org.irislang.jiris.irisclass.IrisFloat.IrisFloatTag;
import org.irislang.jiris.irisclass.IrisInteger.IrisIntegerTag;
import org.irislang.jiris.irisclass.IrisString.IrisStringTag;
import org.irislang.jiris.irisclass.IrisUniqueString.IrisUniqueStringTag;

import net.bytebuddy.jar.asm.Type;

final public class IrisDevUtil {
		
	public static IrisValue Nil() {
		return IrisInterpreter.INSTANCE.Nil();
	}
	
	public static IrisValue True() {
		return IrisInterpreter.INSTANCE.True();
	}
	
	public static IrisValue False() {
		return IrisInterpreter.INSTANCE.False();
	}
	
	public static IrisValue CreateInt(int integer) {
		IrisClass intClass = GetClass("Integer");
		IrisObject intObject = new IrisObject();
		
		intObject.setObjectClass(intClass);
		IrisIntegerTag integerTag = new IrisIntegerTag(integer);
		intObject.setNativeObject(integerTag);
		
		return IrisValue.WrapObject(intObject);
	}
	
	public static IrisValue CreateFloat(double dfloat) {
		IrisClass floatClass = GetClass("Float");
		IrisObject floatObject = new IrisObject();
		
		floatObject.setObjectClass(floatClass);
		IrisFloatTag floatTag = new IrisFloatTag(dfloat);
		floatObject.setNativeObject(floatTag);
		
		return IrisValue.WrapObject(floatObject);
	}	
	
	public static IrisValue CreateString(String str) {
		IrisClass stringClass = GetClass("String");
		IrisObject stringObject = new IrisObject();
		
		stringObject.setObjectClass(stringClass);
		IrisStringTag stringTag = new IrisStringTag(str);
		stringObject.setNativeObject(stringTag);
		
		return IrisValue.WrapObject(stringObject);
	}
	
	public static IrisValue CreateUniqueString(String ustr) {
		
		IrisValue obj = IrisUniqueString.GetUniqueString(ustr);
		if(obj != null) {
			return obj;
		}
		
		IrisClass ustringClass = GetClass("UniqueString");
		IrisObject ustringObject = new IrisObject();

		ustringObject.setObjectClass(ustringClass);
		IrisUniqueStringTag ustringTag = new IrisUniqueStringTag(ustr);
		ustringObject.setNativeObject(ustringTag);
		
		obj = IrisValue.WrapObject(ustringObject);
		
		IrisUniqueString.AddUniqueString(ustr, obj);
		
		return obj;
	}
	
	public static IrisValue CreateArray(ArrayList<IrisValue> elements) {
		IrisClass arrayClass = GetClass("Array");
		IrisObject arrayObject = new IrisObject();
		arrayObject.setObjectClass(arrayClass);
		
		ArrayList<IrisValue> values = null;
		if(elements != null) {
			values = new ArrayList<IrisValue>(elements);
		} else {
			values = new ArrayList<IrisValue>();
		}
		
		arrayObject.setNativeObject(values);
		return IrisValue.WrapObject(arrayObject);
	}
	
	public static int GetInt(IrisValue intVar) {
		IrisIntegerTag integerTag = GetNativeObjectRef(intVar);
		return integerTag.getInteger();
	}
	
	public static double GetFloat(IrisValue floatVar) {
		IrisFloatTag floatTag = GetNativeObjectRef(floatVar);
		return floatTag.getFloat();
	}
	
	public static boolean IsClassObject(IrisValue value) {
		return value.getObject().getObjectClass().getClassName().equals("Class");
	}
	
	public static boolean IsModuleObject(IrisValue value) {
		return value.getObject().getObjectClass().getClassName().equals("Module");
	}

	public static boolean IsInterfaceObject(IrisValue value) {
		return value.getObject().getObjectClass().getClassName().equals("Interface");
	}
	
	public static boolean CheckClass(IrisValue obj,  String className) {
		return obj.getObject().getObjectClass().getClassName() == className;
	}
	
	public static IrisClass GetClass(String classPath) {
		return IrisInterpreter.INSTANCE.GetClass(classPath);
	}
	
	public static IrisModule GetModule(String modulePath) {
		return IrisInterpreter.INSTANCE.GetModule(modulePath);
	}
	
	public static IrisInterface GetInterface(String interfacePath) {
		return IrisInterpreter.INSTANCE.GetInterface(interfacePath);
	}
	
	public static boolean NotFalseOrNil(IrisValue value) {
		return value != IrisDevUtil.Nil() && value != IrisDevUtil.False();
	}
	
	@SuppressWarnings("unchecked")
	public static<T> T GetNativeObjectRef(IrisValue value) {
		return (T)value.getObject().getNativeObject();
	}
	
	public static MethodHandle GetIrisNativeMethodHandle(Class<?> classObj, String methodName) {
		MethodHandle methodHandle = null;
		try {
			methodHandle = INDY_BootstrapMethod(classObj, methodName, IrisValue.class, ArrayList.class, ArrayList.class, IrisContextEnvironment.class, IrisThreadInfo.class);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return methodHandle;
	}
	
	public static MethodHandle GetIrisNativeUserMethodHandle(Class<?> classObj, String methodName) {
		MethodHandle methodHandle = null;
		try {
			methodHandle = INDY_BootstrapMethod(classObj, methodName, IrisContextEnvironment.class, IrisThreadInfo.class);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return methodHandle;
	}
	
	public static IrisThreadInfo GetCurrentThreadInfo() {
		return IrisThreadInfo.GetCurrentThreadInfo();
	}
	
    private static MethodType MT_BootstrapMethod(Class<?> classObj) throws NoSuchMethodException, SecurityException {
    	Method method = classObj.getMethod("BootstrapMethod", classObj.getClass(), MethodHandles.lookup().getClass(), String.class, MethodType.class);
    	String desp = Type.getMethodDescriptor(method);
        return MethodType.fromMethodDescriptorString(desp, null);
    }
    private static MethodHandle MH_BootstrapMethod(Class<?> classObj) throws Throwable {
        return lookup().findStatic(classObj, "BootstrapMethod", MT_BootstrapMethod(classObj));
    }

    private static MethodHandle INDY_BootstrapMethod(Class<?> classObj, String methodName, Class<?>... args) throws Throwable {
    	Method method = classObj.getMethod(methodName, args);
    	if(method == null) {
    		return null;
    	}
    	String decp = Type.getMethodDescriptor(method);
        CallSite cs = (CallSite) MH_BootstrapMethod(classObj).invokeWithArguments(classObj, lookup(), methodName, MethodType.fromMethodDescriptorString(decp, null));
        return cs.dynamicInvoker();
    }
}
