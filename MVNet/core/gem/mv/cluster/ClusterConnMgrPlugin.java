package gem.mv.cluster;

import java.util.List;

import gem.mv.MVFramework;
import gem.mv.MVFrameworkContext;
import gem.mv.MVPlugin;
import gem.mv.bean.ClusterAuthInfo;
import gem.mv.util.IpRule;
import gem.mv.util.MVUtil;
import v.binary3.Binary3CoderMgr;
import v.common.util.IntHashMap;
import v.common.util.SimpleQueue;
import v.server.helper.NetUtil;
import w.WeaveFramework;
import w.Wession;
import w.WessionSet;
import w.WessionSetConfig;
import w.buf.RefBuf;
import w.handler.WeavePingPongHandler;
import w.unit.WeavePromise;
import w.unit.WeaveRequest;

public abstract class ClusterConnMgrPlugin implements MVPlugin {

	public static final String KEY_SECRET_KEY = "mv.cluster.secretKey";
	public static final String KEY_ACCEPT_HOST = "mv.cluster.acceptHost";
	public static final String KEY_ACCEPT_PORT = "mv.cluster.acceptPort";
	public static final String KEY_ACCEPT_URI = "mv.cluster.acceptUri";

	public static final String KEY_IP_RULES = "mv.cluster.ipRules";
	public static final String KEY_IP_WHILE_LIST = "mv.cluster.ipWhiteList";
	public static final String KEY_IP_BLACK_LIST = "mv.cluster.ipBlackList";

	public static final String DEF_SECRET_KEY = "dcfn66ZlRjYZaOIv78mXXwOp";
	public static final String DEF_ACCEPT_HOST = NetUtil.INSIDE_HOST;
	public static final String DEF_ACCEPT_URI = "/mv/cluster";

	protected final IntHashMap<Wession> sessionMap;
	protected final Binary3CoderMgr coderMgr;
	protected final SimpleQueue<ClusterMsgHandler> handlers;
	protected final int serverId;
	protected MVFrameworkContext context;
	protected WeaveFramework weave;

	protected String secretKey = DEF_SECRET_KEY;
	protected String acceptHost = DEF_ACCEPT_HOST;
	protected int acceptPort = -1;
	protected String acceptUri = DEF_ACCEPT_URI;

	protected IpRule rule;

	protected WeavePingPongHandler pingpongHandler;
	protected String acceptWsUrl;
	protected WessionSet sessionSet;

	protected ClusterConnMgrPlugin(int serverId) {
		this.serverId = serverId;
		this.sessionMap = new IntHashMap<>();
		this.coderMgr = new Binary3CoderMgr();
		this.handlers = new SimpleQueue<>();
	}

	public final void setPingpongHandler(WeavePingPongHandler pingpongHandler) {
		this.pingpongHandler = pingpongHandler;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void onCreate(MVFrameworkContext context) {
		this.context = context;

		this.secretKey = context.getProperty(KEY_SECRET_KEY, DEF_SECRET_KEY);
		this.acceptHost = context.getProperty(KEY_ACCEPT_HOST, DEF_ACCEPT_HOST);
		this.acceptPort = context.getProperty(KEY_ACCEPT_PORT, Integer.class, -1);
		this.acceptUri = MVUtil.getUri(context.getProperty(KEY_ACCEPT_URI, DEF_ACCEPT_URI));

		if (serverId <= MVFramework.MAX_SERVER_ID) {
			List<String> ipRules = context.getProperty(KEY_IP_RULES, List.class, null);
			List<String> whileList = context.getProperty(KEY_IP_WHILE_LIST, List.class, null);
			List<String> blackList = context.getProperty(KEY_IP_BLACK_LIST, List.class, null);
			this.rule = new IpRule(ipRules);
			rule.addWhileIps(whileList);
			rule.addBlackIps(blackList);
			if (acceptPort < 0)
				acceptPort = NetUtil.getEnablePort();
			acceptWsUrl = MVUtil.getWsUrl(acceptHost, acceptPort, acceptUri);
			MVUtil.log.info("server wsUrl:" + acceptWsUrl);
		}
	}

	@Override
	public final void onInit() {
		WessionSetConfig setConfig;
		if (serverId > MVFramework.MAX_SERVER_ID)
			setConfig = context.createSetConfig();
		else
			setConfig = context.createSetConfig(acceptPort);
		setConfig.setPingPongHandler(pingpongHandler);
		setConfig.setPing(2000, 10000);
		setConfig.setAuthHandler(new CLusterAuthHandler(serverId, secretKey, acceptWsUrl, acceptUri, acceptPort, rule));
		setConfig.setSessionHandler(new CLusterSessionHandler(this));
		sessionSet = setConfig.registerSet();
	}

	@Override
	public final void onDestory() {
		sessionMap.clear();
	}

	public final int serverId() {
		return serverId;
	}

	public final WeavePromise<Wession> conn(int targetServerId, String wsUrl) {
		if (targetServerId == serverId)
			return null;
		WeaveRequest request = new WeaveRequest(wsUrl);
		long timestamp = System.currentTimeMillis();
		String sign = CLusterAuthHandler.getSign(wsUrl, serverId, targetServerId, secretKey, timestamp);
		request.setParam(CLusterAuthHandler.SERVER_ID, serverId);
		request.setParam(CLusterAuthHandler.SIGN, sign);
		request.setParam(CLusterAuthHandler.TIMESTAMP, timestamp);
		ClusterAuthInfo info = new ClusterAuthInfo(targetServerId);
		return weave.conn(sessionSet.getName(), request, info);
	}

	public final Wession getClusterSession(int serverId) {
		return sessionMap.get(serverId);
	}

	public final String getAcceptWsUrl() {
		return acceptWsUrl;
	}

	public final boolean send(int targetServerId, Object obj) {
		if (targetServerId == this.serverId) {
			handleMsg(targetServerId, obj);
			return true;
		}
		Wession session = sessionMap.get(targetServerId);
		if (session == null)
			return false;
		send(targetServerId, session, obj);
		return true;
	}

	public final void send(Wession session, Object obj) {
		int serverId = ((ClusterAuthInfo) session.info()).serverId();
		send(serverId, session, obj);
	}

	private final void send(int targetServerId, Wession session, Object obj) {
		RefBuf buf = session.allocate(1024);
		buf.writeInt(targetServerId);
		buf.writeInt(serverId);
		coderMgr.encode(obj, buf);
		session.sendBinary(buf);
		buf.release();
	}

	final void handleMsgBuf(RefBuf buf) {
		int fromServerId = buf.readInt();
		Object obj = coderMgr.decode(buf);
		handleMsg(fromServerId, obj);
	}

	final void handleMsg(int fromServerId, Object obj) {
		for (ClusterMsgHandler handler : handlers)
			if (handler.onClusterMsg(fromServerId, obj))
				break;
	}
}
