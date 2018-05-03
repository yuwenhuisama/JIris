package org.irislang.jiris.compiler.statement

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.compiler.assistpart.IrisBlock
import org.irislang.jiris.compiler.assistpart.IrisIdentifier
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.Label
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import org.irislang.jiris.IrisNativeJavaClass

/**
 * Created by Huisama on 2017/5/3 0003.
 */
class IrisOrderStatement(val orderBlock: IrisBlock,
                         val irregularObject: IrisIdentifier,
                         val serveBlock: IrisBlock,
                         val ignoreBlock: IrisBlock?) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        val localFromLabel = Label()
        val localToLabel = Label()
        val tryBegin = Label()
        val tryEnd = Label()
        val catchSuccess = Label()
        visitor.visitTryCatchBlock(tryBegin, tryEnd, catchSuccess,
                "org/irislang/jiris/core/exceptions/IrisRuntimeException")

        val noCatched = Label()
        val finallyDone = Label()
        visitor.visitTryCatchBlock(tryBegin, noCatched, finallyDone, null)

        visitor.visitLabel(tryBegin)

        if (!orderBlock.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        visitor.visitLabel(tryEnd)
        val noException = Label()
        visitor.visitJumpInsn(Opcodes.GOTO, noException)
        visitor.visitLabel(catchSuccess)

        if (!currentCompiler.isFirstStackFrameGenerated) {
            if (currentCompiler.isStaticDefine) {
                visitor.visitFrame(Opcodes.F_FULL, 3,
                        arrayOf<Any>("org/irislang/jiris/core/IrisContextEnvironment",
                                "org/irislang/jiris/core/IrisThreadInfo",
                                "org/irislang/jiris/core/IrisValue"),
                        1, arrayOf<Any>("org/irislang/jiris/core/exceptions/IrisRuntimeException"))
            } else {
                visitor.visitFrame(Opcodes.F_FULL, 4,
                        arrayOf<Any>(currentCompiler.currentClassName, "org/irislang/jiris/core/IrisContextEnvironment",
                                "org/irislang/jiris/core/IrisThreadInfo", "org/irislang/jiris/core/IrisValue"),
                        1,
                        arrayOf<Any>("org/irislang/jiris/core/exceptions/IrisRuntimeException"))
            }
        } else {
            visitor.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                    arrayOf<Any>("org/irislang/jiris/core/exceptions/IrisRuntimeException"))
        }
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfIrregularVar())
        visitor.visitLabel(localFromLabel)
        visitor.visitLdcInsn(irregularObject.identifier)
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfIrregularVar())
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/exceptions/IrisRuntimeException",
                "getExceptionObject", "()Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "SetLocalVariable",
                "(Ljava/lang/String;" +
                "Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false)
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())

        if (!serveBlock.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }
        visitor.visitLabel(localToLabel)
        currentCompiler.AddIrregularVariableLabelPair(IrisCompiler.IrregularVariableLabelPair(localFromLabel,
                localToLabel))

        visitor.visitLabel(noCatched)
        if (ignoreBlock != null && !ignoreBlock.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        val endLable = Label()
        visitor.visitJumpInsn(Opcodes.GOTO, endLable)
        visitor.visitLabel(finallyDone)

        visitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, arrayOf<Any>("java/lang/Throwable"))
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfIrregularVar() + 1)

        if (ignoreBlock != null && !ignoreBlock.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfIrregularVar() + 1)
        visitor.visitInsn(Opcodes.ATHROW)
        visitor.visitLabel(noException)
        visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null)

        if (ignoreBlock != null && !ignoreBlock.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        visitor.visitLabel(endLable)
        visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null)

        return true
    }
}
