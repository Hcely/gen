package zr.aop.unit.count;

import java.util.LinkedList;
import java.util.List;

public class MethodCountSumInfo {
	protected String methodName;
	protected List<MethodCountInfo> counts;

	public MethodCountSumInfo() {
	}

	public MethodCountSumInfo(String methodName) {
		this.methodName = methodName;
		this.counts = new LinkedList<>();
	}

	public void add(MethodCount count) {
		if (counts == null)
			counts = new LinkedList<>();
		counts.add(new MethodCountInfo(count));
	}

	public void reset() {
		if (counts != null)
			counts.clear();
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<MethodCountInfo> getCounts() {
		return counts;
	}

	public void setCounts(List<MethodCountInfo> counts) {
		this.counts = counts;
	}

}
