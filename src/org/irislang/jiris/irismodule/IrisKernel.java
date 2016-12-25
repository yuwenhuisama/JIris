package org.irislang.jiris.irismodule;

import java.util.ArrayList;

import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.core.IrisMethod.CallSide;
import org.irislang.jiris.core.IrisMethod.MethodAuthority;
import org.irislang.jiris.dev.IrisDevUtil;
import org.irislang.jiris.dev.IrisModuleRoot;
import org.irislang.jiris.irisclass.IrisString.IrisStringTag;
import org.irislang.jiris.irisclass.IrisUniqueString.IrisUniqueStringTag;

public class IrisKernel extends IrisModuleRoot {

	public static IrisValue Print(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) throws Throwable {
		IrisValue result = null;
		if(variableParameterList != null) {
			for(IrisValue value : variableParameterList) {
				if(IrisDevUtil.CheckClass(value, "String")) {
					System.out.print(((IrisStringTag)IrisDevUtil.GetNativeObjectRef(value)).getString());
				}
				else if(IrisDevUtil.CheckClass(value, "UniqueString")) {
					System.out.print(((IrisUniqueStringTag)IrisDevUtil.GetNativeObjectRef(value)).getString());
				} else {
					result = value.getObject().CallInstanceMethod("to_string", null, context, threadInfo, CallSide.Outeside);
					System.out.print(((IrisStringTag)IrisDevUtil.GetNativeObjectRef(result)).getString());
				}
			}
		}
		
		return IrisDevUtil.Nil();
	}
	
	@Override
	public String NativeModuleNameDefine() {
		return "Kernel";
	}

	@Override
	public IrisModule NativeUpperModuleDefine() {
		return null;
	}

	@Override
	public void NativeModuleDefine(IrisModule moduleObj) throws Throwable {
		
		moduleObj.AddClassMethod(IrisKernel.class, "Print", "print", 0, true, MethodAuthority.Everyone);
		
		moduleObj.AddInstanceMethod(IrisKernel.class, "Print", "print", 0, true, MethodAuthority.Everyone);
		
	}

}
