package org.irislang.jiris.compiler.expression

import java.util.LinkedList

import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.compiler.assistpart.IrisDeferredBlock
import net.bytebuddy.jar.asm.Type
import org.irislang.jiris.IrisNativeJavaClass

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.assistpart.IrisClosureBlockLiteral
import org.irislang.jiris.compiler.assistpart.IrisIdentifier

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes

class IrisFunctionCallExpression(val `object`: IrisExpression?,
                                 val functionName: IrisIdentifier,
                                 val parameters: LinkedList<IrisExpression>?,
                                 val closureBlock: IrisClosureBlockLiteral?) : IrisExpression() {

    private var m_nativeName: String? = null

    init {
        if (closureBlock != null) {
            MakeDefferedBlock(IrisCompiler.INSTANCE)
        }
    }

    private fun MakeDefferedBlock(currentCompiler: IrisCompiler) {
        val buffer = StringBuilder()
        buffer.append("block$").append(currentCompiler.GetBlockNameCount("block$"))
        m_nativeName = buffer.toString()
        currentCompiler.PushDeferredStatement(IrisDeferredBlock(closureBlock!!.block, m_nativeName!!))
    }

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                          visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        // create closure block if existing
        if (closureBlock != null) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
            //visitor.visitInsn(Opcodes.ICONST_3);

            if (closureBlock.parameters != null) {
                IrisGenerateHelper.LoadInteger(visitor, closureBlock.parameters.size)
                visitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String")

                for (i in 0 until closureBlock.parameters.size) {
                    visitor.visitInsn(Opcodes.DUP)
                    IrisGenerateHelper.LoadInteger(visitor, i)
                    visitor.visitLdcInsn(closureBlock.parameters[i].identifier)
                    visitor.visitInsn(Opcodes.AASTORE)
                }
            } else {
                visitor.visitInsn(Opcodes.ACONST_NULL)
            }

            if (closureBlock.variableParameter != null) {
                visitor.visitLdcInsn(closureBlock.variableParameter.identifier)
            } else {
                visitor.visitInsn(Opcodes.ACONST_NULL)
            }

            visitor.visitLdcInsn(Type.getType("L" + currentCompiler.currentClassName + ";"))
            visitor.visitLdcInsn(m_nativeName)
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                    "CreateClosureBlock",
                    "(Lorg/irislang/jiris/core/IrisContextEnvironment;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;)Lorg/irislang/jiris/core/IrisValue;", false)
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
        }

        var pushedCount = 0

        if (parameters != null) {
            for (expression in parameters) {
                if (!expression.Generate(currentCompiler, currentBuilder, visitor)) {
                    return false
                }
                IrisGenerateHelper.AddParameter(visitor, currentCompiler)
            }
            pushedCount = parameters.size
        }

        if (`object` == null) {
            IrisGenerateHelper.CallMethod(visitor, currentCompiler, functionName.identifier, pushedCount, true)
        } else {
            if (!`object`.Generate(currentCompiler, currentBuilder, visitor)) {
                return false
            }
            IrisGenerateHelper.CallMethod(visitor, currentCompiler, functionName.identifier, pushedCount, false)
        }

        if (pushedCount > 0) {
            IrisGenerateHelper.PopParameter(visitor, currentCompiler, pushedCount)
        }

        if (closureBlock != null) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                    "ClearClosureBlock",
                    "(Lorg/irislang/jiris/core/IrisThreadInfo;)V", false)
        }

        return true
    }

}
