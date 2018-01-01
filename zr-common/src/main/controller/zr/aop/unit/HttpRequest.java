package zr.aop.unit;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import zr.unit.HResult;
import zr.util.JsonUtil;
import zr.util.SpringUtil;

public class HttpRequest {
	private static final ThreadLocal<HttpRequest> reqTL = new ThreadLocal<HttpRequest>() {
		@Override
		protected HttpRequest initialValue() {
			return new HttpRequest();
		}
	};

	public static final HttpRequest curRequest() {
		return reqTL.get();
	}

	protected static HttpRequest getRequest(HttpServletRequest request, MethodFilterSet filterSet) {
		return reqTL.get().set(request, filterSet);
	}

	protected static HttpRequest getResponseRequest(HttpServletRequest request, HResult hr) {
		HttpRequest req = reqTL.get();
		req.set(request, null);
		req.hr = hr;
		return req;
	}

	protected static void finishRequest(HttpRequest req, HResult hr) {
		req.finish(hr);
	}

	protected static void recycleRequest(HttpRequest req) {
		req.clear();
	}

	public static final HttpRequest currentReqeust() {
		return reqTL.get();
	}

	public static final int REQUEST_NORMAL = 1;
	public static final int REQUEST_JSON = 2;
	public static final int REQUEST_MULTIPART = 3;

	private int contentType;
	private String remoteIp;
	private HttpServletRequest request;
	private MethodFilterSet filterSet;
	private long requestTime;

	private long takeTime;
	private HResult hr;

	private Map<String, Object> paramMap;
	private boolean emptyCookies;
	private final Map<String, String> cookieMap;

	protected HttpRequest() {
		this.cookieMap = new HashMap<>();
	}

	private final HttpRequest set(HttpServletRequest request, MethodFilterSet filterSet) {
		checkClear();
		this.request = request;
		this.remoteIp = SpringUtil.getRemoteIp(request);
		this.filterSet = filterSet;
		this.requestTime = System.currentTimeMillis();
		String content = HeaderJacksonMessageConverter.getClearCache();
		if (content != null) {
			paramMap = JsonUtil.json2Obj(content);
			contentType = REQUEST_JSON;
		} else if (request instanceof MultipartHttpServletRequest)
			contentType = REQUEST_MULTIPART;
		else
			contentType = REQUEST_NORMAL;
		return this;
	}

	private final void finish(HResult hr) {
		this.takeTime = System.currentTimeMillis() - requestTime;
		this.hr = hr;
	}

	private final void checkClear() {
		if (contentType != 0)
			clear();
	}

	private final void clear() {
		this.contentType = 0;
		this.request = null;
		this.remoteIp = null;
		this.filterSet = null;
		this.requestTime = 0;
		this.takeTime = -1;
		this.paramMap = null;
		this.emptyCookies = true;
		this.cookieMap.clear();
		this.hr = null;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String getRequestURI() {
		return request.getRequestURI();
	}

	public int getContentType() {
		return contentType;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String name) {
		return (T) request.getAttribute(name);
	}

	public void setAttribute(String name, Object o) {
		request.setAttribute(name, o);
	}

	public String getHeader(String key) {
		return request.getHeader(key);
	}

	public MethodFilterSet getFilterSet() {
		return filterSet;
	}

	public long getCreateTime() {
		return requestTime;
	}

	public long getTakeTime() {
		return takeTime;
	}

	public HResult getHr() {
		return hr;
	}

	public String getCookie(String key) {
		if (emptyCookies) {
			emptyCookies = false;
			Cookie[] cookies = request.getCookies();
			if (cookies != null && cookies.length > 0)
				for (Cookie e : cookies)
					cookieMap.put(e.getName(), e.getValue());
		}
		return cookieMap.get(key);
	}

	public String getParameter(String key) {
		if (paramMap == null)
			return request.getParameter(key);
		Object obj = paramMap.get(key);
		return obj == null ? null : obj.toString();
	}

	public String getArg(String key) {
		String value = getParameter(key);
		if (value != null)
			return value;
		value = getHeader(key);
		if (value != null)
			return value;
		return getCookie(key);
	}

	public String parametersToStr() {
		StringBuilder sb = new StringBuilder(128);
		parametersToStr(sb);
		return sb.toString();
	}

	public void parametersToStr(StringBuilder sb) {
		sb.append("params:{");
		int i = 0;
		if (paramMap == null) {
			Map<String, String[]> params = request.getParameterMap();
			if (params != null) {
				for (Entry<String, String[]> e : params.entrySet()) {
					if (++i > 1)
						sb.append(',');
					sb.append(e.getKey()).append('=').append(e.getValue()[0]);
				}
			}
		} else
			for (Entry<String, Object> e : paramMap.entrySet()) {
				if (++i > 1)
					sb.append(',');
				sb.append(e.getKey()).append('=').append(e.getValue());
			}
		sb.append('}');
	}

}
