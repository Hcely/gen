package gem.mv.plugin.zkcluster;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;

final class ZKClusterSubcriber implements IZkChildListener {
	protected final ZKClusterConfigPlugin clusterConfig;

	public ZKClusterSubcriber(ZKClusterConfigPlugin clusterConfig) {
		this.clusterConfig = clusterConfig;
	}

	@Override
	public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
		clusterConfig.loop.execute(new ZKClusterInfosHandler(clusterConfig));
	}

}
