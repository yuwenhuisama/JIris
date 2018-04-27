package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisGenerateHelper;
import com.irisine.jiris.compiler.assistpart.IrisBlock;
import com.irisine.jiris.compiler.assistpart.IrisDeferredBlock;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/5/2 0002.
 */
public class IrisSetterStatement extends IrisStatement {

    private IrisIdentifier m_setteredVariable = null;
    private IrisIdentifier m_paramName = null;
    private IrisBlock m_block = null;
    private String m_nativeBlockName = null;
    private String m_functionName = null;

    public IrisSetterStatement(IrisIdentifier setteredVariable, IrisIdentifier paramName, IrisBlock block) {
        m_setteredVariable = setteredVariable;
        m_paramName = paramName;
        m_block = block;

        StringBuilder functionNameBuffer = new StringBuilder();
        m_functionName = functionNameBuffer.append("__set_")
                .append(m_setteredVariable.getIdentifier()
                        .substring(1, m_setteredVariable.getIdentifier().length()))
                .toString();

        // Make deferred block
        if(m_block != null) {
            StringBuilder buffer = new StringBuilder();

            buffer.append(m_functionName)
                    .append("$ins$mth$")
                    .append(IrisCompiler.INSTANCE.GetBlockNameCount(m_functionName));

            m_nativeBlockName = buffer.toString();

            IrisCompiler.INSTANCE.PushDeferredStatement(new IrisDeferredBlock(m_block, m_nativeBlockName));
        }
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());

        if(m_block != null) {
            // Preinvoke
            //visitor.visitVarInsn(Opcodes.ALOAD, 0);
            // this.getClass()
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);

            // native block name
            visitor.visitLdcInsn(m_nativeBlockName);

            // function name
            visitor.visitLdcInsn(m_functionName);

            // new String()
            visitor.visitIntInsn(Opcodes.BIPUSH, 1);
            visitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");

            // array[0] = param name
            visitor.visitInsn(Opcodes.DUP);
            visitor.visitIntInsn(Opcodes.BIPUSH, 0);
            visitor.visitLdcInsn(m_paramName.getIdentifier());
            visitor.visitInsn(Opcodes.AASTORE);

            // variable param
            visitor.visitInsn(Opcodes.ACONST_NULL);

            // with
            visitor.visitInsn(Opcodes.ACONST_NULL);
            // without
            visitor.visitInsn(Opcodes.ACONST_NULL);

            // authority
            visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod$MethodAuthority", "Everyone", "Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;");

            // contex
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());

            // thread info
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());

            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(),
                    "DefineInstanceMethod", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false);
        }
        else {
            visitor.visitLdcInsn(m_functionName);
            visitor.visitLdcInsn(m_setteredVariable.getIdentifier());
            visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod$MethodAuthority", "Everyone", "Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;");
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "DefineDefaultSetter", "" +
                    "(Ljava/lang/String;" +
                    "Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false);

        }

        return true;
    }
}
