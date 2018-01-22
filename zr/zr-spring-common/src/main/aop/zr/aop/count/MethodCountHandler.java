package zr.aop.count;

import java.util.List;

import zr.aop.unit.count.MethodCountInfo;

public interface MethodCountHandler {
	public void handle(List<MethodCountInfo> countInfos);
}
