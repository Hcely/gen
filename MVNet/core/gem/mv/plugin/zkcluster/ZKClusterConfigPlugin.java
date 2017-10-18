package gem.mv.plugin.zkcluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gem.mv.MVFramework;
import gem.mv.MVFrameworkContext;
import gem.mv.bean.ClusterInfo;
import gem.mv.cluster.ClusterConnMgrPlugin;
import gem.mv.plugin.ClusterConfigPlugin;
import gem.mv.util.MVUtil;
import gem.mv.util.ZKER;
import v.common.unit.thread.VThreadLoop;

public class ZKClusterConfigPlugin implements ClusterConfigPlugin {
	public static final String KEY_CLUSTER_ZK_SERVERS = "mv.cluster.zk.servers";
	public static final String KEY_CLUSTER_ZK_SESSION_TIMEOUT = "mv.cluster.zk.sessionTimeout";
	public static final String KEY_CLUSTER_INFO = "mv.cluster.info";
	public static final String DEF_ZK_SERVERS = "127.0.0.1:2181";
	public static final int DEF_SESSION_TIMEOUT = 4000;

	public static final String PATH_CLUSTER_INFO = "/mv/cluster/info";

	protected final Map<Integer, ClusterInfo> defInfoMap;
	protected final ZKER zker;
	protected final VThreadLoop loop;
	protected MVFrameworkContext context;
	protected ClusterConnMgrPlugin connMgr;
	protected List<ClusterInfo> infos;

	public ZKClusterConfigPlugin() {
		this.defInfoMap = new HashMap<>();
		this.zker = new ZKER();
		this.loop = new VThreadLoop("ZKClusterConfigLoop", true);
	}

	@Override
	public void onCreate(MVFrameworkContext context) {
		this.context = context;
		this.connMgr = context.getConnMgr();
		List<ClusterInfo> infos = context.propertiesParseList(KEY_CLUSTER_INFO, ClusterInfo.class);
		for (ClusterInfo e : infos)
			addDefInfo(e);
		zker.setZkServers(context.getProperty(KEY_CLUSTER_ZK_SERVERS, DEF_ZK_SERVERS));
		zker.setSessionTime(context.getProperty(KEY_CLUSTER_ZK_SESSION_TIMEOUT, Integer.class, DEF_SESSION_TIMEOUT));
	}

	@Override
	public void onInit() {
		zker.init();
		loop.start();
	}

	@Override
	public void onStart() {
		int serverId = context.getServerId();
		if (serverId < MVFramework.MAX_SERVER_ID) {
			loop.execute(new ZKClusterServerTask(this));
			zker.subscribeState(new ZKClusterStateListener(this));
		}
		zker.subscribeChild(PATH_CLUSTER_INFO, new ZKClusterSubcriber(this));
		loop.schedule(new ZKClusterCheckTask(context.getServerId(), this, connMgr), 200, 2000);
	}

	@Override
	public void onDestory() {
		zker.destory();
		loop.destoryNow();
	}

	@Override
	public void addDefInfo(ClusterInfo info) {
		int serverId = info.getServerId();
		if (serverId > -1 && serverId <= MVFramework.MAX_SERVER_ID)
			defInfoMap.put(serverId, info);
	}

	@Override
	public List<ClusterInfo> getClusters() {
		Map<Integer, ClusterInfo> map = new HashMap<>();
		map.putAll(defInfoMap);
		List<ClusterInfo> infos = this.infos;
		if (infos != null)
			for (ClusterInfo e : infos)
				map.put(e.getServerId(), e);
		return new ArrayList<>(map.values());
	}

	@Override
	public void setConfig(String key, String config) {
		zker.set(key, config);
	}

	@Override
	public String getConfig(String key) {
		return zker.get(key);
	}

	final void setInfos(List<ClusterInfo> infos) {
		this.infos = infos;
		MVUtil.log.info("cluster:" + infos);
	}

	final void updateWsUrl() {
		int serverId = context.getServerId();
		String wsUrl = connMgr.getAcceptWsUrl();
		zker.setTemp(PATH_CLUSTER_INFO, String.valueOf(serverId), wsUrl);
		MVUtil.log.info("upload cluster config:" + wsUrl);
	}

}
