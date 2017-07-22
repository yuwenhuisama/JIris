package org.irislang.jiris.core.exceptions.fatal;

import org.irislang.jiris.core.exceptions.IrisExceptionBase;

/**
 * Created by Huisama on 2017/5/3 0003.
 */
public abstract class IrisFatalException extends IrisExceptionBase {
    public IrisFatalException(String fileName, int lineNumber, String message) {
        super(fileName, lineNumber, message);
    }

    abstract public String GetFatalExceptionName();

    public String GetReportString() {
        StringBuilder builder = new StringBuilder();

        builder.append("<Irregular : ")
                .append(GetFatalExceptionName())
                .append(">")
                .append("\n  Irregular-happened Report : Oh! Master, a FATAL ERROR has happened and Iris is not " +
                        "clever and dosen't kown how to deal with it. Could you please cheak it yourself? \n")
                .append(">The Irregular name is ")
                .append(GetFatalExceptionName())
                .append("\n");

        if(getLineNumber() > 0) {
            builder.append(">and hanppend at line ")
                    .append(getLineNumber())
                    .append(" file ")
                    .append(getFileName())
                    .append("\n");
        }

        if(getMessage() != "") {
            builder.append(">Tip : ")
                    .append(getMessage())
                    .append("\n");
        }

        return builder.toString();
    }
}
