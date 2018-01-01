package zr.mybatis.unit.sql;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import v.common.helper.StrUtil;

public class SqlCriteria {
	private static final String OP_EQ = " = ";
	private static final String OP_NOT_EQ = " <> ";
	private static final String OP_GT = " > ";
	private static final String OP_LT = " < ";
	private static final String OP_GTE = " >= ";
	private static final String OP_LTE = " <= ";
	private static final String OP_IN = " IN ";
	private static final String OP_NOT_IN = " NOT IN ";
	private static final String OP_LIKE = " LIKE ";
	private static final String OP_NOT_LIKE = " NOT LIKE ";
	private static final String OP_IS_NULL = " IS NULL";
	private static final String OP_NOT_NULL = " IS NOT NULL";
	private static final String OP_BETWEEN = " BETWEEN ";
	private static final String OP_NOT_BETWEEN = " NOT BETWEEN";

	private static final List<?> NULL_LIST = Arrays.asList(new Object[] { null });

	protected StringBuilder select;
	protected Map<String, SqlUpdate> updates;
	protected LinkedList<SqlWhere> wheres;
	protected SqlWhere curWhere;
	protected StringBuilder orderBy;
	protected StringBuilder limit;
	protected String tailSql;

	public SqlCriteria() {
	}

	public SqlCriteria select(String... cols) {
		if (select == null)
			select = new StringBuilder(cols.length * 16);
		else if (select.length() > 0)
			select.append(',');
		for (int i = 0, len = cols.length; i < len; ++i) {
			if (i > 0)
				select.append(',');
			select.append('`').append(cols[i]).append('`');
		}
		return this;
	}

	public SqlCriteria select(String col) {
		if (select == null)
			select = new StringBuilder(32);
		else if (select.length() > 0)
			select.append(',');
		select.append('`').append(col).append('`');
		return this;
	}

	public SqlCriteria selectRaw(String col) {
		if (select == null)
			select = new StringBuilder(32);
		else if (select.length() > 0)
			select.append(',');
		select.append(col);
		return this;
	}

	public SqlCriteria setSelectSql(String sql) {
		if (select == null)
			select = new StringBuilder(sql.length());
		else
			select.setLength(0);
		select.append(sql);
		return this;
	}

	public SqlCriteria clearSelect() {
		if (select != null)
			select.setLength(0);
		return this;
	}

	private SqlCriteria update(String key, String field, Object value, byte type) {
		if (updates == null)
			updates = new LinkedHashMap<>();
		updates.put(key, new SqlUpdate(field, value, type));
		return this;
	}

	public boolean containUpdate(String key) {
		return updates.containsKey(key);
	}

	public SqlCriteria update(String key, Object value) {
		return update(key, value, false);
	}

	public SqlCriteria update(String key, Object value, boolean ignoreNull) {
		if (value == null && ignoreNull)
			return this;
		StringBuilder sb = new StringBuilder(key.length() + 3);
		sb.append('`').append(key).append("`=");
		return update(key, StrUtil.sbToString(sb), value, SqlUpdate.TYPE_SET);
	}

	public SqlCriteria updateInc(String key, Number inc) {
		StringBuilder sb = new StringBuilder(key.length() + 2);
		sb.append('`').append(key).append("`");
		return update(key, sb.toString(), inc, SqlUpdate.TYPE_INC);
	}

	public SqlCriteria updateMax(String key, Number num) {
		StringBuilder sb = new StringBuilder(key.length() + 2);
		sb.append('`').append(key).append("`");
		return update(key, sb.toString(), num, SqlUpdate.TYPE_MAX);
	}

	public SqlCriteria updateMin(String key, Number num) {
		StringBuilder sb = new StringBuilder(key.length() + 2);
		sb.append('`').append(key).append("`");
		return update(key, sb.toString(), num, SqlUpdate.TYPE_MIN);
	}

	public SqlCriteria updateAppend(String key, CharSequence str) {
		StringBuilder sb = new StringBuilder(key.length() + 2);
		sb.append('`').append(key).append("`");
		return update(key, sb.toString(), str, SqlUpdate.TYPE_APPEND);
	}

