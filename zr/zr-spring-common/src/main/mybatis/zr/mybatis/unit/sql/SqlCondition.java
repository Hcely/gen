package zr.mybatis.unit.sql;

public class SqlCondition {
	public static final byte TYPE_SINGLE = 1;
	public static final byte TYPE_LIST = 2;
	public static final byte TYPE_BETWEEN = 3;
	public static final byte TYPE_SPECIAL = 4;

	private final String condition;
	private final Object value1;
	private final Object value2;
	private final byte type;

	public SqlCondition(String condition, Object value1, Object value2, byte type) {
		this.condition = condition;
		this.value1 = value1;
		this.value2 = value2;
		this.type = type;
	}

	public String getCondition() {
		return condition;
	}

	public Object getValue1() {
		return value1;
	}

	public Object getValue2() {
		return value2;
	}

	public byte getType() {
		return type;
	}
}
