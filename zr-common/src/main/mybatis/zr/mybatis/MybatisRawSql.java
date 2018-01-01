package zr.mybatis;

import java.util.List;

public class MybatisRawSql {
	protected List<SqlItem> sqls;

	static class SqlItem implements Cloneable {
		protected int type;
		protected String key;
		protected Object value;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

	}
}
