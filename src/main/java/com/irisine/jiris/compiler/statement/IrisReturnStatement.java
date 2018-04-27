package com.irisine.jiris.compiler.statement;

import com.irisine.jiris.compiler.IrisGenerateHelper;
import org.irislang.jiris.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.expression.IrisExpression;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public class IrisReturnStatement extends IrisStatement {

	private IrisExpression m_returnExpression;
	
	public IrisReturnStatement(IrisExpression returnExpression) {
		m_returnExpression = returnExpression;
	}
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder,
			MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());
		if(m_returnExpression != null) {
			if(!m_returnExpression.Generate(currentCompiler, currentBuilder, visitor)) {
				return false;
			}
		}
		visitor.visitJumpInsn(Opcodes.GOTO, currentCompiler.getCurrentEndLable());
		return true;
	}

}
