package org.irislang.jiris

import java.lang.invoke.CallSite
import java.lang.invoke.ConstantCallSite
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedList

import org.irislang.jiris.core.*
import org.irislang.jiris.core.IrisMethod.IrisUserMethod
import org.irislang.jiris.core.IrisContextEnvironment.RunTimeType
import org.irislang.jiris.core.exceptions.IrisExceptionBase
import org.irislang.jiris.core.exceptions.fatal.*
import org.irislang.jiris.dev.IrisDevUtil
import org.irislang.jiris.irisclass.IrisClassBase
import org.irislang.jiris.irisclass.IrisModuleBase

open class IrisNativeJavaClass {

    companion object {
        @Throws(Throwable::class)
        @JvmStatic
        fun BootstrapMethod(classObj: Class<*>, lookup: MethodHandles.Lookup, name: String, mt: MethodType): CallSite {
            return ConstantCallSite(lookup.findStatic(classObj, name, mt))
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun GetFieldValue(headValue: IrisValue, pathConstance: Array<String>?, lastConstance: String): IrisValue {
            var currentValue: IrisValue? = headValue
            var currentModule: IrisModule?

            if (IrisDevUtil.CheckClass(currentValue!!, "Module")) {
                currentModule = (IrisDevUtil.GetNativeObjectRef<Any>(currentValue) as IrisModuleBase.IrisModuleBaseTag).module
            } else {
                // Error
                throw IrisInvalidFieldException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                        "Head value is not a module!")
            }

            if (pathConstance != null) {
                for (identifier in pathConstance) {
                    currentValue = currentModule!!.SearchConstance(identifier)
                    if (currentValue == null) {
                        // Error
                        throw IrisInvalidFieldException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                                "Inner constance of $pathConstance not found!")
                    }

                    if (!IrisDevUtil.CheckClass(currentValue, "Module")) {
                        // Error
                        throw IrisInvalidFieldException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                                "Inner constance of $pathConstance is not a module!")
                    }

                    currentModule = (IrisDevUtil.GetNativeObjectRef<Any>(currentValue) as IrisModuleBase.IrisModuleBaseTag).module
                }
            }

