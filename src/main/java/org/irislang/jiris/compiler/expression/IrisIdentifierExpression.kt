package org.irislang.jiris.compiler.expression

import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.IrisNativeJavaClass

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.assistpart.IrisIdentifier
import org.irislang.jiris.compiler.assistpart.IrisIdentifier.IdentifierType.*

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes

class IrisIdentifierExpression(val identifier: IrisIdentifier) : IrisExpression() {

    val identifierString: String
        get() = identifier.identifier

    override fun LeftValue(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>, visitor: MethodVisitor): IrisExpression.LeftValueResult {
        val result = IrisExpression.LeftValueResult()
        result.identifier = identifier.identifier
        result.result = true

        when (identifier.type) {
            ClassVariable -> result.type = IrisExpression.LeftValueType.ClassVariable
            Constance -> result.type = IrisExpression.LeftValueType.Constance
            GlobalVariable -> result.type = IrisExpression.LeftValueType.GlobalVariable
            InstanceVariable -> result.type = IrisExpression.LeftValueType.InstanceVariable
            LocalVariable -> result.type = IrisExpression.LeftValueType.LocalVariable
        }

        return result
    }

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        visitor.visitLdcInsn(identifier.identifier)
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())

        when (identifier.type) {
            LocalVariable -> visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                    currentCompiler.currentClassName,
                    "GetLocalVariable",
                    "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;",
                    false)
            ClassVariable -> visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                    currentCompiler.currentClassName,
                    "GetClassVariable",
                    "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;",
                    false)
            InstanceVariable -> visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                    currentCompiler.currentClassName,
                    "GetInstanceVariable",
                    "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;",
                    false)
            GlobalVariable -> visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                    currentCompiler.currentClassName,
                    "GetGlobalVariable",
                    "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;",
                    false)
            Constance -> visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                    currentCompiler.currentClassName,
                    "GetConstance",
                    "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;",
                    false)
        }

        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())

        return true
    }

}
