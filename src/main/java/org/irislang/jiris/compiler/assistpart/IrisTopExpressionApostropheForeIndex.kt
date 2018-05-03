package org.irislang.jiris.compiler.assistpart

import org.irislang.jiris.compiler.expression.IrisExpression
import org.irislang.jiris.compiler.expression.IrisIndexExpression

/**
 * Created by Huisama on 2017/4/17 0017.
 */
class IrisTopExpressionApostropheForeIndex(val indexer: IrisExpression) : IrisTopExpressionApostropheBase() {
    override fun ToDirectExpression(foreExpression: IrisExpression, postExpression: IrisTopExpressionApostrophe?): IrisExpression {
        val tmpExpr = IrisIndexExpression(foreExpression, indexer)
        return if (postExpression == null) tmpExpr else postExpression.ToDirectExpression(tmpExpr)
    }
}
