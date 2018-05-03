package org.irislang.jiris.compiler.assistpart

import org.irislang.jiris.compiler.expression.IrisExpression
import org.irislang.jiris.compiler.expression.IrisMemberExpression

/**
 * Created by Huisama on 2017/5/2 0002.
 */
class IrisTopExpressionApostropheForeMember(val propery: IrisIdentifier) : IrisTopExpressionApostropheBase() {

    override fun ToDirectExpression(foreExpression: IrisExpression, postExpression: IrisTopExpressionApostrophe?): IrisExpression {
        val tmpExpr = IrisMemberExpression(foreExpression, propery)
        return if (postExpression == null) tmpExpr else postExpression.ToDirectExpression(tmpExpr)
    }
}
