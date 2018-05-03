package com.irisine.jiris.compiler.expression

import java.util.LinkedList

import com.irisine.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.IrisNativeJavaClass

import com.irisine.jiris.compiler.IrisCompiler

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes

class IrisArrayExpression(val expressions: LinkedList<IrisExpression>?) : IrisExpression() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                          visitor: MethodVisitor): Boolean {

        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        if (expressions != null) {
            for (elem in expressions) {
                if (!elem.Generate(currentCompiler, currentBuilder, visitor)) {
                    return false
                }
                IrisGenerateHelper.AddParameter(visitor, currentCompiler)
            }

            IrisGenerateHelper.GetPartPrametersOf(visitor, currentCompiler, expressions.size)
        } else {
            visitor.visitInsn(Opcodes.ACONST_NULL)
        }

        IrisGenerateHelper.CreateArray(visitor, currentCompiler)

        if (expressions != null) {
            IrisGenerateHelper.PopParameter(visitor, currentCompiler, expressions.size)
        }

        return true
    }

}
