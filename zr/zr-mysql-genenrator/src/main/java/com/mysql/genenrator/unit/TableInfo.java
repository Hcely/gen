package com.mysql.genenrator.unit;

import java.util.LinkedList;
import java.util.List;

public class TableInfo {
	protected final String tableName;
	protected final List<ColumnInfo> columns;

	public TableInfo(String tableName) {
		this.tableName = tableName;
		this.columns = new LinkedList<>();
	}

	public TableInfo addColumn(boolean pri, int columnType, String columnName, boolean autoInc) {
		columns.add(new ColumnInfo(pri, columnType, columnName, autoInc));
		return this;
	}

	public String getTableName() {
		return tableName;
	}

	public List<ColumnInfo> getColumns() {
		return columns;
	}

}
