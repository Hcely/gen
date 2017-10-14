package gem.mv.bean;

import java.util.List;

import gem.mv.util.IpRule;
import v.common.helper.StrUtil;
import v.server.helper.NetUtil;

public final class ClusterConfig {
	public static final String PROPERTIES_PACKAGE = "mv.cluster";

	public static final String DEF_SECRET_KEY = "dcfn66ZlRjYZaOIv78mXXwOp";
	public static final String DEF_ACCEPT_HOST = NetUtil.INSIDE_HOST;
	public static final String DEF_ACCEPT_URI = "/mv/cluster";

	public static final String KEY_SECRET = PROPERTIES_PACKAGE + ".secretKey";
	public static final String KEY_ACCEPT_HOST = PROPERTIES_PACKAGE + ".acceptHost";
	public static final String KEY_ACCEPT_PORT = PROPERTIES_PACKAGE + ".acceptPort";
	public static final String KEY_ACCEPT_URI = PROPERTIES_PACKAGE + ".acceptUri";

	public static final String KEY_IP_RULES = PROPERTIES_PACKAGE + ".ipRules";
	public static final String KEY_IP_WHILE_LIST = PROPERTIES_PACKAGE + ".ipWhiteList";
	public static final String KEY_IP_BLACK_LIST = PROPERTIES_PACKAGE + ".ipBlackList";

	protected String secretKey = DEF_SECRET_KEY;
	protected String acceptHost = DEF_ACCEPT_HOST;
	protected int acceptPort = -1;
	protected String acceptUri = DEF_ACCEPT_URI;

	protected String[] ipRules;
	protected List<String> ipWhiteList;
	protected List<String> ipBlackList;

	ClusterConfig() {
	}

	public String getSecretKey() {
		return secretKey;
	}

	public String getAcceptHost() {
		return acceptHost;
	}

	public int getAcceptPort() {
		return acceptPort;
	}

	public String getAcceptUri() {
		return acceptUri;
	}

	public IpRule getIpRule() {
		IpRule rule = new IpRule(ipRules);
		if (ipWhiteList != null)
			rule.addWhileIps(ipWhiteList);
		if (ipBlackList != null)
			rule.addBlackIps(ipBlackList);
		return rule;
	}

	final void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	final void setAcceptHost(String acceptHost) {
		this.acceptHost = acceptHost;
	}

	final void setAcceptUri(String acceptUri) {
		this.acceptUri = acceptUri;
	}

	final void setAcceptPort(int acceptPort) {
		this.acceptPort = acceptPort;
	}

	final void setIpRules(String ipRules) {
		this.ipRules = StrUtil.spilt(ipRules, ',');
	}

	final void setIpWhiteList(String ipWhiteList) {
		this.ipWhiteList = StrUtil.spiltAsList(ipWhiteList, ',');
	}

	final void setIpBlackList(String ipBlackList) {
		this.ipBlackList = StrUtil.spiltAsList(ipBlackList, ',');
	}

}
