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

public class IrisString extends IrisClassRoot {
	static public class IrisStringTag {
		private String m_string = "";
		
		public IrisStringTag(String string) {
			setString(string);
		}
		
		IrisStringTag Add(IrisStringTag tar) {
			return new IrisStringTag(m_string + tar.getString());
		}
		
		public String getString() {
			return m_string;
		}
		
		public void setString(String string) {
			m_string = string;
		}
	}

	public static IrisValue Add(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		
		if(!IrisDevUtil.CheckClass(parameterList.get(0), "String")) {
			/* Error */
			return IrisDevUtil.Nil();
		}
		
		IrisStringTag cself = (IrisStringTag)self.getObject().getNativeObject();
		IrisStringTag ctar = (IrisStringTag)parameterList.get(0).getObject().getNativeObject();
		IrisStringTag result = cself.Add(ctar);
		
		return IrisDevUtil.CreateString(result.getString());
	}
	
	public static IrisValue Equal(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		
		if(IrisDevUtil.CheckClass(parameterList.get(0), "String")) {
			return IrisDevUtil.False();
		}
		
		IrisStringTag selfStr = IrisDevUtil.GetNativeObjectRef(self);
		IrisStringTag rightStr = IrisDevUtil.GetNativeObjectRef(parameterList.get(0));
		return selfStr.getString().equals(rightStr.getString()) ? IrisDevUtil.True() : IrisDevUtil.False();
	}

	
	@Override
	public String NativeClassNameDefine() {
		return "String";
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
		return new IrisStringTag("");
	}

	@Override
	public void NativeClassDefine(IrisClass classObj) throws IrisExceptionBase {
		classObj.AddInstanceMethod(IrisInteger.class, "Add", "+", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "Equal", "==", 1, false, MethodAuthority.Everyone);
	}
}
