package com.mysql.genenrator.handler;

import com.mysql.genenrator.util.GenenratorUtil;

public class DefEntityNameHandler implements EntityNameHandler {
	protected String tail;

	public String getTail() {
		return tail;
	}

	public void setTail(String tail) {
		this.tail = tail;
	}

	@Override
	public String handle(String tableName) {
		if (tableName.startsWith("t_"))
			tableName = tableName.substring(2);
		else if (tableName.startsWith("table_"))
			tableName = tableName.substring(6);
		StringBuilder sb = new StringBuilder(tableName.length() + 16);
		GenenratorUtil.toHump(sb, tableName);
		if (tail != null && tail.length() > 0)
			appendTail(sb, tail);
		return sb.toString();
	}

	private static final void appendTail(StringBuilder sb, String tail) {
		char c;
		for (int i = 0, len = tail.length(); i < len; ++i) {
			c = tail.charAt(i);
			if (i == 0)
				c = GenenratorUtil.toHighCase(c);
			sb.append(c);
		}
	}

}
