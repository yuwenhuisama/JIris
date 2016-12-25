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
import org.irislang.jiris.irisclass.IrisFloat.IrisFloatTag;


public class IrisInteger extends IrisClassRoot {	
	static public class IrisIntegerTag {
		private int m_integer = 0;
		
		public IrisFloatTag toFloat() {
			return new IrisFloatTag((double)m_integer);
		}
		
		public String toString() {
			return String.valueOf(m_integer);
		}
		
		public IrisIntegerTag(int integer) {
			setInteger(integer);
		}
		
		public IrisIntegerTag Add(IrisIntegerTag tar) {
			return new IrisIntegerTag(m_integer + tar.getInteger());
		}
		
		public IrisIntegerTag Sub(IrisIntegerTag tar) {
			return new IrisIntegerTag(m_integer - tar.getInteger());
		}

		public IrisIntegerTag Mul(IrisIntegerTag tar) {
			return new IrisIntegerTag(m_integer * tar.getInteger());
		}
		
		public IrisIntegerTag Div(IrisIntegerTag tar) {
			return new IrisIntegerTag(m_integer / tar.getInteger());
		}

		public IrisIntegerTag Mod(IrisIntegerTag tar) {
			return new IrisIntegerTag(m_integer % tar.getInteger());
		}
		
		public IrisIntegerTag Power(IrisIntegerTag tar) {
			return new IrisIntegerTag((int)Math.pow((double)m_integer, (double)tar.getInteger()));
		}
		
		public IrisIntegerTag Shl(IrisIntegerTag tar) {
			return new IrisIntegerTag(m_integer >> tar.getInteger());
		}
		
		public IrisIntegerTag Sal(IrisIntegerTag tar) {
			return new IrisIntegerTag(m_integer >>> tar.getInteger());
		}
		
		public IrisIntegerTag Shr(IrisIntegerTag tar) {
			return new IrisIntegerTag(m_integer << tar.getInteger());
		}
		
		public IrisIntegerTag Sar(IrisIntegerTag tar) {
			return new IrisIntegerTag(m_integer << tar.getInteger());
		}
		
		public IrisIntegerTag BitXor(IrisIntegerTag tar) {
			return new IrisIntegerTag(m_integer ^ tar.getInteger());
		}

		public IrisIntegerTag BitOr(IrisIntegerTag tar) {
			return new IrisIntegerTag(m_integer | tar.getInteger());
		}
		
		public IrisIntegerTag BitAnd(IrisIntegerTag tar) {
			return new IrisIntegerTag(m_integer & tar.getInteger());
		}
		
		public IrisIntegerTag BitNot() {
			return new IrisIntegerTag(~m_integer);
		}
		
		public boolean Equal(IrisIntegerTag tar) {
			return m_integer == tar.getInteger();
		}
		
		public boolean NotEqual(IrisIntegerTag tar) {
			return !Equal(tar);
		}
		
		public boolean BigThan(IrisIntegerTag tar) {
			return m_integer > tar.getInteger();
		}
		
		public boolean BigThanOrEqual(IrisIntegerTag tar) {
			return m_integer >= tar.getInteger();
		}
		
		public boolean LessThan(IrisIntegerTag tar) {
			return m_integer < tar.getInteger();
		}
		
		public boolean LessThanOrEqual(IrisIntegerTag tar) {
			return m_integer <= tar.getInteger();
		}
		
		public IrisIntegerTag Plus() {
			return new IrisIntegerTag(m_integer);
		}
		
		public IrisIntegerTag Minus() {
			return new IrisIntegerTag(-m_integer);
		}
		
		public int getInteger() {
			return m_integer;
		}

		public void setInteger(int integer) {
			m_integer = integer;
		}
	}

	enum Operation {
		Add,
		Sub,
		Mul,
		Div,
		Power,
		Mod,

		Shr,
		Shl,
		Sar,
		Sal,
		BitXor,
		BitAnd,
		BitOr,

		Equal,
		NotEqual,
		BigThan,
		BigThanOrEqual,
		LessThan,
		LessThanOrEqual,
	};
	
