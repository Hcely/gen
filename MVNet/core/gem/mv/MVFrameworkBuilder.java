package gem.mv;

import v.plugin.VPFrameworkBuilder;
import w.handler.WeaveErrorHandler;

public interface MVFrameworkBuilder extends VPFrameworkBuilder<MVFramework, MVPlugin> {
	public static final int MAX_SERVER_ID = 1 << 29;

	public void setServerId(int serverId);

	public void setWeaveErrorHandler(WeaveErrorHandler errorHandler);
}
