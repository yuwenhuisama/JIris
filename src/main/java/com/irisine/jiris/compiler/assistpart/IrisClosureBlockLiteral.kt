package com.irisine.jiris.compiler.assistpart

import java.util.LinkedList

import com.irisine.jiris.compiler.IrisSyntaxUnit
import com.irisine.jiris.compiler.statement.IrisStatement

class IrisClosureBlockLiteral(val parameters: LinkedList<IrisIdentifier>?, val variableParameter: IrisIdentifier?, val block: IrisBlock) : IrisSyntaxUnit()
