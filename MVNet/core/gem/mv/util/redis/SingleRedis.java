package gem.mv.util.redis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class SingleRedis implements MRedis {
	protected final JedisPool jp;

	public SingleRedis(JedisPool jp) {
		this.jp = jp;
	}

	@Override
	public JedisPool getConn() {
		return jp;
	}

	@Override
	public void destory() {
		jp.close();
	}

	@Override
	public void set(String key, String value) {
		Jedis j = jp.getResource();
		j.set(key, value);
		j.close();
	}

	@Override
	public boolean set(String key, String value, boolean nxxx, long expireIn) {
		Jedis j = jp.getResource();
		String hr = j.set(key, value, nxxx ? "NX" : "XX", "PX", expireIn);
		j.close();
		return "OK".equalsIgnoreCase(hr);
	}

	@Override
	public boolean set(String key, String value, boolean nxxx) {
		Jedis j = jp.getResource();
		String hr = j.set(key, value, nxxx ? "NX" : "XX");
		j.close();
		return "OK".equalsIgnoreCase(hr);
	}

	@Override
	public void set(String key, String value, long expireIn) {
		Jedis j = jp.getResource();
		j.psetex(key, expireIn, value);
		j.close();
	}

	@Override
	public void mset(Map<String, String> map, boolean isAsbent) {
		List<String> list = new LinkedList<>();
		for (Entry<String, String> e : map.entrySet()) {
			list.add(e.getKey());
			list.add(e.getValue());
		}
		Jedis j = jp.getResource();
		if (isAsbent)
			j.msetnx(list.toArray(new String[list.size()]));
		else
			j.mset(list.toArray(new String[list.size()]));
		j.close();
	}

	@Override
	public String getSet(String key, String value) {
		Jedis j = jp.getResource();
		String hr = j.getSet(key, value);
		j.close();
		return hr;
	}

	@Override
	public String getDel(String key) {
		Jedis j = jp.getResource();
		Transaction t = j.multi();
		Response<String> resp = t.get(key);
		t.del(key);
		t.exec();
		j.close();
		return resp.get();
	}

	@Override
	public String get(String key) {
		Jedis j = jp.getResource();
		String v = j.get(key);
		j.close();
		return v;
	}

	@Override
	public List<String> mget(Collection<String> keys) {
		Jedis j = jp.getResource();
		List<String> v = j.mget(keys.toArray(new String[keys.size()]));
		j.close();
		return v;
	}

	@Override
	public boolean delete(String key) {
		Jedis j = jp.getResource();
		Long hr = j.del(key);
		j.close();
		return hr == null ? false : hr.longValue() > 0;
	}

	@Override
	public boolean setExpireIn(String key, long expireIn) {
		Jedis j = jp.getResource();
		Long hr = j.pexpire(key, expireIn);
		j.close();
		return hr == null ? false : hr.longValue() > 0;
	}

	@Override
	public void hset(String key, String field, String value) {
		Jedis j = jp.getResource();
		j.hset(key, field, value);
		j.close();
	}

	@Override
	public void hmset(String key, Map<String, String> map) {
		Jedis j = jp.getResource();
		j.hmset(key, map);
		j.close();
	}

	@Override
	public boolean hdel(String key, String field) {
		Jedis j = jp.getResource();
		Long hr = j.hdel(key, field);
		j.close();
		return hr == null ? false : hr.longValue() > 0;
	}

	@Override
	public String hget(String key, String field) {
		Jedis j = jp.getResource();
		String value = j.hget(key, field);
		j.close();
		return value;
	}

	@Override
	public List<String> hmget(String key, Collection<String> fields) {
		Jedis j = jp.getResource();
		List<String> v = j.hmget(key, fields.toArray(new String[fields.size()]));
		j.close();
		return v;
	}

	@Override
	public int hlen(String key) {
		Jedis j = jp.getResource();
		Long hr = j.hlen(key);
		j.close();
		return hr == null ? 0 : hr.intValue();
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		Jedis j = jp.getResource();
		Map<String, String> v = j.hgetAll(key);
		j.close();
		return v;
	}

}
