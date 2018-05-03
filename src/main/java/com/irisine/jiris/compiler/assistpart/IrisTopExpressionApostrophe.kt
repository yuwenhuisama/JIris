package com.irisine.jiris.compiler.assistpart

import com.irisine.jiris.compiler.IrisSyntaxUnit
import com.irisine.jiris.compiler.expression.IrisExpression

/**
 * Created by Huisama on 2017/4/17 0017.
 */
class IrisTopExpressionApostrophe(val foreExpression: IrisTopExpressionApostropheBase?, val postExpression: IrisTopExpressionApostrophe?) : IrisSyntaxUnit() {

    fun ToDirectExpression(foreExpression: IrisExpression): IrisExpression {
        return this.foreExpression!!.ToDirectExpression(foreExpression, postExpression)
    }
}
