package zr.aop.unit.count;

import java.util.concurrent.atomic.AtomicLong;

public class MethodCounter {
	protected final AtomicLong count;
	protected final MethodCount[] swapCounts;
	protected int swapIdx;

	protected volatile MethodCount curCount;

	public MethodCounter() {
		this.count = new AtomicLong(0);
		this.swapCounts = new MethodCount[2];
		this.swapIdx = 0;
		for (int i = 0, len = swapCounts.length; i < len; ++i)
			swapCounts[i] = new MethodCount();
		swap();
	}

	public final long inc() {
		return count.incrementAndGet();
	}

	public final void takeCount(long takeTime) {
		curCount.takeCount(takeTime);
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
