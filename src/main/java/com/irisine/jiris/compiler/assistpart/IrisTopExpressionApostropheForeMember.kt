package com.irisine.jiris.compiler.assistpart

import com.irisine.jiris.compiler.expression.IrisExpression
import com.irisine.jiris.compiler.expression.IrisMemberExpression

/**
 * Created by Huisama on 2017/5/2 0002.
 */
class IrisTopExpressionApostropheForeMember(val propery: IrisIdentifier) : IrisTopExpressionApostropheBase() {

    override fun ToDirectExpression(foreExpression: IrisExpression, postExpression: IrisTopExpressionApostrophe?): IrisExpression {
        val tmpExpr = IrisMemberExpression(foreExpression, propery)
        return if (postExpression == null) tmpExpr else postExpression.ToDirectExpression(tmpExpr)
    }
}
