package zr.mybatis.unit;

import java.sql.Timestamp;

public class TimestampBuilder extends DateBuilder {

	public TimestampBuilder(String name) {
		super(name);
	}

	@Override
	public Object getDate(long time) {
		return new Timestamp(time);
	}

}
