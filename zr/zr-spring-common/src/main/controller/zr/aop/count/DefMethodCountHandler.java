package zr.aop.count;

import java.util.List;

import zr.AppContext;
import zr.aop.unit.count.MethodCountInfo;

final class DefMethodCountHandler implements MethodCountHandler {
	public static final DefMethodCountHandler INSTANCE = new DefMethodCountHandler();

	private DefMethodCountHandler() {
	}

	@Override
	public void handle(List<MethodCountInfo> countInfos) {
		for (MethodCountInfo e : countInfos)
			AppContext.logger.info(e);
	}

}
