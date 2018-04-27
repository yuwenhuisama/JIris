package org.irislang.jiris.core

import org.irislang.jiris.IrisInterpreter
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.core.exceptions.fatal.IrisConstanceNotFoundException
import org.irislang.jiris.core.exceptions.fatal.IrisParameterNotFitException
import org.irislang.jiris.core.exceptions.fatal.IrisUnkownFatalException
import org.irislang.jiris.dev.IrisDevUtil

import java.lang.invoke.MethodHandle
import java.util.ArrayList

/**
 * Created by yuwen on 2017/7/3 0003.
 */
class IrisClosureBlock(upperEnvironment: IrisContextEnvironment, parameters: ArrayList<String>,
                       variableParameter: String, methodHandle: MethodHandle) {
    private var m_currentEnvironment: IrisContextEnvironment

    var nativeObject: IrisObject

    private var m_parameters: ArrayList<String> = ArrayList()
    private var m_variableParameter: String? = null

    internal var m_methodHandle: MethodHandle

    init {
        this.m_parameters = parameters
        this.m_variableParameter = variableParameter
        this.m_methodHandle = methodHandle

        this.nativeObject = IrisObject()
        this.nativeObject.objectClass = IrisDevUtil.GetClass("Block")
        this.nativeObject.nativeObject = this

        this.m_currentEnvironment = CreateNewEnvironment(upperEnvironment)
    }

    private fun CreateNewEnvironment(upperEnvrionment: IrisContextEnvironment): IrisContextEnvironment {

        val newEnv = IrisContextEnvironment()

        newEnv.closureBlockObj = nativeObject
        newEnv.runTimeType = IrisContextEnvironment.RunTimeType.RunTime
        newEnv.upperContext = upperEnvrionment
        newEnv.runningType = null

        return newEnv
    }

    @Throws(IrisExceptionBase::class)
    fun Call(parameters: ArrayList<IrisValue>, info: IrisThreadInfo): IrisValue {
        if (!ParameterCheck(parameters)) {
            throw IrisParameterNotFitException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                    IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                    "Parameter not fit: ${parameters.size} for ${m_parameters.size}")
        }

            // parameter -> local variable && variable parameter process
        if (!parameters.isEmpty()) {
            var counter = 0
            val iterator = parameters.iterator()
            for (value in m_parameters) {
                m_currentEnvironment.AddLocalVariable(value, iterator.next())
                ++counter
            }

            if (m_variableParameter != null) {
                val variables = ArrayList(parameters.subList(counter, parameters.size))
                val arrayClass = IrisDevUtil.GetClass("Array")
                val arrayValue = arrayClass!!.CreateNewInstance(variables, this.m_currentEnvironment.upperContext!!, info)
                m_currentEnvironment.AddLocalVariable(m_variableParameter!!, arrayValue)
            }
        }

        val result: IrisValue
        try {
            result = m_methodHandle.invokeExact(m_currentEnvironment, info) as IrisValue
        } catch (throwable: Throwable) {
            if (throwable is IrisExceptionBase) {
                throw throwable
            } else {
                throwable.printStackTrace()
                throw IrisUnkownFatalException("Unkown irregular happened.", info.currentLineNumber, info.currentFileName)
            }
        }

        return result
    }

    private fun ParameterCheck(parameters: ArrayList<IrisValue>): Boolean {
        return if (parameters.size > 0) {
            if (m_variableParameter != null) {
                parameters.size >= m_parameters.size
            } else {
                parameters.size == m_parameters.size
            }
        } else {
            m_parameters.isEmpty()
        }
    }

    fun GetLocalVariable(name: String): IrisValue {
        var target = m_currentEnvironment.GetLocalVariableWithinChain(name)

        if (target == null) {
            target = IrisValue.CloneValue(IrisDevUtil.Nil())
            m_currentEnvironment.AddLocalVariable(name, target)
        }

        return target
    }

    fun AddLocalVariable(name: String, value: IrisValue) {
        m_currentEnvironment.AddLocalVariable(name, value)
    }

    fun GetInstanceVariable(name: String): IrisValue? {
        var tmpEnv: IrisContextEnvironment? = m_currentEnvironment
        var target: IrisValue? = tmpEnv!!.GetLocalVariable(name)

        tmpEnv = tmpEnv.upperContext
        while (tmpEnv != null && target == null) {
            if (tmpEnv.runningType != null) {
                var obj: IrisObject? = null
                when (tmpEnv.runTimeType) {
                    IrisContextEnvironment.RunTimeType.ClassDefineTime -> obj = (tmpEnv.runningType as IrisClass).classObject
                    IrisContextEnvironment.RunTimeType.ModuleDefineTime -> obj = (tmpEnv.runningType as IrisModule).moduleObject
                    IrisContextEnvironment.RunTimeType.RunTime -> obj = tmpEnv.runningType as IrisObject
                    IrisContextEnvironment.RunTimeType.InterfaceDefineTime -> TODO()
                }
                target = obj!!.GetInstanceVariable(name)
                if (target == null) {
                    target = tmpEnv.GetLocalVariable(name)
                }
            } else {
                target = tmpEnv.GetLocalVariable(name)
            }
        }

        if (target == null) {
            target = IrisValue.CloneValue(IrisDevUtil.Nil())
            m_currentEnvironment.AddLocalVariable(name, target)
        }

        return target
    }

    fun GetClassVariable(name: String): IrisValue? {
        var tmpEnv: IrisContextEnvironment? = m_currentEnvironment
        var target: IrisValue? = tmpEnv!!.GetLocalVariable(name)

        tmpEnv = tmpEnv.upperContext
        while (tmpEnv != null && target == null) {
            if (tmpEnv.runningType != null) {
                when (tmpEnv.runTimeType) {
                    IrisContextEnvironment.RunTimeType.ClassDefineTime -> target = (tmpEnv.runningType as IrisClass).GetClassVariable(name)
                    IrisContextEnvironment.RunTimeType.ModuleDefineTime -> target = (tmpEnv.runningType as IrisModule).GetClassVariable(name)
                    IrisContextEnvironment.RunTimeType.RunTime -> target = (tmpEnv.runningType as IrisObject).objectClass!!.GetClassVariable(name)
                    IrisContextEnvironment.RunTimeType.InterfaceDefineTime -> TODO()
                }
                if (target == null) {
                    target = tmpEnv.GetLocalVariable(name)
                }
            } else {
                target = tmpEnv.GetLocalVariable(name)
            }

            tmpEnv = tmpEnv.upperContext
        }

        if (target == null) {
            target = IrisValue.CloneValue(IrisDevUtil.Nil())
            m_currentEnvironment.AddLocalVariable(name, target)
        }

        return target
    }

    @Throws(IrisExceptionBase::class)
    fun GetConstance(name: String): IrisValue {
        var tmpEnv: IrisContextEnvironment? = m_currentEnvironment
        var target: IrisValue? = null

        while (tmpEnv != null) {
            if (tmpEnv.runningType != null) {
                when (tmpEnv.runTimeType) {
                    IrisContextEnvironment.RunTimeType.ClassDefineTime -> target = (tmpEnv.runningType as IrisClass).GetConstance(name)
                    IrisContextEnvironment.RunTimeType.ModuleDefineTime -> target = (tmpEnv.runningType as IrisModule).GetConstance(name)
                    IrisContextEnvironment.RunTimeType.RunTime -> target = (tmpEnv.runningType as IrisObject).objectClass!!.GetConstance(name)
                    IrisContextEnvironment.RunTimeType.InterfaceDefineTime -> TODO()
                }
            }
            tmpEnv = tmpEnv.upperContext
        }

        if (target == null) {
            target = IrisInterpreter.INSTANCE.GetConstance(name)
        }

        if (target == null) {
            throw IrisConstanceNotFoundException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                    IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                    "Constance of ${name} not found.")
        }

        return target
    }

}
