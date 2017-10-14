package gem.mv;

import gem.mv.cluster.ClusterConnMgrPlugin;
import v.plugin.VPFrameworkContext;
import w.WessionSetConfig;

public interface MVFrameworkContext extends VPFrameworkContext {
	public int getServerId();

	public WessionSetConfig createSetConfig();

	public WessionSetConfig createSetConfig(int... ports);

	public ClusterConnMgrPlugin getConnMgr();
}
