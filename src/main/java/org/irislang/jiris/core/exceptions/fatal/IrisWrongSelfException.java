package org.irislang.jiris.core.exceptions.fatal;

/**
 * Created by yuwen on 2017/7/20 0020.
 */
public class IrisWrongSelfException extends IrisFatalException {
    public IrisWrongSelfException(String fileName, int lineNumber, String message) {
        super(fileName, lineNumber, message);
    }

    @Override
    public String GetFatalExceptionName() {
        return "WrongSelfException";
    }
}
