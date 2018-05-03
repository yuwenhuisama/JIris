package com.irisine.jiris.compiler.assistpart

import java.util.LinkedList

import org.irislang.jiris.IrisNativeJavaClass

import com.irisine.jiris.compiler.IrisCompiler
import com.irisine.jiris.compiler.IrisSyntaxUnit
import com.irisine.jiris.compiler.statement.IrisStatement

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
