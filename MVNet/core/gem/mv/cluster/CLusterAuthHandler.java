package gem.mv.cluster;

import java.net.InetSocketAddress;

import gem.mv.bean.ClusterAuthInfo;
import gem.mv.util.IpRule;
import v.common.coder.Md5Coder;
import w.Wession;
import w.handler.WeaveAuthHandler;
import w.unit.AcceptAuthType;
import w.unit.HR;
import w.unit.HttpConstant;
import w.unit.WeaveAuthInfo;
import w.unit.WeaveRequest;

class CLusterAuthHandler implements WeaveAuthHandler {
	public static final String SERVER_ID = "serverId";
	public static final String SIGN = "sign";
	public static final String TIMESTAMP = "timestamp";

	protected final int serverId;
	protected final String secretKey;
	protected final String acceptWsUrl;
	protected final String acceptUri;
	protected final int acceptPort;
	protected final IpRule ipRule;

	public CLusterAuthHandler(int serverId, String secretKey, String acceptWsUrl, String acceptUri, int acceptPort,
			IpRule ipRule) {
		this.serverId = serverId;
		this.secretKey = secretKey;
		this.acceptWsUrl = acceptWsUrl;
		this.acceptUri = acceptUri;
		this.acceptPort = acceptPort;
		this.ipRule = ipRule;
	}

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
		Integer fromServerId = request.getParam(SERVER_ID, Integer.class, null);
		String sign = request.getParam(SIGN);
		Long timestamp = request.getParam(TIMESTAMP, Long.class, null);
		if (fromServerId == null || sign == null || timestamp == null)
			return new HR<>(HttpConstant.code._400_BAD_REQUEST);
		if (fromServerId.intValue() == serverId)
			return new HR<>(HttpConstant.code._403_FORBIDDEN);
		if (sign.length() != 32)
			return new HR<>(HttpConstant.code._403_FORBIDDEN);
		long time = System.currentTimeMillis() - timestamp;
		if (time > 10000 & time < -2000)
			return new HR<>(HttpConstant.code._408_REQUEST_TIMEOUT);
		String sign0 = getSign(acceptWsUrl, fromServerId, serverId, secretKey, timestamp);
		if (!sign0.equals(sign))
			return new HR<>(HttpConstant.code._401_UNAUTHORIZED);
		ClusterAuthInfo info = new ClusterAuthInfo(fromServerId);
		return new HR<WeaveAuthInfo>(info);
	}

	@Override
	public boolean onConflict(Wession oldSession, Wession newSession) {
		return false;
	}

	static final String getSign(String wsUrl, int fromServerId, int toServerId, String secretKey, long timestamp) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("fromServerId=").append(fromServerId);
		sb.append("secretKey=").append(secretKey);
		sb.append("timestamp=").append(timestamp);
		sb.append("toServerId=").append(toServerId);
		sb.append("wsUrl=").append(wsUrl);
		return Md5Coder.md5Str(sb.toString());
	}

}
