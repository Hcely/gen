package zr.aop.unit.count;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class MethodCount {
	public static final int SHIFT = 6;
	public static final int LENGTH = 10;
	protected long startTime;
	protected long endTime;
	/**
	 * count,64ms,128ms,256ms,512ms,1024ms,2048ms,4096ms,8192ms,16384ms,more
	 */
	protected final AtomicIntegerArray arrays;

	public MethodCount() {
		this.arrays = new AtomicIntegerArray(LENGTH);
		reset(0);
	}

	public void reset(long time) {
		this.startTime = time;
		for (int i = 0; i < LENGTH; ++i)
			arrays.set(i, 0);
	}

	public void finish(long time) {
		this.endTime = time;
	}

	public int inc(long takeTime) {
		int sum = arrays.incrementAndGet(0);
		int i = 1;
		takeTime >>>= SHIFT;
		while (takeTime > 0) {
			++i;
			takeTime >>= 1;
		}
		if (i < LENGTH)
			arrays.incrementAndGet(i);
		else
			arrays.incrementAndGet(LENGTH - 1);
		return sum;

	}

	public int getCount() {
		return arrays.get(0);
	}

	/**
	 * count,64ms,128ms,256ms,512ms,1024ms,2048ms,4096ms,8192ms,16384ms,more
	 */
	public int getCount(int idx) {
		return arrays.get(idx);
	}

}
