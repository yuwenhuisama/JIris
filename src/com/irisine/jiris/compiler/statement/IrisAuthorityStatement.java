package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.MethodVisitor;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

/**
 * Created by Huisama on 2017/5/30 0030.
 */
public class IrisAuthorityStatement extends IrisStatement {

    enum Environment {
        Class,
        Module,
    }

    enum Target {
        InstanceMethod,
        ClassMethod,
    }

    enum Authority {
        Everyone,
        Relative,
        Personal,
    }

    private IrisIdentifier m_name = null;
    private Environment m_environment = Environment.Class;
    private Target m_target = Target.InstanceMethod;
    private Authority m_authority = Authority.Everyone;

    public IrisAuthorityStatement(IrisIdentifier name, Environment environment, Target target, Authority authority) {
        m_name = name;
        m_environment = environment;
        m_target = target;
        m_authority = authority;
    }

    @Override
    public boolean Generate(IrisCompiler currentCompiler, DynamicType.Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        return true;
    }
}
