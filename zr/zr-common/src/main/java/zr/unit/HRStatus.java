package zr.unit;

public class HRStatus {
	public static final int CODE_OK = 200;

	public static final HRStatus OK = new HRStatus(CODE_OK, null);

	protected final int code;
	protected final String msg;

	public HRStatus(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public HRStatus msg(String msg) {
		return new HRStatus(this.code, msg);
	}

	@Override
	public String toString() {
		return "[code=" + code + ", msg=" + msg + "]";
	}

	public HResult toHR() {
		return new HResult(this);
	}

	public <T> HRObj<T> toObj() {
		return new HRObj<T>(this);
	}

	public HRStatusException toError() {
		return new HRStatusException(this);
	}

	public HRStatusException toError(Throwable error) {
		return new HRStatusException(this, error);
	}

}
