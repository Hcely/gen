package gem.mv.plugin.cluster;

import java.net.InetSocketAddress;

import gem.mv.util.IpRule;
import w.Wession;
import w.handler.WeaveAuthHandler;
import w.unit.AcceptAuthType;
import w.unit.HR;
import w.unit.WeaveAuthInfo;
import w.unit.WeaveRequest;

public class CLusterAuthHandler implements WeaveAuthHandler {
	protected String secretKey;
	protected String acceptUri;
	protected int acceptPort;
	protected IpRule ipRule;

	@Override
	public AcceptAuthType isAccept(InetSocketAddress remoteAttr, InetSocketAddress localAttr, WeaveRequest request) {
		if (localAttr.getPort() != acceptPort)
			return AcceptAuthType.NOT_ACCEPT;
		if (!acceptUri.equals(request.getUri()))
			return AcceptAuthType.NOT_ACCEPT;
		if (!ipRule.allow(remoteAttr.getAddress().getHostAddress()))
			return AcceptAuthType.REJECT;
		return AcceptAuthType.ACCEPTING_AUTH;
	}

	@Override
	public HR<WeaveAuthInfo> onAcceptAuth(Wession session, WeaveRequest request) {
		
		return null;
	}

	@Override
	public boolean onConflict(Wession oldSession, Wession newSession) {
		return false;
	}

}
