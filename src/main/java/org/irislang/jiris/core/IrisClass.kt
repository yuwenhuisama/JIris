package org.irislang.jiris.core

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

import org.irislang.jiris.IrisInterpreter
import org.irislang.jiris.core.IrisMethod.IrisUserMethod
import org.irislang.jiris.core.IrisMethod.MethodAuthority
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.core.exceptions.fatal.IrisMethodNotFoundException
import org.irislang.jiris.dev.IrisClassRoot
import org.irislang.jiris.dev.IrisDevUtil
import org.irislang.jiris.irisclass.IrisClassBase

class IrisClass : IrisRunningObject {

    var superClass: IrisClass? = null
    var classObject: IrisObject? = null
    var upperModule: IrisModule? = null
    var className = ""
    private var m_externClass: IrisClassRoot? = null

    private val m_involvedModules = HashSet<IrisModule>()
    private val m_involvedInteraces = HashSet<IrisInterface>()

    private val m_classVariables = HashMap<String, IrisValue>()
    private val m_constances = HashMap<String, IrisValue>()
    private val m_instanceMethods = HashMap<String, IrisMethod>()

    private val m_instanceMethodAuthorityMap = HashMap<String, MethodAuthority>()
    private val m_classMethodAuthorityMap = HashMap<String, MethodAuthority>()

    data class SearchResult(var method: IrisMethod? = null,
                            var isCurrentClassMethod: Boolean = false,
                            var isCurrentClassMethodOfSelf: Boolean = false)

    constructor()

    @Throws(IrisExceptionBase::class)
    constructor(externClass: IrisClassRoot) {
        className = externClass.NativeClassNameDefine()
        superClass = externClass.NativeSuperClassDefine()
        upperModule = externClass.NativeUpperModuleDefine()
        m_externClass = externClass

        val classObj = IrisDevUtil.GetClass("Class")

        if (classObj != null) {
            classObject = classObj.CreateNewInstance(ArrayList(), IrisContextEnvironment(), IrisDevUtil.GetCurrentThreadInfo()).`object`
            IrisDevUtil.GetNativeObjectRef<IrisClassBase.IrisClassBaseTag>(classObject!!).classObj = this
        } else {
            classObject = IrisObject()
            classObject!!.objectClass = this
            classObject!!.nativeObject = externClass.NativeAlloc()
            IrisDevUtil.GetNativeObjectRef<IrisClassBase.IrisClassBaseTag>(classObject!!).classObj = this
        }

        m_externClass!!.NativeClassDefine(this)

    }

    @Throws(IrisExceptionBase::class)
    constructor(className: String, upperModule: IrisModule?, superClass: IrisClass) {
        this.className = className
        this.upperModule = upperModule
        this.superClass = superClass

        val classObj = IrisDevUtil.GetClass("Class")
        classObject = classObj!!.CreateNewInstance(ArrayList(), IrisContextEnvironment(), IrisDevUtil.GetCurrentThreadInfo()).`object`
        IrisDevUtil.GetNativeObjectRef<IrisClassBase.IrisClassBaseTag>(classObject!!).classObj = this
    }

    @Throws(IrisExceptionBase::class)
    fun ResetAllMethodsObject() {

        classObject!!.ResetAllMethodsObject()

//        val iterator = m_instanceMethods.entries.iterator()
//        while (iterator.hasNext()) {
//            val entry = iterator.next() as Entry<String, IrisMethod>
//            val method = entry.value
//            method.ResetMethodObject()
//        }

        for (pair in m_instanceMethods) {
            val method = pair.value;
            method.ResetMethodObject()
        }

    }

    fun GetMethod(methodName: String, result: SearchResult) {
        // this class
        var method: IrisMethod? = null
        result.isCurrentClassMethod = false
        result.method = null
        result.isCurrentClassMethodOfSelf = false

        method = _GetMethod(this, methodName, result)

        if (method != null) {
            result.isCurrentClassMethod = true
            result.method = method
            return
        }

        var curClass = superClass

        while (curClass != null) {
            method = _GetMethod(curClass, methodName, null)
            if (method != null) {
                result.isCurrentClassMethod = false
                result.method = method
                return
            }
            curClass = curClass.superClass
        }

    }

    private fun _SearchClassModuleMethod(searchClass: IrisClass, methodName: String): IrisMethod? {
        var method: IrisMethod? = null
        for (module in searchClass.m_involvedModules) {
            method = module.GetMethod(methodName)
            if (method != null) {
                break
            }
        }
        return method
    }

    private fun _GetMethod(searchClass: IrisClass, methodName: String, result: SearchResult?): IrisMethod? {
        var method: IrisMethod? = null
        method = searchClass.m_instanceMethods[methodName]
        if (method == null) {
            // involved module
            method = _SearchClassModuleMethod(searchClass, methodName)
        } else {
            result?.isCurrentClassMethodOfSelf = true
        }

        return method
    }

    @Throws(IrisExceptionBase::class)
    fun CreateNewInstance(parameterList: ArrayList<IrisValue>, context: IrisContextEnvironment,
                          threadInfo: IrisThreadInfo): IrisValue {
        // new object
        val `object` = IrisObject()
        `object`.objectClass = this
        if (m_externClass == null) {
            `object`.nativeObject = null
        } else {
            val nativeObj = m_externClass!!.NativeAlloc()
            `object`.nativeObject = nativeObj
        }

        if (IrisInterpreter.INSTANCE.objectClass != null) {
            `object`.CallInstanceMethod("__format", parameterList, context, threadInfo, IrisMethod.CallSide.Outeside)
        }

        return IrisValue.WrapObject(`object`)
    }