	public SqlCriteria updateRaw(String key, String sql) {
		return update(key, sql, null, SqlUpdate.TYPE_RAW);
	}

	public SqlCriteria clearUpdates() {
		if (updates != null)
			updates.clear();
		return this;
	}

	public SqlCriteria eq(String key, Object value) {
		return eq(key, value, false);
	}

	public SqlCriteria eq(String key, Object value, boolean ignoreNull) {
		return addCondition(key, OP_EQ, value, ignoreNull);
	}

	public SqlCriteria eqNull(String key, Object value) {
		if (value == null)
			return isNull(key);
		return eq(key, value, false);
	}

	public SqlCriteria notEq(String key, Object value) {
		return notEq(key, value, false);
	}

	public SqlCriteria notEq(String key, Object value, boolean ignoreNull) {
		return addCondition(key, OP_NOT_EQ, value, ignoreNull);
	}

	public SqlCriteria gt(String key, Object value) {
		return gt(key, value, false);
	}

	public SqlCriteria gt(String key, Object value, boolean ignoreNull) {
		return addCondition(key, OP_GT, value, ignoreNull);
	}

	public SqlCriteria lt(String key, Object value) {
		return lt(key, value, false);
	}

	public SqlCriteria lt(String key, Object value, boolean ignoreNull) {
		return addCondition(key, OP_LT, value, ignoreNull);
	}

	public SqlCriteria gte(String key, Object value) {
		return gte(key, value, false);
	}

	public SqlCriteria gte(String key, Object value, boolean ignoreNull) {
		return addCondition(key, OP_GTE, value, ignoreNull);
	}

	public SqlCriteria lte(String key, Object value) {
		return lte(key, value, false);
	}

	public SqlCriteria lte(String key, Object value, boolean ignoreNull) {
		return addCondition(key, OP_LTE, value, ignoreNull);
	}

	public SqlCriteria like(String key, String value) {
		return addCondition(key, OP_LIKE, value, true);
	}

	public SqlCriteria notLike(String key, String value) {
		return addCondition(key, OP_NOT_LIKE, value, true);
	}

	public SqlCriteria isNull(String key) {
		return addCondition(key, OP_IS_NULL);
	}

	public SqlCriteria notNull(String key) {
		return addCondition(key, OP_NOT_NULL);
	}

	public SqlCriteria in(String key, Collection<?> values) {
		return in(key, values, false);
	}

	public SqlCriteria in(String key, Collection<?> values, boolean ignoreNull) {
		return addConditionList(key, OP_IN, values, ignoreNull);
	}

	public SqlCriteria notIn(String key, Collection<?> values) {
		return notIn(key, values, false);
	}

	public SqlCriteria notIn(String key, Collection<?> values, boolean ignoreNull) {
		return addConditionList(key, OP_NOT_IN, values, ignoreNull);
	}

	public SqlCriteria between(String key, Object value1, Object value2) {
		return between(key, value1, value2, false);
	}

	public SqlCriteria between(String key, Object value1, Object value2, boolean ignoreNull) {
		return addCondition(key, OP_BETWEEN, value1, value2, ignoreNull);
	}

	public SqlCriteria notBetween(String key, Object value1, Object value2) {
		return notBetween(key, value1, value2, false);
	}

	public SqlCriteria notBetween(String key, Object value1, Object value2, boolean ignoreNull) {
		return addCondition(key, OP_NOT_BETWEEN, value1, value2, ignoreNull);
	}

	public SqlCriteria or() {
		if (curWhere == null)
			return this;
		if (curWhere.isEmpty())
			return this;
		curWhere = null;
		return this;
	}

	public SqlCriteria clearWhere() {
		if (wheres != null)
			wheres.clear();
		if (curWhere != null)
			curWhere.clear();
		return this;
	}

	private SqlCriteria addCondition(String key, String op, Object value, boolean ignoreNull) {
		if (value == null && ignoreNull)
			return this;
		StringBuilder sb = new StringBuilder(key.length() + 2 + op.length());
		sb.append('`').append(key).append('`').append(op);
		return addCondition(StrUtil.sbToString(sb), value, null, SqlCondition.TYPE_SINGLE);
	}

