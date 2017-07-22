package com.irisine.jiris.compiler.expression;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisSyntaxUnit;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;

public abstract class IrisExpression extends IrisSyntaxUnit {
	
	protected enum LeftValueType {
		Constance,
		GlobalVariable,
		ClassVariable,
		InstanceVariable,
		LocalVariable,
		MemberVariable,
		IndexVariable,
	}
	
	protected static class LeftValueResult {
		private LeftValueType m_type = LeftValueType.LocalVariable;
		private boolean m_result = false;
		private String m_identifier = "";
		
		public LeftValueType getType() {
			return m_type;
		}
		public void setType(LeftValueType type) {
			m_type = type;
		}
		public boolean getResult() {
			return m_result;
		}
		public void setResult(boolean result) {
			m_result = result;
		}
		public String getIdentifier() {
			return m_identifier;
		}
		public void setIdentifier(String identifier) {
			m_identifier = identifier;
		}
	}
	
	protected int m_lineNumber = 0;
	
	public abstract boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor);

	public LeftValueResult LeftValue(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
		LeftValueResult result = new LeftValueResult();
		return result;
	}
	
	public IrisExpression() {
	}
	
	public int getLineNumber() {
		return m_lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.m_lineNumber = lineNumber;
	}
}
