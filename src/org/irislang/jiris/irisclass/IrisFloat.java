package org.irislang.jiris.irisclass;

import java.util.ArrayList;

import org.irislang.jiris.core.IrisClass;
import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisMethod.MethodAuthority;
import org.irislang.jiris.core.IrisModule;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;
import org.irislang.jiris.irisclass.IrisInteger.IrisIntegerTag;

public class IrisFloat extends IrisClassRoot {
	
	enum Operation {
		Add,
		Sub,
		Mul,
		Div,
		Power,

		Equal,
		NotEqual,
		BigThan,
		BigThanOrEqual,
		LessThan,
		LessThanOrEqual,
	};
	
	static public class IrisFloatTag {
		private double m_float = 0;
		
		public IrisIntegerTag toInteger() {
			return new IrisIntegerTag((int)m_float);
		}
		
		public String toString() {
			return String.valueOf(m_float);
		}
		
		public IrisFloatTag(double dfloat) {
			setFloat(dfloat);
		}
		
		public IrisFloatTag Add(IrisFloatTag tar) {
			return new IrisFloatTag(m_float + tar.getFloat());
		}
		
		public IrisFloatTag Sub(IrisFloatTag tar) {
			return new IrisFloatTag(m_float - tar.getFloat());
		}

		public IrisFloatTag Mul(IrisFloatTag tar) {
			return new IrisFloatTag(m_float * tar.getFloat());
		}
		
		public IrisFloatTag Div(IrisFloatTag tar) {
			return new IrisFloatTag(m_float / tar.getFloat());
		}

		public IrisFloatTag Mod(IrisFloatTag tar) {
			return new IrisFloatTag(m_float % tar.getFloat());
		}
		
		public IrisFloatTag Power(IrisFloatTag tar) {
			return new IrisFloatTag(Math.pow(m_float, tar.getFloat()));
		}
								
		public boolean Equal(IrisFloatTag tar) {
			return m_float == tar.getFloat();
		}
		
		public boolean NotEqual(IrisFloatTag tar) {
			return !Equal(tar);
		}
		
		public boolean BigThan(IrisFloatTag tar) {
			return m_float > tar.getFloat();
		}
		
		public boolean BigThanOrEqual(IrisFloatTag tar) {
			return m_float >= tar.getFloat();
		}
		
		public boolean LessThan(IrisFloatTag tar) {
			return m_float < tar.getFloat();
		}
		
		public boolean LessThanOrEqual(IrisFloatTag tar) {
			return m_float <= tar.getFloat();
		}
		
		public IrisFloatTag Plus() {
			return new IrisFloatTag(m_float);
		}
		
		public IrisFloatTag Minus() {
			return new IrisFloatTag(-m_float);
		}
		
		public double getFloat() {
			return m_float;
		}

		public void setFloat(double f) {
			m_float = f;
		}			
	}
	
	private static IrisValue CastOperation(Operation type, IrisValue leftValue, IrisValue rightValue) {
		IrisValue result = null;
		boolean needCast = IrisDevUtil.CheckClass(rightValue, "Integer");
		if(!needCast && !IrisDevUtil.CheckClass(rightValue, "Float")) {
			/* Error */
			return IrisDevUtil.Nil();
		}
		IrisFloatTag orgLeftValue = IrisDevUtil.GetNativeObjectRef(leftValue);
		IrisFloatTag resultValue = null;
		IrisFloatTag finallyRightValue = null;
		
		if(needCast) {
			finallyRightValue = ((IrisIntegerTag)IrisDevUtil.GetNativeObjectRef(rightValue)).toFloat();
		} else {
			finallyRightValue = IrisDevUtil.GetNativeObjectRef(rightValue);
		}
		
		switch(type) {
		case Add:
			resultValue = orgLeftValue.Add(finallyRightValue);
			break;
		case Sub:
			resultValue = orgLeftValue.Sub(finallyRightValue);
			break;
		case Mul:
			resultValue = orgLeftValue.Mul(finallyRightValue);
			break;
		case Div:
			resultValue = orgLeftValue.Div(finallyRightValue);
			break;
		case Power:
			resultValue = orgLeftValue.Power(finallyRightValue);
			break;
		default:
			break;
		}
		result = IrisDevUtil.CreateFloat(0.0);
		result.getObject().setNativeObject(resultValue);
		return result;
	}
	
