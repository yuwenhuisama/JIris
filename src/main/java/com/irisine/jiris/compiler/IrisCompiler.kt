package com.irisine.jiris.compiler

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedList

import net.bytebuddy.description.field.FieldDescription
import net.bytebuddy.description.field.FieldList
import net.bytebuddy.description.method.MethodList
import net.bytebuddy.pool.TypePool
import org.irislang.jiris.IrisInterpreter
import org.irislang.jiris.IrisNativeJavaClass
import org.irislang.jiris.IrisRunnable
import org.irislang.jiris.core.IrisContextEnvironment
import org.irislang.jiris.core.IrisThreadInfo
import org.irislang.jiris.core.IrisValue
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.dev.IrisDevUtil

import com.irisine.jiris.compiler.assistpart.IrisBlock
import com.irisine.jiris.compiler.assistpart.IrisDeferredBlock
import com.irisine.jiris.compiler.parser.IrisParser
import com.irisine.jiris.compiler.parser.ParseException
import com.irisine.jiris.compiler.statement.IrisStatement

import net.bytebuddy.ByteBuddy
import net.bytebuddy.asm.AsmVisitorWrapper
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.modifier.*
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition
import net.bytebuddy.dynamic.scaffold.InstrumentedType
import net.bytebuddy.implementation.Implementation
import net.bytebuddy.implementation.Implementation.Context
import net.bytebuddy.implementation.bytecode.ByteCodeAppender
import net.bytebuddy.jar.asm.ClassVisitor
import net.bytebuddy.jar.asm.ClassWriter
import net.bytebuddy.jar.asm.Label
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes

enum class IrisCompiler {

    INSTANCE;

    private var m_currentFile = ""
    private var m_curretByteBuddy: ByteBuddy? = null
    var currentClassName = ""
        private set

    val statements = LinkedList<IrisStatement>()
    var currentBuilder: Builder<IrisNativeJavaClass>? = null
        private set

    private val m_uniqueStrings = LinkedList<String>()
    private val m_uniqueStringHash = HashMap<String, Int>()

    private val m_deferredStatements = LinkedList<IrisDeferredBlock>()
    private val m_blockNameCount = HashMap<String, Int>()

    var nativeJavaClass: Class<*>? = null
        private set

    var currentDefineType = CurrentDefineType.Normal
    var currentDefineName = ""

    var isStaticDefine = false

    var currentEndLable: Label? = null
        private set

    val irregularVariableLabelPairs = LinkedList<IrregularVariableLabelPair>()

    var currentLoopContinueLable: Label? = null

    var currentLoopEndLable: Label? = null

    var isFirstStackFrameGenerated = false

    enum class CurrentDefineType {
        Normal,
        Class,
        Module,
        Interface
    }

    class IrregularVariableLabelPair(val fromLabel: Label, val toLabel: Label)

    fun AddIrregularVariableLabelPair(pair: IrregularVariableLabelPair) {
        irregularVariableLabelPairs.push(pair)
    }

    fun ClearIrregularVariableLabelPair() {
        irregularVariableLabelPairs.clear()
    }

    fun GetIndexOfContextVar(): Int {
        return if (isStaticDefine) 0 else 1
    }

    fun GetIndexOfThreadInfoVar(): Int {
        return GetIndexOfContextVar() + 1
    }

    fun GetIndexOfResultValue(): Int {
        return GetIndexOfContextVar() + 2
    }

    fun GetIndexOfIrregularVar(): Int {
        return GetIndexOfContextVar() + 3
    }

    fun GetBlockNameCount(name: String): Int {
        val count = m_blockNameCount[name]
        if (count == null) {
            m_blockNameCount[name] = 1
            return 1
        } else {
            m_blockNameCount[name] = count + 1
            return count + 1
        }
    }

    fun PushDeferredStatement(statement: IrisDeferredBlock) {
        m_deferredStatements.add(statement)
    }

    fun PopDeferredStatement(): IrisDeferredBlock? {
        return m_deferredStatements.poll()
    }

    fun IsDeferredStatementQueueEmpty(): Boolean {
        return m_deferredStatements.isEmpty()
    }

    fun AddUniqueString(str: String) {
        if (!m_uniqueStringHash.containsKey(str)) {
            m_uniqueStrings.add(str)
            m_uniqueStringHash[str] = m_uniqueStrings.size - 1
        }
    }

    fun GetUinqueIndex(str: String): Int? {
        return m_uniqueStringHash[str]
    }

    fun AddStatement(statement: IrisStatement) {
        statements.add(statement)
    }

