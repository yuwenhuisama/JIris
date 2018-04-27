package com.irisine.jiris.compiler.expression;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisGenerateHelper;
import com.irisine.jiris.compiler.assistpart.IrisHashPair;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.irislang.jiris.IrisNativeJavaClass;

import java.util.LinkedList;

/**
 * Created by Huisama on 2017/4/8 0008.
 */
public class IrisHashExpression extends IrisExpression {

    private LinkedList<IrisHashPair> m_hashPairs = null;

    public IrisHashExpression(LinkedList<IrisHashPair> hashPairs) {
        m_hashPairs = hashPairs;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());

        if(m_hashPairs != null) {
            for (IrisHashPair hashPair : m_hashPairs) {
                if(!hashPair.getKey().Generate(currentCompiler, currentBuilder, visitor)) {
                    return false;
                }
                IrisGenerateHelper.AddParameter(visitor, currentCompiler);

                if(!hashPair.getValue().Generate(currentCompiler, currentBuilder, visitor)) {
                    return false;
                }
                IrisGenerateHelper.AddParameter(visitor, currentCompiler);
            }

            IrisGenerateHelper.GetPartPrametersOf(visitor, currentCompiler, m_hashPairs.size() * 2);
        }
        else {
            visitor.visitInsn(Opcodes.ACONST_NULL);
        }

        IrisGenerateHelper.CreateHash(visitor, currentCompiler);

        if(m_hashPairs != null) {
            IrisGenerateHelper.PopParameter(visitor, currentCompiler, m_hashPairs.size() * 2);
        }

        return true;
    }
}
