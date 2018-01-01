package zr.unit;

public final class HRException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final HResult hr;

	public HRException(int code) {
		this(code, null, null);
	}

	public HRException(int code, String msg) {
		this(code, msg, null);
	}

	public HRException(int code, String msg, Throwable error) {
		hr = new HResult(code, msg, error);
	}

	public HRException(Throwable error) {
		hr = new HResult(error);
	}

	public HRException(HResult hr) {
		this.hr = hr;
	}

	public HResult hr() {
		return hr;
	}

}
