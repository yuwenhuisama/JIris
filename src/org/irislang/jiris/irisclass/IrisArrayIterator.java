package org.irislang.jiris.irisclass;

import net.bytebuddy.pool.TypePool;
import org.irislang.jiris.core.*;
import org.irislang.jiris.core.exceptions.IrisExceptionBase;
import org.irislang.jiris.dev.IrisClassRoot;
import org.irislang.jiris.dev.IrisDevUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Huisama on 2017/4/8 0008.
 */
public class IrisArrayIterator extends IrisClassRoot {
    @Override
    public String NativeClassNameDefine() {
        return "ArrayIterator";
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
        return new IrisArrayIteratorTag();
    }

    @Override
    public void NativeClassDefine(IrisClass classObj) throws IrisExceptionBase {
        classObj.AddInstanceMethod(IrisArrayIterator.class, "Initialize", "__format", 1, false, IrisMethod.MethodAuthority.Everyone);
        classObj.AddInstanceMethod(IrisArrayIterator.class, "Next", "next", 0, false, IrisMethod.MethodAuthority
                .Everyone);
        classObj.AddInstanceMethod(IrisArrayIterator.class, "IsEnd", "is_end", 0, false, IrisMethod
                .MethodAuthority.Everyone);
        classObj.AddInstanceMethod(IrisArrayIterator.class, "GetValue", "get_value", 0, false, IrisMethod
                .MethodAuthority.Everyone);
    }

    public static IrisValue Initialize(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue>
            variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        IrisArrayIteratorTag iter = (IrisArrayIteratorTag)IrisDevUtil.GetNativeObjectRef(self);
        ArrayList<IrisValue> arrayList = (ArrayList<IrisValue>)IrisDevUtil.GetNativeObjectRef(parameterList.get(0));

        iter.Initialize(arrayList);

        return self;
    }

    public static IrisValue Next(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue>
            variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        IrisArrayIteratorTag iter = (IrisArrayIteratorTag)IrisDevUtil.GetNativeObjectRef(self);
        iter.Next();

        return self;
    }
    
    public static IrisValue IsEnd(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        IrisArrayIteratorTag iter = (IrisArrayIteratorTag)IrisDevUtil.GetNativeObjectRef(self);

        return iter.IsEnd() ? IrisDevUtil.True() : IrisDevUtil.False();
    }

    public static IrisValue GetValue(IrisValue self, ArrayList<IrisValue> parameterList, ArrayList<IrisValue> variableParameterList, IrisContextEnvironment context, IrisThreadInfo threadInfo) {
        IrisArrayIteratorTag iter = (IrisArrayIteratorTag)IrisDevUtil.GetNativeObjectRef(self);

        return iter.GetValue();
    }

    public class IrisArrayIteratorTag {
        private Iterator<IrisValue> m_iterator = null;
        private ArrayList<IrisValue> m_arrayList = null;
        private IrisValue m_currentValue = null;

        public void Initialize(ArrayList<IrisValue> arrayList) {
            m_arrayList = arrayList;
            m_iterator = arrayList.iterator();
        }

        public IrisArrayIteratorTag Next() {
            m_currentValue = m_iterator.next();
            return this;
        }

        public IrisValue GetValue() {
            return m_currentValue;
        }

        public boolean IsEnd() {
            return !m_iterator.hasNext();
        }

        public Iterator<IrisValue> GetIter() {
            return m_iterator;
        }
    }
}
