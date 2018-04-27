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

class IrisString : IrisClassRoot() {
    class IrisStringTag(val string: String = "") {

        internal fun Add(tar: IrisStringTag): IrisStringTag {
            return IrisStringTag(string + tar.string)
        }
    }


    override fun NativeClassNameDefine(): String {
        return "String"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        return IrisDevUtil.GetClass("Object")!!
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    override fun NativeAlloc(): Any {
        return IrisStringTag("")
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeClassDefine(classObj: IrisClass) {
        classObj.AddInstanceMethod(IrisInteger::class.java, "Add", "+", 1, false, MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisInteger::class.java, "Equal", "==", 1, false, MethodAuthority.Everyone)
    }

    companion object {

        @JvmStatic
        fun Add(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {

            if (!IrisDevUtil.CheckClass(parameterList[0], "String")) {
                /* Error */
                return IrisDevUtil.Nil()
            }

            val cself = self.`object`!!.nativeObject as IrisStringTag?
            val ctar = parameterList[0].`object`!!.nativeObject as IrisStringTag
            val result = cself!!.Add(ctar)

            return IrisDevUtil.CreateString(result.string)
        }

        @JvmStatic
        fun Equal(self: IrisValue, parameterList: ArrayList<IrisValue>,
                  variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {

            if (IrisDevUtil.CheckClass(parameterList[0], "String")) {
                return IrisDevUtil.False()
            }

            val selfStr = IrisDevUtil.GetNativeObjectRef<IrisStringTag>(self)
            val rightStr = IrisDevUtil.GetNativeObjectRef<IrisStringTag>(parameterList[0])
            return if (selfStr.string == rightStr.string) IrisDevUtil.True() else IrisDevUtil.False()
        }
    }
}
