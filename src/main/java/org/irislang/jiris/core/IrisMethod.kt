package org.irislang.jiris.core

import org.irislang.jiris.core.IrisContextEnvironment.RunTimeType
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.core.exceptions.fatal.IrisParameterNotFitException
import org.irislang.jiris.core.exceptions.fatal.IrisUnkownFatalException
import org.irislang.jiris.dev.IrisDevUtil
import org.irislang.jiris.irisclass.IrisMethodBase

import java.lang.invoke.MethodHandle
import java.util.ArrayList

class IrisMethod {

    var authority: MethodAuthority? = null
    var methodName = ""
    private var m_parameterCount = 0
    private var m_isWithVariableParameter = false
    private var m_userMethod: IrisUserMethod? = null
    var methodObject: IrisObject? = null
        private set
    private var m_methodHanlde: MethodHandle? = null

    private var m_getterSetterType = GetterSetter.Normal
    private var m_targetVariable: String = ""

    enum class MethodAuthority {
        Everyone,
        Relative,
        Personal
    }

    enum class CallSide {
        Outeside,
        Inside
    }

    enum class GetterSetter {
        Getter,
        Setter,
        Normal
    }

    @Throws(IrisExceptionBase::class)
    constructor(methodName: String, parameterCount: Int, isWithVariableParameter: Boolean, authority: MethodAuthority, methodHandle: MethodHandle) {
        this.methodName = methodName
        m_parameterCount = parameterCount
        m_isWithVariableParameter = isWithVariableParameter
        this.authority = authority
        m_userMethod = null
        m_methodHanlde = methodHandle
        val methodClass = IrisDevUtil.GetClass("Method")
        if (methodClass != null) {
            CreateMethodObject(methodClass)
        }
    }

    @Throws(IrisExceptionBase::class)
    constructor(methodName: String, userMethod: IrisUserMethod, authority: MethodAuthority, methodHandle: MethodHandle) {
        this.methodName = methodName
        m_parameterCount = userMethod.parameterList.size
        m_isWithVariableParameter = userMethod.variableParameterName == ""
        m_userMethod = userMethod
        this.authority = authority
        m_methodHanlde = methodHandle

        CreateMethodObject(IrisDevUtil.GetClass("Method")!!)
    }

    @Throws(IrisExceptionBase::class)
    constructor(methodName: String, targetVariable: String, type: GetterSetter, authority: MethodAuthority) {
        this.methodName = methodName
        m_targetVariable = targetVariable
        m_isWithVariableParameter = false
        m_userMethod = null
        m_methodHanlde = null
        this.authority = authority
        m_getterSetterType = type

        when (type) {
            IrisMethod.GetterSetter.Getter -> m_parameterCount = 0
            IrisMethod.GetterSetter.Setter -> m_parameterCount = 1
            IrisMethod.GetterSetter.Normal ->
                // Error
                throw IrisUnkownFatalException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                        "Oh, shit! An UNKNOWN ERROR has been lead to by YOU to Iris! What a SHIT unlucky man you are! "
                                + "Please don't approach Iris ANYMORE ! - Wrong getter/setter type ")
        }

