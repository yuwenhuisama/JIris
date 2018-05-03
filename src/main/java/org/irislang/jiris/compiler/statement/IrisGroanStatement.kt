package org.irislang.jiris.compiler.statement

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.compiler.expression.IrisExpression
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

/**
 * Created by Huisama on 2017/5/3 0003.
 */
class IrisGroanStatement(val expression: IrisExpression) : IrisStatement() {
    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>,
                          visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        if (!expression.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        visitor.visitTypeInsn(Opcodes.NEW, "org/irislang/jiris/core/exceptions/IrisRuntimeException")
        visitor.visitInsn(Opcodes.DUP)
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "getCurrentFileName", "()Ljava/lang/String;", false)
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "org/irislang/jiris/core/IrisThreadInfo", "getCurrentLineNumber", "()I", false)
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                "org/irislang/jiris/core/exceptions/IrisRuntimeException", "<init>",
                "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;I)V", false)

        visitor.visitInsn(Opcodes.ATHROW)

        return true
    }
}