	private static IrisValue CastOperation(Operation type, IrisValue leftValue, IrisValue rightValue) {
		IrisValue result = null;
		boolean needCast = IrisDevUtil.CheckClass(rightValue, "Float");
		if(needCast) {
			if(type != Operation.Mod) {
				IrisFloatTag castLeftValue = ((IrisIntegerTag)IrisDevUtil.GetNativeObjectRef(leftValue)).toFloat();
				IrisFloatTag orgRightValue = IrisDevUtil.GetNativeObjectRef(rightValue);
				IrisFloatTag resultValue = null;
				switch(type) {
				case Add:
					resultValue = castLeftValue.Add(orgRightValue);
					break;
				case Sub:
					resultValue = castLeftValue.Sub(orgRightValue);
					break;
				case Mul:
					resultValue = castLeftValue.Mul(orgRightValue);
					break;
				case Div:
					resultValue = castLeftValue.Div(orgRightValue);
					break;
				case Power:
					resultValue = castLeftValue.Power(orgRightValue);
					break;
				default:
					break;
				}
				result = IrisDevUtil.CreateFloat(0.0);
				result.getObject().setNativeObject(resultValue);
			}
			else {
				IrisIntegerTag castRightValue = ((IrisFloatTag)IrisDevUtil.GetNativeObjectRef(rightValue)).toInteger();
				IrisIntegerTag orgLeftValue = IrisDevUtil.GetNativeObjectRef(leftValue);
				IrisIntegerTag resultValue = orgLeftValue.Mod(castRightValue);
				result = IrisDevUtil.CreateInt(0);
				result.getObject().setNativeObject(resultValue);
			}
		} else {
			IrisIntegerTag orgLeftValue = IrisDevUtil.GetNativeObjectRef(leftValue);
			IrisIntegerTag orgRightValue = IrisDevUtil.GetNativeObjectRef(rightValue);
			IrisIntegerTag resultValue = null;
			switch(type) {
			case Add:
				resultValue = orgLeftValue.Add(orgRightValue);
				break;
			case Sub:
				resultValue = orgLeftValue.Sub(orgRightValue);
				break;
			case Mul:
				resultValue = orgLeftValue.Mul(orgRightValue);
				break;
			case Div:
				resultValue = orgLeftValue.Div(orgRightValue);
				break;
			case Power:
				resultValue = orgLeftValue.Power(orgRightValue);
				break;
			default:
				break;
			}
			
			result = IrisDevUtil.CreateInt(0);
			result.getObject().setNativeObject(resultValue);
			
		}
		return result;
	}
	
	private static IrisValue CmpOperation(Operation type, IrisValue leftValue, IrisValue rightValue) {
		boolean needCast = IrisDevUtil.CheckClass(rightValue, "Float");
		boolean cmpResult = false;
		
		if(needCast) {
			IrisFloatTag castLeftValue = ((IrisIntegerTag)IrisDevUtil.GetNativeObjectRef(leftValue)).toFloat();
			IrisFloatTag orgRightValue =  IrisDevUtil.GetNativeObjectRef(rightValue);
			
			switch(type) {
			case Equal:
				cmpResult = castLeftValue.Equal(orgRightValue);
				break;
			case NotEqual:
				cmpResult = castLeftValue.NotEqual(orgRightValue);
				break;
			case BigThan:
				cmpResult = castLeftValue.BigThan(orgRightValue);
				break;
			case BigThanOrEqual:
				cmpResult = castLeftValue.BigThanOrEqual(orgRightValue);
				break;
			case LessThan:
				cmpResult = castLeftValue.LessThan(orgRightValue);
				break;
			case LessThanOrEqual:
				cmpResult = castLeftValue.LessThanOrEqual(orgRightValue);
				break;
			default:
				break;
			}
			
		} else {
			IrisIntegerTag orgLeftValue = IrisDevUtil.GetNativeObjectRef(leftValue);
			IrisIntegerTag orgRightValue = IrisDevUtil.GetNativeObjectRef(rightValue);
			switch(type) {
			case Equal:
				cmpResult = orgLeftValue.Equal(orgRightValue);
				break;
			case NotEqual:
				cmpResult = orgLeftValue.NotEqual(orgRightValue);
				break;
			case BigThan:
				cmpResult = orgLeftValue.BigThan(orgRightValue);
				break;
			case BigThanOrEqual:
				cmpResult = orgLeftValue.BigThanOrEqual(orgRightValue);
				break;
			case LessThan:
				cmpResult = orgLeftValue.LessThan(orgRightValue);
				break;
			case LessThanOrEqual:
				cmpResult = orgLeftValue.LessThanOrEqual(orgRightValue);
				break;
			default:
				break;
			}
		}
		
		return cmpResult ? IrisDevUtil.True() : IrisDevUtil.False();
	}
	
