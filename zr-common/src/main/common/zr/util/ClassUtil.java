package zr.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ClassUtil {
	public static final Field getFiled(Class<?> clz, String name) {
		while (clz != Object.class) {
			try {
				Field f = clz.getDeclaredField(name);
				if (f != null) {
					f.setAccessible(true);
					return f;
				}
			} catch (Exception e) {
			}
			clz = clz.getSuperclass();
		}
		return null;
	}

	public static final List<Method> getMethods(Class<?> clz) {
		List<Method> hr = new LinkedList<>();
		getMethods(clz, hr);
		return hr;
	}

	public static final List<Field> getFields(Class<?> clz) {
		List<Field> hr = new LinkedList<>();
		getFields(clz, hr);
		return hr;
	}

	private static final void getMethods(Class<?> clz, List<Method> hr) {
		if (clz == Object.class)
			return;
		getMethods(clz.getSuperclass(), hr);
		for (Method e : clz.getDeclaredMethods()) {
			e.setAccessible(true);
			hr.add(e);
		}
	}

	private static final void getFields(Class<?> clz, List<Field> hr) {
		if (clz == Object.class)
			return;
		getFields(clz.getSuperclass(), hr);
		for (Field e : clz.getDeclaredFields()) {
			e.setAccessible(true);
			hr.add(e);
		}
	}

	public static final <T extends Annotation> T getAnnotation(Method m, final Class<T> annoClz) {
		T a = m.getAnnotation(annoClz);
		if (a != null)
			return a;
		return getAnnotation(m.getDeclaringClass(), annoClz);
	}

	public static final <T extends Annotation> T getAnnotation(Class<?> clz, final Class<T> annoClz) {
		T a;
		while (clz != Object.class) {
			if ((a = clz.getAnnotation(annoClz)) == null)
				clz = clz.getSuperclass();
			else
				return a;
		}
		return null;
	}

	public static final <T extends Annotation> List<T> getAnnotations(Method m, final Class<T> annoClz) {
		LinkedList<T> hr = new LinkedList<>();
		T a = m.getAnnotation(annoClz);
		if (a != null)
			hr.addFirst(a);
		getAnnotations(m.getDeclaringClass(), annoClz, hr);
		return hr;
	}

	public static final <T extends Annotation> List<T> getAnnotations(Class<?> clz, final Class<T> annoClz) {
		return getAnnotations(clz, annoClz, new LinkedList<>());
	}

	private static final <T extends Annotation> List<T> getAnnotations(Class<?> clz, final Class<T> annoClz,
			LinkedList<T> hr) {
		T a;
		while (clz != Object.class) {
			if ((a = clz.getAnnotation(annoClz)) != null)
				hr.addFirst(a);
			clz = clz.getSuperclass();
		}
		return hr;
	}
}
