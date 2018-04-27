package org.irislang.jiris.core.exceptions.fatal

/**
 * Created by yuwen on 2017/7/20 0020.
 */
class IrisWrongSelfException(fileName: String, lineNumber: Int, message: String) : IrisFatalException(fileName, lineNumber, message) {

    override fun GetFatalExceptionName(): String {
        return "WrongSelfException"
    }
}
