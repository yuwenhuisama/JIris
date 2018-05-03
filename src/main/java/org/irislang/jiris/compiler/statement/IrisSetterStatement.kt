package org.irislang.jiris.compiler.statement

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.compiler.assistpart.IrisBlock
import org.irislang.jiris.compiler.assistpart.IrisDeferredBlock
import org.irislang.jiris.compiler.assistpart.IrisIdentifier
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

/**
 * Created by Huisama on 2017/5/2 0002.
 */
class IrisSetterStatement(val setteredVariable: IrisIdentifier,
                          val paramName: IrisIdentifier,
                          val block: IrisBlock?) : IrisStatement() {

    private var nativeBlockName: String? = null
    private var functionName: String

    init {

        val functionNameBuffer = StringBuilder()
        functionName = functionNameBuffer.append("__set_")
                .append(this.setteredVariable.identifier
                        .substring(1, this.setteredVariable.identifier.length))
                .toString()

        // Make deferred block
        if (this.block != null) {
            val buffer = StringBuilder()

            buffer.append(functionName)
                    .append("\$ins\$mth$")
                    .append(IrisCompiler.INSTANCE.GetBlockNameCount(functionName))

            nativeBlockName = buffer.toString()

            IrisCompiler.INSTANCE.PushDeferredStatement(IrisDeferredBlock(this.block, nativeBlockName!!))
        }
    }

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        if (block != null) {
            // Preinvoke
            visitor.visitVarInsn(Opcodes.ALOAD, 0)
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)

            // native block name
            visitor.visitLdcInsn(nativeBlockName)

            // function name
            visitor.visitLdcInsn(functionName)

            // new String()
            visitor.visitIntInsn(Opcodes.BIPUSH, 1)
            visitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String")

            // array[0] = param name
            visitor.visitInsn(Opcodes.DUP)
            visitor.visitIntInsn(Opcodes.BIPUSH, 0)
            visitor.visitLdcInsn(paramName!!.identifier)
            visitor.visitInsn(Opcodes.AASTORE)

            // variable param
            visitor.visitInsn(Opcodes.ACONST_NULL)

            // with
            visitor.visitInsn(Opcodes.ACONST_NULL)
            // without
            visitor.visitInsn(Opcodes.ACONST_NULL)

            // authority
            visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod\$MethodAuthority",
                    "Everyone", "Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;")

            // context
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())

            // thread info
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())

            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                    "DefineInstanceMethod",
                    "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false)
        } else {
            visitor.visitLdcInsn(functionName)
            visitor.visitLdcInsn(setteredVariable!!.identifier)
            visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod\$MethodAuthority",
                    "Everyone", "Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;")
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                    "DefineDefaultSetter",
                    "(Ljava/lang/String;" +
                    "Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false)

        }

        return true
    }
}
