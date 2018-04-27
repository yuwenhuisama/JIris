package org.irislang.jiris.core.exceptions

/**
 * Created by Huisama on 2017/5/3 0003.
 */
abstract class IrisExceptionBase(fileName: String, lineNumber: Int, message: String) : Exception(message) {
    var fileName: String? = null
    var lineNumber = -1

    init {
        this.fileName = fileName
        this.lineNumber = lineNumber
    }
}
