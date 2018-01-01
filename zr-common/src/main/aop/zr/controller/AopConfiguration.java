package zr.controller;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import zr.aop.ControllerInterceptor;
import zr.aop.unit.HeaderJacksonMessageConverter;
import zr.aop.unit.SimpleMultipartResolver;

@Configuration
public class AopConfiguration {

	@Bean
	public ControllerInterceptor getInterceptor() {
		return new ControllerInterceptor();
	}

	@Bean
	public RequestMappingHandlerAdapter getConverter() {
		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
		HeaderJacksonMessageConverter converter = new HeaderJacksonMessageConverter();
		adapter.setMessageConverters(Collections.singletonList(converter));
		adapter.setOrder(0);
		return adapter;
	}

	@Bean
	public SimpleMultipartResolver getResolver() {
		return new SimpleMultipartResolver();
	}
}
