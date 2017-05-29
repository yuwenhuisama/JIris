package org.irislang.jiris.irisclass;

import java.util.ArrayList;

import org.irislang.jiris.core.IrisClass;
import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.core.IrisMethod.MethodAuthority;
import org.irislang.jiris.core.exceptions.IrisExceptionBase;
import org.irislang.jiris.core.exceptions.fatal.IrisFatalException;
import org.irislang.jiris.core.exceptions.fatal.IrisMethodDefinedException;
import org.irislang.jiris.core.exceptions.fatal.IrisMethodNotFoundException;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;


public class IrisObjectBase extends IrisClassRoot {

	public static IrisValue Initialize(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return self;
	}
	
	public static IrisValue GetObjectID(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return IrisDevUtil.CreateInt(self.getObject().getObjectID());
	}

	public static IrisValue ToString(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		StringBuffer buffer = new StringBuffer();
		String className = self.getObject().getObjectClass().getClassName();
		String objectID = String.valueOf(self.getObject().getObjectID());
		buffer.append("<").append(className).append(":").append(objectID).append(">");
		return IrisDevUtil.CreateString(buffer.toString());		
	}
	
	public static IrisValue GetClass(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return IrisValue.WrapObject(self.getObject().getObjectClass().getClassObject());
	}
	
	public static IrisValue Equal(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisValue rightValue = parameterList.get(0);
		return self.getObject().equals(rightValue.getObject()) ? IrisDevUtil.True() : IrisDevUtil.False();
	}

	public static IrisValue NotEqual(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisValue rightValue = parameterList.get(0);
		return self.getObject() != rightValue.getObject() ? IrisDevUtil.True() : IrisDevUtil.False();
	}
	
	public static IrisValue LogicOr(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisValue rightValue = parameterList.get(0);
		if(!self.equals(IrisDevUtil.False()) && !self.equals(IrisDevUtil.Nil())) {
			return IrisDevUtil.True();
		}
		else if(!rightValue.equals(IrisDevUtil.False()) && !rightValue.equals(IrisDevUtil.Nil())){
			return IrisDevUtil.True();
		}
		else {
			return IrisDevUtil.False();
		}
	}

	public static IrisValue LogicAnd(IrisValue self, ArrayList<IrisValue> parameterList,
			ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisValue rightValue = parameterList.get(0);
		if(self.equals(IrisDevUtil.False()) || self.equals(IrisDevUtil.Nil())) {
			return IrisDevUtil.False();
		}
		else if(rightValue.equals(IrisDevUtil.False()) || rightValue.equals(IrisDevUtil.Nil())) {
			return IrisDevUtil.False();
		}
		else {
			return IrisDevUtil.True();
		}
	}

	public static IrisValue MissingMethod(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue>
            variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) throws IrisExceptionBase {
	    String methodName = IrisDevUtil.GetString(parameterList.get(0));
	    String className = IrisDevUtil.GetString(parameterList.get(1));
	    StringBuilder result = new StringBuilder();
	    result.append("Method of ").append(methodName).append(" not found in class ").append(className).append(".");
	    throw new IrisMethodNotFoundException(threadInfo.getCurrentFileName(), threadInfo.getCurrentLineNumber(),
                result.toString());
	}
	
	@Override
	public String NativeClassNameDefine() {
		return "Object";
	}

	@Override
	public IrisClass NativeSuperClassDefine() {
		// special
		return null;
	}

	@Override
	public IrisModule NativeUpperModuleDefine() {
		return null;
	}

	@Override
	public Object NativeAlloc() {
		// special
		return null;
	}

	@Override
	public void NativeClassDefine(IrisClass classObj) throws IrisExceptionBase {
		
		classObj.AddInvolvedModule(IrisDevUtil.GetModule("Kernel"));
		
		classObj.AddInstanceMethod(IrisObjectBase.class, "Initialize", "__format", 0, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisObjectBase.class, "ToString", "to_string", 0, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisObjectBase.class, "Equal", "==", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisObjectBase.class, "NotEqual", "!=", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisObjectBase.class, "LogicAnd", "&&", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisObjectBase.class, "LogicOr", "||", 1, false, MethodAuthority.Everyone);
        classObj.AddInstanceMethod(IrisObjectBase.class, "MissingMethod", "missing_method", 2, false,
                MethodAuthority.Everyone);
	}

}
