package com.irisine.jiris.compiler.expression;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import java.util.LinkedList;

/**
 * Created by Huisama on 2017/4/12 0012.
 */
public class IrisFieldExpression extends IrisExpression {
    private LinkedList<IrisIdentifier> m_list = null;
    private IrisIdentifier m_identifier = null;
    boolean m_isTopField = false;

    public IrisFieldExpression(LinkedList<IrisIdentifier> list, IrisIdentifier identifier, boolean isTopField) {
        m_list = list;
        m_identifier = identifier;
        m_isTopField = isTopField;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        return true;
    }
}
