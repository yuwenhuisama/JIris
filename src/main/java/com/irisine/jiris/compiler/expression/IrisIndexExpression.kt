package com.irisine.jiris.compiler.expression

import com.irisine.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.IrisNativeJavaClass

import com.irisine.jiris.compiler.IrisCompiler

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor

class IrisIndexExpression(val target: IrisExpression, val indexer: IrisExpression) : IrisExpression() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                          visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        if (!indexer.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        IrisGenerateHelper.AddParameter(visitor, currentCompiler)

        if (!target.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        IrisGenerateHelper.CallMethod(visitor, currentCompiler, "[]", 1, false)

        IrisGenerateHelper.PopParameter(visitor, currentCompiler, 1)

        return true
    }

    override fun LeftValue(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                           visitor: MethodVisitor): IrisExpression.LeftValueResult {
        val result = IrisExpression.LeftValueResult()

        if (!indexer.Generate(currentCompiler, currentBuilder, visitor)) {
            result.result = false
            return result
        }

        IrisGenerateHelper.AddParameter(visitor, currentCompiler)

        if (!target.Generate(currentCompiler, currentBuilder, visitor)) {
            result.result = false
            return result
        }

        result.type = IrisExpression.LeftValueType.IndexVariable
        result.result = true

        return result
    }

}
