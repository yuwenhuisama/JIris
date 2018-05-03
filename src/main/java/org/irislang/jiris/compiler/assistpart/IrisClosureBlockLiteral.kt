package org.irislang.jiris.compiler.assistpart

import java.util.LinkedList

import org.irislang.jiris.compiler.IrisSyntaxUnit
import org.irislang.jiris.compiler.assistpart.IrisIdentifier

class IrisClosureBlockLiteral(val parameters: LinkedList<IrisIdentifier>?, val variableParameter: IrisIdentifier?, val block: IrisBlock) : IrisSyntaxUnit()
