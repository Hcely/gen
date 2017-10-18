package gem.mv.plugin.zkcluster;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import gem.mv.bean.ClusterInfo;
import v.common.helper.ParseUtil;

class ZKClusterInfosHandler implements Runnable {
	protected final ZKClusterConfigPlugin clusterConfig;

	public ZKClusterInfosHandler(ZKClusterConfigPlugin clusterConfig) {
		this.clusterConfig = clusterConfig;
	}

	@Override
	public void run() {
		Map<String, String> map = clusterConfig.zker.getChildren(ZKClusterConfigPlugin.PATH_CLUSTER_INFO);
		List<ClusterInfo> infos = new LinkedList<>();
		for (Entry<String, String> e : map.entrySet()) {
			Integer serverId = ParseUtil.parse(e.getKey(), Integer.class);
			if (serverId == null)
				continue;
			infos.add(new ClusterInfo(serverId, e.getValue()));
		}
		clusterConfig.setInfos(infos);
	}

}
