package com.irisine.jiris.compiler.expression;

import com.irisine.jiris.compiler.IrisGenerateHelper;
import org.irislang.jiris.compiler.IrisNativeJavaClass;

import com.irisine.jiris.compiler.IrisCompiler;

import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

import java.util.Vector;

public class IrisBinaryExpression extends IrisExpression {

	public enum BinaryExpressionType {
		Assign,
		
		AssignAdd,
		AssignSub,
		AssignMul,
		AssignDiv,
		AssignMod,
		AssignBitAnd,
		AssignBitOr,
		AssignBitXor,
		AssignBitShr,
		AssignBitShl,
		AssignBitSar,
		AssignBitSal,

		LogicOr,
		LogicAnd,

		LogicBitOr,
		LogicBitXor,
		LogicBitAnd,

		Equal,
		NotEqual,

		GreatThan,
		GreatThanOrEqual,
		LessThan,
		LessThanOrEqual,

		BitShr,
		BitShl,
		BitSar,
		BitSal,

		Add,
		Sub,
		Mul,
		Div,
		Mod,

		Power,
	}
	
	private IrisExpression m_leftExpression = null;
	private IrisExpression m_rightExpression = null;
	private BinaryExpressionType m_type = BinaryExpressionType.Assign;
	
	public IrisBinaryExpression(BinaryExpressionType type, IrisExpression leftExpression, IrisExpression rightExpression) {
		super();
		m_leftExpression = leftExpression;
		m_rightExpression = rightExpression;
		m_type = type;
	}
	
	protected boolean OperateGenerate(String operator, IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
		
		if(!m_rightExpression.Generate(currentCompiler, currentBuilder, visitor)) {
			return false;
		}

//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
//		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false);

		IrisGenerateHelper.AddParameter(visitor, currentCompiler);

		if(!m_leftExpression.Generate(currentCompiler, currentBuilder, visitor)) {
			return false;
		}

//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
//		visitor.visitLdcInsn(operator);
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
//		visitor.visitInsn(Opcodes.ICONST_1);
//		visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "CallMethod",  "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;I)Lorg/irislang/jiris/core/IrisValue;", false);
//		visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());

        IrisGenerateHelper.CallMethod(visitor, currentCompiler, operator, 1, false);

//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//		visitor.visitInsn(Opcodes.ICONST_1);
//		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopParameter", "(I)V", false);

        IrisGenerateHelper.PopParameter(visitor, currentCompiler, 1);

		return true;
	}
	
	protected boolean OperateAssignGenerate(String operator, IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
		// calc
		if(!OperateGenerate(operator, currentCompiler, currentBuilder, visitor)) {
			return false;
		}
		
		// assign
		LeftValueResult result = m_leftExpression.LeftValue(currentCompiler, currentBuilder, visitor);
		if(!result.getResult()) {
			return false;
		}
		
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
//		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "setRecord", "(Lorg/irislang/jiris/core/IrisValue;)V", false);

        IrisGenerateHelper.SetRecord(visitor, currentCompiler);
		LoadLeftValue(currentCompiler, visitor, result);
		//visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
		
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//		visitor.visitInsn(Opcodes.ACONST_NULL);
//		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "setRecord", "(Lorg/irislang/jiris/core/IrisValue;)V", false);

        IrisGenerateHelper.ClearRecord(visitor, currentCompiler);
		AfterLoad(currentCompiler, visitor, result);

		return true;
	}

	private void AfterLoad(IrisCompiler currentCompiler, MethodVisitor visitor, LeftValueResult result) {
		if(result.getType() == LeftValueType.MemberVariable) {
//			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//			visitor.visitInsn(Opcodes.ICONST_1);
//			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopParameter", "(I)V", false);
            IrisGenerateHelper.PopParameter(visitor, currentCompiler, 1);
		} else if(result.getType() == LeftValueType.IndexVariable) {
			//visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
			//visitor.visitInsn(Opcodes.ICONST_2);
			//visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "PopParameter", "(I)V", false);
            IrisGenerateHelper.PopParameter(visitor, currentCompiler, 2);
		}
	}

	private void LoadLeftValue(IrisCompiler currentCompiler, MethodVisitor visitor, LeftValueResult result) {
		if(result.getType() != LeftValueType.MemberVariable && result.getType() != LeftValueType.IndexVariable) {
			visitor.visitLdcInsn(result.getIdentifier());

//			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "getRecord", "()Lorg/irislang/jiris/core/IrisValue;", false);

            IrisGenerateHelper.GetRecord(visitor, currentCompiler);

			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());

			switch(result.getType()) {
			case ClassVariable:
				visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetClassVariable", "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false);
				break;
			case Constance:
				visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetConstance", "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false);
				break;
			case GlobalVariable:
				visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetGlobalVariable", "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false);
				break;
			case InstanceVariable:
				visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetInstanceVariable", "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false);
				break;
			case LocalVariable:
				visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.getCurrentClassName(), "SetLocalVariable", "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false);
				break;
			default:
				break;
			}
		} else if(result.getType() == LeftValueType.MemberVariable){

		}
		else if(result.getType() == LeftValueType.IndexVariable) {
			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "getRecord", "()Lorg/irislang/jiris/core/IrisValue;", false);
            IrisGenerateHelper.GetRecord(visitor, currentCompiler);

			visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false);

			IrisGenerateHelper.CallMethod(visitor, currentCompiler, "[]=", 2, false);
			IrisGenerateHelper.PopParameter(visitor, currentCompiler, 2);
