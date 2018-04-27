package org.irislang.jiris.core

import java.util.HashMap

class IrisContextEnvironment {

    var runningType: IrisRunningObject? = null
    var runTimeType = RunTimeType.RunTime

    private val m_localVariableMap = HashMap<String, IrisValue>()
    var upperContext: IrisContextEnvironment? = null

    var currentMethod: IrisMethod? = null

    var closureBlockObj: IrisObject? = null

    enum class RunTimeType {
        ClassDefineTime,
        ModuleDefineTime,
        InterfaceDefineTime,
        RunTime
    }

    fun GetLocalVariableWithinChain(localName: String): IrisValue? {
        var tmp: IrisContextEnvironment? = this
        var value: IrisValue? = null
        while (tmp != null) {
            value = tmp.GetLocalVariable(localName)
            if (value != null) {
                break
            }
            tmp = tmp.upperContext
        }
        return value
    }

    fun GetLocalVariable(localName: String): IrisValue? {
        return m_localVariableMap[localName]
    }

    fun AddLocalVariable(localName: String, value: IrisValue) {
        m_localVariableMap[localName] = value
    }
}
