package zr.mybatis.unit.sql;

import java.util.LinkedList;
import java.util.List;

public final class SqlWhere {
	protected List<SqlCondition> conditions;

	SqlWhere() {
	}

	void addCondition(String condition, Object value1, Object value2, byte type) {
		if (conditions == null)
			conditions = new LinkedList<>();
		conditions.add(new SqlCondition(condition, value1, value2, type));
	}

	void clear() {
		if (conditions != null)
			conditions.clear();
	}

	boolean isEmpty() {
		return conditions == null ? true : conditions.isEmpty();
	}

	public List<SqlCondition> getConditions() {
		return conditions;
	}
}
