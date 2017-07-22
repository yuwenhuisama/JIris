package com.irisine.jiris.compiler.assistpart;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisSyntaxUnit;
import com.irisine.jiris.compiler.expression.IrisExpression;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/4/17 0017.
 */
public abstract class IrisTopExpressionApostropheBase extends IrisSyntaxUnit {
    abstract public IrisExpression ToDirectExpression(IrisExpression foreExpression, IrisTopExpressionApostrophe
            postExpression);
}
