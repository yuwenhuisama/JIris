package com.irisine.jiris.compiler.expression;

import com.irisine.jiris.compiler.IrisCompiler;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.IrisNativeJavaClass;

/**
 * Created by yuwen on 2017/7/16 0016.
 */
public class IrisCastExpression extends IrisExpression {
    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {

        visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "GetCastObject", "(Lorg/irislang/jiris/core/IrisThreadInfo;)Lorg/irislang/jiris/core/IrisValue;", false);
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());

        return true;
    }
}
