package org.irislang.jiris

import org.irislang.jiris.core.IrisContextEnvironment
import org.irislang.jiris.core.IrisThreadInfo
import org.irislang.jiris.core.IrisValue

interface IrisRunnable {
    fun run(context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue
}
