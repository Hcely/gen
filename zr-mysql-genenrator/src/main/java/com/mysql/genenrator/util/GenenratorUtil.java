package com.mysql.genenrator.util;

public class GenenratorUtil {
	public static final void toHump(StringBuilder sb, String name) {
		char c;
		for (int i = 0, len = name.length(); i < len; ++i) {
			c = name.charAt(i);
			if (c == '_') {
				if (++i < len)
					c = toHighCase(name.charAt(i));
				else
					break;
			} else if (i == 0)
				c = toHighCase(c);
			sb.append(c);
		}
	}

	public static final String toHighLow(String name) {
		StringBuilder sb = new StringBuilder(name);
		sb.setCharAt(0, toHighCase(name.charAt(0)));
		return sb.toString();
	}

	public static final char toHighCase(char c) {
		if (c > 96 && c < 123)
			c -= 32;
		return c;
	}

	public static final char toLowCase(char c) {
		if (c > 64 && c < 91)
			c += 32;
		return c;
	}
}
