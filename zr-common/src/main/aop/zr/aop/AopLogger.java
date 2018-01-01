package zr.aop;

import zr.aop.unit.HttpRequest;

public interface AopLogger {
	public void log(HttpRequest request);
}
