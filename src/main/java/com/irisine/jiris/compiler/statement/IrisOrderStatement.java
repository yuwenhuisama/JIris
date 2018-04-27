package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisGenerateHelper;
import com.irisine.jiris.compiler.assistpart.IrisBlock;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/5/3 0003.
 */
public class IrisOrderStatement extends IrisStatement {
    private IrisBlock m_orderBlock = null;
    private IrisIdentifier m_irregularObject = null;
    private IrisBlock m_serveBlock = null;
    private IrisBlock m_ignoreBlock = null;

    public IrisOrderStatement(IrisBlock orderBlock, IrisIdentifier irregularObject, IrisBlock serveBlock, IrisBlock ignoreBlock) {
        m_orderBlock = orderBlock;
        m_irregularObject = irregularObject;
        m_serveBlock = serveBlock;
        m_ignoreBlock = ignoreBlock;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());
//        Label handleBeginLabel = new Label();
//        Label handleEndLabel = new Label();
//        Label catchLabel = new Label();
//
//        Label finallyLabel = new Label();
//        Label finallyEndLabel = new Label();
//        Label noExceptionLabel = new Label();
//        Label notCatched = new Label();
//
//        //Label catchBegin = new Label();
//
//        visitor.visitTryCatchBlock(handleBeginLabel, handleEndLabel, catchLabel,
//                "org/irislang/jiris/core/exceptions/IrisRuntimeException");
//        visitor.visitTryCatchBlock(handleBeginLabel, finallyEndLabel, notCatched, null);
//
//        // -- try --
//        visitor.visitLabel(handleBeginLabel);
//        if(!m_orderBlock.Generate(currentCompiler, currentBuilder, visitor)) {
//            return false;
//        }
//        visitor.visitLabel(handleEndLabel);
//        visitor.visitJumpInsn(Opcodes.GOTO, noExceptionLabel);
//        // -- try --
//
//        // -- catch runtime exception --
//        visitor.visitLabel(catchLabel);
//        if(!currentCompiler.isFirstStackFrameGenerated()) {
//            visitor.visitFrame(Opcodes.F_FULL, 4, new Object[] {"Main", "org/irislang/jiris/core/IrisContextEnvironment",
//                    "org/irislang/jiris/core/IrisThreadInfo", "org/irislang/jiris/core/IrisValue"}, 1, new Object[]
//                    {"org/irislang/jiris/core/exceptions/IrisRuntimeException"});
//        }
//        else {
//            visitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"org/irislang/jiris/core/exceptions/IrisRuntimeException"});
//        }
//        visitor.visitVarInsn(Opcodes.ASTORE, 4);
//
//        // catch
//        visitor.visitVarInsn(Opcodes.ALOAD, 4);
//        if(!m_serveBlock.Generate(currentCompiler, currentBuilder, visitor)) {
//            return false;
//        }
//        visitor.visitLabel(finallyEndLabel);
//
//        if(m_ignoreBlock != null && !m_ignoreBlock.Generate(currentCompiler, currentBuilder, visitor)) {
//            return false;
//        }
//        visitor.visitJumpInsn(Opcodes.GOTO, finallyLabel);
//        // -- catch runtime exception --
//
//        // not catched
//        visitor.visitLabel(notCatched);
//        visitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Throwable"});
//        visitor.visitVarInsn(Opcodes.ASTORE, 5);
//        if(m_ignoreBlock != null && !m_ignoreBlock.Generate(currentCompiler, currentBuilder, visitor)) {
//            return false;
//        }
//        visitor.visitVarInsn(Opcodes.ALOAD, 5);
//        visitor.visitInsn(Opcodes.ATHROW);
//
//        visitor.visitLabel(noExceptionLabel);
//        visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//        if(m_ignoreBlock != null && !m_ignoreBlock.Generate(currentCompiler, currentBuilder, visitor)) {
//            return false;
//        }
//        visitor.visitLabel(finallyLabel);
//        visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

        Label localFromLabel = new Label();
        Label localToLabel = new Label();
        Label tryBegin = new Label();
        Label tryEnd = new Label();
        Label catchSuccess = new Label();
        visitor.visitTryCatchBlock(tryBegin, tryEnd, catchSuccess,
                "org/irislang/jiris/core/exceptions/IrisRuntimeException");

        Label noCatched = new Label();
        Label finallyDone = new Label();
        visitor.visitTryCatchBlock(tryBegin, noCatched, finallyDone, null);

