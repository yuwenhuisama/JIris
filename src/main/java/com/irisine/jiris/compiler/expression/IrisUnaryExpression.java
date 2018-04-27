package com.irisine.jiris.compiler.expression;

import com.irisine.jiris.compiler.IrisGenerateHelper;
import org.irislang.jiris.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;

public class IrisUnaryExpression extends IrisExpression {

	public enum UnaryExpressionType {
		LogicNot,
		BitNot,
		Minus,
		Plus,
	};
	
	private UnaryExpressionType m_type = UnaryExpressionType.BitNot;
	private IrisExpression m_expression = null;
	
	public IrisUnaryExpression(UnaryExpressionType type, IrisExpression expression) {
		super();
		m_type = type;
		m_expression = expression;
	}
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());

		if(!m_expression.Generate(currentCompiler, currentBuilder, visitor)) {
			return false;
		}
		
		switch (m_type) {
		case LogicNot:
			GenerateUnary(currentCompiler, visitor, "!");
			break;
		case BitNot:
			GenerateUnary(currentCompiler, visitor, "~");
			break;
		case Minus:
			GenerateUnary(currentCompiler, visitor, "__minus");
			break;
		case Plus:
			GenerateUnary(currentCompiler, visitor, "__plus");
			break;
		}
		
		return true;
	}
	
	private void GenerateUnary(IrisCompiler currentCompiler, MethodVisitor visitor, String op) {
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
//		visitor.visitLdcInsn(op);
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
//		visitor.visitInsn(Opcodes.ICONST_0);
//		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, className, "CallMethod", "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisMethod$CallSide;)Lorg/irislang/jiris/core/IrisValue;I", false);
//		visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());

		IrisGenerateHelper.CallMethod(visitor, currentCompiler, op, 0, false);
	}

}
