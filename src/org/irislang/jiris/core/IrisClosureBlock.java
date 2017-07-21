package org.irislang.jiris.core;

import org.irislang.jiris.compiler.IrisInterpreter;
import org.irislang.jiris.core.exceptions.IrisExceptionBase;
import org.irislang.jiris.core.exceptions.fatal.IrisConstanceNotFoundException;
import org.irislang.jiris.core.exceptions.fatal.IrisParameterNotFitException;
import org.irislang.jiris.core.exceptions.fatal.IrisUnkownFatalException;
import org.irislang.jiris.dev.IrisDevUtil;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by yuwen on 2017/7/3 0003.
 */
public class IrisClosureBlock {
    private IrisContextEnvironment m_currentEnvironment = null;

    private IrisObject m_nativeObject = null;

    private ArrayList<String> m_parameters = null;
    private String m_variableParameter = null;

    MethodHandle m_methodHandle = null;

    public IrisObject getNativeObject() {
        return m_nativeObject;
    }

    public void setNativeObject(IrisObject nativeObject) {
        m_nativeObject = nativeObject;
}

    public IrisClosureBlock(IrisContextEnvironment upperEnvironment, ArrayList<String> parameters, String variableParameter, MethodHandle methodHandle) {
        this.m_parameters = parameters;
        this.m_variableParameter = variableParameter;
        this.m_methodHandle = methodHandle;

        this.m_nativeObject = new IrisObject();
        this.m_nativeObject.setObjectClass(IrisDevUtil.GetClass("Block"));
        this.m_nativeObject.setNativeObject(this);

        this.m_currentEnvironment = CreateNewEnvironment(upperEnvironment);
    }

    private IrisContextEnvironment CreateNewEnvironment(IrisContextEnvironment upperEnvrionment) {

        IrisContextEnvironment newEnv = new IrisContextEnvironment();

        newEnv.setClosureBlockObj(m_nativeObject);
        newEnv.setRunTimeType(IrisContextEnvironment.RunTimeType.RunTime);
        newEnv.setUpperContext(upperEnvrionment);
        newEnv.setRunningType(null);

        return newEnv;
    }

    public IrisValue Call(ArrayList<IrisValue> parameters, IrisThreadInfo info) throws IrisExceptionBase {
        if(!ParameterCheck(parameters)) {
            throw new IrisParameterNotFitException(IrisDevUtil.GetCurrentThreadInfo().getCurrentFileName(),
                    IrisDevUtil.GetCurrentThreadInfo().getCurrentLineNumber(),
                    "Parameter not fit:" + parameters == null ? " 0" : " " + Integer.toString(parameters.size())
                            + " " +
                            "for " +
                            Integer.toString(m_parameters == null ? 0 : m_parameters.size()) + ".");
        }

        // add local variables
        if (parameters != null) {
//            for (int i = 0; i < parameters.size(); i++) {
//                m_currentEnvironment.AddLocalVariable(m_parameters.get(i), parameters.get(i));
//            }
            // parameter -> local variable && variable parameter process
            if (parameters != null && parameters.size() != 0) {
                int counter = 0;
                Iterator<IrisValue> iterator = parameters.iterator();
                for (String value : m_parameters) {
                    m_currentEnvironment.AddLocalVariable(value, iterator.next());
                    ++counter;
                }

                if (m_variableParameter != null) {
                    ArrayList<IrisValue> variables = new ArrayList<IrisValue>(parameters.subList(counter, parameters.size()));
                    IrisClass arrayClass = IrisDevUtil.GetClass("Array");
                    IrisValue arrayValue = arrayClass.CreateNewInstance(variables, null, info);
                    m_currentEnvironment.AddLocalVariable(m_variableParameter, arrayValue);
                }
            }
        }

        IrisValue result = null;
        try {
            result = (IrisValue)(m_methodHandle.invokeExact(m_currentEnvironment, info));
        } catch (Throwable throwable) {
            //throwable.printStackTrace();
            if(throwable instanceof IrisExceptionBase) {
                throw (IrisExceptionBase)throwable;
            }
            else {
                throwable.printStackTrace();
                throw new IrisUnkownFatalException("Unkown irregular happened.", info.getCurrentLineNumber(), info.getCurrentFileName());
            }
        }
        return result;
    }

    private boolean ParameterCheck(ArrayList<IrisValue> parameters) {
        if (parameters != null && parameters.size() > 0) {
            if (m_variableParameter != null) {
                return parameters.size() >= m_parameters.size();
            } else {
                return parameters.size() == m_parameters.size();
            }
        } else {
            return m_parameters == null || m_parameters.size() == 0;
        }
    }

