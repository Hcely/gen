package zr.aop;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import v.Destoryable;
import v.Initializable;
import zr.AppContext;
import zr.aop.annotation.Filter;
import zr.aop.annotation.FilterConfig;
import zr.aop.annotation.FilterDepend;
import zr.aop.annotation.LoggerConfig;
import zr.aop.count.MethodCountHandler;
import zr.aop.count.MethodCountMgr;
import zr.aop.unit.FilterInfo;
import zr.aop.unit.HttpRequest;
import zr.aop.unit.MethodFilterSet;
import zr.unit.HRException;
import zr.unit.HRStatusException;
import zr.unit.HResult;
import zr.util.ClassUtil;
import zr.util.SpringUtil;

@SuppressWarnings("deprecation")
@Aspect
public class ControllerInterceptor
		implements Initializable, Destoryable, ApplicationContextAware, HandlerExceptionResolver, PriorityOrdered {
	protected ConfigurableApplicationContext context;
	protected Map<Class<?>, AopFilter> filterMap;
	protected Map<Class<?>, AopLogger> loggerMap;
	protected AopLogger defLogger;
	protected Map<Method, MethodFilterSet> filterSetMap;
	protected MethodCountMgr countMgr;

	public ControllerInterceptor() {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = (ConfigurableApplicationContext) applicationContext;
	}

	@PostConstruct
	@Override
	public void init() {
		this.filterSetMap = new HashMap<>();
		initBeans();
		initFilterLoggers();
		initMethodCount();
	}

	private final void initBeans() {
		Collection<Object> controllers = SpringUtil.getBeansWithAnnotation(context, Controller.class).values();
		Set<Class<?>> filterClzs = new HashSet<>();
		Set<Class<?>> loggerClzs = new HashSet<>();
		for (Object e : controllers)
			scanFilterLoggerClz(SpringUtil.getRawClass(e.getClass()), filterClzs, loggerClzs);
		for (Class<?> c : filterClzs)
			registerBean(c);
		for (Class<?> c : loggerClzs)
			registerBean(c);
	}

	private final void initFilterLoggers() {
		this.filterMap = new HashMap<>();
		this.loggerMap = new HashMap<>();
		Collection<AopFilter> filters = SpringUtil.getBeansOfType(context, AopFilter.class).values();
		for (AopFilter e : filters)
			filterMap.put(e.getClass(), e);
		Collection<AopLogger> loggers = SpringUtil.getBeansOfType(context, AopLogger.class).values();
		for (AopLogger e : loggers)
			loggerMap.put(e.getClass(), e);
		defLogger = SpringUtil.getBean(context, AopLogger.class);
		if (defLogger == null)
			defLogger = DefLogger.INSTANCE;
	}

	private final void initMethodCount() {
		MethodCountHandler handler = SpringUtil.getBean(context, MethodCountHandler.class);
		this.countMgr = new MethodCountMgr(handler);
	}

	private final void scanFilterLoggerClz(Class<?> clz, Set<Class<?>> filterClzs, Set<Class<?>> loggerClzs) {
		LoggerConfig anno = ClassUtil.getAnnotation(clz, LoggerConfig.class);
		if (anno != null)
			loggerClzs.add(anno.value());

		List<FilterConfig> annos = ClassUtil.getAnnotations(clz, FilterConfig.class);
		for (FilterConfig fc : annos)
			for (Filter f : fc.value())
				addFilter(f.value(), filterClzs);
		List<Method> methods = ClassUtil.getMethods(clz);
		for (Method m : methods) {
			FilterConfig fc = m.getAnnotation(FilterConfig.class);
			if (fc != null)
				for (Filter f : fc.value())
					addFilter(f.value(), filterClzs);
			anno = m.getAnnotation(LoggerConfig.class);
			if (anno != null)
				loggerClzs.add(anno.value());
		}
	}

	private void addFilter(Class<?> clz, Set<Class<?>> filterClzs) {
		filterClzs.add(clz);
		List<FilterDepend> annos = ClassUtil.getAnnotations(clz, FilterDepend.class);
		for (FilterDepend e : annos)
			for (Class<?> c : e.value())
				addFilter(c, filterClzs);
	}

	private void registerBean(Class<?> clz) {
		if (SpringUtil.getBean(context, clz) != null)
			return;
		AppContext.logger.info("register filter:" + clz.getName());
		BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder.genericBeanDefinition(clz);
		BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) context.getBeanFactory();
		beanFactory.registerBeanDefinition(clz.getName(), beanBuilder.getBeanDefinition());
	}

	@PreDestroy
	@Override
	public void destory() {
		if (filterMap != null)
			filterMap.clear();
		if (loggerMap != null)
			loggerMap.clear();
		if (filterSetMap != null)
			filterSetMap.clear();
		if (countMgr != null)
			countMgr.destory();
		context = null;
	}

	@Pointcut("@annotation(org.springframework.web.bind.annotation.ResponseBody)")
	private void pointCutController() {
	}

	@Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
	private void pointCutRestController() {
	}

	@Around("pointCutController()||pointCutRestController()")
	public Object invoke(ProceedingJoinPoint jp) throws Throwable {
		Signature s = jp.getSignature();
		if (s instanceof MethodSignature) {
			MethodSignature ms = (MethodSignature) s;
			if (ms.getReturnType() == HResult.class)
				return handle(jp, ms);
		}
		return jp.proceed();
	}

	private final HResult handle(ProceedingJoinPoint jp, MethodSignature ms) {
		final MethodFilterSet filterSet = getFilterSet(ms.getMethod());
		final HttpRequest req = Util.getRequest(SpringUtil.getRequest(), filterSet);

		HResult hr = proceed(req, filterSet, jp);
		logRequest(req, filterSet);
		Util.recycleRequest(req);

		if (hr.error() != null)
			throw new HRException(hr);
		return hr;
	}

	private final HResult proceed(HttpRequest req, MethodFilterSet filterSet, ProceedingJoinPoint jp) {
		HResult hr = null;
		FilterInfo[] filters = filterSet.getFilters();
		int i = 0, len = filters.length;
		while (i < len && hr == null)
			try {
				FilterInfo info = filters[i++];
				hr = info.getFilter().before(req, info);
			} catch (HRStatusException e) {
				hr = e.hr();
			} catch (Throwable e) {
				hr = new HResult(e);
			}
		if (hr == null)
			try {
				hr = (HResult) jp.proceed();
				if (hr == null)
					hr = HResult.HR_200;
			} catch (HRException e) {
				hr = e.hr();
			} catch (Throwable e) {
				hr = new HResult(e);
			}
		Util.setResponse(req, hr);
		while (i > 0)
			try {
				FilterInfo info = filters[--i];
				info.getFilter().after(req, info);
			} catch (Throwable e) {
				AppContext.logger.error(e, e);
			}
		Util.finishRequest(req);
		return hr;
	}

	private final void logRequest(HttpRequest request, MethodFilterSet filterSet) {
		AopLogger logger = filterSet.getLogger();
		if (logger != null)
			try {
				logger.log(request);
			} catch (Throwable e) {
				AppContext.logger.error(e, e);
			}
		if (filterSet.isDefLogger())
			try {
				defLogger.log(request);
			} catch (Throwable e) {
				AppContext.logger.error(e, e);
			}
	}

	private final MethodFilterSet getFilterSet(Method method) {
		MethodFilterSet filterSet = filterSetMap.get(method);
		if (filterSet == null)
			synchronized (filterSetMap) {
				if ((filterSet = filterSetMap.get(method)) == null) {
					filterSetMap.put(method, filterSet = Util.getFilterSet(method, this));
					countMgr.addFilterSet(filterSet);
				}
			}
		return filterSet;
	}

	final AopFilter getFilter(Class<?> clz) {
		AopFilter f = filterMap.get(clz);
		if (f == null)
			throw new RuntimeException("can not find filter:" + clz);
		return f;
	}

	final AopLogger getLogger(Class<?> clz) {
		AopLogger l = loggerMap.get(clz);
		if (l == null)
			throw new RuntimeException("can not find logger:" + clz);
		return l;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		HResult hr = null;
		if (ex.getClass() == HRStatusException.class)
			hr = ((HRStatusException) ex).getStatus().toHR();
		else
			hr = handleOtherError(request, ex);
		if (hr == null)
			return null;
		response.addHeader("Cache-Controln", "no-cache");
		response.addHeader("Access-Control-Allow-Origin", "*");
		SpringUtil.writeJson(response, hr);
		return new ModelAndView();
	}

	@Override
	public int getOrder() {
		return 1000;
	}

	private HResult handleOtherError(HttpServletRequest request, Exception ex) {
		HResult hr;
		if (ex instanceof NoSuchRequestHandlingMethodException)
			hr = new HResult(HttpServletResponse.SC_NOT_FOUND, "can not find uri:" + request.getRequestURI());
		else if (ex instanceof HttpRequestMethodNotSupportedException)
			hr = new HResult(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "method not allowed:" + request.getMethod(),
					ex);
		else if (ex instanceof HttpMediaTypeNotSupportedException)
			hr = new HResult(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "unsupported media type", ex);
		else if (ex instanceof HttpMediaTypeNotAcceptableException)
			hr = new HResult(HttpServletResponse.SC_NOT_ACCEPTABLE, "not acceptable media type", ex);
		else if (ex instanceof MissingPathVariableException) {
			String msg = new StringBuilder("miss path param ")
					.append(((MissingPathVariableException) ex).getVariableName()).toString();
			hr = new HResult(HttpServletResponse.SC_BAD_REQUEST, msg);
		} else if (ex instanceof MissingServletRequestParameterException) {
			String msg = new StringBuilder("miss param:")
					.append(((MissingServletRequestParameterException) ex).getParameterName()).toString();
			hr = new HResult(HttpServletResponse.SC_BAD_REQUEST, msg);
		} else if (ex instanceof ServletRequestBindingException)
			hr = new HResult(HttpServletResponse.SC_BAD_REQUEST, "bad requet", ex);
		else if (ex instanceof ConversionNotSupportedException)
			hr = new HResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
		else if (ex instanceof MethodArgumentTypeMismatchException) {
			MethodArgumentTypeMismatchException mex = (MethodArgumentTypeMismatchException) ex;
			String msg = new StringBuilder("can parse param ").append(mex.getName()).append('=').append(mex.getValue())
					.toString();
			hr = new HResult(HttpServletResponse.SC_BAD_REQUEST, msg);
		} else if (ex instanceof HttpMessageNotReadableException)
			hr = new HResult(HttpServletResponse.SC_BAD_REQUEST, "can not read request", ex);
		else if (ex instanceof HttpMessageNotWritableException)
			hr = new HResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "can not write response", ex);
		else if (ex instanceof MethodArgumentNotValidException) {
			hr = new HResult(HttpServletResponse.SC_BAD_REQUEST, "bad request", ex);
		} else if (ex instanceof MissingServletRequestPartException)
			hr = new HResult(HttpServletResponse.SC_BAD_REQUEST, "bad request", ex);
		else if (ex instanceof BindException)
			hr = new HResult(HttpServletResponse.SC_BAD_REQUEST, "bad request", ex);
		else if (ex instanceof NoHandlerFoundException)
			hr = new HResult(HttpServletResponse.SC_NOT_FOUND, "can not find uri:" + request.getRequestURI());
		else if (ex instanceof AsyncRequestTimeoutException)
			hr = new HResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "server timeout", ex);
		else
			hr = new HResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "unknow server error", ex);

		HttpRequest req = null;
		try {
			req = Util.getResponseRequest(request, hr);
			defLogger.log(req);
		} catch (Throwable e) {
			AppContext.logger.error(e, e);
		} finally {
			if (req != null)
				Util.recycleRequest(req);
		}
		return hr;
	}

}
