package org.irislang.jiris.core.exceptions;

/**
 * Created by Huisama on 2017/5/3 0003.
 */
abstract public class IrisExceptionBase extends Exception {
    private String m_fileName = null;
    private int m_lineNumber = -1;

    public IrisExceptionBase(String fileName, int lineNumber, String message) {
        super(message);
        m_fileName = fileName;
        m_lineNumber = lineNumber;
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
}
