package gem.mv.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

import gem.mv.MVFramework;
import v.VContext;
import v.common.helper.IOHelper;
import v.common.helper.RandomHelper;
import v.common.helper.StrUtil;
import v.common.io.BytesOutputStream;
import v.server.helper.NetUtil;

public class MVUtil {
	public static final Logger log = LogManager.getLogger(MVUtil.class);

	public static final ObjectMapper jsonMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

	public static final String objToJson(final Object obj) {
		try {
			return jsonMapper.writeValueAsString(obj);
		} catch (IOException e) {
			return null;
		}
	}

	public static final <T> T jsonToObj(final String json, final Class<? extends T> clazz) {
		if (json == null)
			return null;
		try {
			return jsonMapper.readValue(json, clazz);
		} catch (IOException e) {
			return null;
		}
	}

	public static final <T> List<T> jsonToList(final String json, final Class<? extends T> clazz) {
		if (json == null)
			return null;
		CollectionType type = jsonMapper.getTypeFactory().constructCollectionType(LinkedList.class, clazz);
		try {
			return jsonMapper.readValue(json, type);
		} catch (Exception e) {
			return null;
		}
	}

	public static final <T> Map<String, T> jsonToMap(final String json, final Class<? extends T> clazz) {
		if (json == null)
			return null;
		MapType type = jsonMapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, clazz);
		try {
			return jsonMapper.readValue(json, type);
		} catch (Exception e) {
			return null;
		}
	}

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
		if (str.isEmpty() || str.charAt(0) == '#')
			return;
		String[] s = StrUtil.spilt(str, '=', 2);
		if (s.length == 2) {
			String key = s[0].trim();
			String value = s[1].trim();
			properties.put(key, value);
		}
	}

	public static final int getRandomClientId() {
		int i = RandomHelper.randomInt(Integer.MAX_VALUE) << 12;
		i |= 0xFFF & NetUtil.getMachineCode();
		i &= Integer.MAX_VALUE;
		if (i > MVFramework.MAX_SERVER_ID)
			return i;
		return i + MVFramework.MAX_SERVER_ID + 1;
	}

	public static final int getRandomServiceId() {
		int i = RandomHelper.randomInt(Integer.MAX_VALUE) << 12;
		i |= 0xFFF & NetUtil.getMachineCode();
		i &= Integer.MAX_VALUE;
		i %= (MVFramework.MAX_SERVER_ID + 1);
		return i;
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

	public static final String getUri(String uri) {
		int len = uri.length();
		if (uri.charAt(0) == '/' && uri.charAt(len - 1) != '/')
			return uri;
		StringBuilder sb = new StringBuilder(len + 1);
		if (uri.charAt(0) != '/')
			sb.append('/');
		sb.append(uri);
		if (uri.charAt(len - 1) == '/')
			sb.setLength(sb.length() - 1);
		return StrUtil.sbToString(sb);
	}

	public static final String getWsUrl(String host, int port, String uri) {
		if (NetUtil.INSIDE_HOST.equalsIgnoreCase(host))
			host = NetUtil.getInsideAddr().getHostAddress();
		else if (NetUtil.OUTSIDE_HOST.equalsIgnoreCase(host))
			host = NetUtil.getOutsideAddr().getHostAddress();
		StringBuilder sb = new StringBuilder(128);
		sb.append("ws://").append(host).append(':').append(port);
		if (uri.charAt(0) != '/')
			sb.append('/');
		sb.append(uri);
		return sb.toString();
	}
}
