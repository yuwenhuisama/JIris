package org.irislang.jiris.irisclass

import java.util.ArrayList

import org.irislang.jiris.core.IrisClass
import org.irislang.jiris.core.IrisContextEnvironment
import org.irislang.jiris.core.IrisMethod.MethodAuthority
import org.irislang.jiris.core.IrisModule
import org.irislang.jiris.core.IrisThreadInfo
import org.irislang.jiris.core.IrisValue
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.core.exceptions.fatal.IrisTypeNotCorretException
import org.irislang.jiris.dev.IrisClassRoot
import org.irislang.jiris.dev.IrisDevUtil
import org.irislang.jiris.irisclass.IrisFloat.IrisFloatTag
import org.irislang.jiris.irisclass.IrisInteger.IrisIntegerTag

class IrisFloat : IrisClassRoot() {

    internal enum class Operation {
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
        LessThanOrEqual
    }

    class IrisFloatTag(dfloat: Double) {
        var float = 0.0

        fun toInteger(): IrisIntegerTag {
            return IrisIntegerTag(float.toInt())
        }

        override fun toString(): String {
            return float.toString()
        }

        init {
            float = dfloat
        }

        fun Add(tar: IrisFloatTag?): IrisFloatTag {
            return IrisFloatTag(float + tar!!.float)
        }

        fun Sub(tar: IrisFloatTag?): IrisFloatTag {
            return IrisFloatTag(float - tar!!.float)
        }

        fun Mul(tar: IrisFloatTag?): IrisFloatTag {
            return IrisFloatTag(float * tar!!.float)
        }

        fun Div(tar: IrisFloatTag?): IrisFloatTag {
            return IrisFloatTag(float / tar!!.float)
        }

        fun Mod(tar: IrisFloatTag): IrisFloatTag {
            return IrisFloatTag(float % tar.float)
        }

        fun Power(tar: IrisFloatTag?): IrisFloatTag {
            return IrisFloatTag(Math.pow(float, tar!!.float))
        }

        fun Equal(tar: IrisFloatTag?): Boolean {
            return float == tar!!.float
        }

        fun NotEqual(tar: IrisFloatTag?): Boolean {
            return !Equal(tar)
        }

        fun BigThan(tar: IrisFloatTag?): Boolean {
            return float > tar!!.float
        }

        fun BigThanOrEqual(tar: IrisFloatTag?): Boolean {
            return float >= tar!!.float
        }

        fun LessThan(tar: IrisFloatTag?): Boolean {
            return float < tar!!.float
        }

        fun LessThanOrEqual(tar: IrisFloatTag?): Boolean {
            return float <= tar!!.float
        }

        fun Plus(): IrisFloatTag {
            return IrisFloatTag(float)
        }

        fun Minus(): IrisFloatTag {
            return IrisFloatTag(-float)
        }
    }

