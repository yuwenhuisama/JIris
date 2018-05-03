package org.irislang.jiris.compiler.assistpart

import org.irislang.jiris.compiler.IrisSyntaxUnit
import org.irislang.jiris.compiler.expression.IrisExpression

/**
 * Created by Huisama on 2017/4/17 0017.
 */
abstract class IrisTopExpressionApostropheBase : IrisSyntaxUnit() {
    abstract fun ToDirectExpression(foreExpression: IrisExpression, postExpression: IrisTopExpressionApostrophe?): IrisExpression
}
