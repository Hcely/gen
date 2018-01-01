package zr.util;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import v.common.helper.StrUtil;
import v.server.helper.NetUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ThaiUtil {
	public static final Pattern REGEX_NUMBER = Pattern.compile("^\\d+$");
	public static final Pattern REGEX_USERNAME = Pattern.compile("^[a-zA-Z0-9@_-]{4,32}$");

	public static final void listAddArray(Collection objs, Object[] array) {
		for (Object e : array)
			objs.add(e);
	}

	public static final String list2Str(Collection<?> objs) {
		if (objs == null || objs.isEmpty())
			return "";
		StringBuffer sb = new StringBuffer(128);
		int i = 0;
		for (Object e : objs) {
			if (++i > 1)
				sb.append(',');
			sb.append(e);
		}
		return sb.toString();
	}

	public static final String starStr(String str, int offset, int count) {
		if (str == null)
			return null;
		int len = str.length();
		StringBuilder sb = new StringBuilder(len).append(str);
		int i = offset + count;
		if (i < len)
			len = i;
		for (i = offset; i < len; ++i)
			sb.setCharAt(i, '*');
		return StrUtil.sbToString(sb);
	}

	public static final boolean regexCheck(String regex, String str) {
		if (str == null)
			return false;
		return regexCheck(Pattern.compile(regex), str);
	}

	public static final boolean regexCheck(Pattern regex, String str) {
		if (str == null)
			return false;
		Matcher matcher = regex.matcher(str);
		return matcher.find();
	}

	private static final long TIME_OFFSET = 1420041600000L;
	private static final int MACHINE_CODE = NetUtil.getMachineCode() & ((1 << 10) - 1);
	private static final int MASK = (1 << 12) - 1;
	private static volatile long lastTime = 0;
	private static volatile int incNum = 0;

	public static final synchronized long nextSnowflakeId() {
		long hr = System.currentTimeMillis();
		int num = 0;
		if (lastTime > hr)
			throw new RuntimeException(String
					.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTime - hr));
		else if (lastTime == hr) {
			num = (++incNum) & MASK;
			if (num == 0)
				hr = nextTime(hr);
		} else
			incNum = 0;
		lastTime = hr;
		hr -= TIME_OFFSET;
		hr <<= 10;
		hr |= MACHINE_CODE;
		hr <<= 12;
		hr |= num;
		return hr;
	}

	public static final long getSnowflakeIdTime(long id) {
		return (id >> 22) + TIME_OFFSET;
	}

	private static final long nextTime(final long time) {
		do {
			long curTime = System.currentTimeMillis();
			if (curTime > time)
				return curTime;
			Thread.yield();
		} while (true);
	}

}
