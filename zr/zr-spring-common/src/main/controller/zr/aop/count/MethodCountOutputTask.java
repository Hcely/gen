package zr.aop.count;

class MethodCountOutputTask implements Runnable {
	protected final MethodCountMgr countMgr;
	protected final MethodCountHandler handler;

	MethodCountOutputTask(MethodCountMgr countMgr) {
		this.countMgr = countMgr;
		this.handler = countMgr.handler;
	}

	@Override
	public void run() {
		countMgr.outputCountInfos();
	}

}
