package gem.mv.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import v.common.helper.StrUtil;

public final class IpRule {
	protected final Set<String> whiteList;
	protected final Set<String> blackList;
	protected final Pattern[] ipRules;

	public IpRule(Collection<String> ipRules) {
		this.whiteList = new HashSet<>();
		this.blackList = new HashSet<>();

		if (ipRules == null || ipRules.size() == 0)
			this.ipRules = null;
		else {
			List<Pattern> list = new ArrayList<>();
			for (String e : ipRules)
				if (containsStar(e))
					list.add(buildRule(e));
				else
					whiteList.add(e);
			if (list.isEmpty())
				this.ipRules = null;
			else
				this.ipRules = list.toArray(new Pattern[list.size()]);
		}
	}

	public void addWhileIp(String ip) {
		whiteList.add(ip);
	}

	public void addBlackIp(String ip) {
		blackList.add(ip);
	}

	public void addWhileIps(Collection<String> ips) {
		if (ips != null)
			whiteList.addAll(ips);
	}

	public void addBlackIps(Collection<String> ips) {
		if (ips != null)
			blackList.addAll(ips);
	}

	public boolean allow(String ip) {
		if (whiteList.size() > 0 && whiteList.contains(ip))
			return true;
		if (blackList.size() > 0 && blackList.contains(ip))
			return false;
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
		return true;
	}

	private static final boolean containsStar(String rule) {
		return rule.indexOf('*') > 0;
	}

	private static final Pattern buildRule(String rule) {
		rule = StrUtil.replace(rule, '.', "\\.");
		rule = StrUtil.replace(rule, '*', "\\d{1,3}");
		return Pattern.compile(rule);
	}

}
