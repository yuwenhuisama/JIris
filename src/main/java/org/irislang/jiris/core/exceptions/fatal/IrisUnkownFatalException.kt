package org.irislang.jiris.core.exceptions.fatal

/**
 * Created by Huisama on 2017/5/4 0004.
 */
class IrisUnkownFatalException(fileName: String, lineNumber: Int, message: String) : IrisFatalException(fileName, lineNumber, message) {

    override fun GetFatalExceptionName(): String {
        return "UnkownIrregular"
    }
}