package com.irisine.jiris.compiler.statement

import com.irisine.jiris.compiler.IrisCompiler
import com.irisine.jiris.compiler.IrisGenerateHelper
import com.irisine.jiris.compiler.assistpart.IrisBlock
import com.irisine.jiris.compiler.assistpart.IrisDeferredBlock
import com.irisine.jiris.compiler.assistpart.IrisIdentifier
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

/**
 * Created by Huisama on 2017/5/2 0002.
 */
class IrisGetterStatement(val getteredVariable: IrisIdentifier, val block: IrisBlock?) : IrisStatement() {
    private var nativeBlockName: String? = null
    private var functionName: String

    init {
        val functionNameBuffer = StringBuilder()
        functionName = functionNameBuffer.append("__get_")
                .append(getteredVariable.identifier
                        .substring(1, getteredVariable.identifier.length))
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

            // null
            visitor.visitInsn(Opcodes.ACONST_NULL)

            // variable param
            visitor.visitInsn(Opcodes.ACONST_NULL)

            // with
            visitor.visitInsn(Opcodes.ACONST_NULL)
            // without
            visitor.visitInsn(Opcodes.ACONST_NULL)

            // authority
            visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod\$MethodAuthority",
                    "Everyone", "Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;")

            // contex
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())

            // thread info
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())

            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                    "DefineInstanceMethod",
                    "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false)
        } else {
            visitor.visitLdcInsn(functionName)
            visitor.visitLdcInsn(getteredVariable.identifier)
            visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod\$MethodAuthority",
                    "Everyone", "Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;")
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                    "DefineDefaultGetter",
                    "(Ljava/lang/String;" +
                    "Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false)

        }

        return true
    }
}
