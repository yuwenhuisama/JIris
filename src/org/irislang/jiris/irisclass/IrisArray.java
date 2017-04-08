package org.irislang.jiris.irisclass;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.irislang.jiris.core.IrisClass;
import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisMethod;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;

public class IrisArray extends IrisClassRoot {

	public static IrisValue Initialize(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		
		if(variableParameterList != null) {
			ArrayList<IrisValue> arrayList = IrisDevUtil.GetNativeObjectRef(self);
			arrayList.addAll(variableParameterList);
		}
		
		return self;
	}
	
	public static IrisValue At(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		
		IrisValue index = parameterList.get(0);
		ArrayList<IrisValue> arrayList = IrisDevUtil.GetNativeObjectRef(self);
		
		if(!IrisDevUtil.CheckClass(index, "Integer")) {
			/* Error */
			return IrisDevUtil.Nil();
		}
		
		int indexNum = IrisDevUtil.GetInt(index);
		
		if(indexNum < 0) {
			return arrayList.get(arrayList.size() - (-indexNum % arrayList.size()));
		} else {
			if(indexNum > arrayList.size()) {
				for(int i = 0; i < indexNum - arrayList.size(); ++i) {
					arrayList.add(IrisDevUtil.Nil());
				}
				
				return IrisDevUtil.Nil();
			}
			else {
				return arrayList.get(indexNum);
			}
		}
	}
	
	public static IrisValue Set(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisValue index = parameterList.get(0);
		IrisValue targetValue = parameterList.get(1);
		ArrayList<IrisValue> arrayList = IrisDevUtil.GetNativeObjectRef(self);
		if(!IrisDevUtil.CheckClass(index, "Integer")) {
			/* Error */
			return IrisDevUtil.Nil();
		}

		int indexNum = IrisDevUtil.GetInt(index);

		if(indexNum < 0) {
			arrayList.set(arrayList.size() - (-indexNum % arrayList.size()), targetValue);
		} else {
			if(indexNum >= arrayList.size()) {
				for(int i = 0; i < indexNum - arrayList.size(); ++i) {
					arrayList.add(IrisDevUtil.Nil());
				}
				arrayList.add(targetValue);
			}
			else {
				arrayList.set(indexNum, targetValue);
			}
		}
		return self;
	}

	public static IrisValue Push(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisValue targetValue = parameterList.get(0);
		ArrayList<IrisValue> arrayList = IrisDevUtil.GetNativeObjectRef(self);
		arrayList.add(targetValue);
		return self;
	}
	
	public static IrisValue Pop(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		ArrayList<IrisValue> arrayList = IrisDevUtil.GetNativeObjectRef(self);
		return arrayList.remove(arrayList.size() - 1);
	}

	public static IrisValue Size(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		ArrayList<IrisValue> arrayList = IrisDevUtil.GetNativeObjectRef(self);
		return IrisDevUtil.CreateInt(arrayList.size());
	}

	public static IrisValue GetIterator(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		ArrayList<IrisValue> params = new ArrayList<IrisValue>(1);
		params.add(0, self);
		// ** Error **
		try {
			return IrisDevUtil.CreateInstance(IrisDevUtil.GetClass("ArrayIterator"), params, context, threadInfo);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			return IrisDevUtil.Nil();
		}
	}
	
	@Override
	public String NativeClassNameDefine() {
		return "Array";
	}

	@Override
	public IrisClass NativeSuperClassDefine() {
		return IrisDevUtil.GetClass("Object");
	}

	@Override
	public IrisModule NativeUpperModuleDefine() {
		return null;
	}

	@Override
	public Object NativeAlloc() {
		return new ArrayList<IrisValue>();
	}

	@Override
	public void NativeClassDefine(IrisClass classObj) throws Throwable {
		classObj.AddInstanceMethod(IrisArray.class, "Initialize", "__format", 0, true, IrisMethod.MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisArray.class, "At", "[]", 1, false, IrisMethod.MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisArray.class, "Set", "[]=", 2, false, IrisMethod.MethodAuthority.Everyone);
		
		classObj.AddInstanceMethod(IrisArray.class, "Push", "push", 1, false, IrisMethod.MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisArray.class, "Pop", "pop", 0, false, IrisMethod.MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisArray.class, "GetIterator", "get_iterator", 0, false, IrisMethod
				.MethodAuthority.Everyone);
	}

}
