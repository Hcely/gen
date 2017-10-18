package gem.mv.util.redis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface MRedisValueOps {

	public void mset(Map<String, String> map, boolean isAsbent);

	public void set(String key, String value);

	public boolean set(String key, String value, boolean nxxx);

	public void set(String key, String value, long expireIn);

	public boolean set(String key, String value, boolean nxxx, long expireIn);

	public String getSet(String key, String value);

	public String get(String key);

	public List<String> mget(Collection<String> keys);

	public String getDel(String key);

}
