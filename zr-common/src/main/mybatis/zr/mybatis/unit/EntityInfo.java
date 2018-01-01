package zr.mybatis.unit;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import zr.entity.annotation.AutoIncrement;
import zr.entity.annotation.PrimaryKey;
import zr.mybatis.util.MybatisUtil;
import zr.util.ClassUtil;

public final class EntityInfo {
	private static final String PRIMARY_KEY_NAME = "PRIMARY_KEY";

	public static final EntityInfo build(Class<?> clz) {
		Field[] fields = MybatisUtil.getFields(clz);
		EntityInfo e = new EntityInfo(clz, fields);
		return e;
	}

	public static final boolean isCreateTimeColumn(Field field) {
		Class<?> type = field.getType();
		if (type != Long.class || type != long.class)
			return false;
		String name = field.getName();
		if (name.equalsIgnoreCase("createTime"))
			return true;
		if (name.equalsIgnoreCase("createdTime"))
			return true;
		return false;
	}

	public static final boolean isModifyTimeColumn(Field field) {
		Class<?> type = field.getType();
		if (type != Long.class || type != long.class)
			return false;
		String name = field.getName();
		if (name.equalsIgnoreCase("modifyTime"))
			return true;
		if (name.equalsIgnoreCase("updateTime"))
			return true;
		return false;
	}

	private static final Field getPrimaryKeyByName(Class<?> clz, Map<String, Field> fieldMap) {
		Field field = ClassUtil.getFiled(clz, PRIMARY_KEY_NAME);
		if (field == null)
			return null;
		try {
			String name = (String) field.get(clz);
			return fieldMap.get(name);
		} catch (Exception e) {
		}
		return null;
	}

	protected final Class<?> clz;
	protected final Field[] fields;
	protected final Map<String, Field> fieldMap;
	protected Field primaryField;
	protected Field incField;

	protected String createTimeCol;
	protected String modifyTimeCol;

	public EntityInfo(Class<?> clz, Field[] fields) {
		this.clz = clz;
		this.fields = fields;
		this.fieldMap = new LinkedHashMap<>();

		for (Field f : fields) {
			fieldMap.put(f.getName(), f);
			if (f.getAnnotation(PrimaryKey.class) != null)
				primaryField = f;
			if (f.getAnnotation(AutoIncrement.class) != null)
				incField = f;
			if (isModifyTimeColumn(f))
				modifyTimeCol = f.getName();
			if (isCreateTimeColumn(f))
				createTimeCol = f.getName();
		}
		if (primaryField == null)
			primaryField = getPrimaryKeyByName(clz, fieldMap);
	}

	public Class<?> getClz() {
		return clz;
	}

	public Field[] getFields() {
		return fields;
	}

	public Map<String, Field> getFieldMap() {
		return fieldMap;
	}

	public Field getPrimaryField() {
		return primaryField;
	}

	public Field getIncField() {
		return incField;
	}

	public String getCreateTimeCol() {
		return createTimeCol;
	}

	public String getModifyTimeCol() {
		return modifyTimeCol;
	}

	public Object newInstance() {
		try {
			return clz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
