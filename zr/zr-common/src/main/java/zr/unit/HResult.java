package zr.unit;

import v.common.util.LinkedQueueMap;

public final class HResult extends LinkedQueueMap<String, Object> {
	public static final int CODE_SUCCESS = 200;
	public static final int CODE_SERVER_ERROR = 500;
	public static final HResult HR_200 = new HResult();

	public static final String KEY_CODE = "code";
	public static final String KEY_MSG = "msg";

	private Throwable error = null;
	private int code = 0;
	private String msg = null;

	public HResult() {
		this(CODE_SUCCESS, "ok", null);
	}

	public HResult(int code) {
		this(code, null, null);
	}

	public HResult(int code, String msg) {
		this(code, msg, null);
	}

	public HResult(int code, String msg, Throwable error) {
		addParam(KEY_CODE, code);
		addParam(KEY_MSG, msg);
		this.code = code;
		this.msg = msg;
		this.error = error;
	}

	public HResult(Throwable error) {
		this(CODE_SERVER_ERROR, "server error", error);
	}

	public HResult setCode(int code) {
		this.code = code;
		put(KEY_CODE, KEY_CODE);
		return this;
	}

	public int getCode() {
		if (code == 0) {
			Integer num = (Integer) this.get(KEY_CODE);
			if (num != null)
				code = num;
		}
		return code;
	}

	public HResult setMsg(String msg) {
		this.msg = msg;
		put(KEY_MSG, msg);
		return this;
	}

	public String getMsg() {
		if (msg == null)
			msg = (String) get(KEY_MSG);
		return msg;
	}

	public Throwable error() {
		return error;
	}

	public HResult error(Throwable error) {
		this.error = error;
		return this;
	}

	public HResult addParam(String key, Object value) {
		if (value != null)
			this.add(key, value);
		return this;
	}

}
