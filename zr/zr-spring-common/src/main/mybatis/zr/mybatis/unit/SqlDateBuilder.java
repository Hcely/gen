package zr.mybatis.unit;

import java.sql.Date;

public class SqlDateBuilder extends DateBuilder {

	public SqlDateBuilder(String name) {
		super(name);
	}

	@Override
	public Object getDate(long time) {
		return new Date(time);
	}
}
