package org.irislang.jiris.compiler

import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes

import java.security.spec.ECField

/**
 * Created by Huisama on 2017/3/10 0010.
 */
object IrisGenerateHelper {

    internal var sm_preLineNumber = -1
    fun AddParameter(visitor: MethodVisitor, currentCompiler: IrisCompiler) {
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false)
    }

    fun GetPartPrametersOf(visitor: MethodVisitor, currentCompiler: IrisCompiler, size: Int) {
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        //visitor.visitLdcInsn(new Integer(size));
        LoadInteger(visitor, size)
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "getPartPrameterListOf", "(I)Ljava/util/ArrayList;", false)
    }

    fun CreateArray(visitor: MethodVisitor, currentCompiler: IrisCompiler) {
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil",
                "CreateArray", "(Ljava/util/ArrayList;)Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
    }

    fun CreateHash(visitor: MethodVisitor, currentCompiler: IrisCompiler) {
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil",
                "CreateHash", "" + "(Ljava/util/ArrayList;)Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
    }

    fun CreateInt(visitor: MethodVisitor, currentCompiler: IrisCompiler, value: Int) {
        LoadInteger(visitor, value)
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil",
                "CreateInt", "(I)Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
    }

    fun PopParameter(visitor: MethodVisitor, currentCompiler: IrisCompiler, size: Int) {
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        //visitor.visitLdcInsn(new Integer(size));
        LoadInteger(visitor, size)
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "PopParameter", "(I)V", false)
    }

    fun CallMethod(visitor: MethodVisitor, currentCompiler: IrisCompiler, methodName: String, parameterCount: Int, noCaller: Boolean) {

        if (noCaller) {
            visitor.visitInsn(Opcodes.ACONST_NULL)
        } else {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
        }

        visitor.visitLdcInsn(methodName)
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())

        LoadInteger(visitor, parameterCount)

        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "CallMethod",
                "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;I)Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
    }

    fun GetRecord(visitor: MethodVisitor, currentCompiler: IrisCompiler) {
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "getRecord", "()Lorg/irislang/jiris/core/IrisValue;", false)
    }

    fun SetRecord(visitor: MethodVisitor, currentCompiler: IrisCompiler) {
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "setRecord", "(Lorg/irislang/jiris/core/IrisValue;)V", false)
    }

    fun ClearRecord(visitor: MethodVisitor, currentCompiler: IrisCompiler) {
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitInsn(Opcodes.ACONST_NULL)
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "setRecord", "(Lorg/irislang/jiris/core/IrisValue;)V", false)
    }

    fun LoadInteger(visitor: MethodVisitor, parameterCount: Int) {
        when (parameterCount) {
            0 -> visitor.visitInsn(Opcodes.ICONST_0)
            1 -> visitor.visitInsn(Opcodes.ICONST_1)
            2 -> visitor.visitInsn(Opcodes.ICONST_2)
            3 -> visitor.visitInsn(Opcodes.ICONST_3)
            4 -> visitor.visitInsn(Opcodes.ICONST_4)
            5 -> visitor.visitInsn(Opcodes.ICONST_5)
            else -> visitor.visitLdcInsn(parameterCount)
        }
    }

    fun StackFrameOpreate(visitor: MethodVisitor, currentCompiler: IrisCompiler) {
        if (!currentCompiler.isFirstStackFrameGenerated) {
            visitor.visitFrame(Opcodes.F_APPEND, 1, arrayOf<Any>("org/irislang/jiris/core/IrisValue"), 0, null)
            currentCompiler.isFirstStackFrameGenerated = true
        } else {
            visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
        }
    }

    fun SetLineNumber(visitor: MethodVisitor, currentCompiler: IrisCompiler, lineNumber: Int) {
        if (sm_preLineNumber == lineNumber) {
            return
        }
        sm_preLineNumber = lineNumber
        try {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitLdcInsn(lineNumber)
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                    "setCurrentLineNumber", "(I)V", false)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    fun SetFileName(visitor: MethodVisitor, currentCompiler: IrisCompiler) {
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitFieldInsn(Opcodes.GETSTATIC, currentCompiler.currentClassName, "sm_scriptFileName",
                "Ljava/lang/String;")
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                "setCurrentFileName", "(Ljava/lang/String;)V", false)
    }
}
