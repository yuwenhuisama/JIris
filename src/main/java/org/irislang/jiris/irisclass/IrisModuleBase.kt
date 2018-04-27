package org.irislang.jiris.irisclass

import java.util.ArrayList

import org.irislang.jiris.core.IrisClass
import org.irislang.jiris.core.IrisContextEnvironment
import org.irislang.jiris.core.IrisModule
import org.irislang.jiris.core.IrisThreadInfo
import org.irislang.jiris.core.IrisValue
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.dev.IrisDevUtil
import org.irislang.jiris.dev.IrisClassRoot

class IrisModuleBase : IrisClassRoot() {

    class IrisModuleBaseTag(module: IrisModule) {
        var module: IrisModule? = null

        val moduleName: String
            get() = module!!.moduleName

        init {
            this.module = module
        }
    }

    override fun NativeClassNameDefine(): String {
        return "Module"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        return IrisDevUtil.GetClass("Object") ?: IrisClass()
    }

    override fun NativeAlloc(): Any {
        return IrisModuleBaseTag(IrisModule())
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeClassDefine(classObj: IrisClass) {
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    companion object {

        @JvmStatic
        fun GetModuleName(self: IrisValue, parameterList: ArrayList<IrisValue>,
                          variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return IrisDevUtil.CreateString((IrisDevUtil.GetNativeObjectRef<IrisModuleBaseTag>(self)).moduleName)
        }
    }

}
