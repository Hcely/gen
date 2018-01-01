package zr.bean;

import java.io.IOException;

import org.springframework.http.HttpInputMessage;

import v.common.helper.IOHelper;
import v.common.io.BytesOutputStream;

final class JsonContentCache {
	protected BytesOutputStream out;
	protected String content;

	public String read(HttpInputMessage inputMessage) throws IOException {
		if (out == null)
			out = new BytesOutputStream(256);
		out.reset();
		IOHelper.rw(inputMessage.getBody(), out);
		int size = out.size();
		content = new String(out.buf(), 0, size);
		if (size > 1024)
			out = null;
		return content;
	}

	public String getClearContent() {
		String tmp = content;
		content = null;
		return tmp;
	}
}