	private static IrisValue CmpOperation(Operation type, IrisValue leftValue, IrisValue rightValue) {
		boolean cmpResult = false;
		boolean needCast = IrisDevUtil.CheckClass(rightValue, "Integer");
		if(!needCast && !IrisDevUtil.CheckClass(rightValue, "Float")) {
			/* Error */
			return IrisDevUtil.Nil();
		}
		
		IrisFloatTag orgLeftValue =  IrisDevUtil.GetNativeObjectRef(leftValue);
		IrisFloatTag finallyRightValue =  null;
		
		if(needCast) {
			finallyRightValue = ((IrisIntegerTag)IrisDevUtil.GetNativeObjectRef(rightValue)).toFloat();
		} else {
			finallyRightValue = IrisDevUtil.GetNativeObjectRef(rightValue);
		}
		
		switch(type) {
		case Equal:
			cmpResult = orgLeftValue.Equal(finallyRightValue);
			break;
		case NotEqual:
			cmpResult = orgLeftValue.NotEqual(finallyRightValue);
			break;
		case BigThan:
			cmpResult = orgLeftValue.BigThan(finallyRightValue);
			break;
		case BigThanOrEqual:
			cmpResult = orgLeftValue.BigThanOrEqual(finallyRightValue);
			break;
		case LessThan:
			cmpResult = orgLeftValue.LessThan(finallyRightValue);
			break;
		case LessThanOrEqual:
			cmpResult = orgLeftValue.LessThanOrEqual(finallyRightValue);
			break;
		default:
			break;
		}

		return cmpResult ? IrisDevUtil.True() : IrisDevUtil.False();
	}
			
	public static IrisValue Add(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return CastOperation(Operation.Add, self, parameterList.get(0));
	}
	
	public static IrisValue Sub(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return CastOperation(Operation.Sub, self, parameterList.get(0));
	}
	
	public static IrisValue Mul(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return CastOperation(Operation.Mul, self, parameterList.get(0));
	}

	public static IrisValue Div(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return CastOperation(Operation.Div, self, parameterList.get(0));
	}
	
	public static IrisValue Power(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return CastOperation(Operation.Power, self, parameterList.get(0));
	}

	public static IrisValue Equal(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return CmpOperation(Operation.Equal, self, parameterList.get(0));
	}
	
	public static IrisValue NotEqual(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return CmpOperation(Operation.NotEqual, self, parameterList.get(0));
	}
	
	public static IrisValue BigThan(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return CmpOperation(Operation.BigThan, self, parameterList.get(0));
	}
	
	public static IrisValue BigThanOrEqual(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return CmpOperation(Operation.BigThanOrEqual, self, parameterList.get(0));
	}
	
	public static IrisValue LessThan(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return CmpOperation(Operation.LessThan, self, parameterList.get(0));
	}
	
	public static IrisValue LessThanOrEqual(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return CmpOperation(Operation.LessThanOrEqual, self, parameterList.get(0));
	}
		
	public static IrisValue Plus(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisFloatTag selfValue = IrisDevUtil.GetNativeObjectRef(self);
		IrisValue result = IrisDevUtil.CreateFloat(0.0);
		result.getObject().setNativeObject(selfValue.Plus());
		return result;
	}

	public static IrisValue Minus(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisFloatTag selfValue = IrisDevUtil.GetNativeObjectRef(self);
		IrisValue result = IrisDevUtil.CreateFloat(0.0);
		result.getObject().setNativeObject(selfValue.Minus());
		return result;
	}
	
	public static IrisValue ToInteger(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisValue result = IrisDevUtil.CreateInt((int)IrisDevUtil.GetFloat(self));
		return result;
	}
	
	public static IrisValue ToString(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisValue result = IrisDevUtil.CreateString(((IrisFloatTag)IrisDevUtil.GetNativeObjectRef(self)).toString());
		return result;
	}
	
	@Override
	public String NativeClassNameDefine() {
		return "Float";
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
		return new IrisFloatTag(0.0);
	}

	@Override
	public void NativeClassDefine(IrisClass classObj) throws Throwable {
		classObj.AddInstanceMethod(IrisFloat.class, "Add", "+", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisFloat.class, "Sub", "-", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisFloat.class, "Mul", "*", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisFloat.class, "Div", "/", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisFloat.class, "Power", "**", 1, false, MethodAuthority.Everyone);
		
		classObj.AddInstanceMethod(IrisFloat.class, "Equal", "==", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisFloat.class, "NotEqual", "!=", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisFloat.class, "BigThan", ">", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisFloat.class, "BigThanOrEqual", ">=", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisFloat.class, "LessThan", "<", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisFloat.class, "LessThanOrEqual", "<=", 1, false, MethodAuthority.Everyone);

		classObj.AddInstanceMethod(IrisFloat.class, "Plus", "__plus", 0, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisFloat.class, "Minus", "__minus", 0, false, MethodAuthority.Everyone);
		
		classObj.AddInstanceMethod(IrisFloat.class, "ToInteger", "to_integer", 0, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisFloat.class, "ToString", "to_string", 0, false, MethodAuthority.Everyone);
	}
}
