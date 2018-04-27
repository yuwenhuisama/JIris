package org.irislang.jiris.core.exceptions.fatal

/**
 * Created by Huisama on 2017/5/26 0026.
 */
class IrisTypeNotCorretException(fileName: String, lineNumber: Int, message: String) : IrisFatalException(fileName, lineNumber, message) {

    override fun GetFatalExceptionName(): String {
        return "TypeNotCorrectIrregular"
    }
}
