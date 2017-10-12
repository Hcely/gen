package gem.mv.plugin.cluster;

import gem.mv.MVFrameworkContext;
import gem.mv.MVPlugin;
import v.common.util.IntHashMap;
import w.Wession;
import w.buf.RefBuf;

public final class ClusterConnMgrPlugin implements MVPlugin {
	protected final IntHashMap<Wession> sessionMap;
	protected MVFrameworkContext context;

	public ClusterConnMgrPlugin() {
		this.sessionMap = new IntHashMap<>();
	}

	@Override
	public void onInit(MVFrameworkContext context) {
		this.context = context;
		
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onDestory() {

	}

	public void conn(String wsUrl) {

	}

	public Wession getClusterConn(int serverId) {
		return null;
	}

	public String getWsAcceptUrl() {
		return null;
	}

	public boolean send(int serverId, Object obj) {
		return false;
	}

	public boolean send(int serverId, RefBuf buf) {
		return false;
	}

}
