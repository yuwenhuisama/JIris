package com.irisine.jiris.compiler.assistpart

import java.util.LinkedList

import com.irisine.jiris.compiler.IrisSyntaxUnit

class IrisFunctionHeader(val functionName: IrisIdentifier,
                         val parameters: LinkedList<IrisIdentifier>?,
                         val variableParameter: IrisIdentifier?,
                         val isClassMethod: Boolean) : IrisSyntaxUnit() {
    var lineNumber = -1
}
