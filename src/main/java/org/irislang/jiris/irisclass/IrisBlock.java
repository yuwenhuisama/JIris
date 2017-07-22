package org.irislang.jiris.irisclass;

import org.irislang.jiris.core.*;
import org.irislang.jiris.core.exceptions.IrisExceptionBase;
import org.irislang.jiris.dev.IrisDevUtil;

import java.util.ArrayList;

/**
 * Created by yuwen on 2017/7/3 0003.
 */
public class IrisBlock extends IrisClassBase {
    @Override
    public String NativeClassNameDefine() {
        return "Block";
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
        // special
        return null;
    }

    @Override
    public void NativeClassDefine(IrisClass classObj) throws IrisExceptionBase {
        classObj.AddClassMethod(IrisBlock.class, "New", "new", 0, false, IrisMethod.MethodAuthority.Everyone);
        classObj.AddInstanceMethod(IrisBlock.class, "Call", "call", 0, true, IrisMethod.MethodAuthority.Everyone);
    }

    public static IrisValue New(IrisValue self, ArrayList<IrisValue> parameterList,
                                ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        return IrisValue.WrapObject(threadInfo.GetTopClosureBlock().getNativeObject());
    }

    public static IrisValue Call(IrisValue self, ArrayList<IrisValue> parameterList,
                                ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) throws IrisExceptionBase {
        IrisClosureBlock closureBlock = IrisDevUtil.GetNativeObjectRef(self);
        return closureBlock.Call(variableParameterList, threadInfo);
    }

}
