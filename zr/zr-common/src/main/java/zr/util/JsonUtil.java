package zr.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;

public class JsonUtil {
	public static final ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

	@SuppressWarnings("unchecked")
	public static final <T> Map<String, T> json2Obj(String jsonStr) {
		return json2Obj(jsonStr, Map.class);
	}

	public static final <T> T json2Obj(String jsonStr, Class<T> clazz) {
		if (jsonStr == null || jsonStr.isEmpty())
			return null;
		try {
			return mapper.readValue(jsonStr, clazz);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final String obj2JsonStr(Object obj) {
		if (obj == null)
			return null;
		try {
			return mapper.writeValueAsString(obj);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final List<Object> json2List(String jsonStr) {
		if (jsonStr == null || jsonStr.isEmpty())
			return null;
		CollectionType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Object.class);
		try {
			return mapper.readValue(jsonStr, type);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public static final <T> List<T> json2List(String jsonStr, Class<T> clazz) {
		if (jsonStr == null || jsonStr.isEmpty())
			return null;
		CollectionType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
		try {
			return mapper.readValue(jsonStr, type);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public static final <T> Map<String, T> json2Map(String jsonStr, Class<T> clazz) {
		if (jsonStr == null || jsonStr.isEmpty())
			return null;
		MapLikeType type = mapper.getTypeFactory().constructMapLikeType(LinkedHashMap.class, String.class, clazz);
		try {
			return mapper.readValue(jsonStr, type);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}

	}
}
