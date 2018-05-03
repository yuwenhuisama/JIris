package org.irislang.jiris.compiler.statement

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.assistpart.IrisIdentifier
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

/**
 * Created by Huisama on 2017/5/30 0030.
 */
class IrisAuthorityStatement(val name: IrisIdentifier, val target: Target, val authority: Authority) : IrisStatement() {

    enum class Target {
        InstanceMethod,
        ClassMethod
    }

    enum class Authority {
        Everyone,
        Relative,
        Personal
    }

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {

        visitor.visitLdcInsn(name.identifier)
        when (authority) {
            IrisAuthorityStatement.Authority.Everyone -> visitor.visitFieldInsn(Opcodes.GETSTATIC,
                    "org/irislang/jiris/core/IrisMethod\$MethodAuthority",
                    "Everyone", "Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;")
            IrisAuthorityStatement.Authority.Relative -> visitor.visitFieldInsn(Opcodes.GETSTATIC,
                    "org/irislang/jiris/core/IrisMethod\$MethodAuthority",
                    "Relative", "Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;")
            IrisAuthorityStatement.Authority.Personal -> visitor.visitFieldInsn(Opcodes.GETSTATIC,
                    "org/irislang/jiris/core/IrisMethod\$MethodAuthority",
                    "Personal", "Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;")
        }
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())

        if (target == Target.ClassMethod) {
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "SetClassMethodAuthority",
                    "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;" +
                            "Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;" +
                            ")V", false)
        } else {
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName,
                    "SetInstanceMethodAuthority",
                    "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod\$MethodAuthority;" +
                            "Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;" +
                            ")V", false)
        }

        return true
    }
}
