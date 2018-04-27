package org.irislang.jiris.irisclass

import net.bytebuddy.pool.TypePool
import org.irislang.jiris.core.*
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.dev.IrisClassRoot
import org.irislang.jiris.dev.IrisDevUtil

import java.lang.reflect.Array
import java.util.ArrayList

/**
 * Created by Huisama on 2017/4/8 0008.
 */
class IrisArrayIterator : IrisClassRoot() {
    override fun NativeClassNameDefine(): String {
        return "ArrayIterator"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        return IrisDevUtil.GetClass("Object")!!
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    override fun NativeAlloc(): Any {
        return IrisArrayIteratorTag()
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeClassDefine(classObj: IrisClass) {
        classObj.AddInstanceMethod(IrisArrayIterator::class.java, "Initialize", "__format", 1, false, IrisMethod.MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisArrayIterator::class.java, "Next", "next", 0, false, IrisMethod.MethodAuthority
                .Everyone)
        classObj.AddInstanceMethod(IrisArrayIterator::class.java, "IsEnd", "is_end", 0, false, IrisMethod
                .MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisArrayIterator::class.java, "GetValue", "get_value", 0, false, IrisMethod
                .MethodAuthority.Everyone)
    }

    inner class IrisArrayIteratorTag {
        private var m_iterator: Iterator<IrisValue>? = null
        private var m_arrayList: ArrayList<IrisValue>? = null
        private var m_currentValue: IrisValue? = null

        fun Initialize(arrayList: ArrayList<IrisValue>) {
            m_arrayList = arrayList
            m_iterator = arrayList.iterator()
        }

        fun Next(): IrisArrayIteratorTag {
            m_currentValue = m_iterator!!.next()
            return this
        }

        fun GetValue(): IrisValue? {
            return m_currentValue
        }

        fun IsEnd(): Boolean {
            return !m_iterator!!.hasNext()
        }

        fun GetIter(): Iterator<IrisValue>? {
            return m_iterator
        }
    }

    companion object {

        @JvmStatic
        fun Initialize(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val iter = IrisDevUtil.GetNativeObjectRef<Any>(self) as IrisArrayIteratorTag
            val arrayList = IrisDevUtil.GetNativeObjectRef<ArrayList<IrisValue>>(parameterList[0])

            iter.Initialize(arrayList)

            return self
        }

        @JvmStatic
        fun Next(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val iter = IrisDevUtil.GetNativeObjectRef<Any>(self) as IrisArrayIteratorTag
            iter.Next()

            return self
        }

        @JvmStatic
        fun IsEnd(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val iter = IrisDevUtil.GetNativeObjectRef<Any>(self) as IrisArrayIteratorTag

            return if (iter.IsEnd()) IrisDevUtil.True() else IrisDevUtil.False()
        }

        @JvmStatic
        fun GetValue(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val iter = IrisDevUtil.GetNativeObjectRef<Any>(self) as IrisArrayIteratorTag

            return iter.GetValue()!!
        }
    }
}
