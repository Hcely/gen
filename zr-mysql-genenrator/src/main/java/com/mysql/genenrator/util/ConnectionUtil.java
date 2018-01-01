package com.mysql.genenrator.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ConnectionUtil {
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static final Connection getMysqlConn(String host, int port, String database, String username,
			String password) throws SQLException {
		return getMysqlConn(host, port, database, username, password, null);
	}

	public static final Connection getMysqlConn(String host, int port, String database, String username,
			String password, Map<String, Object> params) throws SQLException {
		StringBuilder sb = new StringBuilder(256);
		sb.append("jdbc:mysql://").append(host).append(':').append(port).append('/').append(database).append('?');
		if (params == null)
			params = new HashMap<>();
		if (!params.containsKey("serverTimezone"))
			params.put("serverTimezone", "UTC");
		for (Entry<String, Object> e : params.entrySet())
			sb.append(e.getKey()).append('=').append(e.getValue()).append('&');
		String url = sb.substring(0, sb.length() - 1);
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}
}
