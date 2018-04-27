package com.irisine.jiris.compiler.expression;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisGenerateHelper;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.IrisNativeJavaClass;

import java.util.LinkedList;

/**
 * Created by Huisama on 2017/4/12 0012.
 */
public class IrisFieldExpression extends IrisExpression {
    private LinkedList<IrisIdentifierExpression> m_list = null;
    private IrisIdentifierExpression m_identifier = null;
    boolean m_isTopField = false;

    public IrisFieldExpression(LinkedList<IrisIdentifierExpression> list, IrisIdentifierExpression identifier, boolean isTopField) {
        m_list = list;
        m_identifier = identifier;
        m_isTopField = isTopField;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());

        IrisIdentifierExpression firstIdentifier = m_list.removeFirst();

        if(!m_isTopField) {
            if(!firstIdentifier.Generate(currentCompiler, currentBuilder, visitor)) {
                return false;
            }
        }
        else {
            visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/compiler/IrisInterpreter", "INSTANCE", "Lorg/irislang/jiris/compiler/IrisInterpreter;");
            visitor.visitLdcInsn(firstIdentifier.getIdentifierString());
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/compiler/IrisInterpreter", "GetConstance", "(Ljava/lang/String;)Lorg/irislang/jiris/core/IrisValue;", false);
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
        }

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());

        if(m_list.isEmpty()) {
            visitor.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            visitor.visitIntInsn(Opcodes.BIPUSH, m_list.size());
            visitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");
            int index = 0;
            for(IrisIdentifierExpression identifierExpression : m_list) {
                visitor.visitInsn(Opcodes.DUP);
                visitor.visitInsn(index++);
                visitor.visitLdcInsn(identifierExpression.getIdentifierString());
                visitor.visitInsn(Opcodes.AASTORE);
            }
        }

        visitor.visitLdcInsn(m_identifier.getIdentifierString());
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "GetFieldValue", "" +
                "(Lorg/irislang/jiris/core/IrisValue;[Ljava/lang/String;Ljava/lang/String;)Lorg/irislang/jiris/core/IrisValue;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());

        return true;
    }
}
