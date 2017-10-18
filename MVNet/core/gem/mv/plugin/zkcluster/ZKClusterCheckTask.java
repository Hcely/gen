package gem.mv.plugin.zkcluster;

import java.util.Collection;
import java.util.List;

import gem.mv.bean.ClusterInfo;
import gem.mv.cluster.ClusterConnMgrPlugin;
import gem.mv.util.MVUtil;
import w.Wession;
import w.unit.WeavePromise;

class ZKClusterCheckTask implements Runnable {
	protected final int serverId;
	protected final ZKClusterConfigPlugin clusterConfig;
	protected final ClusterConnMgrPlugin connMgr;

	ZKClusterCheckTask(int serverId, ZKClusterConfigPlugin clusterConfig, ClusterConnMgrPlugin connMgr) {
		this.serverId = serverId;
		this.clusterConfig = clusterConfig;
		this.connMgr = connMgr;
	}

	@Override
	public void run() {
		List<ClusterInfo> infos = clusterConfig.infos;
		if (infos != null)
			handle(infos);
		handle(clusterConfig.defInfoMap.values());
	}

	private void handle(Collection<ClusterInfo> infos) {
		for (ClusterInfo info : infos) {
			int tsid = info.getServerId();
			if (tsid < serverId) {
				Wession session = connMgr.getClusterSession(tsid);
				if (session != null)
					continue;
				MVUtil.log.info("conn service:" + info.getAcceptUrl());
				try {
					WeavePromise<Wession> promise = connMgr.conn(tsid, info.getAcceptUrl());
					promise.sync();
				} catch (InterruptedException e) {
				}
			}
		}
	}

}
