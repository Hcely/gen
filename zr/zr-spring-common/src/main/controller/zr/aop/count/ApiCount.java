package zr.aop.count;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class ApiCount {
	public static final int LENGTH = 8;
	protected long startTime;
	protected long endTime;
	protected final AtomicInteger count;
	/**
	 * 100,200,500,1000,2000,4000,8000,more
	 */
	protected final AtomicIntegerArray takeCounts;

	public ApiCount() {
		this.count = new AtomicInteger(0);
		this.takeCounts = new AtomicIntegerArray(LENGTH);
		reset(0);
	}

	public void reset(long time) {
		this.startTime = time;
		count.set(0);
		for (int i = 0; i < LENGTH; ++i)
			takeCounts.set(i, 0);
	}

	public void finish(long time) {
		this.endTime = time;
	}

	public void inc(long takeTime) {
		count.incrementAndGet();
		if (takeTime < 101)
			takeCounts.incrementAndGet(0);
		else if (takeTime < 201)
			takeCounts.incrementAndGet(1);
		else if (takeTime < 501)
			takeCounts.incrementAndGet(2);
		else if (takeTime < 1001)
			takeCounts.incrementAndGet(3);
		else if (takeTime < 2001)
			takeCounts.incrementAndGet(4);
		else if (takeTime < 4001)
			takeCounts.incrementAndGet(5);
		else if (takeTime < 8001)
			takeCounts.incrementAndGet(6);
		else
			takeCounts.incrementAndGet(7);
	}

	public int getCount() {
		return count.get();
	}

	/**
	 * 100ms,200ms,500ms,1000ms,2000ms,4000ms,8000ms,more
	 */
	public int getTakeCount(int idx) {
		return takeCounts.get(idx);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(128);
		sb.append("startTime:").append(startTime);
		sb.append(",endTime:").append(endTime);
		sb.append(",count:").append(count.get());
		sb.append(",100ms:").append(takeCounts.get(0));
		sb.append(",200ms:").append(takeCounts.get(1));
		sb.append(",500ms:").append(takeCounts.get(2));
		sb.append(",1000ms:").append(takeCounts.get(3));
		sb.append(",2000ms:").append(takeCounts.get(4));
		sb.append(",4000ms:").append(takeCounts.get(5));
		sb.append(",8000ms:").append(takeCounts.get(6));
		sb.append(",more:").append(takeCounts.get(7));
		return sb.toString();
	}

}
