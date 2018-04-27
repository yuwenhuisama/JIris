package com.irisine.jiris.compiler.statement;
import org.irislang.jiris.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisSyntaxUnit;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;

public abstract class IrisStatement extends IrisSyntaxUnit {
    protected int m_lineNumber = 0;

    public int getLineNumber() {
        return m_lineNumber;
    }
    public void setLineNumber(int lineNumber) {
        this.m_lineNumber = lineNumber;
    }
    public abstract boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor);
}
