package gem.mv.bean;

public class ClusterInfo {
	protected int serverId;
	protected String acceptUrl;

	public ClusterInfo() {
	}

	public ClusterInfo(int serverId, String acceptUrl) {
		this.serverId = serverId;
		this.acceptUrl = acceptUrl;
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

	@Override
	public String toString() {
		return "ClusterInfo [serverId=" + serverId + ", acceptUrl=" + acceptUrl + "]";
	}

}
