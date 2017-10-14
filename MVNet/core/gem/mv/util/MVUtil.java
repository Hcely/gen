package gem.mv.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import v.VContext;
import v.common.helper.IOHelper;
import v.common.helper.StrUtil;
import v.common.io.BytesOutputStream;
import v.server.helper.NetUtil;

public class MVUtil {
	public static final Map<String, String> parseParams(String[] args) {
		Map<String, String> map = new LinkedHashMap<>();
		for (String e : args)
			parseContent(e, map);
		return map;
	}

	public static final void loadProperties(File file, Map<String, String> properties) throws IOException {
		loadProperties(new FileInputStream(file), properties);
	}

	public static final void loadProperties(InputStream in, Map<String, String> properties) throws IOException {
		int size = in.available();
		BytesOutputStream out = new BytesOutputStream(size > 0 ? size : 4096);
		IOHelper.rwWithClose(in, out);
		String str = new String(out.toByteArray(), VContext.UTF_8);
		parseContent(str, properties);
	}

	public static final void parseContent(String content, Map<String, String> properties) {
		List<String> strs = StrUtil.spiltAsList(content, '\n');
		for (String e : strs)
			parseKeyValue(e, properties);
	}

	private static final void parseKeyValue(String str, Map<String, String> properties) {
		str = str.trim();
		if (str.isEmpty())
			return;
		String[] s = StrUtil.spilt(str, '=', 2);
		if (s.length == 2) {
			String key = s[0].trim();
			String value = s[1].trim();
			properties.put(key, value);
		}
	}

	public static final int getClientServerId() {
		return (Integer.MAX_VALUE & NetUtil.getMachineCode()) | 0x40000000;
	}

	public static final String collection2Str(Collection<Object> coll) {
		StringBuilder sb = new StringBuilder(128);
		int i = 0;
		for (Object e : coll) {
			if (i++ > 0)
				sb.append(',');
			sb.append(e);
		}
		return sb.toString();
	}
}
