package org.irislang.jiris.irisclass;

import org.irislang.jiris.core.*;
import org.irislang.jiris.core.exceptions.IrisExceptionBase;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Huisama on 2017/4/8 0008.
 */
public class IrisHash extends IrisClassRoot {
    @Override
    public String NativeClassNameDefine() {
        return "Hash";
    }

    @Override
    public IrisClass NativeSuperClassDefine() {
        return IrisDevUtil.GetClass("Object");
    }

    @Override
    public IrisModule NativeUpperModuleDefine() {
        return null;
    }

    @Override
    public Object NativeAlloc() {
        return new HashMap<IrisValue, IrisValue>();
    }

    @Override
    public void NativeClassDefine(IrisClass classObj) throws IrisExceptionBase {
        classObj.AddInstanceMethod(IrisHash.class, "Initialize", "__format", 0, true, IrisMethod.MethodAuthority
                .Everyone);
        classObj.AddInstanceMethod(IrisHash.class, "At", "[]", 1, false, IrisMethod.MethodAuthority
                .Everyone);
        classObj.AddInstanceMethod(IrisHash.class, "Set", "[]=", 2, false, IrisMethod.MethodAuthority
                .Everyone);
        classObj.AddInstanceMethod(IrisHash.class, "GetIterator", "get_iterator", 0, false, IrisMethod
                .MethodAuthority
                .Everyone);
    }

    public static IrisValue Initialize(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        HashMap<IrisValue, IrisValue> hashMap = (HashMap<IrisValue, IrisValue>)IrisDevUtil.GetNativeObjectRef(self);

        if(variableParameterList != null) {
            int size = variableParameterList.size() / 2;
            for (int i = 0; i < size; i++) {
                hashMap.put(variableParameterList.get(i * 2), variableParameterList.get(i * 2 + 1));
            }
        }

        return self;
    }

    public static IrisValue At(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        HashMap<IrisValue, IrisValue> hashMap = (HashMap<IrisValue, IrisValue>)IrisDevUtil.GetNativeObjectRef(self);
        return hashMap.get(parameterList.get(0));
    }

    public static IrisValue Set(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        HashMap<IrisValue, IrisValue> hashMap = (HashMap<IrisValue, IrisValue>)IrisDevUtil.GetNativeObjectRef(self);
        IrisValue key = (IrisValue)IrisDevUtil.GetNativeObjectRef(parameterList.get(0));
        IrisValue value = (IrisValue)IrisDevUtil.GetNativeObjectRef(parameterList.get(1));

        hashMap.put(key, value);

        return IrisDevUtil.Nil();
    }

    public static IrisValue GetIterator(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        ArrayList<IrisValue> parameters = new ArrayList<IrisValue>(1);
        parameters.add(0, self);

        try {
            return IrisDevUtil.CreateInstance(IrisDevUtil.GetClass("HashIterator"), parameters, context, threadInfo);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return IrisDevUtil.Nil();
        }
    }
}
