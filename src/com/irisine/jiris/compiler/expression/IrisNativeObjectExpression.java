package com.irisine.jiris.compiler.expression;

import com.irisine.jiris.compiler.IrisGenerateHelper;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public class IrisNativeObjectExpression extends IrisExpression {

	public enum NativeObjectType {
		String,
		Integer,
		Float,
		UniqueString,
	};
	
	private String m_string = "";
	private int m_integer = 0;
	private double m_float = 0;
	
	private NativeObjectType m_type = NativeObjectType.String;
	
	public IrisNativeObjectExpression(NativeObjectType type, String string) {
		super();
		m_type = type;
		m_string = string;
	}
	
	public IrisNativeObjectExpression(int integer) {
		super();
		m_integer = integer;
		m_type = NativeObjectType.Integer;
	}
	
	public IrisNativeObjectExpression(double irfloat) {
		super();
		m_float = irfloat;
		m_type = NativeObjectType.Float;
	}
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());

		switch (m_type) {
		case String :
			visitor.visitLdcInsn(m_string);
			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "CreateString", "(Ljava/lang/String;)Lorg/irislang/jiris/core/IrisValue;", false);
			visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
			break;
		case Integer :
			visitor.visitLdcInsn(new Integer(m_integer));
			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "CreateInt", "(I)Lorg/irislang/jiris/core/IrisValue;", false);
			visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
			break;
		case Float :
			visitor.visitLdcInsn(new Double(m_float));
			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "CreateFloat", "(D)Lorg/irislang/jiris/core/IrisValue;", false);
			visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
			break;
		case UniqueString :
			IrisCompiler.INSTANCE.AddUniqueString(m_string);
			int index = IrisCompiler.INSTANCE.GetUinqueIndex(m_string);
			
			visitor.visitFieldInsn(Opcodes.GETSTATIC, currentCompiler.getCurrentClassName(), "sm_uniqueStringObjects", "Ljava/util/ArrayList;");
			visitor.visitLdcInsn(new Integer(index));
			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;", false);
			visitor.visitTypeInsn(Opcodes.CHECKCAST, "org/irislang/jiris/core/IrisValue");
			visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
			
			break;
		}
		
		return true;
	}

}
