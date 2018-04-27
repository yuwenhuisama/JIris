package org.irislang.jiris.irisclass

import org.irislang.jiris.core.*
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.dev.IrisDevUtil

import java.util.ArrayList

/**
 * Created by yuwen on 2017/7/3 0003.
 */
class IrisBlock : IrisClassBase() {
    override fun NativeClassNameDefine(): String {
        return "Block"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        return IrisDevUtil.GetClass("Object")!!
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
        classObj.AddClassMethod(IrisBlock::class.java, "New", "new", 0, false, IrisMethod.MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisBlock::class.java, "Call", "call", 0, true, IrisMethod.MethodAuthority.Everyone)
    }

    companion object {

        @JvmStatic
        fun New(self: IrisValue, parameterList: ArrayList<IrisValue>,
                         variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            return IrisValue.WrapObject(threadInfo.GetTopClosureBlock().nativeObject)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Call(self: IrisValue, parameterList: ArrayList<IrisValue>,
                 variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue? {
            val closureBlock = IrisDevUtil.GetNativeObjectRef<IrisClosureBlock>(self)
            return closureBlock.Call(variableParameterList, threadInfo)
        }
    }

}
