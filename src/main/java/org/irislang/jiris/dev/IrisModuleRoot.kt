package org.irislang.jiris.dev

import java.lang.invoke.CallSite
import java.lang.invoke.ConstantCallSite
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

import org.irislang.jiris.core.IrisModule
import org.irislang.jiris.core.exceptions.IrisExceptionBase

abstract class IrisModuleRoot {

    abstract fun NativeModuleNameDefine(): String
    abstract fun NativeUpperModuleDefine(): IrisModule?
    @Throws(IrisExceptionBase::class)
    abstract fun NativeModuleDefine(moduleObj: IrisModule): Any

    companion object {
        @Throws(Throwable::class)
        @JvmStatic fun BootstrapMethod(classObj: Class<*>, lookup: MethodHandles.Lookup, name: String, mt: MethodType): CallSite {
            return ConstantCallSite(lookup.findStatic(classObj, name, mt))
        }
    }
}
