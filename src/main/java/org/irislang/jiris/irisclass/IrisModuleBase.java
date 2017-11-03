package org.irislang.jiris.irisclass;

import java.util.ArrayList;

import org.irislang.jiris.core.IrisClass;
import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.core.exceptions.IrisExceptionBase;
import org.irislang.jiris.dev.IrisDevUtil;
import org.irislang.jiris.dev.IrisClassRoot;

public class IrisModuleBase extends IrisClassRoot {

	public static class IrisModuleBaseTag {
		IrisModule m_module = null;
		
		public IrisModuleBaseTag(IrisModule module) {
			m_module = module;
		}
		
		public IrisModule getModule() {
			return m_module;
		}
		
		public void setModule(IrisModule module) {
			m_module = module;
		}
		
		public String getModuleName() {
			return m_module.getModuleName();
		}
	}
	
	public static IrisValue GetModuleName(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return IrisDevUtil.CreateString(((IrisModuleBaseTag)IrisDevUtil.GetNativeObjectRef(self)).getModuleName());
	}

	@Override
	public String NativeClassNameDefine() {
		return "Module";
	}

	@Override
	public IrisClass NativeSuperClassDefine() {
		return IrisDevUtil.GetClass("Object");
	}

	@Override
	public Object NativeAlloc() {
		return new IrisModuleBaseTag(null);
	}

	@Override
	public void NativeClassDefine(IrisClass classObj) throws IrisExceptionBase {
		
	}

	@Override
	public IrisModule NativeUpperModuleDefine() {
		return null;
	}

}
