package zr.aop.count;

import zr.aop.unit.count.MethodCountSumInfo;

public interface MethodCountHandler {
	public void onLog(MethodCountSumInfo countSum);
}
