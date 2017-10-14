package gem.mv;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gem.mv.bean.ClusterConfig;
import gem.mv.cluster.ClusterConnMgrPlugin;
import v.common.helper.ParseUtil;
import v.common.helper.ReflectUtil;
import v.common.unit.DefEnumeration;
import v.common.unit.VSimpleStatusObject;
import v.plugin.VPlugin;
import v.plugin.annotation.VPluginBean;
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
	protected final HatchFactoryResourceMgr resourceMgr;

	protected final WeaveBuilder wbuilder;
	protected final DefClusterConnMgrPlugin connMgr;
	protected byte idx;

	protected WeaveFramework weave;

	DefMVFramework(int serverId, HatchFactoryResourceMgr resourceMgr, Map<String, String> properties,
			List<MVPlugin> plugins, WeaveErrorHandler errorHandler) {
		this.serverId = serverId;
		this.properties = properties;
		this.plugins = plugins;
		this.errorHandler = errorHandler;
		this.resourceMgr = resourceMgr;

		this.wbuilder = DefWeaveBuilder.create();
		this.connMgr = new DefClusterConnMgrPlugin(serverId);
		this.idx = 0;

		plugins.add(0, connMgr);
		resourceMgr.addCellClass(ClusterConfig.PROPERTIES_PACKAGE, ClusterConfig.class);
	}

	@Override
	protected void _init0() {
		for (MVPlugin p : plugins)
			p.onCreate(this);

		scanPluginBuilderRes();
		resourceMgr.init();
		for (MVPlugin p : plugins)
			p.onInit();
		initWeave();
		
		injectPlugin();
		for (MVPlugin p : plugins)
			p.onStart();
	}

	private final void scanPluginBuilderRes() {
		for (MVPlugin p : plugins)
			resourceMgr.addCellObj(p);
	}

	private final void injectPlugin() {
		for (MVPlugin p : plugins) {
			List<Field> fields = ReflectUtil.getAllFields(p.getClass());
			for (Field f : fields) {
				if (ReflectUtil.isFinal(f))
					continue;
				Class clz = f.getType();
				if (clz == MVPlugin.class || !MVPlugin.class.isAssignableFrom(clz))
					continue;
				if (f.getAnnotation(VPluginBean.class) == null)
					continue;
				List ps = getPlugins(clz);
				if (ps.size() == 1)
					ReflectUtil.set(f, p, ps.get(0));
			}
		}
	}

	private final void initWeave() {
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
		return ParseUtil.parse(properties.get(key), clazz, nullback);
	}

	@Override
	public Map<String, String> getProperties() {
		return new LinkedHashMap<>(properties);
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
	public ClusterConnMgrPlugin getConnMgr() {
		return connMgr;
	}

	@Override
	public int getServerId() {
		return serverId;
	}

}
