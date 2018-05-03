package org.irislang.jiris.compiler.statement

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.compiler.assistpart.IrisBlock
import org.irislang.jiris.compiler.assistpart.IrisIdentifier
import org.irislang.jiris.compiler.expression.IrisExpression
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

import java.util.LinkedList

/**
 * Created by Huisama on 2017/4/26 0026.
 */
class IrisModuleStatement(val moduleName: IrisIdentifier,
                          val modules: LinkedList<IrisExpression>?,
                          val block: IrisBlock) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        if (modules != null) {
            for (expr in modules) {
                if (!expr.Generate(currentCompiler, currentBuilder, visitor)) {
                    return false
                }

                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                        "AddTempModule", "(Lorg/irislang/jiris/core/IrisValue;)V", false)
            }
        }

        val preType = currentCompiler.currentDefineType
        currentCompiler.currentDefineType = IrisCompiler.CurrentDefineType.Module

        // push environment
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "PushContext", "(Lorg/irislang/jiris/core/IrisContextEnvironment;)V", false)

        visitor.visitLdcInsn(moduleName.identifier)
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "DefineModule", "" +
                "(Ljava/lang/String;" +
                "Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)Lorg/irislang/jiris/core/IrisContextEnvironment;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfContextVar())

        // store object
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisContextEnvironment",
                "getRunningType", "()Lorg/irislang/jiris/core/IrisRunningObject;", false)
        visitor.visitTypeInsn(Opcodes.CHECKCAST, "org/irislang/jiris/core/IrisModule")
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisModule",
                "getModuleObject", "" +
                "()" +
                "Lorg/irislang/jiris/core/IrisObject;", false)
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/core/IrisValue",
                "WrapObject", "(Lorg/irislang/jiris/core/IrisObject;)Lorg/irislang/jiris/core/IrisValue;",
                false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())

        if (!block.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        // pop environment
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "PopContext", "()Lorg/irislang/jiris/core/IrisContextEnvironment;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfContextVar())

        currentCompiler.currentDefineType = preType

        return true
    }
}