        visitor.visitLabel(tryBegin);

        if(!m_orderBlock.Generate(currentCompiler, currentBuilder, visitor)) {
            return false;
        }

        visitor.visitLabel(tryEnd);
        Label noException = new Label();
        visitor.visitJumpInsn(Opcodes.GOTO, noException);
        visitor.visitLabel(catchSuccess);

        if(!currentCompiler.isFirstStackFrameGenerated()) {
            if(currentCompiler.isStaticDefine()) {
                visitor.visitFrame(Opcodes.F_FULL, 3, new Object[] {
                        "org/irislang/jiris/core/IrisContextEnvironment",
                        "org/irislang/jiris/core/IrisThreadInfo", "org/irislang/jiris/core/IrisValue"}, 1, new Object[]
                        {"org/irislang/jiris/core/exceptions/IrisRuntimeException"});
            }
            else
            {
                visitor.visitFrame(Opcodes.F_FULL, 4, new Object[] {currentCompiler.getCurrentClassName(),
                        "org/irislang/jiris/core/IrisContextEnvironment",
                        "org/irislang/jiris/core/IrisThreadInfo", "org/irislang/jiris/core/IrisValue"}, 1, new Object[]
                        {"org/irislang/jiris/core/exceptions/IrisRuntimeException"});
            }
        }
        else {
            visitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"org/irislang/jiris/core/exceptions/IrisRuntimeException"});
        }
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfIrregularVar());
        visitor.visitLabel(localFromLabel);
        visitor.visitLdcInsn(m_irregularObject.getIdentifier());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfIrregularVar());
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/exceptions/IrisRuntimeException", "getExceptionObject", "()Lorg/irislang/jiris/core/IrisValue;", false);
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetLocalVariable", "" +
                "(Ljava/lang/String;" +
                "Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());

        if(!m_serveBlock.Generate(currentCompiler, currentBuilder, visitor)) {
            return false;
        }
        visitor.visitLabel(localToLabel);
        currentCompiler.AddIrregularVariableLabelPair(new IrisCompiler.IrregularVariableLabelPair(localFromLabel,
                localToLabel));

        visitor.visitLabel(noCatched);
        if(!m_ignoreBlock.Generate(currentCompiler, currentBuilder, visitor)) {
            return false;
        }

        Label endLable = new Label();
        visitor.visitJumpInsn(Opcodes.GOTO, endLable);
        visitor.visitLabel(finallyDone);

        visitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Throwable"});
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfIrregularVar() + 1);

        if(!m_ignoreBlock.Generate(currentCompiler, currentBuilder, visitor)) {
            return false;
        }

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfIrregularVar() + 1);
        visitor.visitInsn(Opcodes.ATHROW);
        visitor.visitLabel(noException);
        visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

        if(!m_ignoreBlock.Generate(currentCompiler, currentBuilder, visitor)) {
            return false;
        }

        visitor.visitLabel(endLable);
        visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

//
//        Label l0 = new Label();
//        Label l1 = new Label();
//        Label l2 = new Label();
//        visitor.visitTryCatchBlock(l0, l1, l2, "org/irislang/jiris/core/exceptions/IrisRuntimeException");
//
//        visitor.visitLabel(l0);
//
//        if(!m_orderBlock.Generate(currentCompiler, currentBuilder, visitor)) {
//            return false;
//        }
//
//        visitor.visitLabel(l1);
//        visitor.visitLineNumber(50, l1);
//        Label l4 = new Label();
//        visitor.visitJumpInsn(Opcodes.GOTO, l4);
//        visitor.visitLabel(l2);
//        visitor.visitFrame(Opcodes.F_FULL, 3, new Object[] {
//                "org/irislang/jiris/core/IrisContextEnvironment",
//                "org/irislang/jiris/core/IrisThreadInfo", "org/irislang/jiris/core/IrisValue"}, 1, new Object[] {"org/irislang/jiris/core/exceptions/IrisRuntimeException"});
//
//        visitor.visitLabel(localFromLabel);
//        currentCompiler.AddIrregularVariableLabelPair(new IrisCompiler.IrregularVariableLabelPair(localFromLabel,
//                localToLabel));
//
//        if(!m_serveBlock.Generate(currentCompiler, currentBuilder, visitor)) {
//            return false;
//        }
//        visitor.visitLabel(localToLabel);
//        visitor.visitLabel(l4);
//        visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);


        return true;
    }
}
