package zr.unit;

import java.io.PrintStream;
import java.io.PrintWriter;

public class HRStatusException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	protected final HRStatus status;
	protected final Throwable error;

	public HRStatusException(HRStatus responseCode) {
		this(responseCode, null);
	}

	public HRStatusException(HRStatus responseCode, Throwable error) {
		this.status = responseCode;
		this.error = error;
	}

	public HRStatus getStatus() {
		return status;
	}

	public Throwable getError() {
		return error;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(128);
		StackTraceElement st = getStackTrace()[0];
		sb.append("HRError code:").append(status.code).append(",msg:").append(status.msg);
		sb.append(" at:").append(st);
		return sb.toString();
	}

	@Override
	public void printStackTrace(PrintStream s) {
		synchronized (s) {
			s.println(toString());
			if (error != null)
				error.printStackTrace(s);
		}
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		synchronized (s) {
			s.println(toString());
			if (error != null)
				error.printStackTrace(s);
		}
	}

}
