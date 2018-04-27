package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisGenerateHelper;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;
import jdk.internal.org.objectweb.asm.Opcodes;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import org.irislang.jiris.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/4/8 0008.
 */
public class IrisBreakStatement extends IrisStatement {

    private IrisIdentifier m_lable = null;

    public IrisBreakStatement(IrisIdentifier lable) {
        m_lable = lable;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());
        visitor.visitJumpInsn(Opcodes.GOTO, currentCompiler.getCurrentLoopEndLable());
        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler);
        return true;
    }
}
