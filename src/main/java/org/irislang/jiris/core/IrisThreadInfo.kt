package org.irislang.jiris.core

import org.irislang.jiris.compiler.statement.IrisClassStatement
import com.sun.deploy.panel.UpdatePanelFactory
import jdk.nashorn.internal.runtime.Debug
import org.irislang.jiris.dev.IrisDevUtil
import org.irislang.jiris.irisclass.IrisInteger
import org.omg.CORBA.PRIVATE_MEMBER
import sun.misc.Cleaner

import javax.print.attribute.standard.MediaSize
import java.lang.invoke.MethodHandle
import java.util.ArrayList
import java.util.LinkedList
import java.util.Stack

class IrisThreadInfo {

    val parameterList = ArrayList<IrisValue>()
    var record: IrisValue? = null

    private val m_comparedObj: IrisValue? = null
    private val m_counter = 0
    private val m_loopTimeStack = Stack<IrisValue>()
    private val m_comparedObjectStack = Stack<IrisValue>()
    private val m_counterStack = Stack<IrisValue>()
    private val m_vesselStack = Stack<IrisValue>()
    private val m_iteratorStack = Stack<IrisValue>()
    private val m_environmentStack = Stack<IrisContextEnvironment>()
    private val m_closureBlockStack = Stack<IrisClosureBlock>()

    private var m_tempSuperClass: IrisValue? = null
    private val m_tempModules = LinkedList<IrisValue>()
    val tempInterfaces = LinkedList<IrisValue>()

    var currentLineNumber = -1
    var currentFileName: String = ""

    val counter: IrisValue
        get() = m_counterStack.lastElement()

    fun GetTopContextEnvironment(): IrisContextEnvironment {
        return if(m_environmentStack.isEmpty()) IrisContextEnvironment()  else m_environmentStack.lastElement()
    }

    fun GetTopClosureBlock(): IrisClosureBlock {
        return m_closureBlockStack.lastElement()
    }

    fun PushClosureBlock(block: IrisClosureBlock) {
        m_closureBlockStack.push(block)
    }

    fun PopClosureBlock() {
        m_closureBlockStack.pop()
    }

    fun SetTempSuperClass(value: IrisValue) {
        m_tempSuperClass = value
    }

    fun GetTempSuperClass(): IrisValue? {
        return m_tempSuperClass
    }

    fun ClearTempModules() {
        m_tempModules.clear()
    }

    fun AddTempModule(value: IrisValue) {
        m_tempModules.add(value)
    }

    fun GetTempModules(): LinkedList<IrisValue> {
        return m_tempModules
    }

    fun ClearTempInterfaces() {
        tempInterfaces.clear()
    }

    fun AddTempInterface(value: IrisValue) {
        tempInterfaces.add(value)
    }

    fun PushContext(environment: IrisContextEnvironment) {
        m_environmentStack.push(environment)
    }

    fun PopContext(): IrisContextEnvironment {
        return m_environmentStack.pop()
    }

    fun PushVessel(vessel: IrisValue) {
        m_vesselStack.push(vessel)
    }

    fun GetVessel(): IrisValue {
        return m_vesselStack.lastElement()
    }

    fun PopVessel() {
        m_vesselStack.pop()
    }

    fun PushIterator(iterator: IrisValue) {
        m_iteratorStack.push(iterator)
    }

    fun GetIterator(): IrisValue {
        return m_iteratorStack.lastElement()
    }

    fun PopIterator() {
        m_iteratorStack.pop()
    }

    fun PushComparedObject(value: IrisValue) {
        m_comparedObjectStack.add(value)
    }

    fun PopCompareadObject() {
        m_comparedObjectStack.pop()
    }

    fun GetTopComparedObject(): IrisValue {
        return m_comparedObjectStack.lastElement()
    }

    fun PushLoopTime(value: IrisValue) {
        m_loopTimeStack.add(value)
    }

    fun PopLoopTime() {
        m_loopTimeStack.pop()
    }

    fun GetTopLoopTime(): IrisValue {
        return m_loopTimeStack.lastElement()
    }

    fun pushCounter(counter: IrisValue) {
        m_counterStack.push(counter)
    }

    fun PopCounter() {
        m_counterStack.pop()
    }

    fun increamCounter() {
        val tag = IrisDevUtil.GetNativeObjectRef<Any>(counter) as IrisInteger.IrisIntegerTag
        tag.integer = tag.integer + 1
    }

    fun getPartPrameterListOf(count: Int): ArrayList<IrisValue> {
        return ArrayList(parameterList.subList(parameterList.size - count, parameterList.size))
    }

    fun AddParameter(value: IrisValue) {
        parameterList.add(value)
    }

    fun PopParameter(times: Int) {
        for (i in 0 until times) {
            parameterList.removeAt(parameterList.size - 1)
        }
    }

    fun ClearPrameterList() {
        parameterList.clear()
    }

    companion object {

        private var sm_mainThreadID: Long = 0
        private var sm_mainThreadInfo: IrisThreadInfo? = null

        fun GetCurrentThreadInfo(): IrisThreadInfo? {
            val threadID = Thread.currentThread().id

            return if (threadID == sm_mainThreadID) {
                sm_mainThreadInfo
            } else {
                null
            }
        }

        fun SetMainThreedInfo(threadInfo: IrisThreadInfo) {
            sm_mainThreadInfo = threadInfo
        }

        fun SetMainThreadID(id: Long) {
            sm_mainThreadID = id
        }
    }
}
