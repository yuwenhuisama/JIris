package org.irislang.jiris.irisclass

import java.util.ArrayList

import org.irislang.jiris.core.IrisClass
import org.irislang.jiris.core.IrisContextEnvironment
import org.irislang.jiris.core.IrisModule
import org.irislang.jiris.core.IrisThreadInfo
import org.irislang.jiris.core.IrisValue
import org.irislang.jiris.core.IrisMethod.MethodAuthority
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.core.exceptions.fatal.IrisTypeNotCorretException
import org.irislang.jiris.dev.IrisClassRoot
import org.irislang.jiris.dev.IrisDevUtil
import org.irislang.jiris.irisclass.IrisFloat.IrisFloatTag
import org.irislang.jiris.irisclass.IrisInteger.IrisIntegerTag


class IrisInteger : IrisClassRoot() {
    class IrisIntegerTag(var integer: Int = 0) {

        fun toFloat(): IrisFloatTag {
            return IrisFloatTag(integer.toDouble())
        }

        override fun toString(): String {
            return integer.toString()
        }

        fun Add(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(integer + tar.integer)
        }

        fun Sub(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(integer - tar.integer)
        }

        fun Mul(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(integer * tar.integer)
        }

        fun Div(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(integer / tar.integer)
        }

        fun Mod(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(integer % tar.integer)
        }

        fun Power(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(Math.pow(integer.toDouble(), tar.integer.toDouble()).toInt())
        }

        fun Shl(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(integer shr tar.integer)
        }

        fun Sal(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(integer.ushr(tar.integer))
        }

        fun Shr(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(integer shl tar.integer)
        }

        fun Sar(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(integer shl tar.integer)
        }

        fun BitXor(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(integer xor tar.integer)
        }

        fun BitOr(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(integer or tar.integer)
        }

        fun BitAnd(tar: IrisIntegerTag): IrisIntegerTag {
            return IrisIntegerTag(integer and tar.integer)
        }

        fun BitNot(): IrisIntegerTag {
            return IrisIntegerTag(integer.inv())
        }

        fun Equal(tar: IrisIntegerTag): Boolean {
            return integer == tar.integer
        }

        fun NotEqual(tar: IrisIntegerTag): Boolean {
            return !Equal(tar)
        }

        fun BigThan(tar: IrisIntegerTag): Boolean {
            return integer > tar.integer
        }

        fun BigThanOrEqual(tar: IrisIntegerTag): Boolean {
            return integer >= tar.integer
        }

        fun LessThan(tar: IrisIntegerTag): Boolean {
            return integer < tar.integer
        }

        fun LessThanOrEqual(tar: IrisIntegerTag): Boolean {
            return integer <= tar.integer
        }

        fun Plus(): IrisIntegerTag {
            return IrisIntegerTag(integer)
        }

        fun Minus(): IrisIntegerTag {
            return IrisIntegerTag(-integer)
        }
    }

    internal enum class Operation {
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
        LessThanOrEqual
    }

