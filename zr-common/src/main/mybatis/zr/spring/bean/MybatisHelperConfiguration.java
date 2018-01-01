package zr.spring.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import zr.mybatis.MybatisXmlHelper;

@Configuration
public class MybatisHelperConfiguration {
	@Bean
	public MybatisXmlHelper getObject() {
		return new MybatisXmlHelper();
	}
}
