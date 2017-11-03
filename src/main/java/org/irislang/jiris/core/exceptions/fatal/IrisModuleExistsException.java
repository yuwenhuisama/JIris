package org.irislang.jiris.core.exceptions.fatal;

/**
 * Created by Huisama on 2017/5/25 0025.
 */
public class IrisModuleExistsException  extends IrisFatalException {
    public IrisModuleExistsException(String fileName, int lineNumber, String message) {
        super(fileName, lineNumber, message);
    }

    @Override
    public String GetFatalExceptionName() {
        return "ModuleExistsIrregular";
    }
}
