package gem.mv.util.redis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface MRedisHashOps {
	public void hmset(String key, Map<String, String> map);

	public void hset(String key, String field, String value);

	public boolean hdel(String key, String field);

	public String hget(String key, String field);

	public int hlen(String key);

	public List<String> hmget(String key, Collection<String> fields);

	public Map<String, String> hgetAll(String key);

}
