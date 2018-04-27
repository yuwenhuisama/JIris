package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/5/30 0030.
 */
public class IrisAuthorityStatement extends IrisStatement {
    public enum Target {
        InstanceMethod,
        ClassMethod,
    }

    public enum Authority {
        Everyone,
        Relative,
        Personal,
    }

    private IrisIdentifier m_name = null;
    private Target m_target = Target.InstanceMethod;
    private Authority m_authority = Authority.Everyone;

    public IrisAuthorityStatement(IrisIdentifier name, Target target, Authority authority) {
        m_name = name;
        m_target = target;
        m_authority = authority;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {

        visitor.visitLdcInsn(m_name.getIdentifier());
        switch (m_authority) {
            case Everyone:
                visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod$MethodAuthority",
                        "Everyone", "Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;");
                break;
            case Relative:
                visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod$MethodAuthority",
                        "Relative", "Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;");
                break;
            case Personal:
                visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod$MethodAuthority",
                        "Personal", "Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;");
                break;
        }
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());

        if(m_target == Target.ClassMethod) {
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetClassMethodAuthority",
                    "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;" +
                            "Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;" +
                            ")V", false);
        }
        else {
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(),
                    "SetInstanceMethodAuthority",
                    "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;" +
                            "Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;" +
                            ")V", false);
        }

        return true;
    }
}
