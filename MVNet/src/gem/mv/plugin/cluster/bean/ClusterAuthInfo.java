package gem.mv.plugin.cluster.bean;

import w.unit.WeaveAuthInfo;

public class ClusterAuthInfo implements WeaveAuthInfo {
	protected final int serverId;
	protected final Integer id;

	public ClusterAuthInfo(int serverId) {
		this.serverId = serverId;
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

	public int serverId() {
		return serverId;
	}

}
