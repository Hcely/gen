package gem.mv.cluster;

import java.net.InetSocketAddress;

import gem.mv.bean.ClusterAuthInfo;
import gem.mv.util.MVUtil;
import v.common.util.IntHashMap;
import w.Wession;
import w.buf.RefBuf;
import w.handler.WeaveSessionHandler;

class CLusterSessionHandler implements WeaveSessionHandler {
	protected final ClusterConnMgrPlugin mgr;
	protected final IntHashMap<Wession> sessionMap;
	protected final int serverId;

	CLusterSessionHandler(ClusterConnMgrPlugin mgr) {
		this.mgr = mgr;
		this.sessionMap = mgr.sessionMap;
		this.serverId = mgr.serverId;
	}

	@Override
	public void onOpen(Wession session, boolean hasConflict) throws Throwable {
		ClusterAuthInfo info = (ClusterAuthInfo) session.info();
		synchronized (this) {
			sessionMap.put(info.serverId(), session);
		}
		StringBuilder sb = new StringBuilder(32);
		InetSocketAddress addr = session.remoteAddress();
		sb.append("server:").append(info.serverId()).append('(').append(addr.getAddress().getHostAddress()).append(':')
				.append(addr.getPort()).append(") connection");
		MVUtil.log.info(sb.toString());
	}

	@Override
	public void onTextMsg(Wession session, String text) throws Throwable {
	}

	@Override
	public void onBinaryMsg(Wession session, RefBuf buf) throws Throwable {
		buf.markReadPos();
		int serverId = buf.readInt();
		if (serverId != this.serverId) {
			Wession target = sessionMap.get(serverId);
			if (target != null) {
				buf.resetReadMark();
				target.sendBinary(buf);
			}
		} else
			mgr.handleMsgBuf(buf);
	}

	@Override
	public void onPingPongTimeout(Wession session) throws Throwable {
	}

	@Override
	public void onDisconn(Wession session, boolean isClose) throws Throwable {
		ClusterAuthInfo info = (ClusterAuthInfo) session.info();
		StringBuilder sb = new StringBuilder(32);
		InetSocketAddress addr = session.remoteAddress();
		sb.append("server:").append(info.serverId()).append('(').append(addr.getAddress().getHostAddress()).append(':')
				.append(addr.getPort()).append(") disconn");
		MVUtil.log.info(sb.toString());
	}

	@Override
	public void onDestory(Wession session) throws Throwable {
		int serverId = ((ClusterAuthInfo) session.info()).serverId();
		synchronized (this) {
			sessionMap.remove(serverId, session);
		}
	}
}
