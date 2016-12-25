package com.irisine.jiris.compiler.expression;

import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public class IrisInstantValueExpression extends IrisExpression {

	public enum InstantValueType {
		Nil,
		True,
		False,
	};
	
	private InstantValueType m_type = InstantValueType.Nil;
	
	public IrisInstantValueExpression(InstantValueType type) {
		super();
		m_type = type;
	}
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
		
		switch (m_type) {
		case Nil:
			GenerateInstant(visitor, "Nil");
			break;
		case True:
			GenerateInstant(visitor, "True");
			break;
		case False:
			GenerateInstant(visitor, "False");
			break;
		}
		
		return true;
	}
	
	private void GenerateInstant(MethodVisitor visitor, String obj) {
		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", obj, "()Lorg/irislang/jiris/core/IrisValue;", false);
		visitor.visitVarInsn(Opcodes.ASTORE, 3);
	}
}
