package zr.unit;

import v.common.util.LinkedQueueMap;

public class HResult extends LinkedQueueMap<String, Object> {
	public static final String KEY_CODE = "code";
	public static final String KEY_MSG = "msg";
	public static final String KEY_DATA = "data";

	private int code = 0;
	private String msg = null;

	public HResult() {
		this(HRStatus.OK);
	}

	public HResult(HRStatus respCode) {
		this(respCode.code, respCode.msg);
	}

	public HResult(int code, String msg) {
		addParam(KEY_CODE, code);
		addParam(KEY_MSG, msg);
		this.code = code;
		this.msg = msg;
	}

	public HResult setCode(int code) {
		this.code = code;
		put(KEY_CODE, code);
		return this;
	}

	public HResult setStatus(HRStatus respCode) {
		setCode(respCode.code);
		setMsg(respCode.msg);
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

	public Object getData() {
		return get(KEY_DATA);
	}

	public HResult setData(Object data) {
		put(KEY_DATA, data);
		return this;
	}

	public HResult addParam(String key, Object value) {
		if (value != null)
			this.add(key, value);
		return this;
	}

	public HRStatus toStatus() {
		return new HRStatus(getCode(), getMsg());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> HRObj<T> toObj() {
		return new HRObj(getCode(), getMsg(), getData());
	}

}
