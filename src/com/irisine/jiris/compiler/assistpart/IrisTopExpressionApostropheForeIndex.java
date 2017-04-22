package com.irisine.jiris.compiler.assistpart;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.expression.IrisExpression;
import com.irisine.jiris.compiler.expression.IrisIndexExpression;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/4/17 0017.
 */
public class IrisTopExpressionApostropheForeIndex extends IrisTopExpressionApostropheBase {
    private IrisExpression m_indexer = null;

    public IrisTopExpressionApostropheForeIndex(IrisExpression indexer) {
        m_indexer = indexer;
    }

    @Override
    public IrisExpression ToDirectExpression(IrisExpression foreExpression, IrisTopExpressionApostrophe postExpression) {
        IrisExpression tmpExpr = new IrisIndexExpression(foreExpression, m_indexer);
        return postExpression == null ? tmpExpr : postExpression.ToDirectExpression(tmpExpr);
    }
}
