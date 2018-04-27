package org.irislang.jiris.core.exceptions.fatal

import org.irislang.jiris.core.exceptions.IrisExceptionBase

/**
 * Created by Huisama on 2017/5/26 0026.
 */
class IrisVariableImpossiblyExistsException(fileName: String, lineNumber: Int, message: String) : IrisFatalException(fileName, lineNumber, message) {

    override fun GetFatalExceptionName(): String {
        return "VariableImpossiblyIrregular"
    }
}
