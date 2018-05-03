package org.irislang.jiris.compiler.expression

import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.IrisNativeJavaClass

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.expression.IrisBinaryExpression.BinaryExpressionType.*

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes

class IrisBinaryExpression(val type: BinaryExpressionType, val leftExpression: IrisExpression, val rightExpression: IrisExpression) : IrisExpression() {

    enum class BinaryExpressionType {
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

        Power
    }


    protected fun OperateGenerate(operator: String, currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {

        if (!rightExpression.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        IrisGenerateHelper.AddParameter(visitor, currentCompiler)

        if (!leftExpression.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        IrisGenerateHelper.CallMethod(visitor, currentCompiler, operator, 1, false)

        IrisGenerateHelper.PopParameter(visitor, currentCompiler, 1)

        return true
    }

    protected fun OperateAssignGenerate(operator: String, currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        // calc
        if (!OperateGenerate(operator, currentCompiler, currentBuilder, visitor)) {
            return false
        }

        // assign
        val result = leftExpression.LeftValue(currentCompiler, currentBuilder, visitor)
        if (!result.result) {
            return false
        }

        IrisGenerateHelper.SetRecord(visitor, currentCompiler)
        LoadLeftValue(currentCompiler, visitor, result)

        IrisGenerateHelper.ClearRecord(visitor, currentCompiler)
        AfterLoad(currentCompiler, visitor, result)

        return true
    }

    private fun AfterLoad(currentCompiler: IrisCompiler, visitor: MethodVisitor, result: IrisExpression.LeftValueResult) {
        if (result.type == IrisExpression.LeftValueType.MemberVariable) {
            IrisGenerateHelper.PopParameter(visitor, currentCompiler, 1)
        } else if (result.type == IrisExpression.LeftValueType.IndexVariable) {
            IrisGenerateHelper.PopParameter(visitor, currentCompiler, 2)
        }
    }

    private fun LoadLeftValue(currentCompiler: IrisCompiler, visitor: MethodVisitor, result: IrisExpression.LeftValueResult) {
        if (result.type != IrisExpression.LeftValueType.MemberVariable && result.type != IrisExpression.LeftValueType.IndexVariable) {
            visitor.visitLdcInsn(result.identifier)
            IrisGenerateHelper.GetRecord(visitor, currentCompiler)

            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfContextVar())

            when (result.type) {
                IrisExpression.LeftValueType.ClassVariable -> visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "SetClassVariable", "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false)
                IrisExpression.LeftValueType.Constance -> visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "SetConstance", "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false)
                IrisExpression.LeftValueType.GlobalVariable -> visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "SetGlobalVariable", "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false)
                IrisExpression.LeftValueType.InstanceVariable -> visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "SetInstanceVariable", "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false)
                IrisExpression.LeftValueType.LocalVariable -> visitor.visitMethodInsn(Opcodes.INVOKESTATIC, currentCompiler.currentClassName, "SetLocalVariable", "(Ljava/lang/String;Lorg/irislang/jiris/core/IrisValue;Lorg/irislang/jiris/core/IrisThreadInfo;Lorg/irislang/jiris/core/IrisContextEnvironment;)Lorg/irislang/jiris/core/IrisValue;", false)
                else -> {
                }
            }
            visitor.visitVarInsn(Opcodes.ASTORE, currentCompiler.GetIndexOfResultValue())
        } else if (result.type == IrisExpression.LeftValueType.MemberVariable) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            IrisGenerateHelper.GetRecord(visitor, currentCompiler)
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo",
                    "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false)

            IrisGenerateHelper.CallMethod(visitor, currentCompiler, result.identifier, 1, false)
        } else if (result.type == IrisExpression.LeftValueType.IndexVariable) {
            visitor.visitVarInsn(Opcodes.ALOAD, currentCompiler.GetIndexOfThreadInfoVar())
            IrisGenerateHelper.GetRecord(visitor, currentCompiler)

            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/irislang/jiris/core/IrisThreadInfo", "AddParameter", "(Lorg/irislang/jiris/core/IrisValue;)V", false)

            IrisGenerateHelper.CallMethod(visitor, currentCompiler, "[]=", 2, false)
        }
    }

    protected fun AssignGenterate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {

        // Right value
        if (!rightExpression.Generate(currentCompiler, currentBuilder, visitor)) {
            return false
        }

        IrisGenerateHelper.SetRecord(visitor, currentCompiler)

        val result = leftExpression.LeftValue(currentCompiler, currentBuilder, visitor)
        if (!result.result) {
            return false
        }

        LoadLeftValue(currentCompiler, visitor, result)

        IrisGenerateHelper.ClearRecord(visitor, currentCompiler)

        AfterLoad(currentCompiler, visitor, result)

        return true
    }


    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)

