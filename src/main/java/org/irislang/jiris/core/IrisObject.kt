package org.irislang.jiris.core

import java.util.ArrayList
import java.util.HashMap
import kotlin.collections.Map.Entry

import org.irislang.jiris.core.IrisMethod.CallSide
import org.irislang.jiris.core.IrisMethod.MethodAuthority
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.core.exceptions.fatal.IrisInvalidAuthorityException
import org.irislang.jiris.dev.IrisDevUtil

import javax.swing.undo.AbstractUndoableEdit

class IrisObject : IrisRunningObject {
    var objectClass: IrisClass? = null
    private val m_methods = HashMap<String, IrisMethod>()
    private val m_instanceValues = HashMap<String, IrisValue>()
    var nativeObject: Any? = null
    var objectID = 0

    init {
        objectID = ++sm_objectNumber
    }

    @Throws(IrisExceptionBase::class)
    fun CallInstanceMethod(methodName: String, parameterList: ArrayList<IrisValue>,
                           context: IrisContextEnvironment, threadInfo: IrisThreadInfo, callSide: IrisMethod.CallSide): IrisValue? {
        var method: IrisMethod? = null
        var isCurrentMethod = false

        var authority: MethodAuthority? = null

        // Object's instance method
        if (m_methods.containsKey(methodName)) {
            method = m_methods[methodName]
            isCurrentMethod = true
            authority = method!!.authority
            // instance method in class
        } else {
            val result = IrisClass.SearchResult()
            objectClass!!.GetMethod(methodName, result)
            method = result.method

            if (method == null) {
                /* Error */
                val mName = IrisDevUtil.CreateString(methodName)
                val mClsName = IrisDevUtil.CreateString(objectClass!!.className)
                val tmpList = ArrayList<IrisValue>(2)
                tmpList.add(mName)
                tmpList.add(mClsName)
                return IrisDevUtil.CallMethod(IrisValue.WrapObject(this), "missing_method", tmpList, context, threadInfo)
            }

            isCurrentMethod = result.isCurrentClassMethod

            if (result.isCurrentClassMethodOfSelf) {
                authority = method.authority
            } else {
                authority = objectClass!!.GetMethodAuthorityFromMap(methodName)
                if (authority == null) {
                    authority = method.authority
                }
            }
        }

        // Inside call
        var callResult: IrisValue? = null
        val caller = IrisValue.WrapObject(this)
        if (callSide == CallSide.Inside) {

            if (isCurrentMethod) {
                callResult = method.Call(caller, parameterList, context, threadInfo)
            } else {
                if (authority == MethodAuthority.Personal) {
                    /* Error */
                    throw IrisInvalidAuthorityException(threadInfo.currentFileName, threadInfo
                            .currentLineNumber, "Method with personal authority cannot be called here.")
                } else {
                    callResult = method.Call(caller, parameterList, context, threadInfo)
                }
            }
        } else {
            if (authority != MethodAuthority.Everyone) {
                /* Error */
                throw IrisInvalidAuthorityException(threadInfo.currentFileName, threadInfo
                        .currentLineNumber, "Only method with everyone authority can be called here.")
            } else {
                callResult = method.Call(caller, parameterList, context, threadInfo)
            }
        }// Outside call

        return callResult
    }

    fun AddInstanceMethod(method: IrisMethod) {
        m_methods[method.methodName] = method
    }

    fun AddInstanceVariable(name: String, value: IrisValue) {
        m_instanceValues[name] = value
    }

    fun GetInstanceVariable(name: String): IrisValue? {
        return m_instanceValues[name]
    }

    @Throws(IrisExceptionBase::class)
    fun ResetAllMethodsObject() {
//        val iterator = m_methods.entries.iterator()
//        while (iterator.hasNext()) {
//            val entry = iterator.next() as Entry<String, IrisMethod>
//            val method = entry.value
//            method.ResetMethodObject()
//        }

        for (iter in m_methods) {
            iter.value.ResetMethodObject()
        }
    }

    internal fun GetInstanceMethod(methodName: String): IrisMethod? {
        return if (m_methods.containsKey(methodName)) {
            m_methods[methodName]
        } else {
            null
        }
    }

    companion object {
        private var sm_objectNumber = 0
    }

}
 