	private static IrisValue BitOperation(Operation type, IrisValue leftValue, IrisValue rightValue) {
		if(!IrisDevUtil.CheckClass(rightValue, "Integer")) {
			/* Error */
			return IrisDevUtil.Nil();
		}
		
		IrisIntegerTag orgLeftValue = IrisDevUtil.GetNativeObjectRef(leftValue);
		IrisIntegerTag orgRightValue = IrisDevUtil.GetNativeObjectRef(rightValue);
		IrisIntegerTag resultValue = null;
		
		switch(type) {
		case Sal:
			resultValue = orgLeftValue.Sal(orgRightValue);
			break;
		case Sar:
			resultValue = orgLeftValue.Sar(orgRightValue);
			break;
		case Shl:
			resultValue = orgLeftValue.Shl(orgRightValue);
			break;
		case Shr:
			resultValue = orgLeftValue.Shr(orgRightValue);
			break;
		case BitAnd:
			resultValue = orgLeftValue.BitAnd(orgRightValue);
			break;
		case BitOr:
			resultValue = orgLeftValue.BitOr(orgRightValue);
			break;
		case BitXor:
			resultValue = orgLeftValue.BitXor(orgRightValue);
			break;
		default:
			break;
		}
		
		IrisValue result = IrisDevUtil.CreateInt(0);
		result.getObject().setNativeObject(resultValue);
		return result;
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

	public static IrisValue Mod(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return CastOperation(Operation.Mod, self, parameterList.get(0));
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
	
	public static IrisValue Shr(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return BitOperation(Operation.Shr, self, parameterList.get(0));
	}
	
	public static IrisValue Sar(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return BitOperation(Operation.Sar, self, parameterList.get(0));
	}

	public static IrisValue Shl(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return BitOperation(Operation.Shl, self, parameterList.get(0));
	}

	public static IrisValue Sal(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return BitOperation(Operation.Sal, self, parameterList.get(0));
	}

	public static IrisValue BitXor(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return BitOperation(Operation.BitXor, self, parameterList.get(0));
	}

	public static IrisValue BitOr(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return BitOperation(Operation.BitOr, self, parameterList.get(0));
	}

	public static IrisValue BitAnd(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		return BitOperation(Operation.BitAnd, self, parameterList.get(0));
	}
	
	public static IrisValue BitNot(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisIntegerTag selfValue = IrisDevUtil.GetNativeObjectRef(self);
		IrisValue result = IrisDevUtil.CreateInt(0);
		result.getObject().setNativeObject(selfValue.BitNot());
		return result;
	}
	
	public static IrisValue Plus(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisIntegerTag selfValue = IrisDevUtil.GetNativeObjectRef(self);
		IrisValue result = IrisDevUtil.CreateInt(0);
		result.getObject().setNativeObject(selfValue.Plus());
		return result;
	}

	public static IrisValue Minus(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisIntegerTag selfValue = IrisDevUtil.GetNativeObjectRef(self);
		IrisValue result = IrisDevUtil.CreateInt(0);
		result.getObject().setNativeObject(selfValue.Minus());
		return result;
	}
	
	public static IrisValue ToFloat(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisValue result = IrisDevUtil.CreateFloat((double)IrisDevUtil.GetInt(self));
		return result;
	}
	
	public static IrisValue ToString(IrisValue self,  ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
		IrisValue result = IrisDevUtil.CreateString(((IrisIntegerTag)IrisDevUtil.GetNativeObjectRef(self)).toString());
		return result;
	}
	
	@Override
	public String NativeClassNameDefine() {
		return "Integer";
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
		return new IrisIntegerTag(0);
	}

	@Override
	public void NativeClassDefine(IrisClass classObj) throws Throwable {
		classObj.AddInstanceMethod(IrisInteger.class, "Add", "+", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "Sub", "-", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "Mul", "*", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "Div", "/", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "Mod", "%", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "Power", "**", 1, false, MethodAuthority.Everyone);

		classObj.AddInstanceMethod(IrisInteger.class, "Equal", "==", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "NotEqual", "!=", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "BigThan", ">", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "BigThanOrEqual", ">=", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "LessThan", "<", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "LessThanOrEqual", "<=", 1, false, MethodAuthority.Everyone);
		
		classObj.AddInstanceMethod(IrisInteger.class, "Shr", ">>", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "Sar", ">>>", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "Shl", "<<", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "Sal", "<<<", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "BitXor", "^", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "BitOr", "|", 1, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "BitAnd", "&", 1, false, MethodAuthority.Everyone);

		classObj.AddInstanceMethod(IrisInteger.class, "BitNot", "~", 0, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "Minus", "__minus", 0, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "Plus", "__plus", 0, false, MethodAuthority.Everyone);
		
		classObj.AddInstanceMethod(IrisInteger.class, "ToString", "to_string", 0, false, MethodAuthority.Everyone);
		classObj.AddInstanceMethod(IrisInteger.class, "ToFloat", "to_float", 0, false, MethodAuthority.Everyone);
		
	}
}
