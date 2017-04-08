package com.irisine.jiris.compiler.assistpart;

import com.irisine.jiris.compiler.IrisSyntaxUnit;
import com.irisine.jiris.compiler.expression.IrisExpression;

/**
 * Created by Huisama on 2017/4/8 0008.
 */
public class IrisHashPair extends IrisSyntaxUnit {
    private IrisExpression m_key = null;
    private IrisExpression m_value = null;

    public IrisExpression getKey() {
        return m_key;
    }

    public void setKey(IrisExpression key) {
        m_key = key;
    }

    public IrisExpression getValue() {
        return m_value;
    }

    public void setValue(IrisExpression value) {
        m_value = value;
    }

    public IrisHashPair(IrisExpression key, IrisExpression value) {
        m_key = key;
        m_value = value;
    }
}
