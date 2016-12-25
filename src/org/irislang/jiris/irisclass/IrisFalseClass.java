package org.irislang.jiris.irisclass;

import java.util.ArrayList;

import org.irislang.jiris.core.IrisClass;
import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.core.IrisMethod.MethodAuthority;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;

public class IrisFalseClass extends IrisClassRoot {

	public static class IrisFalseClassTag {
		private String m_name = "false";

		public boolean LogicNot() {
			return true;
		}
		
		public String getName() {
			return m_name;
		}
	}
	
	public static IrisValue LogicNot(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return IrisDevUtil.True();
	}
	
	public static IrisValue GetName(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisFalseClassTag obj = IrisDevUtil.GetNativeObjectRef(self);
		return IrisDevUtil.CreateString(obj.getName());
	}

	
	@Override
	public String NativeClassNameDefine() {
		return "FalseClass";
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
		return new IrisFalseClassTag();
	}

	@Override
	public void NativeClassDefine(IrisClass classObj) throws Throwable {
		classObj.AddInstanceMethod(IrisFalseClass.class, "LogicNot", "!", 0, false, MethodAuthority.Everyone);
	}

}
