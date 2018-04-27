package org.irislang.jiris.irismodule

import java.util.ArrayList

import org.irislang.jiris.core.IrisContextEnvironment
import org.irislang.jiris.core.IrisModule
import org.irislang.jiris.core.IrisThreadInfo
import org.irislang.jiris.core.IrisValue
import org.irislang.jiris.core.IrisMethod.CallSide
import org.irislang.jiris.core.IrisMethod.MethodAuthority
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.dev.IrisDevUtil
import org.irislang.jiris.dev.IrisModuleRoot
import org.irislang.jiris.irisclass.IrisString.IrisStringTag
import org.irislang.jiris.irisclass.IrisUniqueString.IrisUniqueStringTag

class IrisKernel : IrisModuleRoot() {

    override fun NativeModuleNameDefine(): String {
        return "Kernel"
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeModuleDefine(moduleObj: IrisModule) {
        moduleObj.AddClassMethod(IrisKernel::class.java, "Print", "print", 0, true, MethodAuthority.Everyone)
        moduleObj.AddInstanceMethod(IrisKernel::class.java, "Print", "print", 0, true, MethodAuthority.Everyone)
    }

    companion object {

        @Throws(Throwable::class)
        @JvmStatic
        fun Print(self: IrisValue, parameterList: ArrayList<IrisValue>,
                  variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            if (variableParameterList != null) {
                for (value in variableParameterList) {
                    if (IrisDevUtil.CheckClass(value, "String")) {
                        print((IrisDevUtil.GetNativeObjectRef<IrisStringTag>(value)).string)
                    } else if (IrisDevUtil.CheckClass(value, "UniqueString")) {
                        print((IrisDevUtil.GetNativeObjectRef<IrisUniqueStringTag>(value)).string)
                    } else {
                        val result = value.`object`!!.CallInstanceMethod("to_string", ArrayList(),
                                context, threadInfo, CallSide.Outeside)
                        print((IrisDevUtil.GetNativeObjectRef<IrisStringTag>(result!!)).string)
                    }
                }
            }

            return IrisDevUtil.Nil()
        }
    }

}
