package zr.mybatis.unit;

import java.lang.reflect.Field;
import java.sql.Timestamp;
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
		final String name = field.getName();
		if (name.equalsIgnoreCase("createTime") || name.equalsIgnoreCase("createdTime")
				|| name.equalsIgnoreCase("createDate") || name.equalsIgnoreCase("createdDate"))
			return true;
		return false;
	}

	public static final boolean isModifyTimeColumn(Field field) {
		final String name = field.getName();
		if (name.equalsIgnoreCase("modifyTime") || name.equalsIgnoreCase("updateTime")
				|| name.equalsIgnoreCase("updatedTime") || name.equalsIgnoreCase("modifyDate")
				|| name.equalsIgnoreCase("updateDate") || name.equalsIgnoreCase("updatedDate"))
			return true;
		return false;
	}

	public static final DateBuilder getDateBuilder(Field field) {
		Class<?> type = field.getType();
		if (type == Long.class || type == long.class)
			return new LongDateBuilder(field.getName());
		if (type == Timestamp.class)
			return new TimestampDateBuilder(field.getName());
		if (type == java.sql.Date.class)
			return new SqlDateBuilder(field.getName());
		if (type == java.util.Date.class)
			return new DateBuilder(field.getName());
		return null;
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

	protected Class<?> clz;
	protected Field[] fields;
	protected Map<String, Field> fieldMap;
	protected String primaryKey;
	protected Field primaryField;
	protected Field incField;

	protected DateBuilder createTimeBuilder;
	protected DateBuilder modifyTimeBuilder;

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
				modifyTimeBuilder = getDateBuilder(f);
			else if (isCreateTimeColumn(f))
				createTimeBuilder = getDateBuilder(f);
		}
		if (primaryField == null)
			primaryField = getPrimaryKeyByName(clz, fieldMap);
		if (primaryField != null)
			primaryKey = primaryField.getName();
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

	public String getPrimaryKey() {
		return primaryKey;
	}

	public Field getPrimaryField() {
		return primaryField;
	}

	public Field getIncField() {
		return incField;
	}

	public DateBuilder getCreateTimeBuilder() {
		return createTimeBuilder;
	}

	public DateBuilder getModifyTimeBuilder() {
		return modifyTimeBuilder;
	}

	@Override
	public int hashCode() {
		return clz.hashCode();
	}

}