    private fun Generate(): Boolean {

        val index = m_currentFile.lastIndexOf(".")
        val index2 = m_currentFile.lastIndexOf("/")
        IrisInterpreter.INSTANCE.InceamJavaClassFileNumber()
        val filePath = m_currentFile.substring(0, index2 + 1)
        currentClassName = m_currentFile.substring(index2 + 1, index) + "$" + "ir"

        m_curretByteBuddy = ByteBuddy()
        currentBuilder = m_curretByteBuddy!!
                .subclass(IrisNativeJavaClass::class.java)
                .implement(IrisRunnable::class.java)
                .visit(object : AsmVisitorWrapper {
                    override fun mergeReader(arg0: Int): Int {
                        return 0
                    }

                    override fun wrap(instrumentedType: TypeDescription, classVisitor: ClassVisitor, implementationContext: Context, typePool: TypePool, fields: FieldList<FieldDescription.InDefinedShape>, methods: MethodList<*>, writerFlags: Int, readerFlags: Int): ClassVisitor {
                        return classVisitor
                    }

                    override fun mergeWriter(arg0: Int): Int {
                        return ClassWriter.COMPUTE_FRAMES
                    }

                    //			@Override
                    //			public ClassVisitor wrap(TypeDescription arg0, ClassVisitor arg1, int arg2, int arg3) {
                    //				return arg1;
                    //			}
                }).name(currentClassName)

        currentBuilder = currentBuilder!!
                .defineField("sm_uniqueStringObjects",
                        ArrayList::class.java,
                        Visibility.PUBLIC,
                        FieldManifestation.FINAL,
                        Ownership.STATIC)
                .defineField("sm_scriptFileName",
                        String::class.java,
                        Visibility.PUBLIC,
                        FieldManifestation.FINAL,
                        Ownership.STATIC)
                .initializer { mv, arg1, arg2 ->
                    mv.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList")
                    mv.visitInsn(Opcodes.DUP)
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false)
                    mv.visitFieldInsn(Opcodes.PUTSTATIC, currentClassName, "sm_uniqueStringObjects", "Ljava/util/ArrayList;")

                    mv.visitLdcInsn(m_currentFile)
                    mv.visitFieldInsn(Opcodes.PUTSTATIC, currentClassName, "sm_scriptFileName",
                            "Ljava/lang/String;")

                    mv.visitInsn(Opcodes.RETURN)
                    ByteCodeAppender.Size(0, 0)
                }

        var mainDefininition = GenerateIrisNativeJavaMethod("run", object : Implementation {
            override fun prepare(arg0: InstrumentedType): InstrumentedType {
                return arg0
            }

            override fun appender(arg0: Implementation.Target): ByteCodeAppender {
                return RunMethodAppender()
            }
        })

        while (!IsDeferredStatementQueueEmpty()) {
            val deferredStatement = PopDeferredStatement()

            mainDefininition = mainDefininition.defineMethod(deferredStatement!!.generateName,
                    IrisValue::class.java,
                    Visibility.PUBLIC,
                    Ownership.STATIC)
                    .withParameters(IrisContextEnvironment::class.java, IrisThreadInfo::class.java)
                    .throwing(IrisExceptionBase::class.java)
                    .intercept(object : Implementation {
                        override fun prepare(arg0: InstrumentedType): InstrumentedType {
                            return arg0
                        }

                        override fun appender(arg0: Implementation.Target): ByteCodeAppender {
                            return UserMethodAppender(deferredStatement.block)
                        }
                    })
        }

        val classObj = mainDefininition.make()

        nativeJavaClass = classObj.load(javaClass.getClassLoader()).loaded
        InitUniqueStringList(nativeJavaClass!!)

        val jvmcode = classObj.bytes

        val file = File("$filePath$currentClassName.class")
        if (file.exists()) {
            file.delete()
        }
        val fos: FileOutputStream
        try {
            fos = FileOutputStream(file)
            fos.write(jvmcode)
            fos.close()
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return true
    }

    private fun InitUniqueStringList(javaClass: Class<*>) {
        var values: ArrayList<IrisValue>? = null
        try {
            values = javaClass.getField("sm_uniqueStringObjects").get(null) as ArrayList<IrisValue>
        } catch (e1: IllegalArgumentException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
        } catch (e1: IllegalAccessException) {
            e1.printStackTrace()
        } catch (e1: NoSuchFieldException) {
            e1.printStackTrace()
        } catch (e1: SecurityException) {
            e1.printStackTrace()
        }

        for (uString in m_uniqueStrings) {
            values!!.add(IrisDevUtil.CreateUniqueString(uString))
        }
    }

    private fun RunMethodAppender(): ByteCodeAppender {
        return RunMethodAppender(this)
    }

    private fun UserMethodAppender(block: IrisBlock): ByteCodeAppender {
        return UserMethodAppender(this, block)
    }

    private fun GenerateIrisNativeJavaMethod(methodName: String, impl: Implementation): ReceiverTypeDefinition<IrisNativeJavaClass> {

        return currentBuilder!!.defineMethod(methodName, IrisValue::class.java, Visibility.PUBLIC)
                .withParameters(IrisContextEnvironment::class.java, IrisThreadInfo::class.java)
                .throwing(IrisExceptionBase::class.java)
                .intercept(impl)
    }

