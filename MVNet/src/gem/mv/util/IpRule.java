package gem.mv.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import v.common.helper.StrUtil;

public final class IpRule {
	protected final Set<String> whiteList;
	protected final Set<String> blackList;
	protected final Pattern[] ipRules;

	public IpRule(String... ipRules) {
		this.whiteList = new HashSet<>();
		this.blackList = new HashSet<>();
		if (ipRules == null || ipRules.length == 0)
			this.ipRules = null;
		else {
			final int len = ipRules.length;
			this.ipRules = new Pattern[len];
			for (int i = 0; i < len; ++i)
				this.ipRules[i] = buildRule(ipRules[i]);
		}
	}

	public void addWhileIp(String ip) {
		whiteList.add(ip);
	}

	public void addBlackIp(String ip) {
		blackList.add(ip);
	}

	public boolean allow(String ip) {
		if (ipRules != null) {
			boolean b = true;
			for (Pattern r : ipRules)
				if (r.matcher(ip).matches()) {
					b = false;
					break;
				}
			if (b)
				return false;
		}
		if (whiteList.size() > 0 && whiteList.contains(ip))
			return true;
		if (blackList.size() > 0 && blackList.contains(ip))
			return false;
		return true;
	}

	private static final Pattern buildRule(String rule) {
		rule = StrUtil.replace(rule, '.', "\\.");
		rule = StrUtil.replace(rule, '*', "\\d{1,3}");
		return Pattern.compile(rule);
	}

}
