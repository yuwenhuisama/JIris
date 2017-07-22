package org.irislang.jiris.core.exceptions.fatal;

/**
 * Created by Huisama on 2017/5/27 0027.
 */
public class IrisParameterNotFitException extends IrisFatalException {
    public IrisParameterNotFitException(String fileName, int lineNumber, String message) {
        super(fileName, lineNumber, message);
    }

    @Override
    public String GetFatalExceptionName() {
        return "ParameterNotFitIrregular";
    }
}
