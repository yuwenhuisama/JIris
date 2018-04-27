package org.irislang.jiris.dev

import org.irislang.jiris.core.IrisModule

interface IrisInterfaceRoot {
    fun NativeModuleNameDefine(): String
    fun NativeUpperModuleDefine(): IrisModule?
    fun NativeModuleDefine(): Void
}