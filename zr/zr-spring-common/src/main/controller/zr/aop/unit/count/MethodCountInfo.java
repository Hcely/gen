package zr.aop.unit.count;

public class MethodCountInfo {
	protected long startTime;
	protected long endTime;
	protected int[] arrays;

	public MethodCountInfo() {
	}

	public MethodCountInfo(MethodCount count) {
		this.startTime = count.startTime;
		this.endTime = count.endTime;
		this.arrays = new int[MethodCount.LENGTH];
		for (int i = 0; i < MethodCount.LENGTH; ++i)
			arrays[i] = count.getCount(i);
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int[] getArrays() {
		return arrays;
	}

	public void setArrays(int[] arrays) {
		this.arrays = arrays;
	}

}
