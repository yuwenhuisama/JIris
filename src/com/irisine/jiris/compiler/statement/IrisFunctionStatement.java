package com.irisine.jiris.compiler.statement;

import java.util.LinkedList;
import java.util.Stack;

import com.irisine.jiris.compiler.parser.IrisParserDefineType;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;
import com.irisine.jiris.compiler.assistpart.IrisBlock;
import com.irisine.jiris.compiler.assistpart.IrisDeferredBlock;
import com.irisine.jiris.compiler.assistpart.IrisFunctionHeader;
import com.irisine.jiris.compiler.assistpart.IrisIdentifier;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public class IrisFunctionStatement extends IrisStatement {

	private IrisFunctionHeader m_functionHeader = null;
	private IrisBlock m_withBlock = null;
	@SuppressWarnings("unused")
	private IrisBlock m_withoutBlock = null;
	private IrisBlock m_block = null;
	
	private String m_blockNativeName = "";
	private String m_withBlockNativeName = "";
	private String m_withoutBlockNativeName = "";
	
	public String getBlockNativeName() {
		return m_blockNativeName;
	}
	
	public String getWithBlockNativeName() {
		return m_withBlockNativeName;
	}
	
	public String getWithoutBlockNativeName() {
		return m_withoutBlockNativeName;
	}
	
	public IrisFunctionStatement(IrisFunctionHeader functionHeader, IrisBlock withBlock, IrisBlock withouBlock,
                                 IrisBlock block, IrisParserDefineType defineType) {
		m_functionHeader = functionHeader;
		m_withBlock = withBlock;
		m_withoutBlock = withBlock;
		m_block = block;

        MakeDefferdBlock(IrisCompiler.INSTANCE, m_functionHeader, defineType);
    }

    private void MakeDefferdBlock(IrisCompiler currentCompiler, IrisFunctionHeader functionHeader, IrisParserDefineType defineType) {
        StringBuilder buffer = new StringBuilder();
        StringBuilder buffer_with = new StringBuilder();
        StringBuilder buffer_without = new StringBuilder();
        String functionName = functionHeader.getFunctionName().getIdentifier();
        boolean isClassMethod = functionHeader.isClassMethod();

        switch(defineType) {
        case Class:
        case Module:
            buffer.append(functionName).append("$").append(currentCompiler.getCurrentDefineName());
            buffer_with.append(buffer.toString());
            buffer_without.append(buffer.toString());
            if(isClassMethod) {
                buffer.append("$cls$mth$");
                buffer_with.append("$cls$mth$with$");
                buffer_without.append("$cls$mth$without$");
            } else{
                buffer.append("$ins$mth$");
                buffer_with.append("$ins$mth$with$");
                buffer_without.append("$ins$mth$without$");
            }
            buffer.append(currentCompiler.GetBlockNameCount(buffer.toString()));
            buffer_with.append(currentCompiler.GetBlockNameCount(buffer_with.toString()));
            buffer_without.append(currentCompiler.GetBlockNameCount(buffer_without.toString()));
            break;
        case Interface:
            /* Error */
            break;
        case Normal:
            buffer.append(functionName)
                .append("$main$mth$")
                .append(currentCompiler.GetBlockNameCount(buffer.toString()));

            buffer_with.append(functionName)
                .append("$main$mth$with$")
                .append(currentCompiler.GetBlockNameCount(buffer_with.toString()));

            buffer_without.append(functionName)
                .append("$main$mth$without$")
                .append(currentCompiler.GetBlockNameCount(buffer_without.toString()));
            break;
        }

        m_blockNativeName = buffer.toString();
        m_withBlockNativeName = buffer_with.toString();
        m_withoutBlockNativeName = buffer_without.toString();

        currentCompiler.PushDeferredStatement(new IrisDeferredBlock(m_block, m_blockNativeName));

        if(m_withBlock != null) {
            currentCompiler.PushDeferredStatement(new IrisDeferredBlock(m_withBlock, m_withBlockNativeName));
            currentCompiler.PushDeferredStatement(new IrisDeferredBlock(m_withoutBlock, m_withBlockNativeName));
        }
    }

    @Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder,
			MethodVisitor visitor) {

		String functionName = m_functionHeader.getFunctionName().getIdentifier(); 
		LinkedList<IrisIdentifier> parameters = m_functionHeader.getParameters();
		String nativeName = m_blockNativeName;
		
		// Preinvoke
        //visitor.visitVarInsn(Opcodes.ALOAD, 0);
		visitor.visitVarInsn(Opcodes.ALOAD, 0);
		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);

		visitor.visitLdcInsn(nativeName);

		visitor.visitLdcInsn(functionName);
		
		if(parameters != null) {
			visitor.visitIntInsn(Opcodes.BIPUSH, parameters.size());
			visitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");
			
			for(int i = 0; i < parameters.size(); ++i) {
				visitor.visitInsn(Opcodes.DUP);
				visitor.visitIntInsn(Opcodes.BIPUSH, i);
				visitor.visitLdcInsn(parameters.get(i).getIdentifier());
				visitor.visitInsn(Opcodes.AASTORE);
			}

		} else {
			visitor.visitInsn(Opcodes.ACONST_NULL);
		}

		if(m_functionHeader.getVariableParameter() != null) {
			visitor.visitLdcInsn(m_functionHeader.getVariableParameter().getIdentifier());
		} else {
			visitor.visitInsn(Opcodes.ACONST_NULL);
		}
		
		if(m_withBlock != null) {
//			StringBuffer withBlockBuffer = new StringBuffer();
//			withBlockBuffer.append(functionName).append("$main$mth_with$").append(currentCompiler.GetBlockNameCount(withBlockBuffer.toString()));
			visitor.visitLdcInsn(m_withBlockNativeName);
			
//			withBlockBuffer = new StringBuffer();
//			withBlockBuffer.append(functionName).append("$main$mth_without$").append(currentCompiler.GetBlockNameCount(withBlockBuffer.toString()));
			visitor.visitLdcInsn(m_withoutBlockNativeName);
		} else {
			visitor.visitInsn(Opcodes.ACONST_NULL);
			visitor.visitInsn(Opcodes.ACONST_NULL);
		}
		
		visitor.visitFieldInsn(Opcodes.GETSTATIC, "org/irislang/jiris/core/IrisMethod$MethodAuthority", "Everyone", "Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;");
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());

		if(m_functionHeader.isClassMethod()) {
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "DefineClassMethod",
                    "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false);
        }
        else {
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(),
                    "DefineInstanceMethod", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/irislang/jiris/core/IrisMethod$MethodAuthority;Lorg/irislang/jiris/core/IrisContextEnvironment;Lorg/irislang/jiris/core/IrisThreadInfo;)V", false);
        }
		
		return true;
	}

}
