package zr.aop.count;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import v.Destoryable;
import v.common.unit.DateCron;
import v.common.unit.thread.VThreadLoop;
import zr.aop.unit.MethodFilterSet;
import zr.aop.unit.count.MethodCountInfo;

public class MethodCountMgr implements Destoryable {
	protected final ConcurrentLinkedQueue<MethodFilterSet> filterSets;
	protected final MethodCountHandler handler;
	protected final List<MethodCountInfo> infos;
	protected final VThreadLoop loop;

	public MethodCountMgr(MethodCountHandler handler) {
		this.filterSets = new ConcurrentLinkedQueue<>();
		this.handler = handler == null ? DefMethodCountHandler.INSTANCE : handler;
		this.infos = new LinkedList<>();
		this.loop = new VThreadLoop();
		loop.start();
		loop.schedule(1, new MethodCountCollectTask(this), new DateCron(-1, -1, 0));
		loop.schedule(2, new MethodCountOutputTask(this), new DateCron(-1, -1, 0));
	}

	@Override
	public void destory() {
		loop.destory();
	}

	public void addFilterSet(MethodFilterSet filterSet) {
		filterSets.add(filterSet);
	}

	void collectCountInfos() {
		for (MethodFilterSet e : filterSets)
			infos.add(new MethodCountInfo(e.getMethodName(), e.swap()));
	}

	void outputCountInfos() {
		handler.handle(infos);
		infos.clear();
	}

}
