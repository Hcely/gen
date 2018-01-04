package zr.aop.count;

import zr.aop.unit.count.MethodCountSumInfo;

class MethodCountLogTask implements Runnable {
	protected final MethodCountMgr countMgr;
	protected final MethodCountHandler handler;

	MethodCountLogTask(MethodCountMgr countMgr) {
		this.countMgr = countMgr;
		this.handler = countMgr.handler;
	}

	@Override
	public void run() {
		for (MethodCountSumInfo e : countMgr.countSumMap.values())
			handler.onLog(e);
	}

}
