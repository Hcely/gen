package gem.mv.cluster;

import gem.mv.MVFrameworkBuilder;
import gem.mv.MVFrameworkContext;
import gem.mv.MVPlugin;
import gem.mv.cluster.bean.ClusterAuthInfo;
import gem.mv.cluster.bean.ClusterConfig;
import v.binary3.Binary3CoderMgr;
import v.common.helper.StrUtil;
import v.common.util.IntHashMap;
import v.common.util.SimpleQueue;
import v.resource.annotation.VResource;
import v.server.helper.NetUtil;
import w.WeaveFramework;
import w.Wession;
import w.WessionSet;
import w.WessionSetConfig;
import w.buf.RefBuf;
import w.unit.WeavePromise;
import w.unit.WeaveRequest;

public abstract class ClusterConnMgrPlugin implements MVPlugin {
	protected final IntHashMap<Wession> sessionMap;
	protected final Binary3CoderMgr coderMgr;
	protected final SimpleQueue<ClusterMsgHandler> handlers;
	protected final int serverId;
	protected MVFrameworkContext context;
	protected WeaveFramework weave;

	@VResource
	protected ClusterConfig config;
	protected String acceptWsUrl;
	protected WessionSet sessionSet;

	protected ClusterConnMgrPlugin(int serverId) {
		this.serverId = serverId;
		this.sessionMap = new IntHashMap<>();
		this.coderMgr = new Binary3CoderMgr();
		this.handlers = new SimpleQueue<>();
	}

	@Override
	public void onInit(MVFrameworkContext context) {
		this.context = context;
		int port = config.getAcceptPort();
		String uri = config.getAcceptUri();

		WessionSetConfig setConfig;
		if (serverId > MVFrameworkBuilder.MAX_SERVER_ID)
			setConfig = context.createSetConfig();
		else {
			uri = getAcceptUri(uri);
			if (port < 0)
				port = NetUtil.getEnablePort();
			acceptWsUrl = getAcceptWsUrl(config.getAcceptHost(), port, uri);
			setConfig = context.createSetConfig(port);
		}
		setConfig.setPing(2000, 10000);
		setConfig.setAuthHandler(
				new CLusterAuthHandler(serverId, config.getSecretKey(), acceptWsUrl, uri, port, config.getIpRule()));
		setConfig.setSessionHandler(new CLusterSessionHandler(this));
		sessionSet = setConfig.registerSet();
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onDestory() {
		sessionMap.clear();
	}

	public final int serverId() {
		return serverId;
	}

	public final void conn(int targetServerId, String wsUrl) {
		WeaveRequest request = new WeaveRequest(wsUrl);
		long timestamp = System.currentTimeMillis();
		String sign = CLusterAuthHandler.getSign(wsUrl, serverId, targetServerId, config.getSecretKey(), timestamp);
		request.setParam(CLusterAuthHandler.SERVER_ID, targetServerId);
		request.setParam(CLusterAuthHandler.SIGN, sign);
		request.setParam(CLusterAuthHandler.TIMESTAMP, timestamp);
		ClusterAuthInfo info = new ClusterAuthInfo(targetServerId);
		try {
			WeavePromise<Wession> promise = weave.conn(sessionSet.getName(), request, info);
			promise.sync();
		} catch (InterruptedException e) {
		}
	}

	public final Wession getClusterConn(int serverId) {
		return sessionMap.get(serverId);
	}

	public String getAcceptWsUrl() {
		return acceptWsUrl;
	}

	public boolean send(int targetServerId, Object obj) {
		if (targetServerId == this.serverId) {
			onMsg(obj);
			return true;
		}
		Wession session = sessionMap.get(targetServerId);
		if (session == null)
			return false;
		send(targetServerId, session, obj);
		return true;
	}

	public void send(Wession session, Object obj) {
		int serverId = ((ClusterAuthInfo) session.info()).serverId();
		send(serverId, session, obj);
	}

	private void send(int targetServerId, Wession session, Object obj) {
		RefBuf buf = session.allocate(1024);
		buf.writeInt(targetServerId);
		coderMgr.encode(obj, buf);
		session.sendBinary(buf);
		buf.release();
	}

	final void onMsgBuf(RefBuf buf) {
		Object obj = coderMgr.decode(buf);
		onMsg(obj);
	}

	final void onMsg(Object obj) {
		for (ClusterMsgHandler handler : handlers)
			if (handler.onClusterMsg(obj))
				break;
	}

	private static final String getAcceptUri(String uri) {
		if (uri.startsWith("/"))
			return uri;
		StringBuilder sb = new StringBuilder(uri.length() + 1);
		sb.append('/').append(uri);
		return StrUtil.sbToString(sb);
	}

	private static final String getAcceptWsUrl(String host, int port, String uri) {
		if (NetUtil.INSIDE_HOST.equalsIgnoreCase(host))
			host = NetUtil.getInsideAddr().getHostAddress();
		else if (NetUtil.OUTSIDE_HOST.equalsIgnoreCase(host))
			host = NetUtil.getOutsideAddr().getHostAddress();
		StringBuilder sb = new StringBuilder(128);
		sb.append("ws://").append(host).append(':').append(port).append(uri);
		return sb.toString();
	}
}
