package gem.mv.util.redis;

import v.Destoryable;

public interface MRedis extends MRedisValueOps, MRedisHashOps, Destoryable {

	public Object getConn();

	public boolean delete(String key);

	public boolean setExpireIn(String key, long expireIn);

}
