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

public class IrisClassBase extends IrisClassRoot {

	static public class IrisClassBaseTag {
		private IrisClass m_classObj = null;
				
		public String getClassName() {
			return m_classObj.getClassName();
		}
		
		public IrisClass getClassObj() {
			return m_classObj;
		}

		public void setClassObj(IrisClass classObj) {
			m_classObj = classObj;
		}
		
	}
	
	public static IrisValue New(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisClassBaseTag classObj = IrisDevUtil.GetNativeObjectRef(self);
		IrisValue result = IrisDevUtil.Nil();
		try {
			result = classObj.getClassObj().CreateNewInstance(variableParameterList, context, threadInfo);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	

	public static IrisValue GetClassName(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisClassBaseTag classObj = IrisDevUtil.GetNativeObjectRef(self);
		return IrisDevUtil.CreateString(classObj.getClassName());
	}

	
	@Override
	public String NativeClassNameDefine() {
		return "Class";
	}

	@Override
	public IrisClass NativeSuperClassDefine() {
		// Specially
		return null;
	}

	@Override
	public IrisModule NativeUpperModuleDefine() {
		return null;
	}

	@Override
	public Object NativeAlloc() {
		return new IrisClassBaseTag();
	}

	@Override
	public void NativeClassDefine(IrisClass classObj) throws Throwable {
		classObj.AddInstanceMethod(IrisClassBase.class, "New", "new", 0, true, MethodAuthority.Everyone);
	}

}
