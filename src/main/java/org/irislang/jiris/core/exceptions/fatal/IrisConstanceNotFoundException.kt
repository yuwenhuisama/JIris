package org.irislang.jiris.core.exceptions.fatal

import org.irislang.jiris.core.exceptions.IrisRuntimeException

/**
 * Created by yuwen on 2017/7/6 0006.
 */
class IrisConstanceNotFoundException(fileName: String, lineNumber: Int, message: String) : IrisFatalException(fileName, lineNumber, message) {

    override fun GetFatalExceptionName(): String {
        return "ConstanceNotFoundIrregular"
    }
}
