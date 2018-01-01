package zr.aop.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import zr.aop.AopLogger;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface LoggerConfig {
	public Class<? extends AopLogger> value() default AopLogger.class;

	public boolean defLogger() default true;
}