    public IrisValue GetLocalVariable(String name) {
        IrisValue target = m_currentEnvironment.GetLocalVariableWithinChain(name);

        if(target == null) {
            target = IrisValue.CloneValue(IrisDevUtil.Nil());
            m_currentEnvironment.AddLocalVariable(name, target);
        }

        return target;
    }

    public void AddLocalVariable(String name, IrisValue value) {
        m_currentEnvironment.AddLocalVariable(name, value);
    }

    public IrisValue GetInstanceVariable(String name) {
        IrisContextEnvironment tmpEnv = m_currentEnvironment;
        IrisValue target = tmpEnv.GetLocalVariable(name);

        tmpEnv = tmpEnv.getUpperContext();
        while(tmpEnv != null && target == null) {
            if(tmpEnv.getRunningType() != null) {
                IrisObject obj = null;
                switch (tmpEnv.getRunTimeType()) {
                    case ClassDefineTime:
                        obj = ((IrisClass)(tmpEnv.getRunningType())).getClassObject();
                        break;
                    case ModuleDefineTime:
                        obj = ((IrisModule)(tmpEnv.getRunningType())).getModuleObject();
                        break;
//                    case InterfaceDefineTime:
//                        break;
                    case RunTime:
                        obj = (IrisObject)tmpEnv.getRunningType();
                        break;
                }
                target = obj.GetInstanceVariable(name);
                if(target == null) {
                    target = tmpEnv.GetLocalVariable(name);
                }
            }
            else {
                target = tmpEnv.GetLocalVariable(name);
            }
        }

        if(target == null) {
            target = IrisValue.CloneValue(IrisDevUtil.Nil());
            m_currentEnvironment.AddLocalVariable(name, target);
        }

        return target;
    }

    public IrisValue GetClassVariable(String name) {
        IrisContextEnvironment tmpEnv = m_currentEnvironment;
        IrisValue target = tmpEnv.GetLocalVariable(name);

        tmpEnv = tmpEnv.getUpperContext();
        while(tmpEnv != null && target == null) {
            if(tmpEnv.getRunningType() != null) {
                switch (tmpEnv.getRunTimeType()) {
                    case ClassDefineTime:
                        target = ((IrisClass)(tmpEnv.getRunningType())).GetClassVariable(name);
                        break;
                    case ModuleDefineTime:
                        target = ((IrisModule)(tmpEnv.getRunningType())).GetClassVariable(name);
                        break;
//                    case InterfaceDefineTime:
//                        break;
                    case RunTime:
                        target = ((IrisObject)tmpEnv.getRunningType()).getObjectClass().GetClassVariable(name);
                        break;
                }
                if(target == null) {
                    target = tmpEnv.GetLocalVariable(name);
                }
            }
            else {
                target = tmpEnv.GetLocalVariable(name);
            }

            tmpEnv = tmpEnv.getUpperContext();
        }

        if(target == null) {
            target = IrisValue.CloneValue(IrisDevUtil.Nil());
            m_currentEnvironment.AddLocalVariable(name, target);
        }

        return target;
    }

    public IrisValue GetConstance(String name) throws IrisExceptionBase {
        IrisContextEnvironment tmpEnv = m_currentEnvironment;
        IrisValue target = null;

        while(tmpEnv != null) {
            if(tmpEnv.getRunningType() != null) {
                switch (tmpEnv.getRunTimeType()) {
                    case ClassDefineTime:
                        target = ((IrisClass)(tmpEnv.getRunningType())).GetConstance(name);
                        break;
                    case ModuleDefineTime:
                        target = ((IrisModule)(tmpEnv.getRunningType())).GetConstance(name);
                        break;
//                    case InterfaceDefineTime:
//                        break;
                    case RunTime:
                        target = ((IrisObject)tmpEnv.getRunningType()).getObjectClass().GetConstance(name);
                        break;
                }
            }
            tmpEnv = tmpEnv.getUpperContext();
        }

        if(target == null) {
            target = IrisInterpreter.INSTANCE.GetConstance(name);
        }

        if(target == null) {
            throw new IrisConstanceNotFoundException(IrisDevUtil.GetCurrentThreadInfo().getCurrentFileName(),
                    IrisDevUtil.GetCurrentThreadInfo().getCurrentLineNumber(),
                    "Constance of " + name + " not found.");
        }

        return target;
    }

}
