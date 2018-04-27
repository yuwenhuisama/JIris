package org.irislang.jiris.irisclass

import java.util.ArrayList

import org.irislang.jiris.core.IrisClass
import org.irislang.jiris.core.IrisContextEnvironment
import org.irislang.jiris.core.IrisModule
import org.irislang.jiris.core.IrisThreadInfo
import org.irislang.jiris.core.IrisValue
import org.irislang.jiris.core.IrisMethod.MethodAuthority
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.core.exceptions.fatal.IrisFatalException
import org.irislang.jiris.core.exceptions.fatal.IrisMethodDefinedException
import org.irislang.jiris.core.exceptions.fatal.IrisMethodNotFoundException
import org.irislang.jiris.dev.IrisClassRoot
import org.irislang.jiris.dev.IrisDevUtil


class IrisObjectBase : IrisClassRoot() {

    override fun NativeClassNameDefine(): String {
        return "Object"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        // special
        return IrisClass()
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    override fun NativeAlloc(): Any {
        // special
        return Any()
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeClassDefine(classObj: IrisClass) {

        classObj.AddInvolvedModule(IrisDevUtil.GetModule("Kernel")!!)

        classObj.AddInstanceMethod(IrisObjectBase::class.java, "Initialize", "__format", 0, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisObjectBase::class.java, "ToString", "to_string", 0, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisObjectBase::class.java, "Equal", "==", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisObjectBase::class.java, "NotEqual", "!=", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisObjectBase::class.java, "LogicAnd", "&&", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisObjectBase::class.java, "LogicOr", "||", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisObjectBase::class.java, "MissingMethod", "missing_method", 2, false,
                MethodAuthority.Everyone)
    }

    companion object {

        @JvmStatic
        fun Initialize(self: IrisValue, parameterList: ArrayList<IrisValue>,
                       variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return self
        }

        @JvmStatic
        fun GetObjectID(self: IrisValue, parameterList: ArrayList<IrisValue>,
                        variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return IrisDevUtil.CreateInt(self.`object`!!.objectID)
        }

        @JvmStatic
        fun ToString(self: IrisValue, parameterList: ArrayList<IrisValue>,
                     variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val buffer = StringBuffer()
            val className = self.`object`!!.objectClass!!.className
            val objectID = self.`object`!!.objectID.toString()
            buffer.append("<").append(className).append(":").append(objectID).append(">")
            return IrisDevUtil.CreateString(buffer.toString())
        }

        @JvmStatic
        fun GetClass(self: IrisValue, parameterList: ArrayList<IrisValue>,
                     variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return IrisValue.WrapObject(self.`object`!!.objectClass!!.classObject!!)
        }

        @JvmStatic
        fun Equal(self: IrisValue, parameterList: ArrayList<IrisValue>,
                  variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val rightValue = parameterList[0]
            return if (self.`object` == rightValue.`object`) IrisDevUtil.True() else IrisDevUtil.False()
        }

        @JvmStatic
        fun NotEqual(self: IrisValue, parameterList: ArrayList<IrisValue>,
                     variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val rightValue = parameterList[0]
            return if (self.`object` != rightValue.`object`) IrisDevUtil.True() else IrisDevUtil.False()
        }

        @JvmStatic
        fun LogicOr(self: IrisValue, parameterList: ArrayList<IrisValue>,
                    variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val rightValue = parameterList[0]
            return if (self != IrisDevUtil.False() && self != IrisDevUtil.Nil()) {
                IrisDevUtil.True()
            } else if (rightValue != IrisDevUtil.False() && rightValue != IrisDevUtil.Nil()) {
                IrisDevUtil.True()
            } else {
                IrisDevUtil.False()
            }
        }

        @JvmStatic
        fun LogicAnd(self: IrisValue, parameterList: ArrayList<IrisValue>,
                     variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val rightValue = parameterList[0]
            return if (self == IrisDevUtil.False() || self == IrisDevUtil.Nil()) {
                IrisDevUtil.False()
            } else if (rightValue == IrisDevUtil.False() || rightValue == IrisDevUtil.Nil()) {
                IrisDevUtil.False()
            } else {
                IrisDevUtil.True()
            }
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun MissingMethod(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val methodName = IrisDevUtil.GetString(parameterList[0])
            val className = IrisDevUtil.GetString(parameterList[1])
            val result = StringBuilder()
            result.append("Method of ").append(methodName).append(" not found in class ").append(className).append(".")
            throw IrisMethodNotFoundException(threadInfo.currentFileName, threadInfo.currentLineNumber,
                    result.toString())
        }
    }

}
