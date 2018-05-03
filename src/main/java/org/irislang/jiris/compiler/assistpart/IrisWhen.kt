package org.irislang.jiris.compiler.assistpart

import java.util.LinkedList

import org.irislang.jiris.compiler.expression.IrisExpression

class IrisWhen(val conditions: LinkedList<IrisExpression>, val whenBlock: IrisBlock)
