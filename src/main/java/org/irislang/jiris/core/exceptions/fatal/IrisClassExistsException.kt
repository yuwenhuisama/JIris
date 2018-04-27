package org.irislang.jiris.core.exceptions.fatal

/**
 * Created by Huisama on 2017/5/25 0025.
 */
class IrisClassExistsException(fileName: String, lineNumber: Int, message: String) : IrisFatalException(fileName, lineNumber, message) {

    override fun GetFatalExceptionName(): String {
        return "ClassExistsIrregular"
    }
}
