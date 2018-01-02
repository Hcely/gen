package zr.aop.count;

public class ApiCounter {
	protected final ApiCount[] swapCounts;
	protected int swapIdx;
	protected volatile ApiCount curCount;

	public ApiCounter() {
		this.swapCounts = new ApiCount[2];
		this.swapIdx = 0;
		for (int i = 0, len = swapCounts.length; i < len; ++i)
			swapCounts[i] = new ApiCount();
		this.curCount = swapCounts[0];
	}

	public final void inc(long takeTime) {
		curCount.inc(takeTime);
	}

	public final ApiCount swap() {
		int idx = swapIdx & 1;
		ApiCount oldCount = swapCounts[idx];
		idx = (++swapIdx) & 1;
		ApiCount newCount = swapCounts[idx];
		long time = System.currentTimeMillis();
		newCount.reset(time);
		curCount = newCount;
		oldCount.finish(time);
		return oldCount;
	}

}
