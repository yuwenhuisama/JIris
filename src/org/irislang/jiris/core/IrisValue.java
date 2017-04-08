package org.irislang.jiris.core;

public class IrisValue {
	private IrisObject m_object = null;
	
	public static IrisValue WrapObject(IrisObject obj) {
		IrisValue value = new IrisValue();
		value.m_object = obj;
		return value;
	}

	public static IrisValue CloneValue(IrisValue value) {
		IrisValue v = new IrisValue();
		v.m_object = value.getObject();
		return v;
	}

	public IrisObject getObject() {
		return m_object;
	}

	public void setObject(IrisObject object) {
		m_object = object;
	}
	
	boolean equals(IrisValue value) {
		return m_object == value.getObject();
	}
	
}
