package zr.aop.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import zr.aop.AopFilter;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Filter {
	public Class<? extends AopFilter> value();

	public String[] authority() default {};

	public String[] except() default {};

	public boolean override() default false;
}
