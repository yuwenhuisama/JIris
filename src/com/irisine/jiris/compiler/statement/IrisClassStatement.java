package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.assistpart.IrisBlock;
import com.irisine.jiris.compiler.assistpart.IrisElseIf;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;
import com.irisine.jiris.compiler.expression.IrisExpression;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import java.util.LinkedList;

/**
 * Created by Huisama on 2017/4/12 0012.
 */
public class IrisClassStatement extends IrisStatement  {
    private IrisIdentifier m_className = null;
    private IrisExpression m_superClassName = null;
    private LinkedList<IrisExpression> m_modules = null;
    private LinkedList<IrisExpression> m_interfaces = null;
    private IrisBlock m_block = null;

    public IrisClassStatement(IrisIdentifier className, IrisExpression superClassName, LinkedList<IrisExpression> modules, LinkedList<IrisExpression> interfaces, IrisBlock block) {
        m_className = className;
        m_superClassName = superClassName;
        m_modules = modules;
        m_interfaces = interfaces;
        m_block = block;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {

        if(m_superClassName != null) {
            if (!m_superClassName.Generate(currentCompiler, currentBuilder, visitor)) {
                return false;
            }
            // threadInfo.SetTempClass()
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "SetTempSuperClass", "(Lorg/irislang/jiris/core/IrisValue;)V", false);
        }

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

        if(m_interfaces != null) {
            for(IrisExpression expr : m_interfaces) {
                if(!expr.Generate(currentCompiler, currentBuilder, visitor)) {
                    return false;
                }
                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                        "AddTempInterface", "(Lorg/irislang/jiris/core/IrisValue;)V", false);
            }
        }

        IrisCompiler.CurrentDefineType preType = currentCompiler.getCurrentDefineType();
        currentCompiler.setCurrentDefineType(IrisCompiler.CurrentDefineType.Class);

        // push environment
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PushContext", "(Lorg/irislang/jiris/core/IrisContextEnvironment;)V", false);

        visitor.visitLdcInsn(m_className.getIdentifier());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "DefineClass", "(Ljava/lang/String;" +
                "Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)Lorg/irislang/jiris/core/IrisContextEnvironment;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfContextVar());

        // store object
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisContextEnvironment", "getRunningType", "()Lorg/irislang/jiris/core/IrisRunningObject;", false);
        visitor.visitTypeInsn(Opcodes.CHECKCAST, "org/irislang/jiris/core/IrisClass");
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisClass", "getClassObject", "()Lorg/irislang/jiris/core/IrisObject;", false);
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/core/IrisValue", "WrapObject", "(Lorg/irislang/jiris/core/IrisObject;)Lorg/irislang/jiris/core/IrisValue;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());

        if(m_superClassName != null) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetSuperClass", "" +
                    "(Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false);
        }

        if(m_modules != null) {
                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
                visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "AddModule", "" +
                        "(Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false);
        }

        if(m_interfaces != null) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "AddInterface", "" +
                    "(Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false);
        }

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