    fun LoadScriptFromPath(path: String): Boolean {
        m_currentFile = path
        val parser = IrisParser(path)
        try {
            parser.translation_unit()
        } catch (e: Exception) {
            println(e.message)
            //e.printStackTrace();
            parser.ReInit(System.`in`)
            return false
        } catch (e: Error) {
            println(e.message)
            return false
        }

        return Generate()
    }

    fun TestLoad(path: String): Boolean {
        val parser = IrisParser(path)
        try {
            parser.translation_unit()
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return true
    }

    private class RunMethodAppender(val compiler: IrisCompiler) : ByteCodeAppender {

        override fun apply(mv: MethodVisitor, arg1: Context, arg2: MethodDescription): ByteCodeAppender.Size {

            compiler.ClearIrregularVariableLabelPair()

            compiler.isFirstStackFrameGenerated = false
            compiler.isStaticDefine = false
            val lableFrom = Label()
            val lableEnd = Label()
            compiler.currentEndLable = Label()

            IrisGenerateHelper.SetFileName(mv, compiler)

            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "Nil",
                    "()Lorg/irislang/jiris/core/IrisValue;", false)
            mv.visitVarInsn(Opcodes.ASTORE, 3)

            mv.visitLabel(lableFrom)
            for (statement in compiler.statements) {
                statement.Generate(compiler, compiler.currentBuilder!!, mv)
            }

            mv.visitLabel(compiler.currentEndLable)

            mv.visitVarInsn(Opcodes.ALOAD, 3)
            mv.visitInsn(Opcodes.ARETURN)

            mv.visitLabel(lableEnd)
            compiler.currentEndLable = null

            mv.visitLocalVariable("this", "L" + compiler.currentClassName + ";", null, lableFrom, lableEnd, 0)
            mv.visitLocalVariable("context", "Lorg/irislang/jiris/core/IrisContextEnvironment;", null, lableFrom, lableEnd, 1)
            mv.visitLocalVariable("threadInfo", "Lorg/irislang/jiris/core/IrisThreadInfo;", null, lableFrom, lableEnd, 2)
            mv.visitLocalVariable("resultValue", "Lorg/irislang/jiris/core/IrisValue;", null, lableFrom, lableEnd, 3)

            for (pair in compiler.irregularVariableLabelPairs) {
                mv.visitLocalVariable("runtimeIrregular",
                        "Lorg/irislang/jiris/core/exceptions/IrisRuntimeException;", null, pair.fromLabel,
                        pair.toLabel, 4)
            }

            return ByteCodeAppender.Size(0, 0)
        }

    }

    private class UserMethodAppender(compiler: IrisCompiler, block: IrisBlock) : ByteCodeAppender {

        internal var m_compiler: IrisCompiler? = null
        internal var m_block: IrisBlock? = null

        init {
            m_compiler = compiler
            m_block = block
        }

        override fun apply(mv: MethodVisitor, arg1: Context, arg2: MethodDescription): ByteCodeAppender.Size {

            m_compiler!!.ClearIrregularVariableLabelPair()

            m_compiler!!.isFirstStackFrameGenerated = false
            m_compiler!!.isStaticDefine = true
            val lableFrom = Label()
            val lableEnd = Label()
            m_compiler!!.currentEndLable = Label()

            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/irislang/jiris/dev/IrisDevUtil", "Nil",
                    "()Lorg/irislang/jiris/core/IrisValue;", false)
            mv.visitVarInsn(Opcodes.ASTORE, 2)

            mv.visitLabel(lableFrom)

            m_block!!.Generate(m_compiler!!, m_compiler!!.currentBuilder!!, mv)

            mv.visitLabel(m_compiler!!.currentEndLable)
            m_compiler!!.currentEndLable = null

            mv.visitVarInsn(Opcodes.ALOAD, 2)
            mv.visitInsn(Opcodes.ARETURN)

            mv.visitLabel(lableEnd)

            mv.visitLocalVariable("context", "Lorg/irislang/jiris/core/IrisContextEnvironment;",
                    null, lableFrom, lableEnd, 0)
            mv.visitLocalVariable("threadInfo", "Lorg/irislang/jiris/core/IrisThreadInfo;",
                    null, lableFrom, lableEnd, 1)
            mv.visitLocalVariable("resultValue", "Lorg/irislang/jiris/core/IrisValue;",
                    null, lableFrom, lableEnd, 2)

            for (pair in m_compiler!!.irregularVariableLabelPairs) {
                mv.visitLocalVariable("runtimeIrregular",
                        "Lorg/irislang/jiris/core/exceptions/IrisRuntimeException;", null, pair.fromLabel,
                        pair.toLabel, 3)
            }

            return ByteCodeAppender.Size(0, 0)
        }

    }
}
