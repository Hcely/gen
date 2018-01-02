package zr.mybatis.unit;

import java.sql.Timestamp;

public class TimestampDateBuilder extends DateBuilder {

	public TimestampDateBuilder(String name) {
		super(name);
	}

	@Override
	public Object getDate(long time) {
		return new Timestamp(time);
	}

}
