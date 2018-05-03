package org.irislang.jiris.compiler.assistpart

import org.irislang.jiris.compiler.expression.IrisExpression
import org.irislang.jiris.compiler.expression.IrisFunctionCallExpression

import java.util.LinkedList

/**
 * Created by Huisama on 2017/4/17 0017.
 */
class IrisTopExpressionApostropheForeNormalCall(val functionName: IrisIdentifier,
                                                val parameters: LinkedList<IrisExpression>?,
                                                val closureBlockLiteral: IrisClosureBlockLiteral?)
    : IrisTopExpressionApostropheBase() {

    override fun ToDirectExpression(foreExpression: IrisExpression, postExpression: IrisTopExpressionApostrophe?): IrisExpression {
        val tmpExpr = IrisFunctionCallExpression(foreExpression, functionName, parameters, closureBlockLiteral)
        return if (postExpression == null) tmpExpr else postExpression.ToDirectExpression(tmpExpr)
    }
}
