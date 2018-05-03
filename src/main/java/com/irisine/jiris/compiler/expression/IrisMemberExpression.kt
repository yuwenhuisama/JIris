package com.irisine.jiris.compiler.expression

import com.irisine.jiris.compiler.IrisCompiler
import com.irisine.jiris.compiler.IrisGenerateHelper
import com.irisine.jiris.compiler.assistpart.IrisIdentifier
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import org.irislang.jiris.IrisNativeJavaClass

/**
 * Created by Huisama on 2017/5/2 0002.
 */
class IrisMemberExpression(val caller: IrisExpression, val property: IrisIdentifier) : IrisExpression() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        val property = property.identifier
//        val builder = StringBuilder("__get_").append(property)

        val funcName = "__get_${property}"

        if (!caller.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        IrisGenerateHelper.CallMethod(visitor, currentCompiler, funcName, 0, false)

        return true
    }

    override fun LeftValue(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): IrisExpression.LeftValueResult {
        val result = IrisExpression.LeftValueResult()
        val property = property.identifier
//        val builder = StringBuilder("__set_").append(property)

        val funcName = "__set_${property}"

        if (!caller.Generate(currentCompiler, currentBuilder, visitor)) {
            result.result = false
            return result
        }

        result.identifier = funcName
        result.result = true
        result.type = IrisExpression.LeftValueType.MemberVariable
        return result
    }
}
