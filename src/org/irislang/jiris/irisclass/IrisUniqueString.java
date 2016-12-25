package org.irislang.jiris.irisclass;

import java.util.ArrayList;
import java.util.HashMap;

import org.irislang.jiris.core.IrisClass;
import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.core.IrisMethod.MethodAuthority;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;

public class IrisUniqueString extends IrisClassRoot {
	
	static final HashMap<String, IrisValue> sm_uniqueStringCache = new HashMap<String, IrisValue>();
	
	public static IrisValue GetUniqueString(String uniqueString) {
		return sm_uniqueStringCache.get(uniqueString);
	}
	
	public static void AddUniqueString(String uniqueString, IrisValue uniqueObj) {
		sm_uniqueStringCache.put(uniqueString, uniqueObj);
	}
	
	public static class IrisUniqueStringTag {
		
		private String m_string = "";
		
		public String getString() {
			return m_string;
		}
				
		public IrisUniqueStringTag(String string) {
			m_string = string;
		}
	}
	
	public static IrisValue ToString(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisUniqueStringTag obj = IrisDevUtil.GetNativeObjectRef(self);
		return IrisDevUtil.CreateString(obj.getString());
	}
	
	@Override
	public String NativeClassNameDefine() {
		return "UniqueString";
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
		return new IrisUniqueStringTag("");
	}

	@Override
	public void NativeClassDefine(IrisClass classObj) throws Throwable {
		classObj.AddInstanceMethod(IrisUniqueString.class, "ToString", "to_string", 0, false, MethodAuthority.Everyone);
	}
}
