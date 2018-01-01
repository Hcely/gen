package com.mysql.genenrator.handler;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.mysql.genenrator.unit.ColumnInfo;

public class DefFieldTypeHandler implements FieldTypeHandler {
	protected boolean auto2Int = true;

	public boolean isAuto2Int() {
		return auto2Int;
	}

	public void setAuto2Int(boolean auto2Int) {
		this.auto2Int = auto2Int;
	}

	@Override
	public Class<?> handle(int columnType) {
		switch (columnType) {
		case ColumnInfo.TYPE_TINYINT:
			if (!auto2Int)
				return Byte.class;
		case ColumnInfo.TYPE_SMALLINT:
			if (!auto2Int)
				return Short.class;
		case ColumnInfo.TYPE_MEDIUMINT:
		case ColumnInfo.TYPE_INT:
			return Integer.class;
		case ColumnInfo.TYPE_FLOAT:
			return Float.class;
		case ColumnInfo.TYPE_REAL:
		case ColumnInfo.TYPE_DOUBLE:
			return Double.class;
		case ColumnInfo.TYPE_BIGINT:
			return Long.class;

		case ColumnInfo.TYPE_DATE:
			return Date.class;
		case ColumnInfo.TYPE_DATETIME:
		case ColumnInfo.TYPE_TIMESTAMP:
			return Timestamp.class;
		case ColumnInfo.TYPE_TIME:
			return Time.class;
		case ColumnInfo.TYPE_YEAR:
			return Date.class;
		case ColumnInfo.TYPE_STR:
			return String.class;
		}
		return Object.class;
	}

}
