package com.irisine.jiris.compiler.expression;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisGenerateHelper;
import com.irisine.jiris.compiler.IrisSyntaxUnit;
import com.irisine.jiris.compiler.assistpart.IrisTopExpressionApostrophe;
import com.irisine.jiris.compiler.assistpart.IrisTopExpressionApostropheForeIndex;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/4/17 0017.
 */
public class IrisTopExpression extends IrisSyntaxUnit {
    private IrisExpression m_expression = null;
    private IrisTopExpressionApostrophe m_expressionApostrophe = null;

    public IrisTopExpression(IrisExpression expression, IrisTopExpressionApostrophe expressionApostrophe) {
        m_expression = expression;
        m_expressionApostrophe = expressionApostrophe;
    }

    public IrisExpression ToDirectExpression() {
        if(m_expressionApostrophe == null) {
            return m_expression;
        }
        else {
            return m_expressionApostrophe.ToDirectExpression(m_expression);
        }
    }

}
