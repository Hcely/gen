package zr.aop.unit.count;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class MethodCount extends AtomicIntegerArray {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int SHIFT = 5;
	public static final int LENGTH = 12;
	protected long startTime;
	protected long endTime;

	public MethodCount() {
		super(LENGTH);
	}

	public void reset(long time) {
		this.startTime = time;
		for (int i = 0; i < LENGTH; ++i)
			set(i, 0);
	}

	public void finish(long time) {
		this.endTime = time;
	}

	public void takeCount(long takeTime) {
		incrementAndGet(0);
		int i = 1;
		takeTime >>>= SHIFT;
		while (takeTime > 0) {
			++i;
			takeTime >>= 1;
		}
		if (i < LENGTH)
			incrementAndGet(i);
		else
			incrementAndGet(LENGTH - 1);
	}

	public int getCount() {
		return get(0);
	}

	public int getCount(int idx) {
		return get(idx);
	}

}
