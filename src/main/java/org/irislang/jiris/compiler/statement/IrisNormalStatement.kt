package org.irislang.jiris.compiler.statement

import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.IrisNativeJavaClass

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.expression.IrisExpression

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor


class IrisNormalStatement(val expression: IrisExpression) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                          visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        return expression.Generate(currentCompiler, currentBuilder, visitor)
    }

}
