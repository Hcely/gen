package zr.aop.count;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import v.Destoryable;
import v.Initializable;
import v.common.unit.DateCron;
import v.common.unit.thread.VThreadLoop;
import zr.aop.unit.MethodFilterSet;
import zr.aop.unit.count.MethodCount;
import zr.aop.unit.count.MethodCountSumInfo;

public class MethodCountMgr implements Initializable, Destoryable {
	protected final ConcurrentLinkedQueue<MethodFilterSet> filterSets;
	protected final MethodCountHandler handler;
	protected final Map<String, MethodCountSumInfo> countSumMap;
	protected VThreadLoop loop;

	public MethodCountMgr(MethodCountHandler handler) {
		this.filterSets = new ConcurrentLinkedQueue<>();
		this.handler = handler;
		this.countSumMap = new HashMap<>();
	}

	@Override
	public void init() {
		loop = new VThreadLoop();
		loop.start();
		loop.schedule(new MethodCountCollectTask(this), new DateCron(-1, -1, -20));
		loop.schedule(new MethodCountLogTask(this), new DateCron(-1, -1, 0));
	}

	@Override
	public void destory() {
		if (loop != null)
			loop.destory();
	}

	public void addFilterSet(MethodFilterSet filterSet) {
		filterSets.add(filterSet);
	}

	void addCount(String methodName, MethodCount count) {
		MethodCountSumInfo info = countSumMap.get(methodName);
		if (info == null)
			countSumMap.put(methodName, info = new MethodCountSumInfo(methodName));
		info.add(count);
	}

	void resetCounts() {
		for (MethodCountSumInfo e : countSumMap.values())
			e.reset();
	}

}
