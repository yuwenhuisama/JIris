package org.irislang.jiris.core.exceptions;

import org.irislang.jiris.core.IrisValue;

/**
 * Created by Huisama on 2017/5/3 0003.
 */
public class IrisRuntimeException extends IrisExceptionBase {
    private IrisValue m_exceptionObject = null;

    public IrisRuntimeException(IrisValue exceptionObject, String fileName, int lineNumber) {
        super(fileName, lineNumber, "");
        m_exceptionObject = exceptionObject;
    }

    public IrisValue getExceptionObject() {
        return m_exceptionObject;
    }

    public void setExceptionObject(IrisValue exceptionObject) {
        m_exceptionObject = exceptionObject;
    }
}