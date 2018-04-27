package org.irislang.jiris.irisclass

import org.irislang.jiris.core.*
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.dev.IrisClassRoot
import org.irislang.jiris.dev.IrisDevUtil

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Huisama on 2017/4/8 0008.
 */
class IrisHash : IrisClassRoot() {
    override fun NativeClassNameDefine(): String {
        return "Hash"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        return IrisDevUtil.GetClass("Object")!!
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    override fun NativeAlloc(): Any {
        return HashMap<IrisValue, IrisValue>()
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeClassDefine(classObj: IrisClass) {
        classObj.AddInstanceMethod(IrisHash::class.java, "Initialize", "__format", 0, true, IrisMethod.MethodAuthority
                .Everyone)
        classObj.AddInstanceMethod(IrisHash::class.java, "At", "[]", 1, false, IrisMethod.MethodAuthority
                .Everyone)
        classObj.AddInstanceMethod(IrisHash::class.java, "Set", "[]=", 2, false, IrisMethod.MethodAuthority
                .Everyone)
        classObj.AddInstanceMethod(IrisHash::class.java, "GetIterator", "get_iterator", 0, false, IrisMethod
                .MethodAuthority
                .Everyone)
    }

    companion object {

        @JvmStatic
        fun Initialize(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val hashMap = IrisDevUtil.GetNativeObjectRef<HashMap<IrisValue, IrisValue>>(self)

            val size = variableParameterList.size / 2
            for (i in 0 until size) {
                hashMap[variableParameterList[i * 2]] = variableParameterList[i * 2 + 1]
            }

            return self
        }

        @JvmStatic
        fun At(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val hashMap = IrisDevUtil.GetNativeObjectRef<HashMap<IrisValue, IrisValue>>(self)
            return hashMap[parameterList[0]] ?: IrisDevUtil.Nil()
        }

        @JvmStatic
        fun Set(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val hashMap = IrisDevUtil.GetNativeObjectRef<HashMap<IrisValue, IrisValue>>(self)
            val key = IrisDevUtil.GetNativeObjectRef<IrisValue>(parameterList[0])
            val value = IrisDevUtil.GetNativeObjectRef<IrisValue>(parameterList[1])

            hashMap[key] = value

            return IrisDevUtil.Nil()
        }

        @JvmStatic
        fun GetIterator(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val parameters = ArrayList<IrisValue>(1)
            parameters.add(0, self)

            return IrisDevUtil.CreateInstance(IrisDevUtil.GetClass("HashIterator")!!, parameters, context, threadInfo)
        }
    }
}
