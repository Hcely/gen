package zr.aop;

import zr.aop.unit.FilterInfo;
import zr.aop.unit.HttpRequest;
import zr.aop.unit.MethodFilterSet;
import zr.unit.HResult;

public interface AopFilter {
	public default void init(MethodFilterSet set, FilterInfo filterInfo) {
	};

	public HResult before(HttpRequest request, FilterInfo filterInfo);

	public default void after(HttpRequest request, FilterInfo filterInfo) {
	}
}
