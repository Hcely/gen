package gem.mv.bean;

public class ClusterInfo {
	protected int serverId;
	protected String acceptUrl;
	protected long usedMemory;
	protected long totalMemory;
	protected long maxMemory;

	public ClusterInfo() {
	}

	public ClusterInfo(int serverId, String acceptUrl, long usedMemory, long totalMemory, long maxMemory) {
		this.serverId = serverId;
		this.acceptUrl = acceptUrl;
		this.usedMemory = usedMemory;
		this.totalMemory = totalMemory;
		this.maxMemory = maxMemory;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getAcceptUrl() {
		return acceptUrl;
	}

	public void setAcceptUrl(String acceptUrl) {
		this.acceptUrl = acceptUrl;
	}

	public long getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(long usedMemory) {
		this.usedMemory = usedMemory;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public long getMaxMemory() {
		return maxMemory;
	}

	public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}

}
