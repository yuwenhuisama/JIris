package org.irislang.jiris.irisclass

import java.util.ArrayList

import org.irislang.jiris.core.IrisClass
import org.irislang.jiris.core.IrisContextEnvironment
import org.irislang.jiris.core.IrisModule
import org.irislang.jiris.core.IrisThreadInfo
import org.irislang.jiris.core.IrisValue
import org.irislang.jiris.core.IrisMethod.MethodAuthority
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.dev.IrisClassRoot
import org.irislang.jiris.dev.IrisDevUtil

open class IrisClassBase : IrisClassRoot() {

    class IrisClassBaseTag {
        var classObj: IrisClass? = null

        val className: String
            get() = classObj!!.className

    }


    override fun NativeClassNameDefine(): String {
        return "Class"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        // Specially
        return IrisClass()
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    override fun NativeAlloc(): Any {
        return IrisClassBaseTag()
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeClassDefine(classObj: IrisClass) {
        classObj.AddInstanceMethod(IrisClassBase::class.java, "New", "new", 0, true, MethodAuthority.Everyone)
    }

    companion object {

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun New(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val classObj = IrisDevUtil.GetNativeObjectRef<IrisClassBaseTag>(self)
            return IrisDevUtil.CreateInstance(classObj.classObj!!, variableParameterList, context, threadInfo);
        }

        @JvmStatic
        fun GetClassName(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val classObj = IrisDevUtil.GetNativeObjectRef<IrisClassBaseTag>(self)
            return IrisDevUtil.CreateString(classObj.className)
        }
    }

}
