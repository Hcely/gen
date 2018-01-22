package zr.aop.count;

class MethodCountCollectTask implements Runnable {
	protected final MethodCountMgr countMgr;

	MethodCountCollectTask(MethodCountMgr countMgr) {
		this.countMgr = countMgr;
	}

	@Override
	public void run() {
		countMgr.collectCountInfos();
	}

}