    override fun NativeClassNameDefine(): String {
        return "Float"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        return IrisDevUtil.GetClass("Object")!!
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    override fun NativeAlloc(): Any {
        return IrisFloatTag(0.0)
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeClassDefine(classObj: IrisClass) {
        classObj.AddInstanceMethod(IrisFloat::class.java, "Add", "+", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisFloat::class.java, "Sub", "-", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisFloat::class.java, "Mul", "*", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisFloat::class.java, "Div", "/", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisFloat::class.java, "Power", "**", 1, false, MethodAuthority.Everyone)

        classObj.AddInstanceMethod(IrisFloat::class.java, "Equal", "==", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisFloat::class.java, "NotEqual", "!=", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisFloat::class.java, "BigThan", ">", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisFloat::class.java, "BigThanOrEqual", ">=", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisFloat::class.java, "LessThan", "<", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisFloat::class.java, "LessThanOrEqual", "<=", 1, false, MethodAuthority.Everyone)

        classObj.AddInstanceMethod(IrisFloat::class.java, "Plus", "__plus", 0, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisFloat::class.java, "Minus", "__minus", 0, false, MethodAuthority.Everyone)

        classObj.AddInstanceMethod(IrisFloat::class.java, "ToInteger", "to_integer", 0, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisFloat::class.java, "ToString", "to_string", 0, false, MethodAuthority.Everyone)
    }

    companion object {

        @Throws(IrisExceptionBase::class)
        private fun CastOperation(type: Operation, leftValue: IrisValue, rightValue: IrisValue, threadInfo: IrisThreadInfo): IrisValue {
            var result: IrisValue? = null
            val needCast = IrisDevUtil.CheckClass(rightValue, "Integer")
            if (!needCast && !IrisDevUtil.CheckClass(rightValue, "Float")) {
                /* Error */
                throw IrisTypeNotCorretException(threadInfo.currentFileName, threadInfo.currentLineNumber,
                        "Wrong right value's type : it must be a float or an Integer")
            }
            val orgLeftValue = IrisDevUtil.GetNativeObjectRef<IrisFloatTag>(leftValue)
            var resultValue: IrisFloatTag? = null
            var finallyRightValue: IrisFloatTag? = null

            if (needCast) {
                finallyRightValue = (IrisDevUtil.GetNativeObjectRef<Any>(rightValue) as IrisIntegerTag).toFloat()
            } else {
                finallyRightValue = IrisDevUtil.GetNativeObjectRef<IrisFloatTag>(rightValue)
            }

            when (type) {
                IrisFloat.Operation.Add -> resultValue = orgLeftValue.Add(finallyRightValue)
                IrisFloat.Operation.Sub -> resultValue = orgLeftValue.Sub(finallyRightValue)
                IrisFloat.Operation.Mul -> resultValue = orgLeftValue.Mul(finallyRightValue)
                IrisFloat.Operation.Div -> resultValue = orgLeftValue.Div(finallyRightValue)
                IrisFloat.Operation.Power -> resultValue = orgLeftValue.Power(finallyRightValue)
                else -> {
                }
            }
            result = IrisDevUtil.CreateFloat(0.0)
            result.`object`!!.nativeObject = resultValue
            return result
        }

        @Throws(IrisExceptionBase::class)
        private fun CmpOperation(type: Operation, leftValue: IrisValue, rightValue: IrisValue, threadInfo: IrisThreadInfo): IrisValue {
            var cmpResult = false
            val needCast = IrisDevUtil.CheckClass(rightValue, "Integer")
            if (!needCast && !IrisDevUtil.CheckClass(rightValue, "Float")) {
                /* Error */
                throw IrisTypeNotCorretException(threadInfo.currentFileName, threadInfo.currentLineNumber,
                        "Wrong right value's type : it must be a float or an Integer")
            }

            val orgLeftValue = IrisDevUtil.GetNativeObjectRef<IrisFloatTag>(leftValue)
            var finallyRightValue: IrisFloatTag? = null

            if (needCast) {
                finallyRightValue = (IrisDevUtil.GetNativeObjectRef<Any>(rightValue) as IrisIntegerTag).toFloat()
            } else {
                finallyRightValue = IrisDevUtil.GetNativeObjectRef<IrisFloatTag>(rightValue)
            }

            when (type) {
                IrisFloat.Operation.Equal -> cmpResult = orgLeftValue.Equal(finallyRightValue)
                IrisFloat.Operation.NotEqual -> cmpResult = orgLeftValue.NotEqual(finallyRightValue)
                IrisFloat.Operation.BigThan -> cmpResult = orgLeftValue.BigThan(finallyRightValue)
                IrisFloat.Operation.BigThanOrEqual -> cmpResult = orgLeftValue.BigThanOrEqual(finallyRightValue)
                IrisFloat.Operation.LessThan -> cmpResult = orgLeftValue.LessThan(finallyRightValue)
                IrisFloat.Operation.LessThanOrEqual -> cmpResult = orgLeftValue.LessThanOrEqual(finallyRightValue)
                else -> {
                }
            }

            return if (cmpResult) IrisDevUtil.True() else IrisDevUtil.False()
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Add(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return CastOperation(Operation.Add, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Sub(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return CastOperation(Operation.Sub, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Mul(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return CastOperation(Operation.Mul, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Div(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return CastOperation(Operation.Div, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Power(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return CastOperation(Operation.Power, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Equal(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return CmpOperation(Operation.Equal, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun NotEqual(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return CmpOperation(Operation.NotEqual, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun BigThan(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return CmpOperation(Operation.BigThan, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun BigThanOrEqual(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return CmpOperation(Operation.BigThanOrEqual, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun LessThan(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return CmpOperation(Operation.LessThan, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun LessThanOrEqual(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return CmpOperation(Operation.LessThanOrEqual, self, parameterList[0], threadInfo)
        }

        @JvmStatic
        fun Plus(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val selfValue = IrisDevUtil.GetNativeObjectRef<IrisFloatTag>(self)
            val result = IrisDevUtil.CreateFloat(0.0)
            result.`object`!!.nativeObject = selfValue.Plus()
            return result
        }

        @JvmStatic
        fun Minus(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val selfValue = IrisDevUtil.GetNativeObjectRef<IrisFloatTag>(self)
            val result = IrisDevUtil.CreateFloat(0.0)
            result.`object`!!.nativeObject = selfValue.Minus()
            return result
        }

        @JvmStatic
        fun ToInteger(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return IrisDevUtil.CreateInt(IrisDevUtil.GetFloat(self).toInt())
        }

        @JvmStatic
        fun ToString(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return IrisDevUtil.CreateString((IrisDevUtil.GetNativeObjectRef<Any>(self) as IrisFloatTag).toString())
        }
    }
}
