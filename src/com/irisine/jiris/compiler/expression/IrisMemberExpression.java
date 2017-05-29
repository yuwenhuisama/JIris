package com.irisine.jiris.compiler.expression;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisGenerateHelper;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;
import com.irisine.jiris.compiler.statement.IrisStatement;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/5/2 0002.
 */
public class IrisMemberExpression extends IrisExpression {

    private IrisExpression m_caller = null;
    private IrisIdentifier m_property = null;

    public IrisMemberExpression(IrisExpression caller, IrisIdentifier property) {
        m_caller = caller;
        m_property = property;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());

        String property = m_property.getIdentifier();
        StringBuilder builder = new StringBuilder("__get_").append(property);

        if(!m_caller.Generate(currentCompiler, currentBuilder, visitor)) {
            return false;
        }

        IrisGenerateHelper.CallMethod(visitor, currentCompiler, builder.toString(), 0, false);

        return true;
    }

    @Override
    public LeftValueResult LeftValue(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        LeftValueResult result = new LeftValueResult();
        String property = m_property.getIdentifier();
        StringBuilder builder = new StringBuilder("__set_").append(property);

        if(!m_caller.Generate(currentCompiler, currentBuilder, visitor)){
            result.setResult(false);
            return result;
        }

        result.setIdentifier(builder.toString());
        result.setResult(true);
        result.setType(LeftValueType.MemberVariable);
        return result;
    }
}
