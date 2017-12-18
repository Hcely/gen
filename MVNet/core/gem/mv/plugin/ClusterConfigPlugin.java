package gem.mv.plugin;

import java.util.List;

import gem.mv.MVPlugin;
import gem.mv.bean.ClusterInfo;

public interface ClusterConfigPlugin extends MVPlugin {
	public void addDefInfo(ClusterInfo info);

	public List<ClusterInfo> getClusters();

	public void setConfig(String key, String config);

	public void setTempConfig(String key, String config);

	public String getConfig(String key);
}
