package zr.aop.unit.count;

public class MethodCountInfo {
	protected String methodName;
	protected long startTime;
	protected long endTime;
	protected int[] arrays;

	public MethodCountInfo() {
	}

	public MethodCountInfo(String methodName, MethodCount count) {
		this.methodName = methodName;
		this.startTime = count.startTime;
		this.endTime = count.endTime;
		this.arrays = new int[MethodCount.LENGTH];
		for (int i = 0; i < MethodCount.LENGTH; ++i)
			arrays[i] = count.getCount(i);
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(256);
		sb.append("method:").append(methodName).append(",start:").append(startTime).append(",end:").append(endTime)
				.append(",count:").append(arrays[0]);
		int take = 1 << MethodCount.SHIFT;
		int i = 1;
		for (; i < MethodCount.LENGTH - 1; ++i, take <<= 1)
			sb.append(',').append(take).append("ms:").append(arrays[i]);
		sb.append(",more:").append(arrays[i]);
		return sb.toString();
	}

}
