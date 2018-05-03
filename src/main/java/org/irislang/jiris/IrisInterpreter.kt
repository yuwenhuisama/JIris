package org.irislang.jiris

import java.lang.reflect.InvocationTargetException
import java.util.Arrays
import java.util.HashMap
import java.util.LinkedList

import org.irislang.jiris.compiler.IrisCompiler

import org.irislang.jiris.core.*
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.core.exceptions.IrisRuntimeException
import org.irislang.jiris.core.exceptions.fatal.*
import org.irislang.jiris.dev.IrisClassRoot
import org.irislang.jiris.dev.IrisDevUtil
import org.irislang.jiris.dev.IrisModuleRoot
import org.irislang.jiris.irisclass.*
import org.irislang.jiris.irisclass.IrisModuleBase.IrisModuleBaseTag
import org.irislang.jiris.irisclass.IrisClassBase.IrisClassBaseTag
import org.irislang.jiris.irismodule.IrisKernel

class IrisInterpreter private constructor() {

    // Buffers
    var classClass: IrisClass? = null
    var moduleClass: IrisClass? = null
    var interfaceClass: IrisClass? = null
    var objectClass: IrisClass? = null
    var methodClass: IrisClass? = null

    private val m_constances = HashMap<String, IrisValue>()
    private val m_globalValues = HashMap<String, IrisValue>()

    private val m_mainMethods = HashMap<String, IrisMethod>()

    private var m_Nil: IrisValue? = null
    private var m_True: IrisValue? = null
    private var m_False: IrisValue? = null

    var javaClassFileNumber = 0
        private set

    var currentCompiler: IrisCompiler? = null

    fun InceamJavaClassFileNumber() {
        ++javaClassFileNumber
    }

    fun Nil(): IrisValue? {
        return m_Nil
    }

    fun True(): IrisValue? {
        return m_True
    }

    fun False(): IrisValue? {
        return m_False
    }

    fun AddMainMethod(method: IrisMethod) {
        m_mainMethods[method.methodName] = method
    }

    fun GetMainMethod(methodName: String): IrisMethod? {
        return m_mainMethods[methodName]
    }

    @Throws(IrisExceptionBase::class)
    fun GetModule(fullPath: LinkedList<String>): IrisModule? {

        if (fullPath.isEmpty()) {
            throw IrisUnkownFatalException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                    IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                    "Oh, shit! An UNKNOWN ERROR has been lead to by YOU to Iris! What a SHIT unlucky man you are! " +
                            "Please don't approach Iris ANYMORE ! - The interface CANNOT be registerd to Iris.")
        }

        var tmpCur: IrisModule? = null
        var tmpValue: IrisValue? = null
        val firstModuleName = fullPath.pop()

        tmpValue = GetConstance(firstModuleName)
        if (tmpValue != null) {
            if (IrisDevUtil.IsModuleObject(tmpValue)) {
                tmpCur = (IrisDevUtil.GetNativeObjectRef<Any>(tmpValue) as IrisModuleBaseTag).module
            } else {
                return null
            }
        } else {
            return null
        }

