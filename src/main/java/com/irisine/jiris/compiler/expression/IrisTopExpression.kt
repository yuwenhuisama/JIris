package com.irisine.jiris.compiler.expression

import com.irisine.jiris.compiler.IrisSyntaxUnit
import com.irisine.jiris.compiler.assistpart.IrisTopExpressionApostrophe

/**
 * Created by Huisama on 2017/4/17 0017.
 */
class IrisTopExpression(val expression: IrisExpression, val expressionApostrophe: IrisTopExpressionApostrophe?) : IrisSyntaxUnit() {
    fun ToDirectExpression(): IrisExpression? {
        return if (expressionApostrophe == null) {
            expression
        } else {
            expressionApostrophe.ToDirectExpression(expression)
        }
    }

}
