package org.irislang.jiris.compiler.assistpart

import org.irislang.jiris.compiler.IrisSyntaxUnit
import org.irislang.jiris.compiler.expression.IrisExpression

/**
 * Created by Huisama on 2017/4/17 0017.
 */
class IrisTopExpressionApostrophe(val foreExpression: IrisTopExpressionApostropheBase?, val postExpression: IrisTopExpressionApostrophe?) : IrisSyntaxUnit() {

    fun ToDirectExpression(foreExpression: IrisExpression): IrisExpression {
        return this.foreExpression!!.ToDirectExpression(foreExpression, postExpression)
    }
}
