package org.irislang.jiris.compiler.statement

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.compiler.assistpart.IrisIdentifier
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

/**
 * Created by Huisama on 2017/5/2 0002.
 */
class IrisGSetterStatement(val variableName: IrisIdentifier) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        val functionNameBuffer = StringBuilder()
        val getterFunctionName = functionNameBuffer.append("__get_")
                .append(variableName.identifier
                        .substring(1, variableName.identifier.length))
                .toString()
        functionNameBuffer.delete(0, functionNameBuffer.length)

        val setterFunctionName = functionNameBuffer.append("__set_")
                .append(variableName.identifier
                        .substring(1, variableName.identifier.length))
                .toString()

        // Getter
        visitor.visitLdcInsn(getterFunctionName)
        visitor.visitLdcInsn(variableName.identifier)
        visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod\$MethodAuthority",
                "Everyone", "Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;")
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                "DefineDefaultGetter", "" +
                "(Ljava/lang/String;" +
                "Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false)
        // Setter
        visitor.visitLdcInsn(setterFunctionName)
        visitor.visitLdcInsn(variableName.identifier)
        visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod\$MethodAuthority",
                "Everyone", "Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;")
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "DefineDefaultSetter",
                "(Ljava/lang/String;" +
                "Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false)

        return true
    }
}
