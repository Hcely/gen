package zr.aop.unit.count;

public class MethodCounter {
	protected final MethodCount[] swapCounts;
	protected int swapIdx;
	protected volatile MethodCount curCount;

	public MethodCounter() {
		this.swapCounts = new MethodCount[2];
		this.swapIdx = 0;
		for (int i = 0, len = swapCounts.length; i < len; ++i)
			swapCounts[i] = new MethodCount();
		this.curCount = swapCounts[0];
	}

	public final int inc(long takeTime) {
		return curCount.inc(takeTime);
	}

	public final MethodCount swap() {
		int idx = swapIdx & 1;
		MethodCount oldCount = swapCounts[idx];
		idx = (++swapIdx) & 1;
		MethodCount newCount = swapCounts[idx];
		long time = System.currentTimeMillis();
		newCount.reset(time);
		curCount = newCount;
		oldCount.finish(time);
		return oldCount;
	}

}
