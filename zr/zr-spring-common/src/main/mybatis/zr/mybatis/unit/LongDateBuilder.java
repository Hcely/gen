package zr.mybatis.unit;

public class LongDateBuilder extends DateBuilder {

	public LongDateBuilder(String name) {
		super(name);
	}

	@Override
	public Object getDate(long time) {
		return time;
	}
}
