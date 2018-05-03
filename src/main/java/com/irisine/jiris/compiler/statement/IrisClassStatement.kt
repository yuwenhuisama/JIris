package com.irisine.jiris.compiler.statement

import com.irisine.jiris.compiler.IrisCompiler
import com.irisine.jiris.compiler.IrisGenerateHelper
import com.irisine.jiris.compiler.assistpart.IrisBlock
import com.irisine.jiris.compiler.assistpart.IrisIdentifier
import com.irisine.jiris.compiler.expression.IrisExpression
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

import java.util.LinkedList

/**
 * Created by Huisama on 2017/4/12 0012.
 */
class IrisClassStatement(val className: IrisIdentifier,
                         val superClassName: IrisExpression?,
                         val modules: LinkedList<IrisExpression>?,
                         val interfaces: LinkedList<IrisExpression>?,
                         val block: IrisBlock) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        if (superClassName != null) {
            if (!superClassName.Generate(currentCompiler, currentBuilder, visitor)) {
                return false
            }
            // threadInfo.SetTempClass()
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                    "SetTempSuperClass", "(Lorg/irislang/jiris/core/IrisValue;)V", false)
        }

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

        if (interfaces != null) {
            for (expr in interfaces) {
                if (!expr.Generate(currentCompiler, currentBuilder, visitor)) {
                    return false
                }
                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                        "AddTempInterface", "(Lorg/irislang/jiris/core/IrisValue;)V", false)
            }
        }

        val preType = currentCompiler.currentDefineType
        currentCompiler.currentDefineType = IrisCompiler.CurrentDefineType.Class

        // push environment
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "PushContext", "(Lorg/irislang/jiris/core/IrisContextEnvironment;)V", false)

        visitor.visitLdcInsn(className.identifier)
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "DefineClass",
                "(Ljava/lang/String;" +
                        "Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)Lorg/irislang/jiris/core/IrisContextEnvironment;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfContextVar())

        // store object
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisContextEnvironment",
                "getRunningType", "()Lorg/irislang/jiris/core/IrisRunningObject;", false)
        visitor.visitTypeInsn(Opcodes.CHECKCAST, "org/irislang/jiris/core/IrisClass")
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisClass",
                "getClassObject", "()Lorg/irislang/jiris/core/IrisObject;", false)
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/core/IrisValue",
                "WrapObject", "(Lorg/irislang/jiris/core/IrisObject;)Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())

        if (superClassName != null) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "SetSuperClass",
                    "(Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false)
        }

        if (modules != null) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "AddModule",
                    "(Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false)
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                    "ClearTempModules", "()V", false)
        }

        if (interfaces != null) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "AddInterface",
                    "(Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false)
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                    "ClearTempInterfaces", "()V", false)
        }

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
