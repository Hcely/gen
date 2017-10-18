package gem.mv.plugin.zkcluster;

import org.I0Itec.zkclient.IZkStateListener;
import org.apache.zookeeper.Watcher.Event.KeeperState;

final class ZKClusterStateListener implements IZkStateListener {
	protected final ZKClusterConfigPlugin clusterConfig;

	ZKClusterStateListener(ZKClusterConfigPlugin clusterConfig) {
		this.clusterConfig = clusterConfig;
	}

	@Override
	public void handleStateChanged(KeeperState state) throws Exception {
		if (state == KeeperState.SyncConnected)
			clusterConfig.loop.execute(new ZKClusterServerTask(clusterConfig));
	}

	@Override
	public void handleNewSession() throws Exception {

	}

	@Override
	public void handleSessionEstablishmentError(Throwable error) throws Exception {

	}

}
