package org.irislang.jiris.core.exceptions.fatal;

import org.irislang.jiris.core.exceptions.IrisExceptionBase;

/**
 * Created by Huisama on 2017/5/26 0026.
 */
public class IrisVariableImpossiblyExistsException extends IrisFatalException {
    public IrisVariableImpossiblyExistsException(String fileName, int lineNumber, String message) {
        super(fileName, lineNumber, message);
    }

    @Override
    public String GetFatalExceptionName() {
        return "VariableImpossiblyIrregular";
    }
}
