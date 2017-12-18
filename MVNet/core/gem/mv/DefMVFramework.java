package gem.mv;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import gem.mv.cluster.ClusterConnMgrPlugin;
import gem.mv.util.MVUtil;
import v.common.helper.ParseUtil;
import v.common.helper.ReflectUtil;
import v.common.helper.StrUtil;
import v.common.unit.DefEnumeration;
import v.common.unit.VSimpleStatusObject;
import v.plugin.VPlugin;
import v.resource.HatchFactoryResourceMgr;
import v.resource.VResourceMgr;
import w.DefWeaveBuilder;
import w.WeaveBuilder;
import w.WeaveFramework;
import w.WessionSetConfig;
import w.handler.WeaveErrorHandler;
import w.netty.acceptor.NettyAcceptor;
import w.netty.connector.NettyConnector;

@SuppressWarnings({ "unchecked", "rawtypes" })
final class DefMVFramework extends VSimpleStatusObject implements MVFramework, MVFrameworkContext {
	protected final int serverId;
	protected final Map<String, String> properties;
	protected final WeaveErrorHandler errorHandler;
	protected final List<MVPlugin> plugins;
	protected final Set<Class<?>> resourceClzes;
	protected final HatchFactoryResourceMgr resourceMgr;

	protected final WeaveBuilder wbuilder;
	protected final DefClusterConnMgrPlugin connMgr;
	protected byte idx;

	protected WeaveFramework weave;

	DefMVFramework(HatchFactoryResourceMgr resourceMgr, Map<String, String> properties, List<MVPlugin> plugins,
			Set<Class<?>> resourceClzes, WeaveErrorHandler errorHandler) {
		this.serverId = ParseUtil.parse(properties.get(KEY_SERVER_ID), Integer.class, MVUtil.getRandomClientId());
		this.properties = properties;
		this.plugins = plugins;
		this.resourceClzes = resourceClzes;
		this.errorHandler = errorHandler;
		this.resourceMgr = resourceMgr;

		this.wbuilder = DefWeaveBuilder.create();
		this.connMgr = new DefClusterConnMgrPlugin(serverId);
		this.idx = 0;
		plugins.add(0, connMgr);
	}

	private void logProps() {
		List<String> list = new LinkedList<>();
		for (Entry<String, String> e : properties.entrySet())
			list.add(e.getKey() + "=" + e.getValue());
		Collections.sort(list);
		MVUtil.log.info("--init properties start--");
		for (String e : list)
			MVUtil.log.info(e);
		MVUtil.log.info("--init properties end--");
	}

	@Override
	protected void _init0() {
		logProps();
		for (MVPlugin p : plugins)
			p.onCreate(this);
		for (Class<?> e : resourceClzes)
			resourceMgr.addCellClass(e);
		resourceClzes.clear();
		for (MVPlugin p : plugins)
			resourceMgr.addCellObj(p);
		resourceMgr.init();

		for (MVPlugin p : plugins)
			p.onInit();

		initFramework();
		for (MVPlugin p : plugins)
			p.onStart();
	}

	private final void initFramework() {
		if (wbuilder.hasBindPorts()) {
			wbuilder.setAcceptor(new NettyAcceptor());
			wbuilder.setBindHost("0.0.0.0");
		}
		wbuilder.setConnector(new NettyConnector());
		wbuilder.setErrorHandler(errorHandler);
		weave = wbuilder.buildAndOpen();
	}

	@Override
	protected void _destory0() {
		for (MVPlugin p : plugins)
			p.onDestory();

		plugins.clear();
		properties.clear();
		resourceMgr.destory();
		weave.destory();
	}

	@Override
	public String getProperty(String key) {
		return getProperty(key, null);
	}

	@Override
	public String getProperty(String key, String nullback) {
		String value = properties.get(key);
		return value == null ? nullback : value;
	}

	@Override
	public <T> T getProperty(String key, Class<T> clazz, T nullback) {
		String value = properties.get(key);
		if (value == null)
			return nullback;
		if (Collection.class.isAssignableFrom(clazz)) {
			Collection<String> hr;
			if (clazz.isInterface() || ReflectUtil.isAbstract(clazz)) {
				if (Set.class.isAssignableFrom(clazz))
					hr = new HashSet<>();
				else
					hr = new LinkedList<>();
			} else
				hr = (Collection<String>) ReflectUtil.newObj(clazz);

			hr.addAll(StrUtil.spiltAsList(value, ','));
			return (T) hr;
		}
		return ParseUtil.parse(value, clazz, nullback);
	}

	@Override
	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	public <T extends VPlugin<?>> T getPlugin(Class<T> clazz) {
		for (MVPlugin e : plugins)
			if (clazz.isInstance(e))
				return (T) e;
		return null;
	}

	@Override
	public <T extends VPlugin<?>> List<T> getPlugins(Class<T> clazz) {
		List<T> hr = new LinkedList<>();
		for (MVPlugin e : plugins)
			if (clazz.isInstance(e))
				hr.add((T) e);
		return hr;
	}

	@Override
	public WessionSetConfig createSetConfig() {
		return wbuilder.createSetConfig(idx++);
	}

	@Override
	public WessionSetConfig createSetConfig(int... ports) {
		wbuilder.addBindPorts(ports);
		return wbuilder.createSetConfig(idx++);
	}

	@Override
	public Enumeration<VPlugin<?>> enumerationPlugin() {
		return new DefEnumeration(plugins.iterator());
	}

	@Override
	public VResourceMgr getResourceMgr() {
		return resourceMgr;
	}

	@Override
	public <T> T propertiesParse(String name, Class<T> clz) {
		return resourceMgr.getPropertyMapper().parse(properties, name, clz);
	}

	@Override
	public void setProperties(Object obj, String name) {
		resourceMgr.getPropertyMapper().setProperties(obj, properties, name);
	}

	@Override
	public <T> List<T> propertiesParseList(String name, Class<T> clz) {
		return resourceMgr.getPropertyMapper().parseList(properties, name, clz);
	}

	@Override
	public <T> Set<T> propertiesParseSet(String name, Class<T> clz) {
		return resourceMgr.getPropertyMapper().parseSet(properties, name, clz);
	}

	@Override
	public <T> Map<String, T> propertiesParseMap(String name, Class<T> clz) {
		return resourceMgr.getPropertyMapper().parseMap(properties, name, clz);
	}

	@Override
	public ClusterConnMgrPlugin getConnMgr() {
		return connMgr;
	}

	@Override
	public int getServerId() {
		return serverId;
	}

}
