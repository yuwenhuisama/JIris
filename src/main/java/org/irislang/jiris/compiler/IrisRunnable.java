package org.irislang.jiris.compiler;

import org.irislang.jiris.core.IrisContextEnvironment;
import org.irislang.jiris.core.IrisThreadInfo;
import org.irislang.jiris.core.IrisValue;

public interface IrisRunnable {
	public abstract IrisValue run(IrisContextEnvironment context, IrisThreadInfo threadInfo);
}
