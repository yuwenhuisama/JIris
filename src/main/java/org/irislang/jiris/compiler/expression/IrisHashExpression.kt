package org.irislang.jiris.compiler.expression

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.compiler.assistpart.IrisHashPair
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

import java.util.LinkedList

/**
 * Created by Huisama on 2017/4/8 0008.
 */
class IrisHashExpression(val hashPairs: LinkedList<IrisHashPair>?) : IrisExpression() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        if (hashPairs != null) {
            for (hashPair in hashPairs) {
                if (!hashPair.key.Generate(currentCompiler, currentBuilder, visitor)) {
                    return false
                }
                IrisGenerateHelper.AddParameter(visitor, currentCompiler)

                if (!hashPair.value.Generate(currentCompiler, currentBuilder, visitor)) {
                    return false
                }
                IrisGenerateHelper.AddParameter(visitor, currentCompiler)
            }

            IrisGenerateHelper.GetPartPrametersOf(visitor, currentCompiler, hashPairs.size * 2)
        } else {
            visitor.visitInsn(Opcodes.ACONST_NULL)
        }

        IrisGenerateHelper.CreateHash(visitor, currentCompiler)

        if (hashPairs != null) {
            IrisGenerateHelper.PopParameter(visitor, currentCompiler, hashPairs.size * 2)
        }

        return true
    }
}
