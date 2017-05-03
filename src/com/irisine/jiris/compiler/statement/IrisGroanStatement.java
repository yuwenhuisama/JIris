package com.irisine.jiris.compiler.parser;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.expression.IrisExpression;
import com.irisine.jiris.compiler.statement.IrisStatement;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/5/3 0003.
 */
public class IrisGroanStatement extends IrisStatement {
    private IrisExpression m_expression = null;

    public IrisGroanStatement(IrisExpression expression) {
        m_expression = expression;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        return true;
    }
}
