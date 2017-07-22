package org.irislang.jiris.irisclass;

import com.irisine.jiris.compiler.assistpart.IrisElseIf;
import org.irislang.jiris.core.*;
import org.irislang.jiris.core.exceptions.IrisExceptionBase;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Huisama on 2017/4/8 0008.
 */
public class IrisHashIterator extends IrisClassRoot {
    @Override
    public String NativeClassNameDefine() {
        return "HashIterator";
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
        return new IrisHashIteratorTag();
    }

    @Override
    public void NativeClassDefine(IrisClass classObj) throws IrisExceptionBase {
        classObj.AddInstanceMethod(IrisHashIterator.class, "Initialize", "__format", 1, false, IrisMethod.MethodAuthority.Everyone);
        classObj.AddInstanceMethod(IrisHashIterator.class, "Next", "next", 0, false, IrisMethod.MethodAuthority
                .Everyone);
        classObj.AddInstanceMethod(IrisHashIterator.class, "IsEnd", "is_end", 0, false, IrisMethod.MethodAuthority
                .Everyone);
        classObj.AddInstanceMethod(IrisHashIterator.class, "GetKey", "get_key", 0, false, IrisMethod.MethodAuthority
                .Everyone);
        classObj.AddInstanceMethod(IrisHashIterator.class, "GetValue", "get_value", 0, false, IrisMethod
                .MethodAuthority.Everyone);
    }

    public static IrisValue Initialize(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        IrisHashIteratorTag iter = (IrisHashIteratorTag)IrisDevUtil.GetNativeObjectRef(self);
        HashMap<IrisValue, IrisValue> hashMap = (HashMap<IrisValue, IrisValue>)IrisDevUtil.GetNativeObjectRef(parameterList.get(0));
        iter.Initialize(hashMap);
        return self;
    }

    public static IrisValue Next(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        IrisHashIteratorTag iter = (IrisHashIteratorTag)IrisDevUtil.GetNativeObjectRef(self);
        iter.Next();

        return self;
    }

    public static IrisValue IsEnd(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        IrisHashIteratorTag iter = (IrisHashIteratorTag)IrisDevUtil.GetNativeObjectRef(self);
        return iter.IsEnd() ? IrisDevUtil.True() : IrisDevUtil.False();
    }

    public static IrisValue GetKey(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        IrisHashIteratorTag iter = (IrisHashIteratorTag)IrisDevUtil.GetNativeObjectRef(self);
        return iter.GetKey();
    }

    public static IrisValue GetValue(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        IrisHashIteratorTag iter = (IrisHashIteratorTag)IrisDevUtil.GetNativeObjectRef(self);
        return iter.GetValue();
    }

    public class IrisHashIteratorTag {
        private Iterator m_iterator = null;
        private HashMap<IrisValue, IrisValue> m_hashMap = null;
        private HashMap.Entry<IrisValue, IrisValue> m_currentEntry = null;

        public void Initialize(HashMap<IrisValue, IrisValue> hashMap) {
            m_iterator = hashMap.entrySet().iterator();
            m_hashMap = hashMap;
        }

        public IrisHashIteratorTag Next() {
            m_currentEntry = (HashMap.Entry<IrisValue, IrisValue>) m_iterator.next();
            return this;
        }

        public boolean IsEnd() {
            return !m_iterator.hasNext();
        }

        public IrisValue GetKey() {
            return m_currentEntry.getKey();
        }

        public IrisValue GetValue() {
            return m_currentEntry.getValue();
        }

        public Iterator GetIter() {
            return m_iterator;
        }

    }
}
