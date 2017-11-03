package com.irisine.jiris.compiler.assistpart;

import com.irisine.jiris.compiler.expression.IrisExpression;
import com.irisine.jiris.compiler.expression.IrisMemberExpression;

/**
 * Created by Huisama on 2017/5/2 0002.
 */
public class IrisTopExpressionApostropheForeMember extends IrisTopExpressionApostropheBase {

    IrisIdentifier m_propery = null;

    public IrisTopExpressionApostropheForeMember(IrisIdentifier propery) {
        m_propery = propery;
    }

    @Override
    public IrisExpression ToDirectExpression(IrisExpression foreExpression, IrisTopExpressionApostrophe postExpression) {
        IrisExpression tmpExpr = new IrisMemberExpression(foreExpression, m_propery);
        return postExpression == null ? tmpExpr : postExpression.ToDirectExpression(tmpExpr);
    }
}
