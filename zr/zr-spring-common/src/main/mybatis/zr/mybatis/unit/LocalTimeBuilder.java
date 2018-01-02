package zr.mybatis.unit;

import java.time.LocalTime;

public class LocalTimeBuilder extends DateBuilder {

	public LocalTimeBuilder(String name) {
		super(name);
	}

	@Override
	public Object getDate(long time) {
		return LocalTime.now();
	}

}
