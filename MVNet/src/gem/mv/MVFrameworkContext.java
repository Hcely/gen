package gem.mv;

import gem.mv.plugin.cluster.ClusterConnMgrPlugin;
import v.plugin.VPFrameworkContext;
import w.WessionSetConfig;

public interface MVFrameworkContext extends VPFrameworkContext {
	public WessionSetConfig createSetConfig();

	public WessionSetConfig createSetConfig(int... ports);

	public ClusterConnMgrPlugin getConnMgr();
}
