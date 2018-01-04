package zr.aop.count;

import zr.aop.unit.MethodFilterSet;

class MethodCountCollectTask implements Runnable {
	protected final MethodCountMgr countMgr;

	MethodCountCollectTask(MethodCountMgr countMgr) {
		this.countMgr = countMgr;
	}

	@Override
	public void run() {
		for (MethodFilterSet e : countMgr.filterSets)
			countMgr.addCount(e.getMethodName(), e.swap());
	}

}
