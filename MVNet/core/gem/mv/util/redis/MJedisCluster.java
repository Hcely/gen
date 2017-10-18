package gem.mv.util.redis;

import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisClusterCommand;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class MJedisCluster extends JedisCluster {
	public MJedisCluster(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout, int maxAttempts,
			String password, final GenericObjectPoolConfig poolConfig) {
		super(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, password, poolConfig);
	}

	public MJedisCluster(HostAndPort node, GenericObjectPoolConfig poolConfig) {
		super(node, poolConfig);
	}

	public MJedisCluster(HostAndPort node, int timeout, GenericObjectPoolConfig poolConfig) {
		super(node, timeout, poolConfig);
	}

	public MJedisCluster(HostAndPort node, int timeout, int maxAttempts, GenericObjectPoolConfig poolConfig) {
		super(node, timeout, maxAttempts, poolConfig);
	}

	public MJedisCluster(HostAndPort node, int connectionTimeout, int soTimeout, int maxAttempts,
			GenericObjectPoolConfig poolConfig) {
		super(node, connectionTimeout, soTimeout, maxAttempts, poolConfig);
	}

	public MJedisCluster(HostAndPort node, int connectionTimeout, int soTimeout, int maxAttempts, String password,
			GenericObjectPoolConfig poolConfig) {
		super(node, connectionTimeout, soTimeout, maxAttempts, password, poolConfig);
	}

	public MJedisCluster(HostAndPort node, int timeout, int maxAttempts) {
		super(node, timeout, maxAttempts);
	}

	public MJedisCluster(HostAndPort node, int timeout) {
		super(node, timeout);
	}

	public MJedisCluster(HostAndPort node) {
		super(node);
	}

	public MJedisCluster(Set<HostAndPort> nodes, GenericObjectPoolConfig poolConfig) {
		super(nodes, poolConfig);
	}

	public MJedisCluster(Set<HostAndPort> nodes, int timeout, GenericObjectPoolConfig poolConfig) {
		super(nodes, timeout, poolConfig);
		// TODO Auto-generated constructor stub
	}

	public MJedisCluster(Set<HostAndPort> jedisClusterNode, int timeout, int maxAttempts,
			GenericObjectPoolConfig poolConfig) {
		super(jedisClusterNode, timeout, maxAttempts, poolConfig);
	}

	public MJedisCluster(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout, int maxAttempts,
			GenericObjectPoolConfig poolConfig) {
		super(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, poolConfig);
	}

	public MJedisCluster(Set<HostAndPort> nodes, int timeout, int maxAttempts) {
		super(nodes, timeout, maxAttempts);
	}

	public MJedisCluster(Set<HostAndPort> nodes, int timeout) {
		super(nodes, timeout);
	}

	public MJedisCluster(Set<HostAndPort> nodes) {
		super(nodes);
	}

	public String setNXXX(final String key, final String value, final String nxxx) {
		return new JedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String execute(Jedis connection) {
				return connection.set(key, value, nxxx);
			}
		}.run(key);
	}

	public String getDel(final String key) {
		return new JedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String execute(Jedis connection) {
				Transaction t = connection.multi();
				Response<String> resp = t.get(key);
				t.del(key);
				t.exec();
				return resp.get();
			}
		}.run(key);
	}
}
