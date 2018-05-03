package org.irislang.jiris.compiler.expression

import org.irislang.jiris.IrisNativeJavaClass

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.IrisSyntaxUnit
import org.irislang.jiris.compiler.expression.IrisExpression.LeftValueResult

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor

abstract class IrisExpression : IrisSyntaxUnit() {

    var lineNumber = 0

    enum class LeftValueType {
        Constance,
        GlobalVariable,
        ClassVariable,
        InstanceVariable,
        LocalVariable,
        MemberVariable,
        IndexVariable
    }

    class LeftValueResult {
        var type = LeftValueType.LocalVariable
        var result = false
        var identifier = ""
    }

    abstract fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean

    open fun LeftValue(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>, visitor: MethodVisitor): LeftValueResult {
        return LeftValueResult()
    }
}
