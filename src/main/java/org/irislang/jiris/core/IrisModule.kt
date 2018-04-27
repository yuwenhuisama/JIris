package org.irislang.jiris.core

import java.util.HashMap
import java.util.HashSet

import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.core.exceptions.fatal.IrisMethodNotFoundException
import org.irislang.jiris.dev.IrisDevUtil
import org.irislang.jiris.dev.IrisModuleRoot
import org.irislang.jiris.irisclass.IrisModuleBase.IrisModuleBaseTag

class IrisModule : IrisRunningObject {
    var moduleName: String = ""
    var moduleObject: IrisObject? = null
    var upperModule: IrisModule? = null
    private val m_subClasses = HashSet<IrisClass>()
    private val m_subModules = HashSet<IrisModule>()
    private val m_involvedModules = HashSet<IrisModule>()
    private val m_constances = HashMap<String, IrisValue>()
    private val m_classVariables = HashMap<String, IrisValue>()
    private val m_instanceMethods = HashMap<String, IrisMethod>()

    constructor()

    @Throws(IrisExceptionBase::class)
    constructor(nativeModule: IrisModuleRoot) {
        moduleName = nativeModule.NativeModuleNameDefine()
        upperModule = nativeModule.NativeUpperModuleDefine()

        val obj = IrisDevUtil.GetClass("Module")!!.CreateNewInstance(ArrayList(),
                IrisDevUtil.GetCurrentThreadInfo().GetTopContextEnvironment(), IrisDevUtil.GetCurrentThreadInfo())
        (IrisDevUtil.GetNativeObjectRef<IrisModuleBaseTag>(obj)).module = this
        moduleObject = obj.`object`!!

        nativeModule.NativeModuleDefine(this)
    }

    @Throws(IrisExceptionBase::class)
    constructor(moduleName: String, upperModule: IrisModule?) {
        this.moduleName = moduleName
        this.upperModule = upperModule

        val obj = IrisDevUtil.GetClass("Module")!!.CreateNewInstance(ArrayList(),
                IrisDevUtil.GetCurrentThreadInfo().GetTopContextEnvironment(), IrisDevUtil.GetCurrentThreadInfo())
        IrisDevUtil.GetNativeObjectRef<IrisModuleBaseTag>(obj).module = this
        moduleObject = obj.`object`!!
    }

    fun AddSubClass(subClass: IrisClass) {
        m_subClasses.add(subClass)
    }

    fun AddSubModule(subModule: IrisModule) {
        m_subModules.add(subModule)
    }

    fun GetMethod(methodName: String): IrisMethod? {
        return _SearchMethod(this, methodName)
    }

    fun _SearchMethod(curModule: IrisModule, methodName: String): IrisMethod? {
        var method: IrisMethod? = curModule.m_instanceMethods[methodName]
        if (method != null) {
            return method
        }

        for (module in m_involvedModules) {
            method = _SearchMethod(module, methodName)
            if (method != null) {
                return method
            }
        }
        return null
    }

    fun AddConstance(name: String, value: IrisValue) {
        m_constances[name] = value
    }

    fun GetConstance(name: String): IrisValue? {
        return m_constances[name]
    }

    fun SearchConstance(name: String): IrisValue? {
        return _SearchConstance(this, name)
    }

