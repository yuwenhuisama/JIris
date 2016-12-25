package com.irisine.jiris.compiler.statement;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisSyntaxUnit;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;

public abstract class IrisStatement extends IrisSyntaxUnit {
	public abstract boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor);
}
