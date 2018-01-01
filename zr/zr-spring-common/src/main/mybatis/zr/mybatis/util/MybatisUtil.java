package zr.mybatis.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import zr.entity.annotation.Transient;
import zr.mybatis.BaseDaoImpl;

public final class MybatisUtil {
	protected static final AtomicInteger incNum = new AtomicInteger(0);

	public static final String getMapperName() {
		return "_xmlMapper" + incNum.getAndIncrement();
	}

	public static final Class<?> getDaoGenericType(Class<?> clazz) {
		LinkedList<Class<?>> stacks = new LinkedList<>();
		while (clazz.getSuperclass() != BaseDaoImpl.class) {
			stacks.add(clazz);
			clazz = clazz.getSuperclass();
		}
		int idx = 0, len;
		while (clazz != null) {
			Type type = clazz.getGenericSuperclass();
			if (!(type instanceof ParameterizedType))
				break;
			Type pt = ((ParameterizedType) type).getActualTypeArguments()[idx];
			if (pt instanceof Class)
				return (Class<?>) pt;
			if (pt instanceof ParameterizedType)
				return (Class<?>) ((ParameterizedType) pt).getRawType();
			TypeVariable<?>[] types = clazz.getTypeParameters();
			clazz = null;
			for (idx = 0, len = types.length; idx < len; ++idx)
				if (pt == types[idx]) {
					clazz = stacks.pollLast();
					break;
				}
		}
		throw new RuntimeException("can not get type of SimpleDao");
	}

	public static final Class<?> getFieldGenericType(Field field) {
		ParameterizedType type = (ParameterizedType) field.getGenericType();
		Type pt = type.getActualTypeArguments()[0];
		if (pt instanceof ParameterizedType)
			return (Class<?>) ((ParameterizedType) pt).getRawType();
		return (Class<?>) pt;
	}

	public static final Field[] getFields(Class<?> clz) {
		Map<String, Field> map = new LinkedHashMap<>();
		getFields(clz, map);
		Field[] fs = new Field[map.size()];
		return map.values().toArray(fs);
	}

	private static final void getFields(Class<?> clz, Map<String, Field> hr) {
		if (clz == Object.class)
			return;
		getFields(clz.getSuperclass(), hr);
		for (Field f : clz.getDeclaredFields()) {
			int mod = f.getModifiers();
			if (Modifier.isFinal(mod))
				continue;
			if (Modifier.isStatic(mod))
				continue;
			if (f.getAnnotation(Transient.class) != null)
				continue;
			f.setAccessible(true);
			hr.put(f.getName(), f);
		}
	}

	public static final Number getNumber(final Number number, final Class<?> clazz) {
		if (number == null)
			return null;
		if (number.getClass() == clazz)
			return number;
		if (clazz == int.class || clazz == Integer.class)
			return Integer.valueOf(number.intValue());
		if (clazz == long.class || clazz == Long.class)
			return Long.valueOf(number.longValue());
		if (clazz == double.class || clazz == Double.class)
			return Double.valueOf(number.doubleValue());
		if (clazz == byte.class || clazz == Byte.class)
			return Byte.valueOf(number.byteValue());
		if (clazz == short.class || clazz == Short.class)
			return Short.valueOf(number.shortValue());
		if (clazz == float.class || clazz == Float.class)
			return Float.valueOf(number.floatValue());
		return null;
	}

	public static void hump2Underline(StringBuilder sb, String name) {
		char c;
		for (int i = 0, len = name.length(); i < len; ++i) {
			c = name.charAt(i);
			if (c > 64 && c < 91) {
				if (i > 0)
					sb.append('_');
				c += 32;
			}
			sb.append(c);
		}
	}
}
