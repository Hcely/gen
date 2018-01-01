package zr.spring.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import zr.AppContext;

@Configuration
public class AppContextConfiguration {
	@Value("${app.debug:false}")
	protected boolean debug;

	@Bean
	public AppContext getContext() {
		AppContext context = new AppContext();
		context.setDebug(debug);
		return context;
	}
}
