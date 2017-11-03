package org.irislang.jiris.core.exceptions.fatal;

/**
 * Created by Huisama on 2017/5/4 0004.
 */
public class IrisUnkownFatalException extends IrisFatalException {
    public IrisUnkownFatalException(String fileName, int lineNumber, String message) {
        super(fileName, lineNumber, message);
    }

    @Override
    public String GetFatalExceptionName() {
        return "UnkownIrregular";
    }
}