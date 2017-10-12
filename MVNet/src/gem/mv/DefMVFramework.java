package gem.mv;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gem.mv.plugin.cluster.ClusterConnMgrPlugin;
import v.common.helper.ParseUtil;
import v.common.helper.ReflectUtil;
import v.common.unit.DefEnumeration;
import v.common.unit.VSimpleStatusObject;
import v.plugin.VPlugin;
import v.plugin.annotation.VPluginBean;
import v.resource.HatchFactoryResourceMgr;
import v.resource.VResourceMgr;
import v.server.helper.ClassHelper;
import w.DefWeaveBuilder;
import w.WeaveBuilder;
import w.WeaveFramework;
import w.WessionSetConfig;
import w.netty.acceptor.NettyAcceptor;
import w.netty.connector.NettyConnector;

@SuppressWarnings({ "unchecked", "rawtypes" })
final class DefMVFramework extends VSimpleStatusObject implements MVFramework, MVFrameworkContext {
	protected final Map<String, String> properties;
	protected final List<MVPlugin> plugins;
	protected final WeaveBuilder wbuilder;
	protected final HatchFactoryResourceMgr resourceMgr;
	protected final ClusterConnMgrPlugin connMgr;
	protected byte idx;
	protected WeaveFramework w;

	DefMVFramework(HatchFactoryResourceMgr resourceMgr, Map<String, String> properties, List<MVPlugin> plugins) {
		this.properties = properties;
		this.plugins = plugins;
		this.wbuilder = DefWeaveBuilder.create();
		this.resourceMgr = resourceMgr;
		this.connMgr = new ClusterConnMgrPlugin();
		this.idx = 0;
		this.plugins.add(connMgr);
	}

	@Override
	protected void _init0() {
		scanPluginBuilderRes();
		resourceMgr.init();
		injectPlugin();
		for (MVPlugin p : plugins)
			p.onInit(this);
		initW();
		for (MVPlugin p : plugins)
			p.onStart();
	}

	private final void scanPluginBuilderRes() {
		for (MVPlugin p : plugins)
			resourceMgr.addCellObj(p);
	}

	private final void injectPlugin() {
		for (MVPlugin p : plugins) {
			List<Field> fields = ClassHelper.getAllFields(p.getClass());
			for (Field f : fields) {
				if (ClassHelper.isFinal(f))
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

	private final void initW() {
		wbuilder.setAcceptor(new NettyAcceptor());
		wbuilder.setConnector(new NettyConnector());
		wbuilder.setBindHost("0.0.0.0");
		w = wbuilder.buildAndOpen();
	}

	@Override
	protected void _destory0() {

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
	public WessionSetConfig createSetConfig() {
		return wbuilder.createSetConfig(idx++);
	}

}
