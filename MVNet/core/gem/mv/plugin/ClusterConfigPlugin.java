package gem.mv.plugin;

import java.util.List;

import gem.mv.MVPlugin;
import gem.mv.bean.ClusterInfo;

public interface ClusterConfigPlugin extends MVPlugin {
	public List<ClusterInfo> getClusters();

	public void updateConfig(String key, Object config);
}
