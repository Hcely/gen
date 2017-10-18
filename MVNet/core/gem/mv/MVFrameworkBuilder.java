package gem.mv;

import v.plugin.VPFrameworkBuilder;
import w.handler.WeaveErrorHandler;

public interface MVFrameworkBuilder extends VPFrameworkBuilder<MVFramework, MVPlugin> {
	public void setServerId(int serverId);

	public void setWeaveErrorHandler(WeaveErrorHandler errorHandler);
}
