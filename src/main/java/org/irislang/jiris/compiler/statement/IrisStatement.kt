package org.irislang.jiris.compiler.statement

import org.irislang.jiris.IrisNativeJavaClass

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.IrisSyntaxUnit

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor

abstract class IrisStatement : IrisSyntaxUnit() {
    var lineNumber = 0
    abstract fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean
}
