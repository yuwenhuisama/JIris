package org.irislang.jiris.core.exceptions;

import org.irislang.jiris.core.IrisValue;

/**
 * Created by Huisama on 2017/5/3 0003.
 */
public class IrisRuntimeException extends IrisExceptionBase {
    private IrisValue m_exceptionObject = null;
    private String m_fileName = null;
    private int m_lineNumber = -1;

    public IrisRuntimeException(IrisValue exceptionObject, String fileName, int lineNumber) {
        m_exceptionObject = exceptionObject;
        m_fileName = fileName;
        m_lineNumber = lineNumber;
    }

    public IrisValue getExceptionObject() {
        return m_exceptionObject;
    }

    public void setExceptionObject(IrisValue exceptionObject) {
        m_exceptionObject = exceptionObject;
    }

    public String getFileName() {
        return m_fileName;
    }

    public void setFileName(String fileName) {
        m_fileName = fileName;
    }

    public int getLineNumber() {
        return m_lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        m_lineNumber = lineNumber;
    }

    @Override
    public String GetExceptionString() {
        return null;
    }
}