        CreateMethodObject(IrisDevUtil.GetClass("Method")!!)
    }

    @Throws(IrisExceptionBase::class)
    private fun CreateNewContext(caller: IrisObject?, parameterList: ArrayList<IrisValue>, currentContext: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisContextEnvironment {
        val newContex = IrisContextEnvironment()
        newContex.runTimeType = RunTimeType.RunTime
        newContex.runningType = caller
        newContex.upperContext = currentContext
        newContex.currentMethod = this

        if (m_userMethod != null) {
            // TODO: with/without block

            // parameter -> local variable && variable parameter process
            if (parameterList.size != 0) {
                var counter = 0
                val iterator = parameterList.iterator()
                for (value in m_userMethod!!.parameterList) {
                    newContex.AddLocalVariable(value, iterator.next())
                    ++counter
                }

                if (m_isWithVariableParameter) {
                    val variables = ArrayList(parameterList.subList(counter, parameterList.size))
                    val arrayClass = IrisDevUtil.GetClass("Array")
                    val arrayValue = arrayClass!!.CreateNewInstance(variables, threadInfo.GetTopContextEnvironment()!!, threadInfo)
                    newContex.AddLocalVariable(m_userMethod!!.variableParameterName, arrayValue)
                }
            }
        }

        return newContex
    }

    private fun ParameterCheck(parameterList: ArrayList<IrisValue>): Boolean {
        return if (parameterList.size > 0) {
            if (m_isWithVariableParameter) {
                parameterList.size >= m_parameterCount
            } else {
                parameterList.size == m_parameterCount
            }
        } else {
            m_parameterCount == 0
        }
    }

    @Throws(IrisExceptionBase::class)
    private fun CreateMethodObject(methodClass: IrisClass) {
        val methodObj = methodClass.CreateNewInstance(ArrayList(),
                IrisDevUtil.GetCurrentThreadInfo().GetTopContextEnvironment()!!,
                IrisDevUtil.GetCurrentThreadInfo())
        (IrisDevUtil.GetNativeObjectRef<Any>(methodObj) as IrisMethodBase.IrisMethodBaseTag).methodObj = this
        methodObject = methodObj.`object`
    }

    @Throws(IrisExceptionBase::class)
    fun ResetMethodObject() {
        CreateMethodObject(IrisDevUtil.GetClass("Method")!!)
    }

    @Throws(IrisExceptionBase::class)
    fun Call(caller: IrisValue, parameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue? {
        var result: IrisValue? = null

        if (!ParameterCheck(parameterList)) {
            /* Error */
            throw IrisParameterNotFitException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                    IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                    "Parameter not fit: ${parameterList.size} for ${m_parameterCount}")
        }

        // Getter Setter
        if (m_getterSetterType == GetterSetter.Getter) {
            val `object` = caller.`object`!!
            var value: IrisValue? = `object`.GetInstanceVariable(m_targetVariable)
            if (value == null) {
                value = IrisValue.CloneValue(IrisDevUtil.Nil())
                `object`.AddInstanceVariable(m_targetVariable, value)
            }
            return value
        } else if (m_getterSetterType == GetterSetter.Setter) {
            val `object` = caller.`object`
            var value: IrisValue? = `object`!!.GetInstanceVariable(m_targetVariable)
            val setValue = parameterList[0]

            if (value == null) {
                value = IrisValue.CloneValue(setValue)
                `object`.AddInstanceVariable(m_targetVariable, value)
            } else {
                value.`object` = setValue.`object`
            }
            return IrisDevUtil.Nil()
        }

        val newContext = CreateNewContext(caller.`object`, parameterList, context, threadInfo)
        try {
            // Call
            if (parameterList.size == 0) {
                if (m_userMethod == null) {
                    result = m_methodHanlde!!.invokeWithArguments(caller, ArrayList<IrisValue>(), ArrayList<IrisValue>(),
                            newContext, threadInfo) as IrisValue
                } else {
                    result = m_methodHanlde!!.invokeWithArguments(newContext, threadInfo) as IrisValue
                }
            } else {
                if (m_userMethod == null) {
                    // Variable Parameters
                    var variableValues: ArrayList<IrisValue>
                    var normalParameters: ArrayList<IrisValue>
                    if (parameterList.size > m_parameterCount) {
                        variableValues = ArrayList(parameterList.subList(m_parameterCount, parameterList.size))
                    } else {
                        variableValues = ArrayList()
                    }
                    if (m_parameterCount > 0) {
                        normalParameters = ArrayList(parameterList.subList(0, m_parameterCount))
                    } else {
                        normalParameters = ArrayList()
                    }
                    result = m_methodHanlde!!.invokeWithArguments(caller, normalParameters, variableValues, newContext, threadInfo) as IrisValue
                } else {
                    result = m_methodHanlde!!.invokeWithArguments(newContext, threadInfo) as IrisValue
                }
            }
        } catch (e: Throwable) {
            if (e is IrisExceptionBase) {
                throw e
            } else {
                e.printStackTrace()
                throw IrisUnkownFatalException("Unkown irregular happend.", threadInfo.currentLineNumber, threadInfo.currentFileName)
            }
        }

        return result
    }

    @Throws(IrisExceptionBase::class)
    fun CallMain(parameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
        if (!ParameterCheck(parameterList)) {
            /* Error */
            throw IrisParameterNotFitException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                    IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                    "Parameters not fit: ${parameterList.size} for ${m_parameterCount}"
                    )
        }

        val newContext = CreateNewContext(null, parameterList, context, threadInfo)

        try {
            return m_methodHanlde!!.invokeExact(newContext, threadInfo) as IrisValue
        } catch (e: Throwable) {
            if (e is IrisExceptionBase) {
                throw e
            }
            return IrisDevUtil.Nil()
        }

    }

    data class IrisUserMethod (
            var parameterList: ArrayList<String> = ArrayList(),
            var variableParameterName: String = "",
            var withBlockHandle: MethodHandle? = null,
            var withoutBlockHandle: MethodHandle? = null
    )
}
