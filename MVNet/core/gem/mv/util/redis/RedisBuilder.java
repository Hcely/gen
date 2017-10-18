package gem.mv.util.redis;

import java.util.Set;
import java.util.TreeSet;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import v.ObjBuilder;

public class RedisBuilder implements ObjBuilder<MRedis> {
	public static final int DEF_REDIS_PORT = 6379;
	public static final String DEF_HOST = "127.0.0.1";
	protected final JedisPoolConfig config;

	protected int connectionTimeout = 30000;
	protected int soTimeout = 30000;
	protected int maxAttempts = 5;
	protected String password = null;
	protected int port = DEF_REDIS_PORT;
	protected String host = DEF_HOST;
	protected Set<HostAndPort> nodes;

	public RedisBuilder() {
		config = new JedisPoolConfig();
		config.setMaxTotal(128);
	}

	/**
	 * 设置连接池最大允许连接数，默认值：128
	 */
	public RedisBuilder setMaxTotal(int maxTotal) {
		config.setMaxTotal(maxTotal);
		return this;
	}

	/**
	 * 设置连接池最大允许多少个悠闲连接，默认值：8
	 */
	public RedisBuilder setMaxIdle(int maxIdle) {
		config.setMaxIdle(maxIdle);
		return this;
	}

	/**
	 * 设置连接池至少要有多少连接数，默认值：0
	 */
	public RedisBuilder setMinIdle(int minIdle) {
		config.setMinIdle(minIdle);
		return this;
	}

	/**
	 * 设置连接池分配资源结构，true：LIFO(后进先出，栈)，false：FIFO(先进先出，对列)，默认值：true
	 */
	public RedisBuilder setLifo(boolean lifo) {
		config.setLifo(lifo);
		return this;
	}

	/**
	 * 设置连接池在分配连接是否使用公平锁，即先到先得，默认值：false
	 */
	public RedisBuilder setFairness(boolean fairness) {
		config.setFairness(fairness);
		return this;
	}

	/**
	 * 设置获取连接最大等待时间，默认值：-1
	 */
	public RedisBuilder setMaxWaitMillis(long maxWaitMillis) {
		config.setMaxWaitMillis(maxWaitMillis);
		return this;
	}

	/**
	 * 设置连接池连接悠闲时间，默认值：1800000
	 */
	public RedisBuilder setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		return this;
	}

	/**
	 * 设置连接池软连接悠闲时间，默认值：1800000
	 */
	public RedisBuilder setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
		config.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
		return this;
	}

	/**
	 * 设置连接池一次回收多少悠闲连接，0表示不回收，<0表示一次回收(总悠闲连接数/abs(numTestsPerEvictionRun))，默认值：3
	 */
	public RedisBuilder setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		return this;
	}

	/**
	 * 设置在连接创建时，是否测试连接，默认值：false
	 */
	public RedisBuilder setTestOnCreate(boolean testOnCreate) {
		config.setTestOnCreate(testOnCreate);
		return this;
	}

	/**
	 * 设置在连接分配时，是否测试连接，默认值：false
	 */
	public RedisBuilder setTestOnBorrow(boolean testOnBorrow) {
		config.setTestOnBorrow(testOnBorrow);
		return this;
	}

	/**
	 * 设置在连接回收时，是否测试连接，默认值：false
	 */
	public RedisBuilder setTestOnReturn(boolean testOnReturn) {
		config.setTestOnReturn(testOnReturn);
		return this;
	}

	/**
	 * 设置在连接悠闲时，是否测试连接，默认值：false
	 */
	public RedisBuilder setTestWhileIdle(boolean testWhileIdle) {
		config.setTestWhileIdle(testWhileIdle);
		return this;
	}

	/**
	 * 设置连接池回收资源线程执行周期时间，-1表示不启用，默认值：-1
	 */
	public RedisBuilder setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		return this;
	}

	/**
	 * 设置连接池回收资源的策略，默认值： org.apache.commons.pool2.impl.DefaultEvictionPolicy
	 */
	public RedisBuilder setEvictionPolicyClassName(String evictionPolicyClassName) {
		config.setEvictionPolicyClassName(evictionPolicyClassName);
		return this;
	}

	/**
	 * 设置连接池在无连接分配是否等待分配，默认值：true
	 */
	public RedisBuilder setBlockWhenExhausted(boolean blockWhenExhausted) {
		config.setBlockWhenExhausted(blockWhenExhausted);
		return this;
	}

	/**
	 * 设置连接池是否开启jmx监控，默认值：true
	 */
	public RedisBuilder setJmxEnabled(boolean jmxEnabled) {
		config.setJmxEnabled(jmxEnabled);
		return this;
	}

	/**
	 * 设置jmx的objectname的key，默认值：null
	 */
	public RedisBuilder setJmxNameBase(String jmxNameBase) {
		config.setJmxNameBase(jmxNameBase);
		return this;
	}

	/**
	 * 设置jmx的objectname的value，默认值："pool"
	 */
	public RedisBuilder setJmxNamePrefix(String jmxNamePrefix) {
		config.setJmxNamePrefix(jmxNamePrefix);
		return this;
	}

	/**
	 * 设置连接超时，默认值：30000
	 */
	public RedisBuilder setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}

	/**
	 * 设置传输超时，默认值：30000
	 */
	public RedisBuilder setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
		return this;
	}

	/**
	 * 设置集群最大重试次数，默认值：5
	 */
	public RedisBuilder setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
		return this;
	}

	/**
	 * 设置密码，默认值：null
	 */
	public RedisBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * 设置单机redis的端口，默认值：6379
	 */
	public RedisBuilder setPort(int port) {
		this.port = port;
		return this;
	}

	/**
	 * 设置单机redis的host，默认值：127.0.0.1
	 */
	public RedisBuilder setHost(String host) {
		this.host = host;
		return this;
	}

	/**
	 * 添加集群redis的节点
	 */
	public RedisBuilder addNodes(String host) {
		return addNodes(host, 6379);
	}

	/**
	 * 添加集群redis的节点
	 */
	public RedisBuilder addNodes(String host, int port) {
		if (nodes == null)
			nodes = new TreeSet<>();
		nodes.add(new HostAndPort(host, port));
		return this;
	}

	/**
	 * 设置集群redis的节点
	 */
	public RedisBuilder setNodes(Set<String> nodes) {
		if (this.nodes == null)
			this.nodes = new TreeSet<>();
		else
			this.nodes.clear();
		for (String e : nodes)
			this.nodes.add(HostAndPort.parseString(e));
		return this;
	}

	@Override
	public final MRedis build() {
		if (nodes == null || nodes.isEmpty())
			return new SingleRedis(buildJPool());
		else
			return new ClusterRedis(buildJCluster());
	}

	private final JedisPool buildJPool() {
		return new JedisPool(config, host, port, connectionTimeout, soTimeout, password, 0, null, false, null, null,
				null);
	}

	private final MJedisCluster buildJCluster() {
		return new MJedisCluster(nodes, connectionTimeout, soTimeout, maxAttempts, password, config);
	}

	@Override
	public Class<? extends MRedis> getType() {
		return MRedis.class;
	}

}
