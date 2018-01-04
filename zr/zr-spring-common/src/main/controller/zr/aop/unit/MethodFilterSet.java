package zr.aop.unit;

import java.lang.reflect.Method;

import zr.aop.AopLogger;
import zr.aop.unit.count.MethodCount;
import zr.aop.unit.count.MethodCounter;

public class MethodFilterSet {
	protected final String methodName;
	protected final Method method;
	protected final FilterInfo[] filters;
	protected final boolean defLogger;
	protected final AopLogger logger;
	protected final MethodCounter counter;

	public MethodFilterSet(Method method, FilterInfo[] filters, boolean defLogger, AopLogger logger) {
		this.methodName = getMethodName(method);
		this.method = method;
		this.filters = filters;
		this.defLogger = defLogger;
		this.logger = logger;
		this.counter = new MethodCounter();
	}

	public String getMethodName() {
		return methodName;
	}

	public Method getMethod() {
		return method;
	}

	public FilterInfo[] getFilters() {
		return filters;
	}

	public boolean isDefLogger() {
		return defLogger;
	}

	public AopLogger getLogger() {
		return logger;
	}

	public void count(long takeTime) {
		counter.inc(takeTime);
	}

	public MethodCount swap() {
		return counter.swap();
	}

	private static final String getMethodName(Method method) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(method.getDeclaringClass().getName()).append('.').append(method.getName());
		return sb.toString();
	}

}
