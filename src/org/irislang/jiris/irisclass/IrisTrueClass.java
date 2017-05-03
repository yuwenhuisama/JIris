package org.irislang.jiris.irisclass;

import java.util.ArrayList;

import org.irislang.jiris.core.IrisClass;
import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.core.IrisMethod.MethodAuthority;
import org.irislang.jiris.core.exceptions.IrisExceptionBase;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;

public class IrisTrueClass extends IrisClassRoot {

	public static class IrisTrueClassTag {
		private String m_name = "true";

		public boolean LogicNot() {
			return false;
		}
		
		public String getName() {
			return m_name;
		}
	}
	
	public static IrisValue GetName(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisTrueClassTag obj = IrisDevUtil.GetNativeObjectRef(self);
		return IrisDevUtil.CreateString(obj.getName());
	}
	
	public static IrisValue LogicNot(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return IrisDevUtil.False();
	}
	
	@Override
	public String NativeClassNameDefine() {
		return "TrueClass";
	}

	@Override
	public IrisClass NativeSuperClassDefine() {
		return null;
	}

	@Override
	public IrisModule NativeUpperModuleDefine() {
		return null;
	}

	@Override
	public Object NativeAlloc() {
		return new IrisTrueClassTag();
	}

	@Override
	public void NativeClassDefine(IrisClass classObj) throws IrisExceptionBase {
		classObj.AddInstanceMethod(IrisTrueClass.class, "LogicNot", "!", 0, false, MethodAuthority.Everyone);
	}

}
