package gem.mv.plugin;

import java.util.Collection;
import java.util.List;

import gem.mv.MVPlugin;
import gem.mv.bean.RouteInfo;

public interface RouteTablePlugin extends MVPlugin {
	public RouteInfo set(RouteInfo route);

	public void remove(String key, String tag, long time);

	public RouteInfo find(String key, String tag);

	public List<RouteInfo> query(String key);

	public List<RouteInfo> query(Collection<String> keys, String tag);

	public List<RouteInfo> query(Collection<String> keys);
}