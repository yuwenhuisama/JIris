package com.irisine.jiris.compiler.statement

import com.irisine.jiris.compiler.IrisCompiler
import com.irisine.jiris.compiler.IrisGenerateHelper
import com.irisine.jiris.compiler.assistpart.IrisIdentifier
import jdk.internal.org.objectweb.asm.Opcodes
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import org.irislang.jiris.IrisNativeJavaClass

/**
 * Created by Huisama on 2017/4/8 0008.
 */
class IrisBreakStatement(val lable: IrisIdentifier?) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        visitor.visitJumpInsn(Opcodes.GOTO, currentCompiler.currentLoopEndLable)
        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)
        return true
    }
}
