package org.irislang.jiris.compiler.statement

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.IrisGenerateHelper
import org.irislang.jiris.compiler.assistpart.IrisIdentifier
import jdk.internal.org.objectweb.asm.Opcodes
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import org.irislang.jiris.IrisNativeJavaClass

/**
 * Created by Huisama on 2017/4/8 0008.
 */
class IrisContinueStatement(val lable: IrisIdentifier?) : IrisStatement() {

    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        IrisGenerateHelper.SetLineNumber(visitor, currentCompiler, lineNumber)
        visitor.visitJumpInsn(Opcodes.GOTO, currentCompiler.currentLoopContinueLable)
        IrisGenerateHelper.StackFrameOpreate(visitor, currentCompiler)
        return true
    }
}
