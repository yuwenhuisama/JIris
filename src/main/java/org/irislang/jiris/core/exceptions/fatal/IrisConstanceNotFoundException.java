package org.irislang.jiris.core.exceptions.fatal;

import org.irislang.jiris.core.exceptions.IrisRuntimeException;

/**
 * Created by yuwen on 2017/7/6 0006.
 */
public class IrisConstanceNotFoundException extends IrisFatalException {
    public IrisConstanceNotFoundException(String fileName, int lineNumber, String message) {
        super(fileName, lineNumber, message);
    }

    @Override
    public String GetFatalExceptionName() {
        return "ConstanceNotFoundIrregular";
    }
}
