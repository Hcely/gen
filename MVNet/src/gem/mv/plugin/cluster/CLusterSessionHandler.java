package gem.mv.plugin.cluster;

import w.Wession;
import w.buf.RefBuf;
import w.handler.WeaveSessionHandler;

public class CLusterSessionHandler implements WeaveSessionHandler {

	@Override
	public void onOpen(Wession session, boolean hasConflict) throws Throwable {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextMsg(Wession session, String text) throws Throwable {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBinaryMsg(Wession session, RefBuf buf) throws Throwable {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPingPongTimeout(Wession session) throws Throwable {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconn(Wession session, boolean isClose) throws Throwable {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestory(Wession session) throws Throwable {
		// TODO Auto-generated method stub

	}

}
