package org.irislang.jiris.irisclass;

import java.util.ArrayList;

import org.irislang.jiris.core.IrisClass;
import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisMethod;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;

public class IrisMethodBase extends IrisClassRoot {

	public static class IrisMethodBaseTag {
		
		private IrisMethod  m_methodObj = null;

		public String getMethodName() {
			return m_methodObj.getMethodName();
		}
		
		public IrisMethod getMethodObj() {
			return m_methodObj;
		}

		public void setMethodObj(IrisMethod methodObj) {
			m_methodObj = methodObj;
		}
	}
	
	public static IrisValue GetName(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisMethodBaseTag methodObj = IrisDevUtil.GetNativeObjectRef(self);
		return IrisDevUtil.CreateString(methodObj.getMethodName());
	}
	
	@Override
	public String NativeClassNameDefine() {
		return "Method";
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
		return new IrisMethodBaseTag();
	}

	@Override
	public void NativeClassDefine(IrisClass classObj) {
		
	}

}
