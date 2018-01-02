package zr.mybatis.unit;

import java.time.LocalDateTime;

public class LocalDateTimeBuilder extends DateBuilder {

	public LocalDateTimeBuilder(String name) {
		super(name);
	}

	@Override
	public Object getDate(long time) {
		return LocalDateTime.now();
	}

}