            currentValue = currentModule!!.SearchConstance(lastConstance)
            if (currentValue == null) {
                throw IrisInvalidFieldException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                        "Last inner constance of $lastConstance is not a module!")
            }

            return currentValue
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun CallMethod(`object`: IrisValue?, methodName: String, threadInfo: IrisThreadInfo, context: IrisContextEnvironment, parameterCount: Int): IrisValue? {
            val result: IrisValue?
            // hide call
            if (`object` == null) {
                // main
                if (context.runTimeType !== RunTimeType.RunTime) {
                    val method = IrisInterpreter.INSTANCE.GetMainMethod(methodName)
                    if (method == null) {
                        result = IrisDevUtil.GetModule("Kernel")!!.moduleObject!!.CallInstanceMethod(methodName, threadInfo.getPartPrameterListOf(parameterCount), context, threadInfo, IrisMethod.CallSide.Outeside)
                    } else {
                        result = method.CallMain(threadInfo.getPartPrameterListOf(parameterCount), context, threadInfo)
                    }
                } else {
                    if (context.runningType != null) {
                        result = (context.runningType as IrisObject).CallInstanceMethod(methodName, threadInfo.getPartPrameterListOf(parameterCount), context, threadInfo, IrisMethod.CallSide.Inside)
                    } else {
                        val method = IrisInterpreter.INSTANCE.GetMainMethod(methodName)
                        if (method == null) {
                            result = IrisDevUtil.GetModule("Kernel")!!.moduleObject!!.CallInstanceMethod(methodName, threadInfo.getPartPrameterListOf(parameterCount), context, threadInfo, IrisMethod.CallSide.Outeside)
                        } else {
                            result = method.CallMain(threadInfo.getPartPrameterListOf(parameterCount), context, threadInfo)
                        }
                    }
                }
            } else {
                // normal call
                result = `object`.`object`!!.CallInstanceMethod(methodName, threadInfo.getPartPrameterListOf(parameterCount), context, threadInfo, IrisMethod.CallSide.Outeside)
            }
            return result
        }

        @JvmStatic
        fun GetLocalVariable(variableName: String, threadInfo: IrisThreadInfo, context: IrisContextEnvironment): IrisValue {
            var value: IrisValue? = if (context.closureBlockObj != null)
                (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).GetLocalVariable(variableName)
            else
                context.GetLocalVariable(variableName)
            if (value == null) {
                value = IrisValue.CloneValue(IrisDevUtil.Nil())
                if (context.closureBlockObj != null) {
                    (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).AddLocalVariable(variableName, value)
                } else {
                    context.AddLocalVariable(variableName, value)
                }
            } else {
                value = IrisValue.CloneValue(value)
            }
            return IrisValue.CloneValue(value)
        }

        @JvmStatic
        fun SetLocalVariable(variableName: String, value: IrisValue, threadInfo: IrisThreadInfo, context: IrisContextEnvironment): IrisValue {
            val testValue = if (context.closureBlockObj != null)
                (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).GetLocalVariable(variableName)
            else
                context.GetLocalVariable(variableName)
            if (testValue == null) {
                if (context.closureBlockObj != null) {
                    (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).AddLocalVariable(variableName, IrisValue.CloneValue(value))
                } else {
                    context.AddLocalVariable(variableName, IrisValue.CloneValue(value))
                }
            } else {
                testValue.`object` = value.`object`
            }
            return value
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun GetClassVariable(variableName: String, threadInfo: IrisThreadInfo,
                                      context: IrisContextEnvironment): IrisValue {
            var value: IrisValue? = null
            // Main context
            if (context.runningType == null) {
                value = if (context.closureBlockObj != null)
                    (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).GetClassVariable(variableName)
                else
                    context.GetLocalVariable(variableName)
                if (value == null) {
                    value = IrisValue.CloneValue(IrisDevUtil.Nil())
                    if (context.closureBlockObj != null) {
                        (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).AddLocalVariable(variableName, IrisValue.CloneValue(value))
                    } else {
                        context.AddLocalVariable(variableName, value)
                    }
                } else {
                    value = IrisValue.CloneValue(value)
                }
            } else {
                when (context.runTimeType) {
                    IrisContextEnvironment.RunTimeType.ClassDefineTime -> {
                        value = (context.runningType as IrisClass).SearchClassVariable(variableName)
                        if (value == null) {
                            value = IrisValue.CloneValue(IrisDevUtil.Nil())
                            (context.runningType as IrisClass).AddClassVariable(variableName, value)
                        } else {
                            value = IrisValue.CloneValue(value)
                        }
                    }
                    IrisContextEnvironment.RunTimeType.ModuleDefineTime -> {
                        value = (context.runningType as IrisModule).SearchClassVariable(variableName)
                        if (value == null) {
                            value = IrisValue.CloneValue(IrisDevUtil.Nil())
                            (context.runningType as IrisModule).AddClassVariable(variableName, value)
                        } else {
                            value = IrisValue.CloneValue(value)
                        }
                    }
                    IrisContextEnvironment.RunTimeType.InterfaceDefineTime ->
                        /* Error */
                        throw IrisVariableImpossiblyExistsException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Class variable won't exist in " + "interface")
                    IrisContextEnvironment.RunTimeType.RunTime -> {
                        value = (context.runningType as IrisClass).SearchClassVariable(variableName)
                        if (value == null) {
                            value = IrisValue.CloneValue(IrisDevUtil.Nil())
                            (context.runningType as IrisClass).AddClassVariable(variableName, value)
                        } else {
                            value = IrisValue.CloneValue(value)
                        }
                    }
                }
            }// Class Type
            return value
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun SetClassVariable(variableName: String, value: IrisValue, threadInfo: IrisThreadInfo,
                                      context: IrisContextEnvironment): IrisValue {
            var testValue: IrisValue? = null
            // Main context
            if (context.runningType == null) {
                //testValue = context.GetLocalVariable(variableName);
                testValue = if (context.closureBlockObj != null)
                    (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).GetClassVariable(variableName)
                else
                    context.GetLocalVariable(variableName)
                if (testValue == null) {
                    //context.AddLocalVariable(variableName, IrisValue.CloneValue(value));
                    if (context.closureBlockObj != null) {
                        (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).AddLocalVariable(variableName, IrisValue.CloneValue(value))
                    } else {
                        context.AddLocalVariable(variableName, IrisValue.CloneValue(value))
                    }
                } else {
                    testValue.`object` = value.`object`
                }
            } else {
                when (context.runTimeType) {
                    IrisContextEnvironment.RunTimeType.ClassDefineTime -> {
                        testValue = (context.runningType as IrisClass).SearchClassVariable(variableName)
                        if (testValue == null) {
                            (context.runningType as IrisClass).AddClassVariable(variableName, IrisValue.CloneValue(value))
                        } else {
                            testValue.`object` = value.`object`
                        }
                    }
                    IrisContextEnvironment.RunTimeType.ModuleDefineTime -> {
                        testValue = (context.runningType as IrisModule).SearchClassVariable(variableName)
                        if (testValue == null) {
                            (context.runningType as IrisModule).AddClassVariable(variableName, IrisValue.CloneValue(value))
                        } else {
                            testValue.`object` = value.`object`
                        }
                    }
                    IrisContextEnvironment.RunTimeType.InterfaceDefineTime ->
                        /* Error */
                        throw IrisVariableImpossiblyExistsException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Class variable won't be defined " + "in interface")
                    IrisContextEnvironment.RunTimeType.RunTime -> {
                        testValue = (context.runningType as IrisClass).SearchClassVariable(variableName)
                        if (testValue == null) {
                            (context.runningType as IrisClass).AddClassVariable(variableName, IrisValue.CloneValue(value))
                        } else {
                            testValue.`object` = value.`object`
                        }
                    }
                }
            }

            return value
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun GetConstance(variableName: String, threadInfo: IrisThreadInfo, context: IrisContextEnvironment): IrisValue {
            var value: IrisValue? = null
            if (context.runningType == null) {
                //value = IrisInterpreter.INSTANCE.GetConstance(variableName);
                value = if (context.closureBlockObj != null)
                    (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).GetConstance(variableName)
                else
                    IrisInterpreter.INSTANCE.GetConstance(variableName)
                if (value == null) {
                    value = IrisValue.CloneValue(IrisDevUtil.Nil())
                    //IrisInterpreter.INSTANCE.AddConstance(variableName, value);
                    if (context.closureBlockObj != null) {
                        //((IrisClosureBlock)(IrisDevUtil.GetNativeObjectRef(context.getClosureBlockObj()))).AddLocalVariable(variableName, IrisValue.CloneValue(value));
                        throw IrisVariableImpossiblyExistsException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Constance won't be declared in Block.")
                    } else {
                        IrisInterpreter.INSTANCE.AddConstance(variableName, IrisValue.CloneValue(value))
                    }
                } else {
                    value = IrisValue.CloneValue(value)
                }
            } else {
                when (context.runTimeType) {
                    IrisContextEnvironment.RunTimeType.ClassDefineTime -> {
                        value = (context.runningType as IrisClass).SearchConstance(variableName)
                        if (value == null) {
                            value = IrisInterpreter.INSTANCE.GetConstance(variableName)
                        }

                        if (value == null) {
                            value = IrisValue.CloneValue(IrisDevUtil.Nil())
                            (context.runningType as IrisClass).AddConstance(variableName, value)
                        } else {
                            value = IrisValue.CloneValue(value)
                        }
                    }
                    IrisContextEnvironment.RunTimeType.ModuleDefineTime -> {
                        value = (context.runningType as IrisModule).SearchConstance(variableName)
                        if (value == null) {
                            value = IrisInterpreter.INSTANCE.GetConstance(variableName)
                        }

                        if (value == null) {
                            value = IrisValue.CloneValue(IrisDevUtil.Nil())
                            (context.runningType as IrisModule).AddConstance(variableName, value)
                        } else {
                            value = IrisValue.CloneValue(value)
                        }
                    }
                    IrisContextEnvironment.RunTimeType.InterfaceDefineTime ->
                        /* Error */
                        throw IrisVariableImpossiblyExistsException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Constance won't exist in interface.")
                    IrisContextEnvironment.RunTimeType.RunTime -> {
                        value = (context.runningType as IrisObject).objectClass!!.SearchConstance(variableName)
                        if (value == null) {
                            value = IrisInterpreter.INSTANCE.GetConstance(variableName)
                        }

                        if (value == null) {
                            value = IrisValue.CloneValue(IrisDevUtil.Nil())
                            (context.runningType as IrisObject).objectClass!!.AddConstance(variableName, value)
                        } else {
                            value = IrisValue.CloneValue(value)
                        }
                    }
                }
            }
            return value
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun SetConstance(variableName: String, value: IrisValue, threadInfo: IrisThreadInfo,
                                  context: IrisContextEnvironment): IrisValue {
            var testValue: IrisValue? = null
            if (context.runningType == null) {
                //testValue = IrisInterpreter.INSTANCE.GetConstance(variableName);
                testValue = if (context.closureBlockObj != null)
                    (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).GetConstance(variableName)
                else
                    IrisInterpreter.INSTANCE.GetConstance(variableName)
                if (testValue == null) {
                    //IrisInterpreter.INSTANCE.AddConstance(variableName, IrisValue.CloneValue(value));
                    if (context.closureBlockObj != null) {
                        //((IrisClosureBlock)(IrisDevUtil.GetNativeObjectRef(context.getClosureBlockObj()))).AddLocalVariable(variableName, IrisValue.CloneValue(value));
                        throw IrisVariableImpossiblyExistsException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Constance won't be declared in Block.")
                    } else {
                        IrisInterpreter.INSTANCE.AddConstance(variableName, IrisValue.CloneValue(value))
                    }
                } else {
                    /* Error */
                    throw IrisConstanceReassignedException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                            IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Constance of" + variableName +
                            "has already assigned.")
                }
            } else {
                when (context.runTimeType) {
                    IrisContextEnvironment.RunTimeType.ClassDefineTime -> {
                        testValue = (context.runningType as IrisClass).GetConstance(variableName)
                        if (testValue == null) {
                            (context.runningType as IrisClass).AddConstance(variableName, IrisValue.CloneValue(value))
                        } else {
                            /* Error */
                            throw IrisConstanceReassignedException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                    IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Constance of" + variableName +
                                    "has already assigned.")
                        }
                    }
                    IrisContextEnvironment.RunTimeType.ModuleDefineTime -> {
                        testValue = (context.runningType as IrisModule).GetConstance(variableName)
                        if (testValue == null) {
                            (context.runningType as IrisModule).AddConstance(variableName, IrisValue.CloneValue(value))
                        } else {
                            /* Error */
                            throw IrisConstanceReassignedException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                    IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Constance of" + variableName +
                                    "has already assigned.")
                        }
                    }
                    IrisContextEnvironment.RunTimeType.InterfaceDefineTime ->
                        /* Error */
                        throw IrisVariableImpossiblyExistsException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Constance can not be defined in " + "interface")
                    IrisContextEnvironment.RunTimeType.RunTime -> {
                        testValue = (context.runningType as IrisObject).objectClass!!.GetConstance(variableName)
                        if (testValue == null) {
                            (context.runningType as IrisObject).objectClass!!.AddConstance(variableName, IrisValue.CloneValue(value))
                        } else {
                            /* Error */
                            throw IrisConstanceReassignedException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                    IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Constance of" + variableName +
                                    "has already assigned.")
                        }
                    }
                }
            }
            return value
        }

        @JvmStatic
        fun GetGlobalVariable(variableName: String, threadInfo: IrisThreadInfo, context: IrisContextEnvironment): IrisValue {
            var value: IrisValue? = null
            value = IrisInterpreter.INSTANCE.GetGlobalValue(variableName)
            if (value == null) {
                value = IrisValue.CloneValue(IrisDevUtil.Nil())
                IrisInterpreter.INSTANCE.AddGlobalValue(variableName, value)
            }
            return value
        }

        @JvmStatic
        fun SetGlobalVariable(variableName: String, value: IrisValue, threadInfo: IrisThreadInfo, context: IrisContextEnvironment): IrisValue {
            val testValue = IrisInterpreter.INSTANCE.GetGlobalValue(variableName)
            if (testValue == null) {
                IrisInterpreter.INSTANCE.AddGlobalValue(variableName, IrisValue.CloneValue(value))
            } else {
                testValue.`object` = value.`object`
            }
            return value
        }

        @JvmStatic
        fun GetInstanceVariable(variableName: String, threadInfo: IrisThreadInfo, context: IrisContextEnvironment): IrisValue {
            var value: IrisValue? = null
            val obj = context.runningType as IrisObject?

            if (obj != null) {
                value = obj.GetInstanceVariable(variableName)
                if (value == null) {
                    value = IrisValue.CloneValue(IrisDevUtil.Nil())
                    obj.AddInstanceVariable(variableName, value)
                } else {
                    value = IrisValue.CloneValue(value)
                }
            } else {
                //value = context.GetLocalVariable(variableName);
                value = if (context.closureBlockObj != null)
                    (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).GetInstanceVariable(variableName)
                else
                    context.GetLocalVariable(variableName)
                if (value == null) {
                    value = IrisValue.CloneValue(IrisDevUtil.Nil())
                    //context.AddLocalVariable(variableName, IrisValue.CloneValue(value));
                    if (context.closureBlockObj != null) {
                        (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).AddLocalVariable(variableName, IrisValue.CloneValue(value))
                    } else {
                        context.AddLocalVariable(variableName, IrisValue.CloneValue(value))
                    }
                } else {
                    value = IrisValue.CloneValue(value)
                }
            }

            return value
        }

        @JvmStatic
        fun SetInstanceVariable(variableName: String, value: IrisValue, threadInfo: IrisThreadInfo, context: IrisContextEnvironment): IrisValue {

            var testValue: IrisValue? = null
            val obj = context.runningType as IrisObject?

            if (obj != null) {
                testValue = obj.GetInstanceVariable(variableName)
                if (testValue == null) {
                    obj.AddInstanceVariable(variableName, IrisValue.CloneValue(value))
                } else {
                    testValue.`object` = value.`object`
                }
            } else {
                //testValue = context.GetLocalVariable(variableName);
                testValue = if (context.closureBlockObj != null)
                    (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).GetInstanceVariable(variableName)
                else
                    context.GetLocalVariable(variableName)
                if (testValue == null) {
                    //context.AddLocalVariable(variableName, IrisValue.CloneValue(value));
                    if (context.closureBlockObj != null) {
                        (IrisDevUtil.GetNativeObjectRef<Any>(context.closureBlockObj!!) as IrisClosureBlock).AddLocalVariable(variableName, IrisValue.CloneValue(value))
                    } else {
                        context.AddLocalVariable(variableName, IrisValue.CloneValue(value))
                    }
                } else {
                    testValue.`object` = value.`object`
                }
            }

            return value
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun CompareCounterLess(org: IrisValue, tar: IrisValue, threadInfo: IrisThreadInfo, context: IrisContextEnvironment): Boolean {
            threadInfo.AddParameter(tar)
            val result = CallMethod(org, "<", threadInfo, context, 1)
            threadInfo.PopParameter(1)
            return result == IrisDevUtil.True()
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun DefineDefaultGetter(methodName: String,
                                         targetVariale: String,
                                         authority: IrisMethod.MethodAuthority,
                                         context: IrisContextEnvironment,
                                         threadInfo: IrisThreadInfo) {

            if (context.runTimeType === RunTimeType.ClassDefineTime || context.runTimeType === RunTimeType.ModuleDefineTime) {
                val classObj = context.runningType as IrisClass?
                val method = IrisMethod(methodName, targetVariale, IrisMethod.GetterSetter.Getter, authority)
                classObj!!.AddInstanceMethod(method)
            } else {
                // Error
                throw IrisAccessorDefinedException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Getter can only be defined in class or module")
            }
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun DefineDefaultSetter(methodName: String,
                                         targetVariale: String,
                                         authority: IrisMethod.MethodAuthority,
                                         context: IrisContextEnvironment,
                                         threadInfo: IrisThreadInfo) {
            if (context.runTimeType === RunTimeType.ClassDefineTime) {
                val classObj = context.runningType as IrisClass?
                val method = IrisMethod(methodName, targetVariale, IrisMethod.GetterSetter.Setter, authority)
                classObj!!.AddInstanceMethod(method)
            } else {
                // Error
                throw IrisAccessorDefinedException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Setter can only be defined in class " + "or module")
            }
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun DefineInstanceMethod(
                nativeClass: Class<*>,
                nativeName: String,
                methodName: String,
                parameters: Array<String>?,
                variableParameter: String?,
                withBlockName: String?,
                withoutBlockName: String?,
                authority: IrisMethod.MethodAuthority,
                context: IrisContextEnvironment,
                threadInfo: IrisThreadInfo) {

            val userMethod = IrisUserMethod()
            if (parameters != null) {
                userMethod.parameterList = ArrayList(Arrays.asList(*parameters))
            } else {
                userMethod.parameterList = ArrayList()
            }

            if (variableParameter != null) {
                userMethod.variableParameterName = variableParameter
            }

            if (context.runTimeType === IrisContextEnvironment.RunTimeType.RunTime) {
                IrisInterpreter.INSTANCE.AddMainMethod(
                        IrisMethod(
                                methodName,
                                userMethod,
                                authority,
                                IrisDevUtil.GetIrisNativeUserMethodHandle(nativeClass, nativeName)))
            } else if (context.runTimeType === RunTimeType.ClassDefineTime) {
                val classObj = context.runningType as IrisClass?
                classObj!!.AddInstanceMethod(nativeClass, nativeName, methodName, userMethod, authority)
            } else if (context.runTimeType === RunTimeType.ModuleDefineTime) {
                val moduleObj = context.runningType as IrisModule?
                moduleObj!!.AddInstanceMethod(nativeClass, nativeName, methodName, userMethod, authority)
            }
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun DefineClassMethod(
                nativeClass: Class<*>,
                nativeName: String,
                methodName: String,
                parameters: Array<String>?,
                variableParameter: String?,
                withBlockName: String,
                withoutBlockName: String,
                authority: IrisMethod.MethodAuthority,
                context: IrisContextEnvironment,
                threadInfo: IrisThreadInfo) {
            val userMethod = IrisUserMethod()
            if (parameters != null) {
                userMethod.parameterList = ArrayList(Arrays.asList(*parameters))
            } else {
                userMethod.parameterList = ArrayList()
            }

            if (variableParameter != null) {
                userMethod.variableParameterName = variableParameter
            }

            if (context.runTimeType === IrisContextEnvironment.RunTimeType.RunTime || context.runTimeType === RunTimeType.InterfaceDefineTime) {
                // Error
                throw IrisMethodDefinedException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber, "Class method can only be defined in " + " class or module")
            } else if (context.runTimeType === IrisContextEnvironment.RunTimeType.ClassDefineTime) {
                val classObj = context.runningType as IrisClass?
                classObj!!.AddClassMethod(nativeClass, nativeName, methodName, userMethod, authority)
            } else if (context.runTimeType === RunTimeType.ModuleDefineTime) {
                val moduleObj = context.runningType as IrisModule?
                moduleObj!!.AddClassMethod(nativeClass, nativeName, methodName, userMethod, authority)
            }
        }

        @JvmStatic
        fun DefineClass(className: String, context: IrisContextEnvironment,
                                 threadInfo: IrisThreadInfo): IrisContextEnvironment {
            val newEnv = IrisContextEnvironment()
            newEnv.runTimeType = IrisContextEnvironment.RunTimeType.ClassDefineTime
            newEnv.upperContext = context

            // check if open class
            var upperModule: IrisModule? = null
            var upperContext: IrisContextEnvironment? = context
            while (upperContext != null) {
                upperModule = upperContext.runningType as IrisModule?
                if (upperModule != null) {
                    break
                }
                upperContext = upperContext.upperContext
            }

            var currentClass: IrisClass? = null
            var result: IrisValue? = null
            if (upperModule != null) {
                result = upperModule.GetConstance(className)
            } else {
                result = IrisInterpreter.INSTANCE.GetConstance(className)
            }

            if (result != null && IrisDevUtil.CheckClass(result, "Class")) {
                currentClass = (IrisDevUtil.GetNativeObjectRef<Any>(result) as IrisClassBase.IrisClassBaseTag).classObj
            } else {
                try {
                    currentClass = IrisClass(className, upperModule, IrisDevUtil.GetClass("Object")!!)
                    if (upperModule != null) {
                        upperModule.AddConstance(className, IrisValue.WrapObject(currentClass.classObject!!))
                        upperModule.AddSubClass(currentClass)
                    } else {
                        IrisInterpreter.INSTANCE.AddConstance(className, IrisValue.WrapObject(currentClass.classObject!!))
                    }
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }

            }

            //
            newEnv.runningType = currentClass

            return newEnv
        }

        @JvmStatic
        fun DefineModule(moduleName: String, context: IrisContextEnvironment,
                                  threadInfo: IrisThreadInfo): IrisContextEnvironment {
            val newEnv = IrisContextEnvironment()
            newEnv.runTimeType = RunTimeType.ModuleDefineTime
            newEnv.upperContext = context

            // check if open module
            var upperModule: IrisModule? = null
            var upperContext: IrisContextEnvironment? = context
            while (upperContext != null) {
                upperModule = upperContext.runningType as IrisModule?
                if (upperModule != null) {
                    break
                }
                upperContext = upperContext.upperContext
            }

            var currentModule: IrisModule? = null
            var result: IrisValue? = null
            if (upperModule != null) {
                result = upperModule.SearchConstance(moduleName)
            } else {
                result = IrisInterpreter.INSTANCE.GetConstance(moduleName)
            }

            if (result != null && IrisDevUtil.CheckClass(result, "Module")) {
                currentModule = (IrisDevUtil.GetNativeObjectRef<Any>(result) as IrisModuleBase.IrisModuleBaseTag).module
            } else {
                try {
                    currentModule = IrisModule(moduleName, upperModule)
                    if (upperModule != null) {
                        upperModule.AddConstance(moduleName, IrisValue.WrapObject(currentModule.moduleObject!!))
                        upperModule.AddSubModule(currentModule)
                    } else {
                        IrisInterpreter.INSTANCE.AddConstance(moduleName, IrisValue.WrapObject(currentModule.moduleObject!!))
                    }
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }

            }

            //
            newEnv.runningType = currentModule

            return newEnv
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun SetSuperClass(context: IrisContextEnvironment, threadInfo: IrisThreadInfo) {
            val superClass = threadInfo.GetTempSuperClass()

            if (context.runTimeType !== RunTimeType.ClassDefineTime) {
                // Error
                throw IrisUnkownFatalException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                        "Oh, shit! An UNKNOWN ERROR has been lead to by YOU to Iris! What a SHIT unlucky man you are! " +
                                "Please don't approach Iris ANYMORE ! - Super class can not be set here.")
            }

            if (!IrisDevUtil.CheckClass(superClass!!, "Class")) {
                // Error
                throw IrisTypeNotCorretException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                        IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                        "Super class must be a class.")
            }

            val classObj = context.runningType as IrisClass?
            classObj!!.superClass = (IrisDevUtil.GetNativeObjectRef<Any>(superClass) as IrisClassBase.IrisClassBaseTag).classObj
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun AddModule(context: IrisContextEnvironment, threadInfo: IrisThreadInfo) {
            val tempModules = threadInfo.GetTempModules()

            for (involvedModule in tempModules) {
                if (context.runTimeType === RunTimeType.ClassDefineTime) {
                    val classObj = context.runningType as IrisClass?
                    if (!IrisDevUtil.CheckClass(involvedModule, "Module")) {
                        // Error
                        throw IrisTypeNotCorretException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                                "Only module can be involved.")
                    }
                    val tmpModuleObj = (IrisDevUtil.GetNativeObjectRef<IrisModuleBase.IrisModuleBaseTag>(involvedModule)).module
                    classObj!!.AddInvolvedModule(tmpModuleObj!!)
                } else if (context.runTimeType === RunTimeType.ModuleDefineTime) {
                    val moduleObj = context.runningType as IrisModule?
                    if (!IrisDevUtil.CheckClass(involvedModule, "Module")) {
                        // Error
                        throw IrisTypeNotCorretException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                                IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                                "Only module can be involved.")
                    }
                    val tmpModuleObj = IrisDevUtil.GetNativeObjectRef<Any>(involvedModule) as IrisModule
                    moduleObj!!.AddInvolvedModule(tmpModuleObj)
                } else {
                    // Error
                    throw IrisUnkownFatalException(IrisDevUtil.GetCurrentThreadInfo().currentFileName,
                            IrisDevUtil.GetCurrentThreadInfo().currentLineNumber,
                            "Oh, shit! An UNKNOWN ERROR has been lead to by YOU to Iris! What a SHIT unlucky man you are! "
                                    + "Please don't approach Iris ANYMORE ! - Module can not be involved here.")
                }
            }
        }

        @JvmStatic
        fun AddInterface(context: IrisContextEnvironment, threadInfo: IrisThreadInfo) {

        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun SetClassMethodAuthority(methodName: String, authority: IrisMethod.MethodAuthority,
                                             environment: IrisContextEnvironment, threadInfo: IrisThreadInfo) {
            when (environment.runTimeType) {
                IrisContextEnvironment.RunTimeType.ClassDefineTime -> (environment.runningType as IrisClass).SetClassMethodAuthority(methodName, authority)
                IrisContextEnvironment.RunTimeType.ModuleDefineTime -> (environment.runningType as IrisModule).SetClassMethodAuthority(methodName, authority)
                IrisContextEnvironment.RunTimeType.InterfaceDefineTime -> {
                }
                IrisContextEnvironment.RunTimeType.RunTime -> {
                }
            }
        }

        @Throws(IrisExceptionBase::class)
        @JvmStatic
        fun SetInstanceMethodAuthority(methodName: String, authority: IrisMethod.MethodAuthority,
                                                environment: IrisContextEnvironment, threadInfo: IrisThreadInfo) {
            when (environment.runTimeType) {
                IrisContextEnvironment.RunTimeType.ClassDefineTime -> (environment.runningType as IrisClass).SetInstanceMethodAuthority(methodName, authority)
                IrisContextEnvironment.RunTimeType.ModuleDefineTime -> (environment.runningType as IrisModule).SetInstanceMethodAuthority(methodName, authority)
                IrisContextEnvironment.RunTimeType.InterfaceDefineTime -> {
                }
                IrisContextEnvironment.RunTimeType.RunTime -> {
                }
            }
        }

        @JvmStatic
        fun CreateClosureBlock(upperEnvironment: IrisContextEnvironment, parameters: Array<String>?,
                                        variableParameter: String, nativeMethodClass: Class<*>, nativeMethodName: String,
                                        threadInfo: IrisThreadInfo): IrisValue {
            threadInfo.PushClosureBlock(
                    IrisClosureBlock(upperEnvironment, if (parameters != null) ArrayList(Arrays.asList(*parameters)) else ArrayList(), variableParameter,
                            IrisDevUtil.GetIrisClosureBlockHandle(nativeMethodClass, nativeMethodName)
                    )
            )
            return IrisValue.WrapObject(threadInfo.GetTopClosureBlock().nativeObject)
        }

        @JvmStatic
        fun ClearClosureBlock(threadInfo: IrisThreadInfo) {
            threadInfo.PopClosureBlock()
        }

        @JvmStatic
        fun GetCastObject(threadInfo: IrisThreadInfo): IrisValue {
            return IrisValue.WrapObject(threadInfo.GetTopClosureBlock().nativeObject)
        }

        @JvmStatic
        @Throws(IrisExceptionBase::class)
        fun GetSelfObject(context: IrisContextEnvironment, info: IrisThreadInfo): IrisValue {
            if (context.closureBlockObj != null) {
                var tmpEnv: IrisContextEnvironment? = context
                while (tmpEnv != null) {
                    if (tmpEnv.runningType != null && tmpEnv.runTimeType === RunTimeType.RunTime) {
                        return IrisValue.WrapObject(context.runningType as IrisObject)
                    }
                    tmpEnv = tmpEnv.upperContext
                }
                throw IrisWrongSelfException(info.currentFileName, info.currentLineNumber, "No object can be found with self.")
            } else {
                return IrisValue.WrapObject(context.runningType as IrisObject)
            }
        }
    }
}