        var result = false
        when (type) {
            Assign -> result = AssignGenterate(currentCompiler, currentBuilder, visitor)

            AssignAdd -> result = OperateAssignGenerate("+", currentCompiler, currentBuilder, visitor)
            AssignSub -> result = OperateAssignGenerate("-", currentCompiler, currentBuilder, visitor)
            AssignMul -> result = OperateAssignGenerate("*", currentCompiler, currentBuilder, visitor)
            AssignDiv -> result = OperateAssignGenerate("/", currentCompiler, currentBuilder, visitor)
            AssignMod -> result = OperateAssignGenerate("%", currentCompiler, currentBuilder, visitor)
            AssignBitAnd -> result = OperateAssignGenerate("&", currentCompiler, currentBuilder, visitor)
            AssignBitOr -> result = OperateAssignGenerate("|", currentCompiler, currentBuilder, visitor)
            AssignBitXor -> result = OperateAssignGenerate("^", currentCompiler, currentBuilder, visitor)
            AssignBitShr -> result = OperateAssignGenerate(">>", currentCompiler, currentBuilder, visitor)
            AssignBitShl -> result = OperateAssignGenerate("<<", currentCompiler, currentBuilder, visitor)
            AssignBitSar -> result = OperateAssignGenerate(">>>", currentCompiler, currentBuilder, visitor)
            AssignBitSal -> result = OperateAssignGenerate("<<<", currentCompiler, currentBuilder, visitor)

            LogicOr -> result = OperateGenerate("||", currentCompiler, currentBuilder, visitor)
            LogicAnd -> result = OperateGenerate("&&", currentCompiler, currentBuilder, visitor)

            LogicBitOr -> result = OperateGenerate("|", currentCompiler, currentBuilder, visitor)
            LogicBitXor -> result = OperateGenerate("^", currentCompiler, currentBuilder, visitor)
            LogicBitAnd -> result = OperateGenerate("&", currentCompiler, currentBuilder, visitor)

            Equal -> result = OperateGenerate("==", currentCompiler, currentBuilder, visitor)
            NotEqual -> result = OperateGenerate("!=", currentCompiler, currentBuilder, visitor)

            GreatThan -> result = OperateGenerate(">", currentCompiler, currentBuilder, visitor)
            GreatThanOrEqual -> result = OperateGenerate(">=", currentCompiler, currentBuilder, visitor)
            LessThan -> result = OperateGenerate("<", currentCompiler, currentBuilder, visitor)
            LessThanOrEqual -> result = OperateGenerate("<=", currentCompiler, currentBuilder, visitor)

            BitShr -> result = OperateGenerate(">>", currentCompiler, currentBuilder, visitor)
            BitShl -> result = OperateGenerate("<<", currentCompiler, currentBuilder, visitor)
            BitSar -> result = OperateGenerate(">>>", currentCompiler, currentBuilder, visitor)
            BitSal -> result = OperateGenerate("<<<", currentCompiler, currentBuilder, visitor)

            Add -> result = OperateGenerate("+", currentCompiler, currentBuilder, visitor)
            Sub -> result = OperateGenerate("-", currentCompiler, currentBuilder, visitor)
            Mul -> result = OperateGenerate("*", currentCompiler, currentBuilder, visitor)
            Div -> result = OperateGenerate("/", currentCompiler, currentBuilder, visitor)
            Mod -> result = OperateGenerate("%", currentCompiler, currentBuilder, visitor)

            Power -> result = OperateGenerate("**", currentCompiler, currentBuilder, visitor)
        }
        return result
    }

}
