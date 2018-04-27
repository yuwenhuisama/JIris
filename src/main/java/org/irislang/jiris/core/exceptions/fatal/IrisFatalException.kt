package org.irislang.jiris.core.exceptions.fatal

import org.irislang.jiris.core.exceptions.IrisExceptionBase

/**
 * Created by Huisama on 2017/5/3 0003.
 */
abstract class IrisFatalException(fileName: String, lineNumber: Int, message: String) : IrisExceptionBase(fileName, lineNumber, message) {

    abstract fun GetFatalExceptionName(): String

    fun GetReportString(): String {
        val builder = StringBuilder()

        builder.append("<Irregular : ")
                .append(GetFatalExceptionName())
                .append(">")
                .append("\n  Irregular-happened Report : Oh! Master, a FATAL ERROR has happened and Iris is not " + "clever and dosen't kown how to deal with it. Could you please cheak it yourself? \n")
                .append(">The Irregular name is ")
                .append(GetFatalExceptionName())
                .append("\n")

        if (lineNumber > 0) {
            builder.append(">and hanppend at line ")
                    .append(lineNumber)
                    .append(" file ")
                    .append(fileName)
                    .append("\n")
        }

        if (message !== "") {
            builder.append(">Tip : ")
                    .append(message)
                    .append("\n")
        }

        return builder.toString()
    }
}
