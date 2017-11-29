package gem.mv;

import gem.mv.cluster.ClusterConnMgrPlugin;
import v.plugin.VPFramework;

public interface MVFramework extends VPFramework {
	public static final int MAX_SERVER_ID = 1 << 27;

	public ClusterConnMgrPlugin getConnMgr();
}
