package zr.util;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import v.common.helper.StrUtil;

public class RedisIncNumUtil {
	private static RedisIncNumUtil instance;
	@Autowired
	protected StringRedisTemplate redisTemplate;

	public static final long inc(String key) {
		key = getKey(key);
		return instance.redisTemplate.opsForValue().increment(key, 1L);
	}

	@PostConstruct
	void init() {
		instance = this;
	}

	private static final String getKey(String key) {
		StringBuilder sb = new StringBuilder(4 + key.length());
		sb.append("inc-").append(key);
		return StrUtil.sbToString(sb);
	}
}
