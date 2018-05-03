package com.irisine.jiris.compiler.assistpart

import com.irisine.jiris.compiler.IrisSyntaxUnit

class IrisIdentifier(val type: IdentifierType, val identifier: String) : IrisSyntaxUnit() {
    var lineNumber = 0

    enum class IdentifierType {
        Constance,
        LocalVariable,
        GlobalVariable,
        InstanceVariable,
        ClassVariable
    }
}
