package gem.mv.plugin.route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

import gem.mv.MVFrameworkContext;
import gem.mv.bean.RouteInfo;
import gem.mv.plugin.RouteTablePlugin;
import gem.mv.util.MVUtil;
import gem.mv.util.redis.MRedis;
import gem.mv.util.redis.RedisBuilder;
import v.common.helper.StrUtil;
import v.resource.annotation.VBuilderResource;

public class RedisRouteTablePlugin implements RouteTablePlugin {
	private static final long LOCK_TIME = 1000;
	@VBuilderResource(value = RedisBuilder.class, propertisPackage = "mv.redis")
	protected MRedis redis;
	protected int serverId;

	@Override
	public void onCreate(MVFrameworkContext context) {
		this.serverId = context.getServerId();
	}

	@Override
	public void onInit() {
		MVUtil.log.info(redis);
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onDestory() {
	}

	@Override
	public RouteInfo set(final RouteInfo route) {
		final String key = route.getKey();
		final String lockKey = getLockKey(key);
		while (true) {
			String lockValue = String.valueOf(System.currentTimeMillis());
			if (!redis.set(lockKey, lockValue, true, LOCK_TIME))
				LockSupport.parkNanos(5000000);
			else
				try {
					Map<String, RouteInfo> map = parseRoutes(redis.get(key));
					if (map == null) {
						map = new LinkedHashMap<>();
						map.put(route.getTag(), route);
						redis.set(key, MVUtil.objToJson(map));
						return null;
					} else {
						RouteInfo r = map.put(route.getTag(), route);
						if (r == null || r.getCreateTime() < route.getCreateTime())
							redis.set(key, MVUtil.objToJson(map));
						return r;
					}
				} finally {
					if (!redis.delete(lockKey) && lockValue.equals(redis.get(lockKey)))
						redis.delete(lockKey);
				}
		}
	}

	@Override
	public void remove(final String key, final String tag, final long time) {
		final String lockKey = getLockKey(key);
		while (true) {
			String lockValue = String.valueOf(System.currentTimeMillis());
			if (!redis.set(lockKey, lockValue, true, LOCK_TIME))
				LockSupport.parkNanos(5000000);
			else
				try {
					Map<String, RouteInfo> map = parseRoutes(redis.get(key));
					if (map == null)
						return;
					RouteInfo r = map.remove(tag);
					if (r != null && r.getServerId() == serverId && r.getCreateTime() <= time)
						if (map.isEmpty())
							redis.delete(key);
						else
							redis.set(key, MVUtil.objToJson(map));
				} finally {
					if (!redis.delete(lockKey) && lockValue.equals(redis.get(lockKey)))
						redis.delete(lockKey);
				}
		}
	}

	@Override
	public RouteInfo find(String key, String tag) {
		String str = redis.get(key);
		Map<String, RouteInfo> routes = parseRoutes(str);
		return routes.get(tag);
	}

	@Override
	public List<RouteInfo> query(String key) {
		String str = redis.get(key);
		Map<String, RouteInfo> routes = parseRoutes(str);
		return new ArrayList<>(routes.values());
	}

	@Override
	public List<RouteInfo> query(Collection<String> keys, String tag) {
		List<String> values = redis.mget(keys);
		List<RouteInfo> routes = new LinkedList<>();
		for (String e : values) {
			if (e == null)
				continue;
			Map<String, RouteInfo> map = MVUtil.jsonToMap(e, RouteInfo.class);
			RouteInfo r = map.get(tag);
			if (r != null)
				routes.add(r);
		}
		return routes;
	}

	@Override
	public List<RouteInfo> query(Collection<String> keys) {
		List<String> values = redis.mget(keys);
		List<RouteInfo> routes = new LinkedList<>();
		for (String e : values) {
			if (e == null)
				continue;
			Map<String, RouteInfo> map = MVUtil.jsonToMap(e, RouteInfo.class);
			routes.addAll(map.values());
		}
		return routes;
	}

	private static final Map<String, RouteInfo> parseRoutes(String value) {
		if (value == null)
			return null;
		return MVUtil.jsonToMap(value, RouteInfo.class);
	}

	private static final String getLockKey(String key) {
		StringBuilder sb = new StringBuilder(key.length() + 5);
		sb.append("lock-").append(key);
		return StrUtil.sbToString(sb);
	}

}
