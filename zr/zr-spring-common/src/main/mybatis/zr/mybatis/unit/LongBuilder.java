package zr.mybatis.unit;

public class LongBuilder extends DateBuilder {

	public LongBuilder(String name) {
		super(name);
	}

	@Override
	public Object getDate(long time) {
		return time;
	}
}
