package zr.aop;

import zr.AppContext;
import zr.aop.unit.HttpRequest;
import zr.unit.HResult;
import zr.util.JsonUtil;

final class DefLogger implements AopLogger {
	public static final DefLogger INSTANCE = new DefLogger();

	private DefLogger() {
	}

	@Override
	public void log(HttpRequest request) {
		StringBuilder sb;
		if (AppContext.isDebug())
			sb = new StringBuilder(1024);
		else
			sb = new StringBuilder(256);
		sb.append(request.getRequestURI()).append('(').append(request.getTakeTime()).append("ms) ip:")
				.append(request.getRemoteIp()).append(' ');
		request.parametersToStr(sb);

		HResult hr = request.getHr();
		if (AppContext.isDebug())
			sb.append(" response:").append(JsonUtil.obj2JsonStr(hr));
		else
			sb.append(" response ").append(hr.getCode()).append('-').append(hr.getMsg());
		if (hr.error() == null)
			AppContext.logger.info(sb);
		else
			AppContext.logger.error(sb, hr.error());

	}

}
