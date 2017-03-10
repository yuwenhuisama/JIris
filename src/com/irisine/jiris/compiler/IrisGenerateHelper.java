package com.irisine.jiris.compiler;

import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

import java.security.spec.ECField;

/**
 * Created by Huisama on 2017/3/10 0010.
 */
public final class IrisGenerateHelper {
    public static void AddParameter(MethodVisitor visitor, IrisCompiler currentCompiler) {
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false);
    }

    public static void GetPartPrametersOf(MethodVisitor visitor, IrisCompiler currentCompiler, int size) {
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        //visitor.visitLdcInsn(new Integer(size));
        LoadInteger(visitor, size);
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "getPartPrameterListOf", "(I)Ljava/util/ArrayList;", false);
    }

    public static void CreateArray(MethodVisitor visitor, IrisCompiler currentCompiler) {
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "CreateArray", "(Ljava/util/ArrayList;)Lorg/irislang/jiris/core/IrisValue;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
    }

    public static void CreateInt(MethodVisitor visitor, IrisCompiler currentCompiler, int value) {
        LoadInteger(visitor, value);
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "CreateInt", "(I)Lorg/irislang/jiris/core/IrisValue;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
    }

    public static void PopParameter(MethodVisitor visitor, IrisCompiler currentCompiler, int size) {
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        //visitor.visitLdcInsn(new Integer(size));
        LoadInteger(visitor, size);
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopParameter", "(I)V", false);
    }

    public static void CallMethod(MethodVisitor visitor, IrisCompiler currentCompiler, String methodName, int parameterCount, boolean noCaller){

        if(noCaller) {
            visitor.visitInsn(Opcodes.ACONST_NULL);
        } else {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
        }

        visitor.visitLdcInsn(methodName);
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
        //visitor.visitInsn(Opcodes.ICONST_1);

        LoadInteger(visitor, parameterCount);

        //visitor.visitLdcInsn(new Integer(parameterCount));
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "CallMethod",  "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;I)Lorg/irislang/jiris/core/IrisValue;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
    }

    public static void GetRecord(MethodVisitor visitor, IrisCompiler currentCompiler) {
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "getRecord", "()Lorg/irislang/jiris/core/IrisValue;", false);
    }

    public static void SetRecord(MethodVisitor visitor, IrisCompiler currentCompiler){
     	visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "setRecord", "(Lorg/irislang/jiris/core/IrisValue;)V", false);
    }

    public static void ClearRecord(MethodVisitor visitor, IrisCompiler currentCompiler) {
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitInsn(Opcodes.ACONST_NULL);
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "setRecord", "(Lorg/irislang/jiris/core/IrisValue;)V", false);
    }

    private static void LoadInteger(MethodVisitor visitor, int parameterCount) {
        switch (parameterCount){
            case 0:
                visitor.visitInsn(Opcodes.ICONST_0);
                break;
            case 1:
                visitor.visitInsn(Opcodes.ICONST_1);
                break;
            case 2:
                visitor.visitInsn(Opcodes.ICONST_2);
                break;
            case 3:
                visitor.visitInsn(Opcodes.ICONST_3);
                break;
            case 4:
                visitor.visitInsn(Opcodes.ICONST_4);
                break;
            case 5:
                visitor.visitInsn(Opcodes.ICONST_5);
                break;
            default:
                visitor.visitLdcInsn(new Integer(parameterCount));
                break;
        }
    }

    public static void StackFrameOpreate(MethodVisitor visitor, IrisCompiler currentCompiler) {
        if(!currentCompiler.isFirstStackFrameGenerated()) {
            visitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {"org/irislang/jiris/core/IrisValue"}, 0, null);
            currentCompiler.setFirstStackFrameGenerated(true);
        } else {
            visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        }
    }

}
