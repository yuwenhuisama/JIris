package com.irisine.jiris.compiler.expression;

import java.util.LinkedList;

import com.irisine.jiris.compiler.IrisGenerateHelper;
import com.irisine.jiris.compiler.assistpart.IrisDeferredBlock;
import net.bytebuddy.jar.asm.Type;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.assistpart.IrisClosureBlockLiteral;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public class IrisFunctionCallExpression extends IrisExpression {
	
	private IrisExpression m_object = null;
	private IrisIdentifier m_functionName = null;
	private LinkedList<IrisExpression> m_parameters = null;
	@SuppressWarnings("unused")
	private IrisClosureBlockLiteral m_closureBlock = null;

	private String m_nativeName = null;

	public IrisFunctionCallExpression(IrisExpression object, IrisIdentifier functionName, LinkedList<IrisExpression> parameters, IrisClosureBlockLiteral closureBlock) {
		m_object = object;
		m_functionName = functionName;
		m_parameters = parameters;
		m_closureBlock = closureBlock;

		if(m_closureBlock != null) {
		    MakeDefferedBlock(IrisCompiler.INSTANCE);
        }
	}

    private void MakeDefferedBlock(IrisCompiler currentCompiler) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("block$").append(currentCompiler.GetBlockNameCount("block$"));
        m_nativeName = buffer.toString();
        currentCompiler.PushDeferredStatement(new IrisDeferredBlock(m_closureBlock.getBlock(), m_nativeName));
    }

    @Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder,
			MethodVisitor visitor) {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, getLineNumber());

        // create closure block if existing
        if(m_closureBlock != null) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
            //visitor.visitInsn(Opcodes.ICONST_3);

            if(m_closureBlock.getParameters() != null) {
                IrisGenerateHelper.LoadInteger(visitor, m_closureBlock.getParameters().size());
                visitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");

                for (int i = 0; i < m_closureBlock.getParameters().size(); i++) {
                    visitor.visitInsn(Opcodes.DUP);
                    IrisGenerateHelper.LoadInteger(visitor, i);
                    visitor.visitLdcInsn(m_closureBlock.getParameters().get(i).getIdentifier());
                    visitor.visitInsn(Opcodes.AASTORE);
                }
            }
            else {
                visitor.visitInsn(Opcodes.ACONST_NULL);
            }

            if(m_closureBlock.getVariableParameter() != null) {
                visitor.visitLdcInsn(m_closureBlock.getVariableParameter().getIdentifier());
            }
            else {
                visitor.visitInsn(Opcodes.ACONST_NULL);
            }

            visitor.visitLdcInsn(Type.getType("L" + currentCompiler.getCurrentClassName() + ";"));
            visitor.visitLdcInsn(m_nativeName);
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(),
                    "CreateClosureBlock",
                    "(Lorg/irislang/jiris/core/IrisContextEnvironment;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;)Lorg/irislang/jiris/core/IrisValue;", false);
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
        }

		int pushedCount = 0;
		
		if(m_parameters != null) {
			for(IrisExpression expression : m_parameters) {
				if(!expression.Generate(currentCompiler, currentBuilder, visitor)) {
					return false;
				}
//				visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//				visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
//				visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false);
				IrisGenerateHelper.AddParameter(visitor, currentCompiler);
			}
			pushedCount = m_parameters.size();
		}
		
		if(m_object == null) {
			//visitor.visitInsn(Opcodes.ACONST_NULL);
			IrisGenerateHelper.CallMethod(visitor, currentCompiler, m_functionName.getIdentifier(), pushedCount, true);
		} else {
			if(!m_object.Generate(currentCompiler, currentBuilder, visitor)) {
				return false;
			}
			//visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
			IrisGenerateHelper.CallMethod(visitor, currentCompiler, m_functionName.getIdentifier(), pushedCount, false);
		}
		
//		visitor.visitLdcInsn(m_functionName.getIdentifier());
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
//		visitor.visitLdcInsn(new Integer(pushedCount));
//		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "CallMethod", "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;I)Lorg/irislang/jiris/core/IrisValue;", false);
//		visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
		
		if(pushedCount > 0) {
//			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//			visitor.visitLdcInsn(new Integer(pushedCount));
//			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopParameter", "(I)V", false);
			IrisGenerateHelper.PopParameter(visitor, currentCompiler, pushedCount);
		}

		if(m_closureBlock != null) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "ClearClosureBlock", "(Lorg/irislang/jiris/core/IrisThreadInfo;)V", false);
        }

		return true;
	}

}