    override fun NativeClassNameDefine(): String {
        return "Integer"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        return IrisDevUtil.GetClass("Object")!!
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    override fun NativeAlloc(): Any {
        return IrisIntegerTag(0)
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeClassDefine(classObj: IrisClass) {
        classObj.AddInstanceMethod(IrisInteger::class.java, "Add", "+", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "Sub", "-", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "Mul", "*", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "Div", "/", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "Mod", "%", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "Power", "**", 1, false, MethodAuthority.Everyone)

        classObj.AddInstanceMethod(IrisInteger::class.java, "Equal", "==", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "NotEqual", "!=", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "BigThan", ">", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "BigThanOrEqual", ">=", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "LessThan", "<", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "LessThanOrEqual", "<=", 1, false, MethodAuthority.Everyone)

        classObj.AddInstanceMethod(IrisInteger::class.java, "Shr", ">>", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "Sar", ">>>", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "Shl", "<<", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "Sal", "<<<", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "BitXor", "^", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "BitOr", "|", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "BitAnd", "&", 1, false, MethodAuthority.Everyone)

        classObj.AddInstanceMethod(IrisInteger::class.java, "BitNot", "~", 0, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "Minus", "__minus", 0, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "Plus", "__plus", 0, false, MethodAuthority.Everyone)

        classObj.AddInstanceMethod(IrisInteger::class.java, "ToString", "to_string", 0, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "ToFloat", "to_float", 0, false, MethodAuthority.Everyone)

    }

    companion object {

        @Throws(IrisExceptionBase::class)
        private fun CastOperation(type: Operation, leftValue: IrisValue, rightValue: IrisValue, threadInfo: IrisThreadInfo): IrisValue {
            val result: IrisValue

            val needCast = IrisDevUtil.CheckClass(rightValue, "Float")
            if (!needCast && !IrisDevUtil.CheckClass(rightValue, "Integer")) {
                throw IrisTypeNotCorretException(threadInfo.currentFileName, threadInfo.currentLineNumber,
                        "Wrong right value's type : it must be a Float or an Integer")
            }

            if (needCast) {
                if (type != Operation.Mod) {
                    val castLeftValue = (IrisDevUtil.GetNativeObjectRef<Any>(leftValue) as IrisIntegerTag).toFloat()
                    val orgRightValue = IrisDevUtil.GetNativeObjectRef<IrisFloatTag>(rightValue)
                    var resultValue: IrisFloatTag? = null
                    when (type) {
                        IrisInteger.Operation.Add -> resultValue = castLeftValue.Add(orgRightValue)
                        IrisInteger.Operation.Sub -> resultValue = castLeftValue.Sub(orgRightValue)
                        IrisInteger.Operation.Mul -> resultValue = castLeftValue.Mul(orgRightValue)
                        IrisInteger.Operation.Div -> resultValue = castLeftValue.Div(orgRightValue)
                        IrisInteger.Operation.Power -> resultValue = castLeftValue.Power(orgRightValue)
                        else -> {
                        }
                    }
                    result = IrisDevUtil.CreateFloat(0.0)
                    result.`object`!!.nativeObject = resultValue
                } else {
                    val castRightValue = (IrisDevUtil.GetNativeObjectRef<Any>(rightValue) as IrisFloatTag).toInteger()
                    val orgLeftValue = IrisDevUtil.GetNativeObjectRef<IrisIntegerTag>(leftValue)
                    val resultValue = orgLeftValue.Mod(castRightValue)
                    result = IrisDevUtil.CreateInt(0)
                    result.`object`!!.nativeObject = resultValue
                }
            } else {
                val orgLeftValue = IrisDevUtil.GetNativeObjectRef<IrisIntegerTag>(leftValue)
                val orgRightValue = IrisDevUtil.GetNativeObjectRef<IrisIntegerTag>(rightValue)
                var resultValue: IrisIntegerTag? = null
                when (type) {
                    IrisInteger.Operation.Add -> resultValue = orgLeftValue.Add(orgRightValue)
                    IrisInteger.Operation.Sub -> resultValue = orgLeftValue.Sub(orgRightValue)
                    IrisInteger.Operation.Mul -> resultValue = orgLeftValue.Mul(orgRightValue)
                    IrisInteger.Operation.Div -> resultValue = orgLeftValue.Div(orgRightValue)
                    IrisInteger.Operation.Power -> resultValue = orgLeftValue.Power(orgRightValue)
                    else -> {
                    }
                }

                result = IrisDevUtil.CreateInt(0)
                result.`object`!!.nativeObject = resultValue

            }
            return result
        }

        @Throws(IrisExceptionBase::class)
        private fun CmpOperation(type: Operation, leftValue: IrisValue, rightValue: IrisValue, threadInfo: IrisThreadInfo): IrisValue {
            val needCast = IrisDevUtil.CheckClass(rightValue, "Float")
            var cmpResult = false

            if (!needCast && !IrisDevUtil.CheckClass(rightValue, "Integer")) {
                throw IrisTypeNotCorretException(threadInfo.currentFileName, threadInfo.currentLineNumber,
                        "Wrong right value's type : it must be a Float or an Integer")
            }

            if (needCast) {
                val castLeftValue = (IrisDevUtil.GetNativeObjectRef<Any>(leftValue) as IrisIntegerTag).toFloat()
                val orgRightValue = IrisDevUtil.GetNativeObjectRef<IrisFloatTag>(rightValue)

                when (type) {
                    IrisInteger.Operation.Equal -> cmpResult = castLeftValue.Equal(orgRightValue)
                    IrisInteger.Operation.NotEqual -> cmpResult = castLeftValue.NotEqual(orgRightValue)
                    IrisInteger.Operation.BigThan -> cmpResult = castLeftValue.BigThan(orgRightValue)
                    IrisInteger.Operation.BigThanOrEqual -> cmpResult = castLeftValue.BigThanOrEqual(orgRightValue)
                    IrisInteger.Operation.LessThan -> cmpResult = castLeftValue.LessThan(orgRightValue)
                    IrisInteger.Operation.LessThanOrEqual -> cmpResult = castLeftValue.LessThanOrEqual(orgRightValue)
                    else -> {
                    }
                }

            } else {
                val orgLeftValue = IrisDevUtil.GetNativeObjectRef<IrisIntegerTag>(leftValue)
                val orgRightValue = IrisDevUtil.GetNativeObjectRef<IrisIntegerTag>(rightValue)
                when (type) {
                    IrisInteger.Operation.Equal -> cmpResult = orgLeftValue.Equal(orgRightValue)
                    IrisInteger.Operation.NotEqual -> cmpResult = orgLeftValue.NotEqual(orgRightValue)
                    IrisInteger.Operation.BigThan -> cmpResult = orgLeftValue.BigThan(orgRightValue)
                    IrisInteger.Operation.BigThanOrEqual -> cmpResult = orgLeftValue.BigThanOrEqual(orgRightValue)
                    IrisInteger.Operation.LessThan -> cmpResult = orgLeftValue.LessThan(orgRightValue)
                    IrisInteger.Operation.LessThanOrEqual -> cmpResult = orgLeftValue.LessThanOrEqual(orgRightValue)
                    else -> {
                    }
                }
            }

            return if (cmpResult) IrisDevUtil.True() else IrisDevUtil.False()
        }

        @Throws(IrisExceptionBase::class)
        private fun BitOperation(type: Operation, leftValue: IrisValue, rightValue: IrisValue, threadInfo: IrisThreadInfo): IrisValue {
            if (!IrisDevUtil.CheckClass(rightValue, "Integer")) {
                throw IrisTypeNotCorretException(threadInfo.currentFileName, threadInfo.currentLineNumber,
                        "Wrong right value's type : it must be an Integer")
            }

            val orgLeftValue = IrisDevUtil.GetNativeObjectRef<IrisIntegerTag>(leftValue)
            val orgRightValue = IrisDevUtil.GetNativeObjectRef<IrisIntegerTag>(rightValue)
            var resultValue: IrisIntegerTag? = null

            when (type) {
                IrisInteger.Operation.Sal -> resultValue = orgLeftValue.Sal(orgRightValue)
                IrisInteger.Operation.Sar -> resultValue = orgLeftValue.Sar(orgRightValue)
                IrisInteger.Operation.Shl -> resultValue = orgLeftValue.Shl(orgRightValue)
                IrisInteger.Operation.Shr -> resultValue = orgLeftValue.Shr(orgRightValue)
                IrisInteger.Operation.BitAnd -> resultValue = orgLeftValue.BitAnd(orgRightValue)
                IrisInteger.Operation.BitOr -> resultValue = orgLeftValue.BitOr(orgRightValue)
                IrisInteger.Operation.BitXor -> resultValue = orgLeftValue.BitXor(orgRightValue)
                else -> {
                }
            }

            val result = IrisDevUtil.CreateInt(0)
            result.`object`!!.nativeObject = resultValue
            return result
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
        fun Mod(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return CastOperation(Operation.Mod, self, parameterList[0], threadInfo)
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

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Shr(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return BitOperation(Operation.Shr, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Sar(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return BitOperation(Operation.Sar, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Shl(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return BitOperation(Operation.Shl, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Sal(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return BitOperation(Operation.Sal, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun BitXor(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return BitOperation(Operation.BitXor, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun BitOr(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return BitOperation(Operation.BitOr, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun BitAnd(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return BitOperation(Operation.BitAnd, self, parameterList[0], threadInfo)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun BitNot(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val selfValue = IrisDevUtil.GetNativeObjectRef<IrisIntegerTag>(self)
            val result = IrisDevUtil.CreateInt(0)
            result.`object`!!.nativeObject = selfValue.BitNot()
            return result
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Plus(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val selfValue = IrisDevUtil.GetNativeObjectRef<IrisIntegerTag>(self)
            val result = IrisDevUtil.CreateInt(0)
            result.`object`!!.nativeObject = selfValue.Plus()
            return result
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Minus(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val selfValue = IrisDevUtil.GetNativeObjectRef<IrisIntegerTag>(self)
            val result = IrisDevUtil.CreateInt(0)
            result.`object`!!.nativeObject = selfValue.Minus()
            return result
        }

        @JvmStatic
        fun ToFloat(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return IrisDevUtil.CreateFloat(IrisDevUtil.GetInt(self).toDouble())
        }

        @JvmStatic
        fun ToString(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return IrisDevUtil.CreateString((IrisDevUtil.GetNativeObjectRef<Any>(self) as IrisIntegerTag).toString())
        }
    }
}
