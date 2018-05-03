package org.irislang.jiris.compiler.assistpart

import org.irislang.jiris.compiler.IrisSyntaxUnit

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
