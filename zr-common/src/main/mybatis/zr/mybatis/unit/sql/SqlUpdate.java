package zr.mybatis.unit.sql;

public class SqlUpdate {
	public static final byte TYPE_RAW = 0;
	public static final byte TYPE_SET = 1;
	public static final byte TYPE_INC = 2;
	public static final byte TYPE_MAX = 3;
	public static final byte TYPE_MIN = 4;
	public static final byte TYPE_APPEND = 5;

	private final String key;
	private final Object value;
	private final byte type;

	public SqlUpdate(String key, Object value, byte type) {
		this.key = key;
		this.value = value;
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public byte getType() {
		return type;
	}

}
