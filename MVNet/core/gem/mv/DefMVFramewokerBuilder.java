package gem.mv;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
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

	protected WeaveErrorHandler errorHandler;
	protected int serverId;

	public DefMVFramewokerBuilder() {
		this.properties = new HashMap<>();
		this.resourceMgr = new HatchFactoryResourceMgr(properties);
		this.pluginMap = new LinkedHashMap<>();
		this.serverId = -1;
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
	public void scanClass(String... classPaths) throws Throwable {
		Set<String> clzes = new LinkedHashSet<>();
		for (String loc : classPaths) {
			List<String> list = ClassHelper.scanClasses(loc);
			clzes.addAll(list);
		}
		for (String s : clzes) {
			Class<?> clz = ReflectUtil.getClassByName(s);
			if (MVPlugin.class.isAssignableFrom(clz)) {
				if (clz.getAnnotation(VPluginBean.class) != null)
					addPlugin((MVPlugin) ReflectUtil.newObj(clz));
			} else if (clz.getAnnotation(VResource.class) != null)
				resourceMgr.addCellClass(clz);
		}
	}

	@Override
	public void setProperty(String key, String value) {
		properties.put(key, value);
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
				setProperty(key + '.' + k0, e.getValue());
			}
		} else
			setProperty(key, value.toString());
	}

	@Override
	public void scanPropertiesFile(String... locations) {
		for (String e : locations)
			try {
				if (e.startsWith("classpath")) {
					List<ClassResource> resources = ClassHelper.scanResources(e);
					for (ClassResource r : resources)
						MVUtil.loadProperties(r.getIn(), properties);
				} else if (e.startsWith("file")) {
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
		DefMVFramework framework = new DefMVFramework(serverId, resourceMgr, properties, plugins, errorHandler);
		return framework;
	}

	@Override
	public Class<DefMVFramework> getType() {
		return DefMVFramework.class;
	}

}
