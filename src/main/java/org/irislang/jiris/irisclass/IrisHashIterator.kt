package org.irislang.jiris.irisclass

import com.irisine.jiris.compiler.assistpart.IrisElseIf
import org.irislang.jiris.core.*
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.dev.IrisClassRoot
import org.irislang.jiris.dev.IrisDevUtil

import java.util.ArrayList
import java.util.HashMap
import java.util.Map

/**
 * Created by Huisama on 2017/4/8 0008.
 */
class IrisHashIterator : IrisClassRoot() {
    override fun NativeClassNameDefine(): String {
        return "HashIterator"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        return IrisDevUtil.GetClass("Object")!!
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    override fun NativeAlloc(): Any {
        return IrisHashIteratorTag()
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeClassDefine(classObj: IrisClass) {
        classObj.AddInstanceMethod(IrisHashIterator::class.java, "Initialize", "__format", 1, false, IrisMethod.MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisHashIterator::class.java, "Next", "next", 0, false, IrisMethod.MethodAuthority
                .Everyone)
        classObj.AddInstanceMethod(IrisHashIterator::class.java, "IsEnd", "is_end", 0, false, IrisMethod.MethodAuthority
                .Everyone)
        classObj.AddInstanceMethod(IrisHashIterator::class.java, "GetKey", "get_key", 0, false, IrisMethod.MethodAuthority
                .Everyone)
        classObj.AddInstanceMethod(IrisHashIterator::class.java, "GetValue", "get_value", 0, false, IrisMethod
                .MethodAuthority.Everyone)
    }

    inner class IrisHashIteratorTag {
        private var m_iterator: Iterator<*>? = null
        private var m_hashMap: HashMap<IrisValue, IrisValue>? = null
        private var m_currentEntry: kotlin.collections.Map.Entry<IrisValue, IrisValue>? = null

        fun Initialize(hashMap: HashMap<IrisValue, IrisValue>) {
            m_iterator = hashMap.entries.iterator()
            m_hashMap = hashMap
        }

        fun Next(): IrisHashIteratorTag {
            m_currentEntry = m_iterator!!.next() as kotlin.collections.Map.Entry<IrisValue, IrisValue>
            return this
        }

        fun IsEnd(): Boolean {
            return !m_iterator!!.hasNext()
        }

        fun GetKey(): IrisValue {
            return m_currentEntry!!.key
        }

        fun GetValue(): IrisValue {
            return m_currentEntry!!.value
        }

        fun GetIter(): Iterator<*>? {
            return m_iterator
        }

    }

    companion object {

        @JvmStatic
        fun Initialize(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val iter = IrisDevUtil.GetNativeObjectRef<Any>(self) as IrisHashIteratorTag
            val hashMap = IrisDevUtil.GetNativeObjectRef<HashMap<IrisValue, IrisValue>>(parameterList[0])
            iter.Initialize(hashMap)
            return self
        }

        @JvmStatic
        fun Next(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val iter = IrisDevUtil.GetNativeObjectRef<Any>(self) as IrisHashIteratorTag
            iter.Next()

            return self
        }

        @JvmStatic
        fun IsEnd(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val iter = IrisDevUtil.GetNativeObjectRef<Any>(self) as IrisHashIteratorTag
            return if (iter.IsEnd()) IrisDevUtil.True() else IrisDevUtil.False()
        }

        @JvmStatic
        fun GetKey(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val iter = IrisDevUtil.GetNativeObjectRef<Any>(self) as IrisHashIteratorTag
            return iter.GetKey()
        }

        @JvmStatic
        fun GetValue(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val iter = IrisDevUtil.GetNativeObjectRef<Any>(self) as IrisHashIteratorTag
            return iter.GetValue()
        }
    }
}
