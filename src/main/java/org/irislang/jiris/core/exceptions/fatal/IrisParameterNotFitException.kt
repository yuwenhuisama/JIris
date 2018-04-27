package org.irislang.jiris.core.exceptions.fatal

/**
 * Created by Huisama on 2017/5/27 0027.
 */
class IrisParameterNotFitException(fileName: String, lineNumber: Int, message: String) : IrisFatalException(fileName, lineNumber, message) {

    override fun GetFatalExceptionName(): String {
        return "ParameterNotFitIrregular"
    }
}
