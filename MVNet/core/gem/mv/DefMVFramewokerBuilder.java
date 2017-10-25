package gem.mv;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import gem.mv.util.MVUtil;
import v.common.helper.FileUtil;
import v.common.helper.ReflectUtil;
import v.plugin.annotation.VPluginBean;
import v.resource.HatchFactoryResourceMgr;
import v.resource.annotation.VResource;
import v.server.helper.ClassHelper;
import v.server.helper.ClassHelper.ClassResource;
import w.handler.WeaveErrorHandler;

public class DefMVFramewokerBuilder implements MVFrameworkBuilder {
	protected final Map<String, String> properties;
	protected final HatchFactoryResourceMgr resourceMgr;
	protected final Map<Class<?>, MVPlugin> pluginMap;
	protected final Set<Class<?>> resourceClzes;

	protected WeaveErrorHandler errorHandler;
	protected int serverId;

	public DefMVFramewokerBuilder() {
		this.properties = new HashMap<>();
		this.resourceClzes = new HashSet<>();
		this.resourceMgr = new HatchFactoryResourceMgr(properties);
		this.pluginMap = new LinkedHashMap<>();
		this.serverId = -1;
	}

	@Override
	public void addPlugin(Class<? extends MVPlugin> pluginClz) {
		if (!pluginMap.containsKey(pluginClz))
			pluginMap.put(pluginClz, ReflectUtil.newObj(pluginClz));
	}

	@Override
	public void addPlugin(MVPlugin plugin) {
		pluginMap.put(plugin.getClass(), plugin);
	}

	@Override
	public void addResource(Class<?> clz) {
		resourceMgr.addCellClass(clz);
	}

	@Override
	public void addResource(Object obj) {
		resourceMgr.addCellObj(obj);
	}

	@Override
	public void scanClass(String... classPaths) {
		scanClass(null, classPaths);
	}

	@Override
	public void scanClass(Class<?> clz, String... classPaths) {
		Set<String> clzes = new LinkedHashSet<>();
		for (String loc : classPaths) {
			try {
				List<String> list = ClassHelper.scanClasses(clz, loc);
				clzes.addAll(list);
			} catch (IOException e) {
			}
		}
		for (String s : clzes) {
			Class<?> c = ReflectUtil.getClassByName(s);
			if (MVPlugin.class.isAssignableFrom(c)) {
				if (c.getAnnotation(VPluginBean.class) != null)
					addPlugin((MVPlugin) ReflectUtil.newObj(c));
			} else if (c.getAnnotation(VResource.class) != null)
				resourceClzes.add(c);
		}
	}

	@Override
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	@Override
	public void setProperty(Map<String, String> props) {
		for (Entry<String, String> e : props.entrySet())
			setProperty(e.getKey(), e.getValue());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setProperty(String key, Object value) {
		if (value instanceof Collection)
			setProperty(key, MVUtil.collection2Str((Collection<Object>) value));
		else if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) value;
			for (Entry e : map.entrySet()) {
				String k0 = e.getKey().toString();
				if (key == null)
					setProperty(k0, e.getValue());
				else
					setProperty(key + '.' + k0, e.getValue());
			}
		} else
			setProperty(key, value.toString());

	}

	@Override
	public void scanPropertiesFile(String... locations) {
		scanPropertiesFile(null, locations);
	}

	@Override
	public void scanPropertiesFile(Class<?> clz, String... locations) {
		for (String e : locations)
			try {
				if (e.startsWith("classpath")) {
					List<ClassResource> resources = ClassHelper.scanResources(clz, e);
					for (ClassResource r : resources)
						MVUtil.loadProperties(r.getIn(), properties);
				} else {
					List<File> files = FileUtil.scanResources(e);
					for (File f : files)
						MVUtil.loadProperties(f, properties);
				}
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
	}

	@Override
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	@Override
	public void setWeaveErrorHandler(WeaveErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	@Override
	public MVFramework build() {
		if (serverId < 0)
			serverId = MVUtil.getClientServerId();
		List<MVPlugin> plugins = new LinkedList<>(pluginMap.values());
		DefMVFramework framework = new DefMVFramework(serverId, resourceMgr, properties, plugins, resourceClzes,
				errorHandler);
		return framework;
	}

	@Override
	public Class<DefMVFramework> getType() {
		return DefMVFramework.class;
	}

}
