package org.irislang.jiris.irisclass

import java.util.ArrayList
import java.util.HashMap

import org.irislang.jiris.core.IrisClass
import org.irislang.jiris.core.IrisContextEnvironment
import org.irislang.jiris.core.IrisModule
import org.irislang.jiris.core.IrisThreadInfo
import org.irislang.jiris.core.IrisValue
import org.irislang.jiris.core.IrisMethod.MethodAuthority
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.dev.IrisClassRoot
import org.irislang.jiris.dev.IrisDevUtil

class IrisUniqueString : IrisClassRoot() {

    class IrisUniqueStringTag(val string: String = "") {
    }

    override fun NativeClassNameDefine(): String {
        return "UniqueString"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        return IrisDevUtil.GetClass("Object")!!
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    override fun NativeAlloc(): Any {
        return IrisUniqueStringTag("")
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeClassDefine(classObj: IrisClass) {
        classObj.AddInstanceMethod(IrisUniqueString::class.java, "ToString", "to_string", 0, false, MethodAuthority.Everyone)
    }

    companion object {

        internal val sm_uniqueStringCache = HashMap<String, IrisValue>()

        fun GetUniqueString(uniqueString: String): IrisValue? {
            return sm_uniqueStringCache[uniqueString]
        }

        fun AddUniqueString(uniqueString: String, uniqueObj: IrisValue) {
            sm_uniqueStringCache[uniqueString] = uniqueObj
        }

        @JvmStatic
        fun ToString(self: IrisValue, parameterList: ArrayList<IrisValue>,
                     variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val obj = IrisDevUtil.GetNativeObjectRef<IrisUniqueStringTag>(self)
            return IrisDevUtil.CreateString(obj.string)
        }
    }
}
