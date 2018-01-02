package zr.mybatis.unit;

import java.time.LocalDate;

public class LocalDateBuilder extends DateBuilder {

	public LocalDateBuilder(String name) {
		super(name);
	}

	@Override
	public Object getDate(long time) {
		return LocalDate.now();
	}

}
