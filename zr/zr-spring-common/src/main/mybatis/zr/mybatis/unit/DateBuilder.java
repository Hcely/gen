package zr.mybatis.unit;

import java.util.Date;

public class DateBuilder {

	protected final String name;

	public DateBuilder(String name) {
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public Object getDate(long time) {
		return new Date(time);
	}

}
