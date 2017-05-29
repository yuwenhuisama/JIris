package org.irislang.jiris.core.exceptions.fatal;

/**
 * Created by Huisama on 2017/5/26 0026.
 */
public class IrisTypeNotCorretException extends IrisFatalException {
    public IrisTypeNotCorretException(String fileName, int lineNumber, String message) {
        super(fileName, lineNumber, message);
    }

    @Override
    public String GetFatalExceptionName() {
        return "TypeNotCorrectIrregular";
    }
}
