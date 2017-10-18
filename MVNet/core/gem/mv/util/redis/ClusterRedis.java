package gem.mv.util.redis;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ClusterRedis implements MRedis {
	protected final MJedisCluster jc;

	public ClusterRedis(MJedisCluster jc) {
		this.jc = jc;
	}

	@Override
	public MJedisCluster getConn() {
		return jc;
	}

	@Override
	public void destory() {
		try {
			jc.close();
		} catch (IOException e) {
		}
	}

	@Override
	public void set(String key, String value) {
		jc.set(key, value);
	}

	@Override
	public boolean set(String key, String value, boolean nxxx, long expireIn) {
		String hr = jc.set(key, value, nxxx ? "NX" : "XX", "PX", expireIn);
		return "OK".equalsIgnoreCase(hr);
	}

	@Override
	public void mset(Map<String, String> map, boolean isAsbent) {
		List<String> list = new LinkedList<>();
		for (Entry<String, String> e : map.entrySet()) {
			list.add(e.getKey());
			list.add(e.getValue());
		}
		if (isAsbent)
			jc.msetnx(list.toArray(new String[list.size()]));
		else
			jc.mset(list.toArray(new String[list.size()]));
	}

	@Override
	public boolean set(String key, String value, boolean nxxx) {
		String hr = jc.setNXXX(key, value, nxxx ? "NX" : "XX");
		return "OK".equalsIgnoreCase(hr);
	}

	@Override
	public void set(String key, String value, long expireIn) {
		jc.psetex(key, expireIn, value);
	}

	@Override
	public String getSet(String key, String value) {
		return jc.getSet(key, value);
	}

	@Override
	public String getDel(String key) {
		return jc.getDel(key);
	}

	@Override
	public String get(String key) {
		return jc.get(key);
	}

	@Override
	public List<String> mget(Collection<String> keys) {
		return jc.mget(keys.toArray(new String[keys.size()]));
	}

	@Override
	public boolean delete(String key) {
		Long hr = jc.del(key);
		return hr == null ? false : hr.longValue() > 0;
	}

	@Override
	public boolean setExpireIn(String key, long expireIn) {
		Long hr = jc.pexpire(key, expireIn);
		return hr == null ? false : hr.longValue() > 0;
	}

	@Override
	public void hset(String key, String field, String value) {
		jc.hset(key, field, value);
	}

	@Override
	public void hmset(String key, Map<String, String> map) {
		jc.hmset(key, map);
	}

	@Override
	public boolean hdel(String key, String field) {
		Long hr = jc.hdel(key, field);
		return hr == null ? false : hr.longValue() > 0;
	}

	@Override
	public String hget(String key, String field) {
		return jc.hget(key, field);
	}

	@Override
	public List<String> hmget(String key, Collection<String> fields) {
		return jc.hmget(key, fields.toArray(new String[fields.size()]));
	}

	@Override
	public int hlen(String key) {
		Long hr = jc.hlen(key);
		return hr == null ? 0 : hr.intValue();
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		Map<String, String> v = jc.hgetAll(key);
		return v;
	}

}
