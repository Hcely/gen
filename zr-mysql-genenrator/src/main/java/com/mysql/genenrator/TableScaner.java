package com.mysql.genenrator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.mysql.genenrator.unit.ColumnInfo;
import com.mysql.genenrator.unit.TableInfo;

public class TableScaner {
	public static final List<String> showTables(Connection conn) throws SQLException {
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SHOW TABLES");
		List<String> tables = new LinkedList<>();
		while (rs.next())
			tables.add(rs.getString(1));
		rs.close();
		statement.close();
		return tables;
	}

	public static final List<TableInfo> getTableInfos(Connection conn, List<String> tables) throws SQLException {
		Statement statement = conn.createStatement();
		List<TableInfo> infos = new LinkedList<>();
		for (String e : tables) {
			ResultSet rs = statement.executeQuery("SHOW COLUMNS FROM " + e);
			TableInfo info = new TableInfo(e);
			while (rs.next()) {
				String columnName = rs.getString(1);
				int columnType = ColumnInfo.getColumnType(rs.getString(2));
				String str = rs.getString(4);
				boolean pri = false;
				boolean autoInc = false;
				if (str != null)
					pri = str.toLowerCase().contains("pri");
				str = rs.getString(6);
				if (str != null)
					autoInc = str.toLowerCase().contains("auto_increment");
				info.addColumn(pri, columnType, columnName, autoInc);
			}
			rs.close();
			infos.add(info);
		}
		statement.close();
		return infos;
	}
}
