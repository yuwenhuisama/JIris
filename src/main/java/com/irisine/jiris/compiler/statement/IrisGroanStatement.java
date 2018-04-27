package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisGenerateHelper;
import com.irisine.jiris.compiler.expression.IrisExpression;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/5/3 0003.
 */
public class IrisGroanStatement extends IrisStatement {
    private IrisExpression m_expression = null;

    public IrisGroanStatement(IrisExpression expression) {
        m_expression = expression;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());

        if(m_expression != null && !m_expression.Generate(currentCompiler, currentBuilder, visitor)) {
            return false;
        }

        visitor.visitTypeInsn(Opcodes.NEW, "org/irislang/jiris/core/exceptions/IrisRuntimeException");
        visitor.visitInsn(Opcodes.DUP);
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "getCurrentFileName", "()Ljava/lang/String;", false);
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "getCurrentLineNumber", "()I", false);
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/irislang/jiris/core/exceptions/IrisRuntimeException", "<init>", "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;I)V", false);

        visitor.visitInsn(Opcodes.ATHROW);


        return true;
    }
}
