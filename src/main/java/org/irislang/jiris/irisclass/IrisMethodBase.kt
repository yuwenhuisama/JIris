package org.irislang.jiris.irisclass

import java.util.ArrayList

import org.irislang.jiris.core.IrisClass
import org.irislang.jiris.core.IrisContextEnvironment
import org.irislang.jiris.core.IrisMethod
import org.irislang.jiris.core.IrisModule
import org.irislang.jiris.core.IrisThreadInfo
import org.irislang.jiris.core.IrisValue
import org.irislang.jiris.dev.IrisClassRoot
import org.irislang.jiris.dev.IrisDevUtil

class IrisMethodBase : IrisClassRoot() {

    class IrisMethodBaseTag {

        var methodObj: IrisMethod? = null

        val methodName: String
            get() = methodObj!!.methodName
    }

    override fun NativeClassNameDefine(): String {
        return "Method"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        return IrisDevUtil.GetClass("Object")!!
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    override fun NativeAlloc(): Any {
        return IrisMethodBaseTag()
    }

    override fun NativeClassDefine(classObj: IrisClass) {

    }

    companion object {

        @JvmStatic
        fun GetName(self: IrisValue, parameterList: ArrayList<IrisValue>,
                    variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val methodObj = IrisDevUtil.GetNativeObjectRef<IrisMethodBaseTag>(self)
            return IrisDevUtil.CreateString(methodObj.methodName)
        }
    }

}
