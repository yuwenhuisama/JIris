package com.irisine.jiris.compiler.statement

import com.irisine.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.IrisNativeJavaClass

import com.irisine.jiris.compiler.IrisCompiler
import com.irisine.jiris.compiler.expression.IrisExpression

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor


class IrisNormalStatement(val expression: IrisExpression) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                          visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        return expression.Generate(currentCompiler, currentBuilder, visitor)
    }

}
