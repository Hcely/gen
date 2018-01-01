package com.mysql.genenrator.unit;

public class ColumnInfo {
	public static final int TYPE_TINYINT = 1;
	public static final int TYPE_SMALLINT = 2;
	public static final int TYPE_MEDIUMINT = 3;
	public static final int TYPE_INT = 4;
	public static final int TYPE_FLOAT = 5;
	public static final int TYPE_REAL = 6;
	public static final int TYPE_DOUBLE = 7;
	public static final int TYPE_BIGINT = 8;

	public static final int TYPE_DATE = 10;
	public static final int TYPE_DATETIME = 11;
	public static final int TYPE_TIMESTAMP = 12;
	public static final int TYPE_TIME = 13;
	public static final int TYPE_YEAR = 14;

	public static final int TYPE_STR = 20;
	public static final int TYPE_OBJ = 30;

	protected final boolean pri;
	protected final int columnType;
	protected final String columnName;
	protected final boolean autoInc;
	protected Class<?> fieldType;

	public ColumnInfo(boolean pri, int columnType, String columnName, boolean autoInc) {
		this.pri = pri;
		this.columnType = columnType;
		this.columnName = columnName;
		this.autoInc = autoInc;
	}

	public boolean isPri() {
		return pri;
	}

	public boolean isAutoInc() {
		return autoInc;
	}

	public int getColumnType() {
		return columnType;
	}

	public String getColumnName() {
		return columnName;
	}

	public Class<?> getFieldType() {
		return fieldType;
	}

	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}

	public static final int getColumnType(String type) {
		type = type.toLowerCase();
		if (type.startsWith("tinyint"))
			return TYPE_TINYINT;
		if (type.startsWith("smallint"))
			return TYPE_SMALLINT;
		if (type.startsWith("mediumint"))
			return TYPE_MEDIUMINT;
		if (type.startsWith("int"))
			return TYPE_INT;
		if (type.startsWith("float"))
			return TYPE_FLOAT;
		if (type.startsWith("real"))
			return TYPE_REAL;
		if (type.startsWith("decimal"))
			return TYPE_DOUBLE;
		if (type.startsWith("double"))
			return TYPE_DOUBLE;
		if (type.startsWith("bigint"))
			return TYPE_BIGINT;

		if (type.equals("date"))
			return TYPE_DATE;
		if (type.equals("datetime"))
			return TYPE_DATETIME;
		if (type.equals("timestamp"))
			return TYPE_TIMESTAMP;
		if (type.equals("time"))
			return TYPE_TIME;
		if (type.equals("year"))
			return TYPE_YEAR;

		if (type.startsWith("char"))
			return TYPE_STR;
		if (type.startsWith("varchar"))
			return TYPE_STR;
		if (type.equals("text"))
			return TYPE_STR;
		if (type.equals("mediumtext"))
			return TYPE_STR;
		if (type.equals("longtext"))
			return TYPE_STR;

		return TYPE_OBJ;

	}
}
