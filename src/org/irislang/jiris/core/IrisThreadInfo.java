package org.irislang.jiris.core;

import org.irislang.jiris.dev.IrisDevUtil;
import org.irislang.jiris.irisclass.IrisInteger;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

public class IrisThreadInfo {
	
	private static long sm_mainThreadID = 0;
	private static IrisThreadInfo sm_mainThreadInfo = null;
	
	public static IrisThreadInfo GetCurrentThreadInfo() {
		long threadID = Thread.currentThread().getId();
		
		if(threadID == sm_mainThreadID) {
			return sm_mainThreadInfo;
		} else {
			return null;
		}
	}
	
	public static void SetMainThreedInfo(IrisThreadInfo threadInfo) {
		sm_mainThreadInfo = threadInfo;
	}
	
	public static void SetMainThreadID(long id) {
		sm_mainThreadID = id;
	}

	private ArrayList<IrisValue> m_parameterList = new ArrayList<IrisValue>();
	private IrisValue m_record = null;

	private IrisValue m_comparedObj = null;
	private int m_counter = 0;
	private Stack<IrisValue> m_loopTimeStack = new Stack<IrisValue>();
	private Stack<IrisValue> m_comparedObjectStack = new Stack<IrisValue>();
	private Stack<IrisValue> m_counterStack = new Stack<IrisValue>();

	public void PushComparedObject(IrisValue value) { m_comparedObjectStack.add(value);}

	public void PopCompareadObject() { m_comparedObjectStack.pop(); }

	public IrisValue GetTopComparedObject() { return m_comparedObjectStack.lastElement(); }

	public void PushLoopTime(IrisValue value) {
		m_loopTimeStack.add(value);
	}
		
	public void PopLoopTime() {
		m_loopTimeStack.pop();
	}

	public IrisValue GetTopLoopTime() {
		return m_loopTimeStack.lastElement();
	}
	
	public IrisValue getCounter() {
		return m_counterStack.lastElement();
	}
	
	public void pushCounter(IrisValue counter) {
		m_counterStack.push(counter);
	}

	public void PopCounter() {
		m_counterStack.pop();
	}

	public void increamCounter() {
		IrisInteger.IrisIntegerTag tag = (IrisInteger.IrisIntegerTag)IrisDevUtil.GetNativeObjectRef(getCounter());
		tag.setInteger(tag.getInteger() + 1);
	}
	
	public ArrayList<IrisValue> getPartPrameterListOf(int count) {
		return new ArrayList<IrisValue>(m_parameterList.subList(m_parameterList.size() - count, m_parameterList.size()));
	}
	
	public ArrayList<IrisValue> getParameterList() {
		return m_parameterList;
	}
	
	public void AddParameter(IrisValue value) {
		m_parameterList.add(value);
	}
	
	public void PopParameter(int times) {
		for(int i = 0; i < times; ++i) {
			m_parameterList.remove(m_parameterList.size() - 1);
		}
	}
	
	public void ClearPrameterList() {
		m_parameterList.clear();
	}
		
	public IrisValue getRecord() {
		return m_record;
	} 
	
	public void setRecord(IrisValue parameter) {
		m_record = parameter;
	}
}
