package gem.mv;

import java.util.List;

import v.plugin.VPFrameworkBuilder;
import w.handler.WeaveErrorHandler;

public interface MVFrameworkBuilder extends VPFrameworkBuilder<MVFramework, MVPlugin> {
	public void setWeaveErrorHandler(WeaveErrorHandler errorHandler);

	public void setServerId(int serverId);

	public void setClusterSecretKey(String secretKey);

	public void setClusterAcceptHost(String acceptHost);

	public void setClusterAcceptPort(int acceptPort);

	public void setClusterAcceptUri(String acceptUri);

	public void setClusterIpRules(List<String> ipRules);

	public void setClusterIpWhiteList(List<String> ipWhiteList);

	public void setClusterIpBlackList(List<String> ipBlackList);

}
