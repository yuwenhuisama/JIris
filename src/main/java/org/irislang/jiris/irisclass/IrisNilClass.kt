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


class IrisNilClass : IrisClassRoot() {

    class IrisNilClassTag {
        val name = "nil"

        fun LogicNot(): Boolean {
            return true
        }
    }


    override fun NativeClassNameDefine(): String {
        return "NilClass"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        return IrisDevUtil.GetClass("Object")!!
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    override fun NativeAlloc(): Any {
        return IrisNilClassTag()
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeClassDefine(classObj: IrisClass) {
        classObj.AddInstanceMethod(IrisNilClass::class.java, "LogicNot", "!", 0, false, MethodAuthority.Everyone)
    }

    companion object {

        @JvmStatic
        fun GetName(self: IrisValue, parameterList: ArrayList<IrisValue>,
                    variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val obj = IrisDevUtil.GetNativeObjectRef<IrisNilClassTag>(self)
            return IrisDevUtil.CreateString(obj.name)
        }

        @JvmStatic
        fun LogicNot(self: IrisValue, parameterList: ArrayList<IrisValue>,
                     variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return IrisDevUtil.True()
        }
    }
}
