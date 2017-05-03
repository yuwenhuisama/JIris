package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.expression.IrisExpression;
import com.irisine.jiris.compiler.statement.IrisStatement;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

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

        if(m_expression != null && !m_expression.Generate(currentCompiler, currentBuilder, visitor)) {
            return false;
        }

        visitor.visitTypeInsn(Opcodes.NEW, "org/irislang/jiris/core/exceptions/IrisRuntimeException");
        visitor.visitInsn(Opcodes.DUP);
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
        visitor.visitLdcInsn("");
        visitor.visitInsn(Opcodes.ICONST_0);
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/irislang/jiris/core/exceptions/IrisRuntimeException", "<init>", "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;I)V", false);
        visitor.visitInsn(Opcodes.ATHROW);

        return true;
    }
}
