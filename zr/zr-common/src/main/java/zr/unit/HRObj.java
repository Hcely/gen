package zr.unit;

public class HRObj<T> extends HRStatus {
	protected T data;

	public HRObj() {
		this(HRStatus.OK);
	}

	public HRObj(HRStatus respCode) {
		this(respCode.code, respCode.msg, null);
	}

	public HRObj(T data) {
		this(HRStatus.CODE_OK, null, data);
	}

	public HRObj(int code, String msg) {
		this(code, msg, null);
	}

	public HRObj(int code, String msg, T data) {
		super(code, msg);
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public HRObj<T> setData(T data) {
		this.data = data;
		return this;
	}

	public HResult toHr() {
		return new HResult(code, msg).addParam(HResult.KEY_DATA, data);
	}

	public HRStatus toStatus() {
		return new HRStatus(code, msg);
	}
}