	private SqlCriteria addCondition(String key, String op, Object value1, Object value2, boolean ignoreNull) {
		if (value1 == null || value2 == null)
			if (ignoreNull)
				return this;
		StringBuilder sb = new StringBuilder(key.length() + 2 + op.length());
		sb.append('`').append(key).append('`').append(op);
		return addCondition(StrUtil.sbToString(sb), value1, value2, SqlCondition.TYPE_BETWEEN);
	}

	private SqlCriteria addCondition(String key, String op) {
		StringBuilder sb = new StringBuilder(key.length() + 2 + op.length());
		sb.append('`').append(key).append('`').append(op);
		return addCondition(StrUtil.sbToString(sb), null, null, SqlCondition.TYPE_SPECIAL);
	}

	private SqlCriteria addConditionList(String key, String op, Collection<?> value, boolean ignoreNull) {
		if (value == null)
			if (ignoreNull)
				return this;
			else
				value = NULL_LIST;
		else if (value.isEmpty())
			value = NULL_LIST;
		StringBuilder sb = new StringBuilder(key.length() + 2 + op.length());
		sb.append('`').append(key).append('`').append(op);
		return addCondition(StrUtil.sbToString(sb), value, null, SqlCondition.TYPE_LIST);
	}

	private SqlCriteria addCondition(String condition, Object value1, Object value2, byte type) {
		if (curWhere == null)
			curWhere = new SqlWhere();
		if (curWhere.isEmpty()) {
			if (wheres == null)
				wheres = new LinkedList<>();
			wheres.add(curWhere);
		}
		curWhere.addCondition(condition, value1, value2, type);
		return this;
	}

	public SqlCriteria asc(String key) {
		return orderBy(key, true);
	}

	public SqlCriteria desc(String key) {
		return orderBy(key, false);
	}

	public SqlCriteria asc(String... keys) {
		for (String e : keys)
			orderBy(e, true);
		return this;
	}

	public SqlCriteria desc(String... keys) {
		for (String e : keys)
			orderBy(e, false);
		return this;
	}

	public SqlCriteria orderBy(String key, boolean asc) {
		if (orderBy == null)
			orderBy = new StringBuilder(32);
		if (orderBy.length() == 0)
			orderBy.append("ORDER BY ");
		else
			orderBy.append(',');
		orderBy.append('`').append(key).append('`');
		if (asc)
			orderBy.append(" ASC");
		else
			orderBy.append(" DESC");
		return this;
	}

	public SqlCriteria clearOrderBy() {
		if (orderBy != null)
			orderBy.setLength(0);
		return this;
	}

	public SqlCriteria limit(int count) {
		return limit(0, count);
	}

	public SqlCriteria limit(int offset, int count) {
		if (limit == null)
			limit = new StringBuilder(16);
		else
			limit.setLength(0);
		limit.append("LIMIT ").append(offset).append(',').append(count);
		return this;
	}

	public SqlCriteria clearLimit() {
		if (limit != null)
			limit.setLength(0);
		return this;
	}

	public SqlCriteria setTailSql(String tailSql) {
		this.tailSql = tailSql;
		return this;
	}

	public SqlCriteria clear() {
		clearSelect();
		clearUpdates();
		clearWhere();
		clearOrderBy();
		clearLimit();
		tailSql = null;
		return this;
	}

	boolean isSelectValid() {
		return select != null && select.length() > 0;
	}

	public boolean isUpdatesValid() {
		return updates != null && updates.size() > 0;
	}

	boolean isWheresValid() {
		return wheres != null && wheres.size() > 0;
	}

	boolean isOrderByValid() {
		return orderBy != null && orderBy.length() > 0;
	}

	boolean isLimitValid() {
		return limit != null && limit.length() > 0;
	}

	boolean isTailValid() {
		return tailSql != null && tailSql.length() > 0;
	}

	StringBuilder getSelect() {
		return select;
	}

	Collection<SqlUpdate> getUpdates() {
		return updates.values();
	}

	LinkedList<SqlWhere> getWheres() {
		return wheres;
	}

	StringBuilder getOrderBy() {
		return orderBy;
	}

	StringBuilder getLimit() {
		return limit;
	}

	String getTailSql() {
		return tailSql;
	}
}
