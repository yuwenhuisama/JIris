package com.irisine.jiris.compiler.assistpart;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisSyntaxUnit;
import com.irisine.jiris.compiler.expression.IrisExpression;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/4/17 0017.
 */
public class IrisTopExpressionApostrophe extends IrisSyntaxUnit {
    private IrisTopExpressionApostropheBase m_foreExpression = null;
    private IrisTopExpressionApostrophe m_postExpression = null;

    public IrisTopExpressionApostrophe(IrisTopExpressionApostropheBase foreExpression, IrisTopExpressionApostrophe postExpression) {
        m_foreExpression = foreExpression;
        m_postExpression = postExpression;
    }

    public IrisExpression ToDirectExpression(IrisExpression foreExpression) {
        return m_foreExpression.ToDirectExpression(foreExpression, m_postExpression);
    }
}
