package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisGenerateHelper;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/5/2 0002.
 */
public class IrisGSetterStatement extends IrisStatement{
    private IrisIdentifier m_variableName = null;

    public IrisGSetterStatement(IrisIdentifier variableName) {
        m_variableName = variableName;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());
        StringBuilder functionNameBuffer = new StringBuilder();
        String getterFunctionName = functionNameBuffer.append("__get_")
                .append(m_variableName.getIdentifier()
                        .substring(1, m_variableName.getIdentifier().length()))
                .toString();
        functionNameBuffer.delete(0, functionNameBuffer.length());

        String setterFunctionName = functionNameBuffer.append("__set_")
                .append(m_variableName.getIdentifier()
                        .substring(1, m_variableName.getIdentifier().length()))
                .toString();

        // Getter
        visitor.visitLdcInsn(getterFunctionName);
        visitor.visitLdcInsn(m_variableName.getIdentifier());
        visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod$MethodAuthority", "Everyone", "Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;");
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(),
                "DefineDefaultGetter", "" +
                        "(Ljava/lang/String;" +
                        "Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false);
        // Setter
        visitor.visitLdcInsn(setterFunctionName);
        visitor.visitLdcInsn(m_variableName.getIdentifier());
        visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod$MethodAuthority", "Everyone", "Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;");
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "DefineDefaultSetter", "" +
                "(Ljava/lang/String;" +
                "Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false);

        return true;
    }
}
