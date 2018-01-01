package zr.aop.unit;

import java.lang.reflect.Method;

import zr.aop.AopLogger;

public class MethodFilterSet {
	protected final Method method;
	protected final FilterInfo[] filters;
	protected final boolean defLogger;
	protected final AopLogger logger;

	public MethodFilterSet(Method method, FilterInfo[] filters, boolean defLogger, AopLogger logger) {
		this.method = method;
		this.filters = filters;
		this.defLogger = defLogger;
		this.logger = logger;
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

}
