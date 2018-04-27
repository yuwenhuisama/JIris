package org.irislang.jiris.core.exceptions

import org.irislang.jiris.core.IrisValue

/**
 * Created by Huisama on 2017/5/3 0003.
 */
class IrisRuntimeException(exceptionObject: IrisValue, fileName: String, lineNumber: Int) : IrisExceptionBase(fileName, lineNumber, "") {
    var exceptionObject: IrisValue? = null

    init {
        this.exceptionObject = exceptionObject
    }
}