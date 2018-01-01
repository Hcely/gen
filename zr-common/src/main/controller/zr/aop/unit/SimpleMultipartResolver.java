package zr.aop.unit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class SimpleMultipartResolver extends CommonsMultipartResolver {
	private static final ThreadLocal<MultipartHttpServletRequest> requestTL = new ThreadLocal<>();

	public static final MultipartHttpServletRequest getMultipartRequest() {
		return requestTL.get();
	}

	public SimpleMultipartResolver() {
	}

	public SimpleMultipartResolver(ServletContext servletContext) {
		super(servletContext);
	}

	@Override
	public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
		MultipartHttpServletRequest mrequest = super.resolveMultipart(request);
		requestTL.set(mrequest);
		return mrequest;
	}

	@Override
	public void cleanupMultipart(MultipartHttpServletRequest request) {
		requestTL.remove();
		super.cleanupMultipart(request);
	}

}