        for (moduleName in fullPath) {
            tmpValue = tmpCur!!.GetConstance(moduleName)
            if (tmpValue != null) {
                if (IrisDevUtil.IsModuleObject(tmpValue)) {
                    tmpCur = (IrisDevUtil.GetNativeObjectRef<Any>(tmpValue) as IrisModuleBaseTag).module
                } else {
                    break
                }
            }
        }
        return tmpCur
    }

    fun GetClass(fullPath: LinkedList<String>): IrisClass? {
        val className = fullPath.removeLast()

        var tmpUpperModule: IrisModule? = null
        var tmpValue: IrisValue? = null

        if (fullPath.isEmpty()) {
            tmpValue = GetConstance(className)
            if (tmpValue == null) {
                return null
            }
            return if (IrisDevUtil.IsClassObject(tmpValue)) {
                (IrisDevUtil.GetNativeObjectRef<Any>(tmpValue) as IrisClassBaseTag).classObj
            } else {
                null
            }
        } else {
            try {
                tmpUpperModule = GetModule(fullPath)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            if (tmpUpperModule != null) {
                tmpValue = tmpUpperModule.GetConstance(className)
                return if (tmpValue != null) {
                    if (IrisDevUtil.IsClassObject(tmpValue)) {
                        (IrisDevUtil.GetNativeObjectRef<Any>(tmpValue) as IrisClassBaseTag).classObj
                    } else {
                        null
                    }
                } else {
                    null
                }
            } else {
                return null
            }
        }
    }

    fun GetInterface(fullPath: LinkedList<String>): IrisInterface? {
        val className = fullPath.removeLast()

        var tmpUpperModule: IrisModule? = null
        var tmpValue: IrisValue? = null

        if (fullPath.isEmpty()) {
            tmpValue = GetConstance(className)
            if (tmpValue == null) {
                return null
            }
            return if (IrisDevUtil.IsInterfaceObject(tmpValue)) {
                IrisDevUtil.GetNativeObjectRef<IrisInterface>(tmpValue)
            } else {
                null
            }
        } else {
            try {
                tmpUpperModule = GetModule(fullPath)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            if (tmpUpperModule != null) {
                tmpValue = tmpUpperModule.GetConstance(className)
                return if (tmpValue != null) {
                    if (IrisDevUtil.IsInterfaceObject(tmpValue)) {
                        IrisDevUtil.GetNativeObjectRef<IrisInterface>(tmpValue)
                    } else {
                        null
                    }
                } else {
                    null
                }
            } else {
                return null
            }
        }
    }

    fun GetModule(fullPath: String): IrisModule? {
        val result = fullPath.split("::".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val stringList = LinkedList(Arrays.asList(*result))
        var tmpMoudule = GetModule(stringList)
        return tmpMoudule
    }

    fun GetClass(fullPath: String): IrisClass? {
        val result = fullPath.split("::".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val stringList = LinkedList(Arrays.asList(*result))
        return GetClass(stringList)
    }

    fun GetInterface(fullPath: String): IrisInterface? {
        val result = fullPath.split("::".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val stringList = LinkedList(Arrays.asList(*result))
        return GetInterface(stringList)
    }

    @Throws(IrisExceptionBase::class)
    fun RegistClass(classObj: IrisClassRoot): Boolean {

        val upperModule = classObj.NativeUpperModuleDefine()
        val className = classObj.NativeClassNameDefine()

        if (upperModule == null) {
            if (GetConstance(className) != null) {
                throw IrisClassExistsException(IrisDevUtil.GetCurrentThreadInfo()
                        .currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                        "Class $className has been already registered.")
            }
        } else {
            if (upperModule.GetConstance(className) != null) {
                throw IrisClassExistsException(IrisDevUtil.GetCurrentThreadInfo()
                        .currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                        "Class $className has been already registered.")
            }
        }

        val classInternObj = IrisClass(classObj)
        val classValue = IrisValue.WrapObject(classInternObj.classObject!!)

        if (upperModule == null) {
            AddConstance(className, classValue)
        } else {
            upperModule.AddConstance(className, classValue)
            upperModule.AddSubClass(classInternObj)
        }

        return true
    }

    @Throws(IrisExceptionBase::class)
    fun RegistModule(moduleObj: IrisModuleRoot): Boolean {

        val upperModule = moduleObj.NativeUpperModuleDefine()
        val moduleName = moduleObj.NativeModuleNameDefine()

        if (upperModule == null) {
            if (GetConstance(moduleName) != null) {
                throw IrisModuleExistsException(IrisDevUtil.GetCurrentThreadInfo()
                        .currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                        "Module $moduleName has been already registered.")
            }
        } else {
            if (upperModule.GetConstance(moduleName) != null) {
                throw IrisModuleExistsException(IrisDevUtil.GetCurrentThreadInfo()
                        .currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                        "Module $moduleName has been already registered.")
            }
        }

        val moduleInternObj = IrisModule(moduleObj)
        val moduleValue = IrisValue.WrapObject(moduleInternObj.moduleObject!!)

        if (upperModule == null) {
            AddConstance(moduleName, moduleValue)
        } else {
            upperModule.AddConstance(moduleName, moduleValue)
            upperModule.AddSubModule(moduleInternObj)
        }

        return true
    }

    fun AddConstance(name: String, value: IrisValue) {
        m_constances[name] = value
    }

    fun GetConstance(name: String): IrisValue? {
        return m_constances[name]
    }

    fun AddGlobalValue(name: String, value: IrisValue) {
        m_globalValues[name] = value
    }

    fun GetGlobalValue(name: String): IrisValue? {
        return m_globalValues[name]
    }

    @Throws(IrisExceptionBase::class)
    fun Initialize(): Boolean {

        IrisThreadInfo.SetMainThreadID(Thread.currentThread().id)
        val mainThreadInfo = IrisThreadInfo()
        IrisThreadInfo.SetMainThreedInfo(mainThreadInfo)

        RegistClass(IrisClassBase())
        classClass = GetClass("Class")

        RegistClass(IrisModuleBase())
        moduleClass = GetClass("Module")

        RegistModule(IrisKernel())

        RegistClass(IrisObjectBase())
        objectClass = GetClass("Object")
        objectClass!!.AddInvolvedModule(IrisDevUtil.GetModule("Kernel")!!)

        classClass!!.superClass = objectClass
        moduleClass!!.superClass = objectClass

        RegistClass(IrisMethodBase())
        methodClass = GetClass("Method")

        classClass!!.ResetAllMethodsObject()
        objectClass!!.ResetAllMethodsObject()
        moduleClass!!.ResetAllMethodsObject()
        methodClass!!.ResetAllMethodsObject()
        IrisDevUtil.GetModule("Kernel")!!.ResetAllMethodsObject()

        RegistClass(IrisBlock())

        RegistClass(IrisInteger())
        RegistClass(IrisFloat())
        RegistClass(IrisString())
        RegistClass(IrisUniqueString())

        RegistClass(IrisTrueClass())
        RegistClass(IrisFalseClass())
        RegistClass(IrisNilClass())

        RegistClass(IrisArray())
        RegistClass(IrisArrayIterator())
        RegistClass(IrisHash())
        RegistClass(IrisHashIterator())

        m_True = IrisDevUtil.GetClass("TrueClass")!!.CreateNewInstance(ArrayList(), IrisContextEnvironment(), IrisDevUtil.GetCurrentThreadInfo())
        m_False = IrisDevUtil.GetClass("FalseClass")!!.CreateNewInstance(ArrayList(), IrisContextEnvironment(), IrisDevUtil.GetCurrentThreadInfo())
        m_Nil = IrisDevUtil.GetClass("NilClass")!!.CreateNewInstance(ArrayList(), IrisContextEnvironment(), IrisDevUtil.GetCurrentThreadInfo())

        return true
    }

    fun Run(): Boolean {
        if (currentCompiler == null) {
            return false
        }

        val runClass = currentCompiler!!.nativeJavaClass!!
        val mainEnv = IrisContextEnvironment()

        try {
            val instance = runClass.newInstance()
            val method = runClass.getMethod("run", IrisContextEnvironment::class.java, IrisThreadInfo::class.java)
            method.invoke(instance, mainEnv, IrisDevUtil.GetCurrentThreadInfo())
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            return false
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return false
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            return false
        } catch (e: SecurityException) {
            e.printStackTrace()
            return false
        } catch (e: InstantiationException) {
            e.printStackTrace()
            return false
        } catch (e: InvocationTargetException) {
            val t = e.targetException
            if (t is IrisFatalException) {
                //System.out.(((IrisFatalException)t).GetReportString());
                //Logger logger = Logger.getLogger("Iris FatalException");
                val logger = org.apache.logging.log4j.LogManager.getLogger("jiris")
                logger.error("\n" + t.GetReportString())
            } else if (t is IrisRuntimeException) {
                val irregularObject = t.exceptionObject
                val stringResult: IrisValue?
                try {
                    stringResult = IrisDevUtil.CallMethod(irregularObject!!, "to_string", ArrayList(),
                            IrisDevUtil.GetCurrentThreadInfo().GetTopContextEnvironment(),
                            IrisDevUtil.GetCurrentThreadInfo())
                } catch (e2: IrisExceptionBase) {
                    e2.printStackTrace()
                    return false
                }

                val outString = IrisDevUtil.GetString(stringResult!!)

                val logger = org.apache.logging.log4j.LogManager.getLogger("jiris")
                logger.error("\n" + IrisIrregularNotHandledException(IrisDevUtil.GetCurrentThreadInfo()
                        .currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, outString).GetReportString())
            } else {
                e.printStackTrace()
            }
            return false
        }

        return true
    }

    fun ShutDown(): Boolean {
        return true
    }

    companion object {
        val INSTANCE = IrisInterpreter()
    }
}
