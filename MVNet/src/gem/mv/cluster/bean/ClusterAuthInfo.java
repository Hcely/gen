package gem.mv.cluster.bean;

import w.unit.WeaveAuthInfo;

public final class ClusterAuthInfo implements WeaveAuthInfo {
	protected final int serverId;
	protected final long createTime;
	protected final Integer id;

	public ClusterAuthInfo(int serverId) {
		this.serverId = serverId;
		this.createTime = System.currentTimeMillis();
		this.id = serverId;
	}

	@Override
	public Object id() {
		return id;
	}

	@Override
	public int hash() {
		return serverId;
	}

	public long createTime() {
		return createTime;
	}

	public int serverId() {
		return serverId;
	}

}
