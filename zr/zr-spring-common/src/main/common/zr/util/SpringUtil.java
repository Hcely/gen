package zr.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import zr.AppContext;
import zr.bean.SimpleMultipartResolver;

public class SpringUtil {
	private static final String IP_HEADER0 = "X-real-ip";

	public static final void writeJson(HttpServletResponse response, Object obj) {
		String json = JsonUtil.obj2JsonStr(obj);
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(json);
		} catch (Throwable e) {
			AppContext.logger.error("与客户端通讯异常", e);
		}
	}

	public static final Set<String> getMethodUris(Method method) {
		Set<String> classUris = getClassUris(method.getDeclaringClass());
		Set<String> methodUris = getMethodUris0(method);
		if (methodUris == null)
			return null;
		if (classUris == null)
			return methodUris;
		Set<String> uris = new LinkedHashSet<>();
		for (String c : classUris) {
			if (c.equals("/"))
				c = null;
			for (String m : methodUris) {
				if (c == null)
					uris.add(m);
				else {
					StringBuilder sb = new StringBuilder(c.length() + m.length());
					sb.append(c).append(m);
					uris.add(sb.toString());
				}
			}
		}
		return uris;
	}

	private static final Set<String> getClassUris(Class<?> clz) {
		RequestMapping rm = clz.getAnnotation(RequestMapping.class);
		if (rm == null)
			return null;
		return getUri(rm);
	}

	private static final Set<String> getMethodUris0(Method m) {
		RequestMapping mapping = m.getAnnotation(RequestMapping.class);
		if (mapping != null)
			return getUri(mapping);
		GetMapping get = m.getAnnotation(GetMapping.class);
		if (get != null)
			return getUri(get);
		PostMapping post = m.getAnnotation(PostMapping.class);
		if (post != null)
			return getUri(post);
		PutMapping put = m.getAnnotation(PutMapping.class);
		if (put != null)
			return getUri(put);
		DeleteMapping delete = m.getAnnotation(DeleteMapping.class);
		if (delete != null)
			return getUri(delete);
		PatchMapping patch = m.getAnnotation(PatchMapping.class);
		if (patch != null)
			return getUri(patch);
		return null;
	}

	private static final Set<String> getUri(RequestMapping anno) {
		String[] values = anno.value();
		if (values.length == 0)
			values = anno.path();
		return getUris(values);
	}

	private static final Set<String> getUri(GetMapping anno) {
		String[] values = anno.value();
		if (values.length == 0)
			values = anno.path();
		return getUris(values);
	}

	private static final Set<String> getUri(PostMapping anno) {
		String[] values = anno.value();
		if (values.length == 0)
			values = anno.path();
		return getUris(values);
	}

	private static final Set<String> getUri(PutMapping anno) {
		String[] values = anno.value();
		if (values.length == 0)
			values = anno.path();
		return getUris(values);
	}

	private static final Set<String> getUri(DeleteMapping anno) {
		String[] values = anno.value();
		if (values.length == 0)
			values = anno.path();
		return getUris(values);
	}

	private static final Set<String> getUri(PatchMapping anno) {
		String[] values = anno.value();
		if (values.length == 0)
			values = anno.path();
		return getUris(values);
	}

	private static final Set<String> getUris(String[] values) {
		Set<String> hr = new LinkedHashSet<>();
		if (values.length == 0)
			hr.add("/");
		else
			for (String e : values) {
				e = getUri(e);
				if (e != null)
					hr.add(e);
			}
		return hr;
	}

	private static final String getUri(String name) {
		if (name == null || name.isEmpty())
			return "/";
		if (name.charAt(0) != '/')
			name = new StringBuilder(name.length() + 1).append('/').append(name).toString();
		return name;
	}

	public static final HttpServletRequest getRequest() {
		HttpServletRequest request = SimpleMultipartResolver.getMultipartRequest();
		return request == null ? getRawRequest() : request;
	}

	public static final HttpServletRequest getRawRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	public static final HttpServletResponse getResponse() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
	}

	public static final String getRemoteIp() {
		try {
			return getRemoteIp(getRawRequest());
		} catch (Throwable e) {
			return null;
		}
	}

	public static final String getRemoteIp(HttpServletRequest request) {
		String ip = request.getHeader(IP_HEADER0);
		if (StringUtils.isBlank(ip))
			ip = request.getRemoteAddr();
		return ip;
	}

	public static final <T> T getBean(ApplicationContext context, Class<T> clz) {
		do {
			Map<String, T> obj = context.getBeansOfType(clz);
			if (obj == null || obj.isEmpty())
				context = context.getParent();
			else
				return obj.values().iterator().next();
		} while (context != null);
		return null;
	}

	public static final Map<String, Object> getBeansWithAnnotation(ApplicationContext context,
			Class<? extends Annotation> clz) {
		Map<String, Object> hr = new HashMap<>();
		getBeansWithAnnotation(context, clz, hr);
		return hr;
	}

	private static final <T> void getBeansWithAnnotation(ApplicationContext context, Class<? extends Annotation> clz,
			Map<String, Object> hr) {
		if (context.getParent() != null)
			getBeansWithAnnotation(context.getParent(), clz, hr);
		Map<String, Object> map = context.getBeansWithAnnotation(clz);
		hr.putAll(map);
	}

	public static final <T> Map<String, T> getBeansOfType(ApplicationContext context, Class<T> clz) {
		Map<String, T> hr = new HashMap<>();
		getBeansOfType(context, clz, hr);
		return hr;
	}

	private static final <T> void getBeansOfType(ApplicationContext context, Class<T> clz, Map<String, T> hr) {
		if (context.getParent() != null)
			getBeansOfType(context.getParent(), clz, hr);
		Map<String, T> map = context.getBeansOfType(clz);
		hr.putAll(map);
	}

	@SuppressWarnings("unchecked")
	public static final <T> T getRawObj(T obj) {
		try {
			if (AopUtils.isCglibProxy(obj))
				return (T) getRawCGLibProxy(obj);
			else if (AopUtils.isJdkDynamicProxy(obj))
				return (T) getRawJdkProxy(obj);
		} catch (Exception e) {
			AppContext.logger.error(e, e);
		}
		return obj;
	}

	public static final Class<?> getRawClass(Class<?> clz) {
		if (SpringProxy.class.isAssignableFrom(clz) && (Proxy.isProxyClass(clz) || ClassUtils.isCglibProxyClass(clz)))
			return clz.getSuperclass();
		if (SpringProxy.class.isAssignableFrom(clz) && Proxy.isProxyClass(clz))
			return clz.getSuperclass();
		return clz;
	}

	private static final Object getRawCGLibProxy(Object obj) throws Exception {
		Class<?> clazz = obj.getClass();
		Field f = clazz.getDeclaredField("CGLIB$CALLBACK_0");
		f.setAccessible(true);
		Object dynamicAdvisedInterceptor = f.get(obj);
		Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
		advised.setAccessible(true);
		Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
		return target;
	}

	private static final Object getRawJdkProxy(Object obj) throws Exception {
		Class<?> clazz = obj.getClass();
		Field h = clazz.getSuperclass().getDeclaredField("h");
		h.setAccessible(true);
		AopProxy aopProxy = (AopProxy) h.get(obj);
		Field advised = aopProxy.getClass().getDeclaredField("advised");
		advised.setAccessible(true);
		Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
		return target;
	}
}
