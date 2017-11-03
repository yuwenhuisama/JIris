package com.irisine.jiris.compiler.assistpart;

import com.irisine.jiris.compiler.expression.IrisExpression;
import com.irisine.jiris.compiler.expression.IrisFunctionCallExpression;

import java.util.LinkedList;

/**
 * Created by Huisama on 2017/4/17 0017.
 */
public class IrisTopExpressionApostropheForeNormalCall extends IrisTopExpressionApostropheBase {

    private IrisIdentifier m_functionName = null;
    private LinkedList<IrisExpression> m_parameters = null;
    private IrisClosureBlockLiteral m_closureBlockLiteral = null;

    public IrisTopExpressionApostropheForeNormalCall(IrisIdentifier functionName, LinkedList<IrisExpression>
            prameters, IrisClosureBlockLiteral closureBlockLiteral) {
        m_functionName = functionName;
        m_parameters = prameters;
        m_closureBlockLiteral = closureBlockLiteral;
    }

    @Override
    public IrisExpression ToDirectExpression(IrisExpression foreExpression, IrisTopExpressionApostrophe postExpression) {
        IrisExpression tmpExpr = new IrisFunctionCallExpression(foreExpression, m_functionName, m_parameters, m_closureBlockLiteral);
        return postExpression == null ? tmpExpr : postExpression.ToDirectExpression(tmpExpr);
    }
}
