package zr.aop.unit;

import java.lang.reflect.Method;

import zr.aop.AopLogger;
import zr.aop.count.ApiCounter;

public class MethodFilterSet {
	protected final Method method;
	protected final FilterInfo[] filters;
	protected final boolean defLogger;
	protected final AopLogger logger;
	protected final ApiCounter counter;

	public MethodFilterSet(Method method, FilterInfo[] filters, boolean defLogger, AopLogger logger) {
		this.method = method;
		this.filters = filters;
		this.defLogger = defLogger;
		this.logger = logger;
		this.counter = new ApiCounter();
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

}