//			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
//			visitor.visitLdcInsn("[]=");
//			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//			visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar());
//			visitor.visitInsn(Opcodes.ICONST_2);
//			visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/compiler/IrisNativeJavaClass", "CallMethod", "(Lorg/irislang/jiris/core/IrisValue;Ljava/lang/String;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;I)Lorg/irislang/jiris/core/IrisValue;", false);
		}
        visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
    }

	protected boolean AssignGenterate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
		
		// Right value
		if(!m_rightExpression.Generate(currentCompiler, currentBuilder, visitor)) {
			return false;
		}
		
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfResultValue());
//		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "setRecord", "(Lorg/irislang/jiris/core/IrisValue;)V", false);

        IrisGenerateHelper.SetRecord(visitor, currentCompiler);

		LeftValueResult result = m_leftExpression.LeftValue(currentCompiler, currentBuilder, visitor);
		if(!result.getResult()) {
			return false;
		}

		LoadLeftValue(currentCompiler, visitor, result);

		//visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue());
		
//		visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar());
//		visitor.visitInsn(Opcodes.ACONST_NULL);
//		visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "setRecord", "(Lorg/irislang/jiris/core/IrisValue;)V", false);

        IrisGenerateHelper.ClearRecord(visitor, currentCompiler);

		AfterLoad(currentCompiler, visitor, result);

		return true;
	}
	
	
	@Override
	public boolean Generate(IrisCompiler currentCompiler, Builder<IrisNativeJavaClass> currentBuilder, MethodVisitor visitor) {
		boolean result = false;
		switch (m_type) {
		case Assign:
			result = AssignGenterate(currentCompiler, currentBuilder, visitor);
			break;
			
		case AssignAdd:
			result = OperateAssignGenerate("+", currentCompiler, currentBuilder, visitor);
			break;
		case AssignSub:
			result = OperateAssignGenerate("-", currentCompiler, currentBuilder, visitor);
			break;
		case AssignMul:
			result = OperateAssignGenerate("*", currentCompiler, currentBuilder, visitor);
			break;
		case AssignDiv:
			result = OperateAssignGenerate("/", currentCompiler, currentBuilder, visitor);
			break;
		case AssignMod:
			result = OperateAssignGenerate("%", currentCompiler, currentBuilder, visitor);
			break;
		case AssignBitAnd:
			result = OperateAssignGenerate("&", currentCompiler, currentBuilder, visitor);
			break;
		case AssignBitOr:
			result = OperateAssignGenerate("|", currentCompiler, currentBuilder, visitor);
			break;
		case AssignBitXor:
			result = OperateAssignGenerate("^", currentCompiler, currentBuilder, visitor);
			break;
		case AssignBitShr:
			result = OperateAssignGenerate(">>", currentCompiler, currentBuilder, visitor);
			break;
		case AssignBitShl:
			result = OperateAssignGenerate("<<", currentCompiler, currentBuilder, visitor);
			break;
		case AssignBitSar:
			result = OperateAssignGenerate(">>>", currentCompiler, currentBuilder, visitor);
			break;
		case AssignBitSal:
			result = OperateAssignGenerate("<<<", currentCompiler, currentBuilder, visitor);
			break;

		case LogicOr:
			result = OperateGenerate("||", currentCompiler, currentBuilder, visitor);
			break;
		case LogicAnd:
			result = OperateGenerate("&&", currentCompiler, currentBuilder, visitor);
			break;

		case LogicBitOr:
			result = OperateGenerate("|", currentCompiler, currentBuilder, visitor);
			break;
		case LogicBitXor:
			result = OperateGenerate("^", currentCompiler, currentBuilder, visitor);
			break;
		case LogicBitAnd:
			result = OperateGenerate("&", currentCompiler, currentBuilder, visitor);
			break;

		case Equal:
			result = OperateGenerate("==", currentCompiler, currentBuilder, visitor);
			break;
		case NotEqual:
			result = OperateGenerate("!=", currentCompiler, currentBuilder, visitor);
			break;

		case GreatThan:
			result = OperateGenerate(">", currentCompiler, currentBuilder, visitor);
			break;
		case GreatThanOrEqual:
			result = OperateGenerate(">=", currentCompiler, currentBuilder, visitor);
			break;
		case LessThan:
			result = OperateGenerate("<", currentCompiler, currentBuilder, visitor);
			break;
		case LessThanOrEqual:
			result = OperateGenerate("<=", currentCompiler, currentBuilder, visitor);
			break;

		case BitShr:
			result = OperateGenerate(">>", currentCompiler, currentBuilder, visitor);
			break;
		case BitShl:
			result = OperateGenerate("<<", currentCompiler, currentBuilder, visitor);
			break;
		case BitSar:
			result = OperateGenerate(">>>", currentCompiler, currentBuilder, visitor);
			break;
		case BitSal:
			result = OperateGenerate("<<<", currentCompiler, currentBuilder, visitor);
			break;

		case Add:
			result = OperateGenerate("+", currentCompiler, currentBuilder, visitor);
			break;
		case Sub:
			result = OperateGenerate("-", currentCompiler, currentBuilder, visitor);
			break;
		case Mul:
			result = OperateGenerate("*", currentCompiler, currentBuilder, visitor);
			break;
		case Div:
			result = OperateGenerate("/", currentCompiler, currentBuilder, visitor);
			break;
		case Mod:
			result = OperateGenerate("%", currentCompiler, currentBuilder, visitor);
			break;

		case Power:
			result = OperateGenerate("**", currentCompiler, currentBuilder, visitor);
			break;
		}
		return result;
	}

}
