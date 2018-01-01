package zr.aop;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import zr.aop.annotation.Filter;
import zr.aop.annotation.FilterConfig;
import zr.aop.annotation.FilterDepend;
import zr.aop.annotation.LoggerConfig;
import zr.aop.unit.FilterInfo;
import zr.aop.unit.HttpRequest;
import zr.aop.unit.MethodFilterSet;
import zr.unit.HResult;
import zr.util.ClassUtil;

class Util extends HttpRequest {
	private static final FilterInfo[] EMPTY_FILTERS = {};
	private static final String[] EMPTY_AUTHORITYS = {};

	public static HttpRequest getRequest(HttpServletRequest request, MethodFilterSet filterSet) {
		return HttpRequest.getRequest(request, filterSet);
	}

	public static HttpRequest getResponseRequest(HttpServletRequest request, HResult hr) {
		return HttpRequest.getResponseRequest(request, hr);
	}

	public static void finishRequest(HttpRequest req, HResult hr) {
		HttpRequest.finishRequest(req, hr);
	}

	public static void recycleRequest(HttpRequest req) {
		HttpRequest.recycleRequest(req);
	}

	public static final MethodFilterSet getFilterSet(Method method, ControllerInterceptor aop) {
		Map<Class<?>, Set<String>> map = getFilterConfig(method);
		FilterInfo[] filters = EMPTY_FILTERS;
		AopLogger logger = null;
		boolean defLogger = true;
		LoggerConfig anno = ClassUtil.getAnnotation(method, LoggerConfig.class);
		if (anno != null && anno.value() != AopLogger.class) {
			logger = aop.getLogger(anno.value());
			defLogger = anno.defLogger();
		}
		if (map.size() > 0) {
			List<FilterInfo> list = new LinkedList<>();
			for (Entry<Class<?>, Set<String>> e : map.entrySet()) {
				AopFilter filter = aop.getFilter(e.getKey());
				String[] authoritys = EMPTY_AUTHORITYS;
				Set<String> set = e.getValue();
				if (set.size() > 0)
					authoritys = set.toArray(new String[set.size()]);
				list.add(new FilterInfo(filter, authoritys));
			}
			filters = list.toArray(new FilterInfo[list.size()]);
		}
		MethodFilterSet filterSet = new MethodFilterSet(method, filters, defLogger, logger);
		for (FilterInfo f : filterSet.getFilters())
			f.getFilter().init(filterSet, f);
		return filterSet;
	}

	private static final Map<Class<?>, Set<String>> getFilterConfig(Method method) {
		Map<Class<?>, Set<String>> filterMap = new LinkedHashMap<>();
		List<FilterConfig> annos = ClassUtil.getAnnotations(method, FilterConfig.class);
		for (FilterConfig fc : annos) {
			if (fc.override())
				filterMap.clear();
			else
				for (Class<?> c : fc.except())
					filterMap.remove(c);
			for (Filter f : fc.value()) {
				Set<String> authoritys = addFilter(f.value(), filterMap);
				if (f.override())
					authoritys.clear();
				else
					for (String e : f.except())
						authoritys.remove(e);
				for (String e : f.authority())
					authoritys.add(e);
			}
		}
		return filterMap;
	}

	private static final Set<String> addFilter(Class<?> filterClz, Map<Class<?>, Set<String>> filterMap) {
		Set<String> authoritys = filterMap.get(filterClz);
		if (authoritys != null)
			return authoritys;
		Set<Class<?>> depends = getDepends(filterClz);
		for (Class<?> c : depends)
			addFilter(c, filterMap);
		filterMap.put(filterClz, authoritys = new LinkedHashSet<>());
		return authoritys;
	}

	private static final Set<Class<?>> getDepends(Class<?> filterClz) {
		Set<Class<?>> filters = new LinkedHashSet<>();
		List<FilterDepend> annos = ClassUtil.getAnnotations(filterClz, FilterDepend.class);
		for (FilterDepend d : annos) {
			if (d.override())
				filters.clear();
			else
				for (Class<?> c : d.except())
					filters.remove(c);
			for (Class<?> c : d.value())
				filters.add(c);
		}
		return filters;
	}
}
