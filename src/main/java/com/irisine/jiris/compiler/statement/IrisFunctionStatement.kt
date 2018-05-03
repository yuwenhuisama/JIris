package com.irisine.jiris.compiler.statement

import com.irisine.jiris.compiler.IrisGenerateHelper
import com.irisine.jiris.compiler.parser.IrisParserDefineType
import org.irislang.jiris.IrisNativeJavaClass

import com.irisine.jiris.compiler.IrisCompiler
import com.irisine.jiris.compiler.assistpart.IrisBlock
import com.irisine.jiris.compiler.assistpart.IrisDeferredBlock
import com.irisine.jiris.compiler.assistpart.IrisFunctionHeader

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes

class IrisFunctionStatement(val functionHeader: IrisFunctionHeader,
                            val withBlock: IrisBlock?,
                            val withoutBlock: IrisBlock?,
                            val block: IrisBlock,
                            val defineType: IrisParserDefineType) : IrisStatement() {

    var blockNativeName = ""
        private set
    var withBlockNativeName = ""
        private set
    var withoutBlockNativeName = ""
        private set

    init {
        MakeDefferdBlock(IrisCompiler.INSTANCE, this.functionHeader, defineType)
    }

    private fun MakeDefferdBlock(currentCompiler: IrisCompiler, functionHeader: IrisFunctionHeader, defineType: IrisParserDefineType) {
        val buffer = StringBuilder()
        val buffer_with = StringBuilder()
        val buffer_without = StringBuilder()
        val functionName = functionHeader.functionName.identifier
        val isClassMethod = functionHeader.isClassMethod

        when (defineType) {
            IrisParserDefineType.Class, IrisParserDefineType.Module -> {
                buffer.append(functionName).append("$").append(currentCompiler.currentDefineName)
                buffer_with.append(buffer.toString())
                buffer_without.append(buffer.toString())
                if (isClassMethod) {
                    buffer.append("\$cls\$mth$")
                    buffer_with.append("\$cls\$mth\$with$")
                    buffer_without.append("\$cls\$mth\$without$")
                } else {
                    buffer.append("\$ins\$mth$")
                    buffer_with.append("\$ins\$mth\$with$")
                    buffer_without.append("\$ins\$mth\$without$")
                }
                buffer.append(currentCompiler.GetBlockNameCount(buffer.toString()))
                buffer_with.append(currentCompiler.GetBlockNameCount(buffer_with.toString()))
                buffer_without.append(currentCompiler.GetBlockNameCount(buffer_without.toString()))
            }
            IrisParserDefineType.Interface -> {
            }
            IrisParserDefineType.Normal -> {
                buffer.append(functionName)
                        .append("\$main\$mth$")
                        .append(currentCompiler.GetBlockNameCount(buffer.toString()))

                buffer_with.append(functionName)
                        .append("\$main\$mth\$with$")
                        .append(currentCompiler.GetBlockNameCount(buffer_with.toString()))

                buffer_without.append(functionName)
                        .append("\$main\$mth\$without$")
                        .append(currentCompiler.GetBlockNameCount(buffer_without.toString()))
            }
        }/* Error */

        blockNativeName = buffer.toString()
        withBlockNativeName = buffer_with.toString()
        withoutBlockNativeName = buffer_without.toString()

        currentCompiler.PushDeferredStatement(IrisDeferredBlock(block, blockNativeName))

        if (withBlock != null) {
            currentCompiler.PushDeferredStatement(IrisDeferredBlock(withBlock, withBlockNativeName))
            currentCompiler.PushDeferredStatement(IrisDeferredBlock(withoutBlock!!, withBlockNativeName))
        }
    }

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                          visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        val functionName = functionHeader.functionName.identifier
        val parameters = functionHeader.parameters
        val nativeName = blockNativeName

        // Preinvoke
        visitor.visitVarInsn(Opcodes.ALOAD, 0)
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)

        visitor.visitLdcInsn(nativeName)

        visitor.visitLdcInsn(functionName)

        if (parameters != null) {
            visitor.visitIntInsn(Opcodes.BIPUSH, parameters.size)
            visitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String")

            for (i in parameters.indices) {
                visitor.visitInsn(Opcodes.DUP)
                visitor.visitIntInsn(Opcodes.BIPUSH, i)
                visitor.visitLdcInsn(parameters[i].identifier)
                visitor.visitInsn(Opcodes.AASTORE)
            }

        } else {
            visitor.visitInsn(Opcodes.ACONST_NULL)
        }

        if (functionHeader.variableParameter != null) {
            visitor.visitLdcInsn(functionHeader.variableParameter.identifier)
        } else {
            visitor.visitInsn(Opcodes.ACONST_NULL)
        }

        if (withBlock != null) {
            visitor.visitLdcInsn(withBlockNativeName)
            visitor.visitLdcInsn(withoutBlockNativeName)
        } else {
            visitor.visitInsn(Opcodes.ACONST_NULL)
            visitor.visitInsn(Opcodes.ACONST_NULL)
        }

        visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod\$MethodAuthority",
                "Everyone", "Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;")
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())

        if (functionHeader.isClassMethod) {
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "DefineClassMethod",
                    "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false)
        } else {
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                    "DefineInstanceMethod", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false)
        }

        return true
    }

}
