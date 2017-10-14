package gem.mv;

import gem.mv.cluster.ClusterConnMgrPlugin;
import w.WeaveFramework;

final class DefClusterConnMgrPlugin extends ClusterConnMgrPlugin {
	protected DefClusterConnMgrPlugin(int serverId) {
		super(serverId);
	}

	final void setWeave(WeaveFramework weave) {
		this.weave = weave;
	}
}
