package com.irisine.jiris.compiler.assistpart;

import java.util.LinkedList;

import org.irislang.jiris.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.IrisSyntaxUnit;
import com.irisine.jiris.compiler.statement.IrisStatement;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;

public class IrisBlock extends IrisSyntaxUnit {
	private LinkedList<IrisStatement> m_statments = null;
	
	public IrisBlock(LinkedList<IrisStatement> statements) {
		m_statments = statements;
	}
	
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder,
			MethodVisitor visitor) {
		
		if(m_statments == null) {
			return true;
		}
		
		for(IrisStatement statement : m_statments) {
			if(!statement.Generate(currentCompiler, currentBuilder, visitor)) {
				return false;
			}
		}
		
		return true;
	}
}
