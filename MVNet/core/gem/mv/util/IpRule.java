package gem.mv.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import v.common.helper.StrUtil;

public final class IpRule {
	protected final Pattern[] ipRules;
	protected final Set<String> whiteList;
	protected final Set<String> blackList;

	public IpRule(Collection<String> ipRules, List<String> whiteList, List<String> blackList) {
		if (ipRules == null || ipRules.size() == 0)
			this.ipRules = null;
		else {
			List<Pattern> list = new ArrayList<>();
			for (String e : ipRules)
				if (containsStar(e))
					list.add(buildRule(e));
				else {
					if (whiteList == null)
						whiteList = new LinkedList<>();
					whiteList.add(e);
				}
			if (list.isEmpty())
				this.ipRules = null;
			else
				this.ipRules = list.toArray(new Pattern[list.size()]);
		}
		this.whiteList = whiteList == null ? null : new HashSet<>(whiteList);
		this.blackList = blackList == null ? null : new HashSet<>(blackList);
	}

	public boolean allow(String ip) {
		if (whiteList != null && whiteList.contains(ip))
			return true;
		if (blackList != null && blackList.contains(ip))
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