    @Throws(IrisExceptionBase::class)
    fun AddClassMethod(nativeClass: Class<*>, nativeName: String, methodName: String, parameterAmount: Int,
                       isWithVariableParameter: Boolean, authority: IrisMethod.MethodAuthority) {
        val method = IrisMethod(methodName,
                parameterAmount,
                isWithVariableParameter,
                authority,
                IrisDevUtil.GetIrisNativeMethodHandle(nativeClass, nativeName))
        AddClassMethod(method)
    }

    @Throws(IrisExceptionBase::class)
    fun AddClassMethod(nativeClass: Class<*>, nativeName: String, methodName: String, userMethod: IrisUserMethod,
                       authority: MethodAuthority) {
        val method = IrisMethod(methodName, userMethod, authority,
                IrisDevUtil.GetIrisNativeUserMethodHandle(nativeClass, nativeName))
        AddClassMethod(method)
    }

    @Throws(IrisExceptionBase::class)
    fun AddInstanceMethod(nativeClass: Class<*>, nativeName: String, methodName: String, parameterAmount: Int,
                          isWithVariableParameter: Boolean, authority: IrisMethod.MethodAuthority) {
        val method = IrisMethod(methodName,
                parameterAmount,
                isWithVariableParameter,
                authority,
                IrisDevUtil.GetIrisNativeMethodHandle(nativeClass, nativeName))
        AddInstanceMethod(method)
    }

    @Throws(IrisExceptionBase::class)
    fun AddInstanceMethod(nativeClass: Class<*>, nativeName: String, methodName: String, userMethod: IrisUserMethod, authority: MethodAuthority) {
        val method = IrisMethod(methodName, userMethod, authority, IrisDevUtil.GetIrisNativeUserMethodHandle(nativeClass, nativeName))
        AddInstanceMethod(method)
    }

    fun AddClassMethod(method: IrisMethod) {
        classObject!!.AddInstanceMethod(method)
    }

    fun AddInstanceMethod(method: IrisMethod) {
        if (m_instanceMethodAuthorityMap.containsKey(method.methodName)) {
            m_instanceMethodAuthorityMap.remove(method.methodName)
        }
        m_instanceMethods[method.methodName] = method
    }

    fun AddInvolvedModule(moduleObj: IrisModule) {
        m_involvedModules.add(moduleObj)
    }

    fun AddInvolvedInterface(interfaceObj: IrisInterface) {
        m_involvedInteraces.add(interfaceObj)
    }

    fun AddConstance(name: String, value: IrisValue) {
        m_constances[name] = value
    }

    fun GetConstance(name: String): IrisValue? {
        return m_constances[name]
    }

    fun SearchConstance(name: String): IrisValue? {
        var curClass: IrisClass? = this
        var result: IrisValue? = null

        while (curClass != null) {
            result = _GetConstance(curClass, name)
            if (result != null) {
                break
            }
            curClass = curClass.superClass
        }

        return result
    }

    private fun _GetConstance(curClass: IrisClass, name: String): IrisValue? {
        var result: IrisValue? = curClass.GetConstance(name)
        if (result == null) {
            result = _SearchClassModuleConstance(curClass, name)
        }

        return result
    }

    private fun _SearchClassModuleConstance(curClass: IrisClass, name: String): IrisValue? {
        var result: IrisValue? = null
        for (module in curClass.m_involvedModules) {
            result = module.SearchConstance(name)
            if (result != null) {
                break
            }
        }
        return result
    }

    fun AddClassVariable(name: String, value: IrisValue) {
        m_classVariables[name] = value
    }

    fun GetClassVariable(name: String): IrisValue? {
        return m_classVariables[name]
    }

    fun SearchClassVariable(name: String): IrisValue? {
        var curClass: IrisClass? = this
        var result: IrisValue? = null

        while (curClass != null) {
            result = _GetClassVariable(curClass, name)
            if (result != null) {
                break
            }
            curClass = curClass.superClass
        }
        return result
    }

    private fun _GetClassVariable(curClass: IrisClass, name: String): IrisValue? {
        var result: IrisValue? = curClass.GetClassVariable(name)
        if (result == null) {
            result = _SearchClassModuleClassVariable(curClass, name)
        }

        return result
    }

    private fun _SearchClassModuleClassVariable(curClass: IrisClass, name: String): IrisValue? {
        var result: IrisValue? = null
        for (module in curClass.m_involvedModules) {
            result = module.SearchClassVariable(name)
            if (result != null) {
                break
            }
        }
        return result
    }

    @Throws(IrisExceptionBase::class)
    fun SetInstanceMethodAuthority(methodName: String, authority: MethodAuthority) {
        var method: IrisMethod? = null
        if (m_instanceMethods.containsKey(methodName)) {
            method = m_instanceMethods[methodName]
            method!!.authority = authority
        } else {
            val result = SearchResult()
            GetMethod(methodName, result)
            if (result.method != null) {
                m_instanceMethodAuthorityMap[methodName] = authority
            } else {
                val rstring = StringBuilder()
                rstring.append("Method of ").append(methodName).append(" not found in class ").append(className).append(".")
                throw IrisMethodNotFoundException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                        rstring.toString())
            }
        }
    }

    @Throws(IrisExceptionBase::class)
    fun SetClassMethodAuthority(methodName: String, authority: MethodAuthority) {
        val method = classObject!!.GetInstanceMethod(methodName)
        if (method != null) {
            method.authority = authority
        } else {
            classObject!!.objectClass!!.SetInstanceMethodAuthority(methodName, authority)
        }
    }

    fun GetMethodAuthorityFromMap(methodName: String): MethodAuthority? {
        return if (m_instanceMethodAuthorityMap.containsKey(methodName)) {
            m_instanceMethodAuthorityMap[methodName]
        } else {
            null
        }
    }
}