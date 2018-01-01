package zr.bean;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import zr.unit.KeyValue;

public class HeaderJacksonMessageConverter extends MappingJackson2HttpMessageConverter {
	private static final ThreadLocal<JsonContentCache> cacheTL = new ThreadLocal<JsonContentCache>() {
		@Override
		protected JsonContentCache initialValue() {
			return new JsonContentCache();
		}
	};
	protected final List<KeyValue<String, String>> responseHeaders = new LinkedList<>();

	public static final String getClearCache() {
		return cacheTL.get().getClearContent();
	}

	public HeaderJacksonMessageConverter(ObjectMapper objectMapper) {
		super(objectMapper);
		init();
	}

	public HeaderJacksonMessageConverter() {
		init();
	}

	private final void init() {
		getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		responseHeaders.add(new KeyValue<>("Cache-Control", "no-cache"));
		responseHeaders.add(new KeyValue<>("Access-Control-Allow-Origin", "*"));
	}

	public void setNonNull(boolean b) {
		getObjectMapper().setSerializationInclusion(b ? Include.NON_NULL : Include.USE_DEFAULTS);
	}

	public void setResponseHeaders(List<String> headers) {
		responseHeaders.clear();
		Map<String, String> map = new HashMap<>();
		for (String e : headers) {
			String[] strs = e.split(":", 2);
			if (strs.length == 2)
				map.put(strs[0].trim(), strs[1].trim());
		}
		for (Entry<String, String> e : map.entrySet())
			responseHeaders.add(new KeyValue<>(e.getKey(), e.getValue()));
	}

	@Override
	protected void addDefaultHeaders(HttpHeaders headers, Object t, MediaType contentType) throws IOException {
		super.addDefaultHeaders(headers, t, contentType);
		for (KeyValue<String, String> e : responseHeaders)
			headers.set(e.getKey(), e.getValue());
	}

	@Override
	protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return read(clazz, null, inputMessage);
	}

	@Override
	public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		JavaType javaType = getJavaType(type, contextClass);
		JsonContentCache reader = cacheTL.get();
		try {
			String content = reader.read(inputMessage);
			if (inputMessage instanceof MappingJacksonInputMessage) {
				Class<?> deserializationView = ((MappingJacksonInputMessage) inputMessage).getDeserializationView();
				if (deserializationView != null)
					return this.objectMapper.readerWithView(deserializationView).forType(javaType).readValue(content);
			}
			return this.objectMapper.readValue(content, javaType);
		} catch (IOException ex) {
			throw new HttpMessageNotReadableException("Could not read document: " + ex.getMessage(), ex);
		}
	}

}
