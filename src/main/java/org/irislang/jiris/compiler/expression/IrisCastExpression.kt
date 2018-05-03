package org.irislang.jiris.compiler.expression

import org.irislang.jiris.compiler.IrisCompiler
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

/**
 * Created by yuwen on 2017/7/16 0016.
 */
class IrisCastExpression : IrisExpression() {
    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                "GetCastObject",
                "(Lorg/irislang/jiris/core/IrisThreadInfo;)Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())

        return true
    }
}
