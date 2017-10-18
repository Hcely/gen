package gem.mv.plugin.zkcluster;

final class ZKClusterServerTask implements Runnable {
	protected final ZKClusterConfigPlugin clusterConfig;

	ZKClusterServerTask(ZKClusterConfigPlugin clusterConfig) {
		this.clusterConfig = clusterConfig;
	}

	@Override
	public void run() {
		clusterConfig.updateWsUrl();
	}

}
