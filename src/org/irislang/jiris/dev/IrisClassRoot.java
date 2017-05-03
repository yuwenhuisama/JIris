package org.irislang.jiris.dev;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.irislang.jiris.core.IrisClass;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.exceptions.IrisExceptionBase;

public abstract class IrisClassRoot {

	public static CallSite BootstrapMethod(Class<?> classObj, MethodHandles.Lookup lookup, String name, MethodType mt) throws Throwable {
        return new ConstantCallSite(lookup.findStatic(classObj, name, mt));
    }
	
	abstract public String NativeClassNameDefine();
	abstract public IrisClass NativeSuperClassDefine();
	abstract public IrisModule NativeUpperModuleDefine();
	abstract public Object NativeAlloc();
	abstract public void NativeClassDefine(IrisClass classObj) throws IrisExceptionBase;
}