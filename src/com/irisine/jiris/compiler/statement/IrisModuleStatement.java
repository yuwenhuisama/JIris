package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisGenerateHelper;
import com.irisine.jiris.compiler.assistpart.IrisBlock;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;
import com.irisine.jiris.compiler.expression.IrisExpression;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import java.util.LinkedList;

/**
 * Created by Huisama on 2017/4/26 0026.
 */
public class IrisModuleStatement extends IrisStatement {
    private IrisIdentifier m_moduleName = null;
    private LinkedList<IrisExpression> m_modules = null;
    //private LinkedList<IrisExpression> m_interfaces = null;
    private IrisBlock m_block = null;

    public IrisModuleStatement(IrisIdentifier className, LinkedList<IrisExpression> modules, IrisBlock block) {
        m_moduleName = className;
        m_modules = modules;
        //m_interfaces = interfaces;
        m_block = block;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());
        if(m_modules != null) {
            for(IrisExpression expr : m_modules) {
                if(!expr.Generate(currentCompiler, currentBuilder, visitor)) {
                    return false;
                }

                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "AddTempModule", "(Lorg/irislang/jiris/core/IrisValue;)V", false);
            }
        }

        IrisCompiler.CurrentDefineType preType = currentCompiler.getCurrentDefineType();
        currentCompiler.setCurrentDefineType(IrisCompiler.CurrentDefineType.Module);

        // push environment
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PushContext", "(Lorg/irislang/jiris/core/IrisContextEnvironment;)V", false);

        visitor.visitLdcInsn(m_moduleName.getIdentifier());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "DefineModule", "" +
                "(Ljava/lang/String;" +
                "Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)Lorg/irislang/jiris/core/IrisContextEnvironment;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfContextVar());

        // store object
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisContextEnvironment", "getRunningType", "()Lorg/irislang/jiris/core/IrisRunningObject;", false);
        visitor.visitTypeInsn(Opcodes.CHECKCAST, "org/irislang/jiris/core/IrisModule");
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisModule", "getModuleObject", "" +
                "()" +
                "Lorg/irislang/jiris/core/IrisObject;", false);
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/core/IrisValue", "WrapObject", "(Lorg/irislang/jiris/core/IrisObject;)Lorg/irislang/jiris/core/IrisValue;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());

        if(!m_block.Generate(currentCompiler, currentBuilder, visitor)) {
            return false;
        }

        // pop environment
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopContext", "()Lorg/irislang/jiris/core/IrisContextEnvironment;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfContextVar());

        currentCompiler.setCurrentDefineType(preType);

        return true;
    }
}
