package com.mysql.genenrator.start;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.mysql.genenrator.EntityWriter;
import com.mysql.genenrator.TableScaner;
import com.mysql.genenrator.unit.TableInfo;
import com.mysql.genenrator.util.ConnectionUtil;

public class Start {
	public static final String username = "root";
	public static final String password = "Root_123456";
	public static final String database = "demodb";
	public static final String host = "127.0.0.1";
	public static final int port = 3306;
	public static final String outputFolder = "d:/entityOutput";
	public static final String packageName = "com.thai.entity";
	public static final String tailName = "entity";

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		System.out.println("start.");
		Connection conn = ConnectionUtil.getMysqlConn(host, port, database, username, password);
		List<String> tables = TableScaner.showTables(conn);
		List<TableInfo> infos = TableScaner.getTableInfos(conn, tables);
		conn.close();
		EntityWriter writer = new EntityWriter(tailName);
		for (TableInfo e : infos)
			writer.write(outputFolder, packageName, e);
		System.out.println("ok.");
	}

}
