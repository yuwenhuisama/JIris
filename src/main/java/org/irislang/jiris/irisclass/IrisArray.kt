package org.irislang.jiris.irisclass

import java.lang.reflect.Array
import java.util.ArrayList

import org.irislang.jiris.core.IrisClass
import org.irislang.jiris.core.IrisContextEnvironment
import org.irislang.jiris.core.IrisMethod
import org.irislang.jiris.core.IrisModule
import org.irislang.jiris.core.IrisThreadInfo
import org.irislang.jiris.core.IrisValue
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.core.exceptions.fatal.IrisTypeNotCorretException
import org.irislang.jiris.dev.IrisClassRoot
import org.irislang.jiris.dev.IrisDevUtil

class IrisArray : IrisClassRoot() {

    override fun NativeClassNameDefine(): String {
        return "Array"
    }

    override fun NativeSuperClassDefine(): IrisClass {
        return IrisDevUtil.GetClass("Object")!!
    }

    override fun NativeUpperModuleDefine(): IrisModule? {
        return null
    }

    override fun NativeAlloc(): Any {
        return ArrayList<IrisValue>()
    }

    @Throws(IrisExceptionBase::class)
    override fun NativeClassDefine(classObj: IrisClass) {
        classObj.AddInstanceMethod(IrisArray::class.java, "Initialize", "__format", 0, true, IrisMethod.MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisArray::class.java, "At", "[]", 1, false, IrisMethod.MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisArray::class.java, "Set", "[]=", 2, false, IrisMethod.MethodAuthority.Everyone)

        classObj.AddInstanceMethod(IrisArray::class.java, "Push", "push", 1, false, IrisMethod.MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisArray::class.java, "Pop", "pop", 0, false, IrisMethod.MethodAuthority.Everyone)
        classObj.AddInstanceMethod(IrisArray::class.java, "GetIterator", "get_iterator", 0, false, IrisMethod
                .MethodAuthority.Everyone)
    }

    companion object {

        @JvmStatic
        fun Initialize(self: IrisValue, parameterList: ArrayList<IrisValue>,
                       variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {

            val arrayList = IrisDevUtil.GetNativeObjectRef<ArrayList<IrisValue>>(self)
            arrayList.addAll(variableParameterList)

            return self
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun At(self: IrisValue, parameterList: ArrayList<IrisValue>,
               variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {

            val index = parameterList[0]
            val arrayList = IrisDevUtil.GetNativeObjectRef<ArrayList<IrisValue>>(self)

            if (!IrisDevUtil.CheckClass(index, "Integer")) {
                /* Error */
                throw IrisTypeNotCorretException(threadInfo.currentFileName, threadInfo.currentLineNumber,
                        "Parameter of [] can only be an Integer.")
            }

            val indexNum = IrisDevUtil.GetInt(index)

            if (indexNum < 0) {
                return arrayList[arrayList.size - -indexNum % arrayList.size]
            } else {
                if (indexNum > arrayList.size) {
                    for (i in 0 until indexNum - arrayList.size) {
                        arrayList.add(IrisDevUtil.Nil())
                    }
                    return IrisDevUtil.Nil()
                } else {
                    return arrayList[indexNum]
                }
            }
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun Set(self: IrisValue, parameterList: ArrayList<IrisValue>,
                variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val index = parameterList[0]
            val targetValue = parameterList[1]
            val arrayList = IrisDevUtil.GetNativeObjectRef<ArrayList<IrisValue>>(self)
            if (!IrisDevUtil.CheckClass(index, "Integer")) {
                /* Error */
                throw IrisTypeNotCorretException(threadInfo.currentFileName, threadInfo.currentLineNumber,
                        "Index parameter of []= can only be an Integer.")
            }

            val indexNum = IrisDevUtil.GetInt(index)

            if (indexNum < 0) {
                arrayList[arrayList.size - -indexNum % arrayList.size] = targetValue
            } else {
                if (indexNum >= arrayList.size) {
                    for (i in 0 until indexNum - arrayList.size) {
                        arrayList.add(IrisDevUtil.Nil())
                    }
                    arrayList.add(targetValue)
                } else {
                    arrayList[indexNum] = targetValue
                }
            }
            return self
        }

        @JvmStatic
        fun Push(self: IrisValue, parameterList: ArrayList<IrisValue>,
                 variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val targetValue = parameterList[0]
            val arrayList = IrisDevUtil.GetNativeObjectRef<ArrayList<IrisValue>>(self)
            arrayList.add(targetValue)
            return self
        }

        @JvmStatic
        fun Pop(self: IrisValue, parameterList: ArrayList<IrisValue>,
                variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val arrayList = IrisDevUtil.GetNativeObjectRef<ArrayList<IrisValue>>(self)
            return arrayList.removeAt(arrayList.size - 1)
        }

        @JvmStatic
        fun Size(self: IrisValue, parameterList: ArrayList<IrisValue>,
                 variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val arrayList = IrisDevUtil.GetNativeObjectRef<ArrayList<IrisValue>>(self)
            return IrisDevUtil.CreateInt(arrayList.size)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun GetIterator(self: IrisValue, parameterList: ArrayList<IrisValue>, variableParameterList: ArrayList<IrisValue>, context: IrisContextEnvironment, threadInfo: IrisThreadInfo): IrisValue {
            val params = ArrayList<IrisValue>(1)
            params.add(0, self)
            // ** Error **
            return IrisDevUtil.CreateInstance(IrisDevUtil.GetClass("ArrayIterator")!!, params, context, threadInfo)
        }
    }

}
