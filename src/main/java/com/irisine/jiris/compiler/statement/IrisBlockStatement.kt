package com.irisine.jiris.compiler.statement

import com.irisine.jiris.compiler.IrisCompiler
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.jar.asm.MethodVisitor
import org.irislang.jiris.IrisNativeJavaClass

/**
 * Created by yuwen on 2017/7/16 0016.
 */
class IrisBlockStatement : IrisStatement() {
    override fun Generate(currentCompiler: IrisCompiler, currentBuilder: DynamicType.Builder<IrisNativeJavaClass>, visitor: MethodVisitor): Boolean {
        return false
    }
}
