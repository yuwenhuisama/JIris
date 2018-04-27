package org.irislang.jiris.dev

import java.lang.invoke.MethodHandles.lookup
import java.lang.invoke.CallSite
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Method
import java.util.ArrayList

import org.irislang.jiris.IrisInterpreter
import org.irislang.jiris.core.*
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.irisclass.IrisUniqueString
import org.irislang.jiris.irisclass.IrisFloat.IrisFloatTag
import org.irislang.jiris.irisclass.IrisInteger.IrisIntegerTag
import org.irislang.jiris.irisclass.IrisString.IrisStringTag
import org.irislang.jiris.irisclass.IrisUniqueString.IrisUniqueStringTag

import net.bytebuddy.jar.asm.Type

class IrisDevUtil {

    companion object {
        @JvmStatic
        fun Nil(): IrisValue {
            return IrisInterpreter.INSTANCE.Nil()!!
        }

        @JvmStatic
        fun True(): IrisValue {
            return IrisInterpreter.INSTANCE.True()!!
        }

        @JvmStatic
        fun False(): IrisValue {
            return IrisInterpreter.INSTANCE.False()!!
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun CallMethod(caller: IrisValue, methodName: String, parameterList: ArrayList<IrisValue>,
                       context: IrisContextEnvironment?, threadInfo: IrisThreadInfo): IrisValue? {
            val curEnv: IrisContextEnvironment;
            if (context == null) {
                curEnv = threadInfo.GetTopContextEnvironment() ?: IrisContextEnvironment()
            } else {
                curEnv = context
            }

            return caller.`object`!!.CallInstanceMethod(methodName, parameterList, curEnv, threadInfo, IrisMethod
                    .CallSide.Outeside)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun CreateInstance(classObj: IrisClass, params: ArrayList<IrisValue>, context: IrisContextEnvironment?, threadInfo: IrisThreadInfo?): IrisValue {
            return classObj.CreateNewInstance(params, context!!, threadInfo!!)
        }

        @JvmStatic
        fun CreateInt(integer: Int): IrisValue {
            val intClass = GetClass("Integer")
            val intObject = IrisObject()

            intObject.objectClass = intClass
            val integerTag = IrisIntegerTag(integer)
            intObject.nativeObject = integerTag

            return IrisValue.WrapObject(intObject)
        }

        @JvmStatic
        fun CreateFloat(dfloat: Double): IrisValue {
            val floatClass = GetClass("Float")
            val floatObject = IrisObject()

            floatObject.objectClass = floatClass
            val floatTag = IrisFloatTag(dfloat)
            floatObject.nativeObject = floatTag

            return IrisValue.WrapObject(floatObject)
        }

        @JvmStatic
        fun CreateString(str: String): IrisValue {
            val stringClass = GetClass("String")
            val stringObject = IrisObject()

            stringObject.objectClass = stringClass
            val stringTag = IrisStringTag(str)
            stringObject.nativeObject = stringTag

            return IrisValue.WrapObject(stringObject)
        }

        @JvmStatic
        fun CreateUniqueString(ustr: String): IrisValue {

            var obj: IrisValue? = IrisUniqueString.GetUniqueString(ustr)
            if (obj != null) {
                return obj
            }

            val ustringClass = GetClass("UniqueString")
            val ustringObject = IrisObject()

            ustringObject.objectClass = ustringClass
            val ustringTag = IrisUniqueStringTag(ustr)
            ustringObject.nativeObject = ustringTag

            obj = IrisValue.WrapObject(ustringObject)

            IrisUniqueString.AddUniqueString(ustr, obj)

            return obj
        }

        @JvmStatic
        fun CreateArray(elements: ArrayList<IrisValue>?): IrisValue {
            val arrayClass = GetClass("Array")
            val arrayObject = IrisObject()
            arrayObject.objectClass = arrayClass

            var values: ArrayList<IrisValue>? = null
            if (elements != null) {
                values = ArrayList(elements)
            } else {
                values = ArrayList()
            }

            arrayObject.nativeObject = values
            return IrisValue.WrapObject(arrayObject)
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun CreateHash(pairs: ArrayList<IrisValue>): IrisValue {
            val hashClass = GetClass("Hash")
            return CreateInstance(hashClass!!, pairs, null, null)
        }

        @JvmStatic
        fun GetInt(intVar: IrisValue): Int {
            val integerTag = GetNativeObjectRef<IrisIntegerTag>(intVar)
            return integerTag.integer
        }

        @JvmStatic
        fun GetFloat(floatVar: IrisValue): Double {
            val floatTag = GetNativeObjectRef<IrisFloatTag>(floatVar)
            return floatTag.float
        }

        // Todo: Compare object's class directly to obejct
        @JvmStatic
        fun IsClassObject(value: IrisValue): Boolean {
            return value.`object`?.objectClass?.className == "Class"
        }

        // Todo: Compare object's class directly to obejct
        @JvmStatic
        fun IsModuleObject(value: IrisValue): Boolean {
            return value.`object`?.objectClass?.className == "Module"
        }

        // Todo: Compare object's class directly to obejct
        @JvmStatic
        fun IsInterfaceObject(value: IrisValue): Boolean {
            return value.`object`?.objectClass?.className == "Interface"
        }

        // TODO: Implement checkclass as infix
        @JvmStatic
        fun CheckClass(obj: IrisValue, className: String): Boolean {
            return obj.`object`?.objectClass?.className == className
        }

        @JvmStatic
        fun GetClass(classPath: String): IrisClass? {
            return IrisInterpreter.INSTANCE.GetClass(classPath)
        }

        @JvmStatic
        fun GetModule(modulePath: String): IrisModule? {
            return IrisInterpreter.INSTANCE.GetModule(modulePath)
        }

        @JvmStatic
        fun GetInterface(interfacePath: String): IrisInterface? {
            return IrisInterpreter.INSTANCE.GetInterface(interfacePath)
        }

        @JvmStatic
        fun NotFalseOrNil(value: IrisValue): Boolean {
            return value != IrisDevUtil.Nil() && value != IrisDevUtil.False()
        }

        @JvmStatic
        fun <T> GetNativeObjectRef(value: IrisValue): T {
            return value.`object`!!.nativeObject as T
        }

        @JvmStatic
        fun <T> GetNativeObjectRef(value: IrisObject): T {
            return value.nativeObject as T
        }

        @JvmStatic
        fun GetIrisNativeMethodHandle(classObj: Class<*>, methodName: String): MethodHandle {
            var methodHandle: MethodHandle? = null
            try {
                methodHandle = INDY_BootstrapMethod(classObj, methodName, IrisValue::class.java, ArrayList::class.java, ArrayList::class.java, IrisContextEnvironment::class.java, IrisThreadInfo::class.java)
            } catch (e: Throwable) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return methodHandle!!
        }

        @JvmStatic
        fun GetIrisNativeUserMethodHandle(classObj: Class<*>, methodName: String): MethodHandle {
            var methodHandle: MethodHandle? = null
            try {
                methodHandle = INDY_BootstrapMethod(classObj, methodName, IrisContextEnvironment::class.java, IrisThreadInfo::class.java)
            } catch (e: Throwable) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return methodHandle!!
        }

        @JvmStatic
        fun GetIrisClosureBlockHandle(classObj: Class<*>, methodName: String): MethodHandle {
            var methodHandle: MethodHandle? = null
            try {
                methodHandle = INDY_BootstrapMethod(classObj, methodName, IrisContextEnvironment::class.java, IrisThreadInfo::class.java)
            } catch (e: Throwable) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return methodHandle!!
        }

        @JvmStatic
        fun GetCurrentThreadInfo(): IrisThreadInfo {
            return IrisThreadInfo.GetCurrentThreadInfo()!!
        }

        @JvmStatic
        fun GetString(obj: IrisValue): String {
            return if (CheckClass(obj, "String")) {
                (GetNativeObjectRef<Any>(obj) as IrisStringTag).string
            } else if (CheckClass(obj, "UniqueString")) {
                (GetNativeObjectRef<Any>(obj) as IrisUniqueStringTag).string
            } else {
                ""
            }
        }

        @Throws(NoSuchMethodException::class, SecurityException::class)
        @JvmStatic
        private fun MT_BootstrapMethod(classObj: Class<*>): MethodType {
            val method = classObj.getMethod("BootstrapMethod", classObj.javaClass, MethodHandles.lookup().javaClass, String::class.java, MethodType::class.java)
            val desp = Type.getMethodDescriptor(method)
            return MethodType.fromMethodDescriptorString(desp, null)
        }

        @Throws(Throwable::class)
        @JvmStatic
        private fun MH_BootstrapMethod(classObj: Class<*>): MethodHandle {
            return lookup().findStatic(classObj, "BootstrapMethod", MT_BootstrapMethod(classObj))
        }

        @Throws(Throwable::class)
        @JvmStatic
        private fun INDY_BootstrapMethod(classObj: Class<*>, methodName: String, vararg args: Class<*>): MethodHandle? {
            val method = classObj.getMethod(methodName, *args) ?: return null
            val decp = Type.getMethodDescriptor(method)
            val cs = MH_BootstrapMethod(classObj).invokeWithArguments(classObj, lookup(), methodName, MethodType.fromMethodDescriptorString(decp, null)) as CallSite
            return cs.dynamicInvoker()
        }
    }
}
