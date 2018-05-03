package com.irisine.jiris.compiler.assistpart

import com.irisine.jiris.compiler.IrisSyntaxUnit
import com.irisine.jiris.compiler.expression.IrisExpression

/**
 * Created by Huisama on 2017/4/17 0017.
 */
abstract class IrisTopExpressionApostropheBase : IrisSyntaxUnit() {
    abstract fun ToDirectExpression(foreExpression: IrisExpression, postExpression: IrisTopExpressionApostrophe?): IrisExpression
}
