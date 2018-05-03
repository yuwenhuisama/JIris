package com.irisine.jiris.compiler.assistpart

import java.util.LinkedList

import com.irisine.jiris.compiler.expression.IrisExpression

class IrisWhen(val conditions: LinkedList<IrisExpression>, val whenBlock: IrisBlock)
