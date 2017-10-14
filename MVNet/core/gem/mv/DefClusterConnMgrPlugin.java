package gem.mv;

import gem.mv.cluster.ClusterConnMgrPlugin;

final class DefClusterConnMgrPlugin extends ClusterConnMgrPlugin {
	protected DefClusterConnMgrPlugin(int serverId) {
		super(serverId);
	}

	@Override
	public void onStart() {
		this.weave = ((DefMVFramework) context).weave;
	}
}
