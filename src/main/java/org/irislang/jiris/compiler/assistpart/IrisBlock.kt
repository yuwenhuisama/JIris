package org.irislang.jiris.compiler.assistpart

import java.util.LinkedList

import org.irislang.jiris.IrisNativeJavaClass

import org.irislang.jiris.compiler.IrisCompiler
import org.irislang.jiris.compiler.IrisSyntaxUnit
import org.irislang.jiris.compiler.statement.IrisStatement

import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.jar.asm.MethodVisitor

class IrisBlock(val statements: LinkedList<IrisStatement>? = null) : IrisSyntaxUnit() {

    fun Generate(currentCompiler: IrisCompiler, currentBuilder: Builder<IrisNativeJavaClass>,
                 visitor: MethodVisitor): Boolean {

        if (statements == null) {
            return true
        }

        for (statement in statements) {
            if (!statement.Generate(currentCompiler, currentBuilder, visitor)) {
                return false
            }
        }

        return true
    }
}
