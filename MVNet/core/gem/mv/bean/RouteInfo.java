package gem.mv.bean;

public class RouteInfo {
	protected String key;
	protected String tag;
	protected long createTime;
	protected int serverId;
	protected String sessionId;

	public RouteInfo() {
	}

	public RouteInfo(String key, String tag, long createTime, int serverId, String sessionId) {
		this.key = key;
		this.tag = tag;
		this.createTime = createTime;
		this.serverId = serverId;
		this.sessionId = sessionId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