    private fun _SearchConstance(curModule: IrisModule, name: String): IrisValue? {
        var result: IrisValue? = curModule.GetConstance(name)

        if (result != null) {
            return result
        }

        for (module in curModule.m_involvedModules) {
            result = _SearchConstance(module, name)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun GetClassVariable(name: String): IrisValue? {
        return m_classVariables[name]
    }

    fun SearchClassVariable(name: String): IrisValue? {
        return _SearchClassVariable(this, name)
    }

    private fun _SearchClassVariable(curModule: IrisModule, name: String): IrisValue? {
        var result: IrisValue? = curModule.GetConstance(name)

        if (result != null) {
            return result
        }

        for (module in curModule.m_involvedModules) {
            result = _SearchClassVariable(module, name)
            if (result != null) {
                return result
            }
        }
        return null
    }

    @Throws(IrisExceptionBase::class)
    fun AddClassMethod(nativeClass: Class<*>, nativeName: String, methodName: String, parameterAmount: Int, isWithVariableParameter: Boolean, authority: IrisMethod.MethodAuthority) {
        val method = IrisMethod(methodName,
                parameterAmount,
                isWithVariableParameter,
                authority,
                IrisDevUtil.GetIrisNativeMethodHandle(nativeClass, nativeName))
        AddClassMethod(method)
    }

    @Throws(IrisExceptionBase::class)
    fun AddClassMethod(nativeClass: Class<*>, nativeName: String, methodName: String, userMethod: IrisMethod.IrisUserMethod, authority: IrisMethod.MethodAuthority) {
        val method = IrisMethod(methodName, userMethod, authority, IrisDevUtil.GetIrisNativeUserMethodHandle(nativeClass, nativeName))
        AddClassMethod(method)
    }

    @Throws(IrisExceptionBase::class)
    fun AddInstanceMethod(nativeClass: Class<*>, nativeName: String, methodName: String, parameterAmount: Int, isWithVariableParameter: Boolean, authority: IrisMethod.MethodAuthority) {
        val method = IrisMethod(methodName,
                parameterAmount,
                isWithVariableParameter,
                authority,
                IrisDevUtil.GetIrisNativeMethodHandle(nativeClass, nativeName))
        AddInstanceMethod(method)
    }

    @Throws(IrisExceptionBase::class)
    fun AddInstanceMethod(nativeClass: Class<*>, nativeName: String, methodName: String, userMethod: IrisMethod.IrisUserMethod, authority: IrisMethod.MethodAuthority) {
        val method = IrisMethod(methodName, userMethod, authority, IrisDevUtil.GetIrisNativeUserMethodHandle(nativeClass, nativeName))
        AddInstanceMethod(method)
    }

    private fun AddClassMethod(method: IrisMethod) {
        moduleObject!!.AddInstanceMethod(method)
    }

    @Throws(IrisExceptionBase::class)
    fun ResetAllMethodsObject() {
        moduleObject!!.ResetAllMethodsObject()

//        val iterator = m_instanceMethods.entries.iterator()
//        while (iterator.hasNext()) {
//            val entry = iterator.next() as Entry<String, IrisMethod>
//            val method = entry.value
//            method.ResetMethodObject()
//        }

        for (iter in m_instanceMethods) {
            iter.value.ResetMethodObject()
        }
    }

    @Throws(IrisExceptionBase::class)
    fun SetInstanceMethodAuthority(methodName: String, authority: IrisMethod.MethodAuthority) {
        var method: IrisMethod? = null
        if (m_instanceMethods.containsKey(methodName)) {
            method = m_instanceMethods[methodName]
        } else {
            throw IrisMethodNotFoundException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                    IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                    "Method of ${methodName} not found in module ${moduleName} .")
        }
    }

    @Throws(IrisExceptionBase::class)
    fun SetClassMethodAuthority(methodName: String, authority: IrisMethod.MethodAuthority) {
        val method = moduleObject!!.GetInstanceMethod(methodName)
        if (method != null) {
            method.authority = authority
        } else {
            moduleObject!!.objectClass!!.SetInstanceMethodAuthority(methodName, authority)
        }
    }

    private fun AddInstanceMethod(method: IrisMethod) {
        m_instanceMethods[method.methodName] = method
    }

    fun AddClassVariable(variableName: String, value: IrisValue) {
        m_classVariables[variableName] = value
    }

    fun AddInvolvedModule(moduleObj: IrisModule) {
        m_involvedModules.add(moduleObj)
    }
}
