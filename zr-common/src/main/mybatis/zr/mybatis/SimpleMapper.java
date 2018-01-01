package zr.mybatis;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.cursor.Cursor;
import org.mybatis.spring.SqlSessionTemplate;

import zr.mybatis.unit.MapperInfo;
import zr.mybatis.unit.Page;
import zr.mybatis.unit.sql.SqlCriteria;
import zr.mybatis.util.MybatisUtil;

public final class SimpleMapper<T> {
	private final MapperInfo mapperInfo;
	private final SqlSessionTemplate template;
	private final Field[] fields;
	private final Field incField;
	private final Field primaryField;
	private final String primaryKey;
	private final String createTimeCol;
	private final String modifyTimeCol;
	private final boolean ignoreEmpty;

	private final String insertStatement;
	private final String selectStatement;
	private final String selectMapStatement;
	private final String countStatement;
	private final String updateStatement;
	private final String deleteStatement;

	SimpleMapper(String mapperName, MapperInfo info) {
		this.mapperInfo = info;
		this.template = info.getTemplate();
		this.fields = info.getFields();
		this.incField = info.getIncField();
		this.primaryField = info.getPrimaryField();
		this.primaryKey = primaryField.getName();
		this.createTimeCol = info.getCreateTimeCol();
		this.modifyTimeCol = info.getModifyTimeCol();
		this.ignoreEmpty = info.isIgnoreEmpty();

		this.insertStatement = mapperName + ".insert";
		this.selectStatement = mapperName + ".selectObj";
		this.selectMapStatement = mapperName + ".selectMap";
		this.countStatement = mapperName + ".count";
		this.updateStatement = mapperName + ".update";
		this.deleteStatement = mapperName + ".delete";
	}

	public MapperInfo getMapperInfo() {
		return mapperInfo;
	}

	public SqlSessionTemplate getTemplate() {
		return template;
	}

	public int insert(T e) {
		Map<String, Object> map = new LinkedHashMap<>();
		for (Field f : fields) {
			try {
				Object value = f.get(e);
				if (value == null)
					continue;
				map.put(f.getName(), value);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		long time = System.currentTimeMillis();
		if (createTimeCol != null && !map.containsKey(createTimeCol))
			map.put(createTimeCol, time);
		if (modifyTimeCol != null && !map.containsKey(modifyTimeCol))
			map.put(modifyTimeCol, time);

		int hr = template.insert(insertStatement, map);
		if (incField != null) {
			Number value = (Number) map.get(incField.getName());
			if (value != null)
				try {
					incField.set(e, MybatisUtil.getNumber(value, incField.getType()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		}
		return hr;
	}

	public void batchInsert(Collection<T> list) {
		if (list == null || list.isEmpty())
			return;
		for (T e : list)
			insert(e);
	}

	public T selectById(Object value) {
		return template.selectOne(selectStatement, new SqlCriteria().eq(primaryKey, value).limit(1));
	}

	public List<T> selectByIds(Collection<?> value) {
		return selectListBy(primaryKey, value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <K> Map<K, T> selectByIdsAsMap(Collection<K> value) {
		Map map = new HashMap();
		if (!value.isEmpty()) {
			Cursor<T> curtor = template.selectCursor(selectStatement, new SqlCriteria().in(primaryKey, value));
			for (Iterator<T> it = curtor.iterator(); it.hasNext();) {
				T e = it.next();
				map.put(getPrimaryValue(e), e);
			}
		}
		return map;
	}

	public T selectBy(String key, Object value) {
		return selectOne(new SqlCriteria().eq(key, value));
	}

	public List<T> selectListBy(String key, Object value) {
		return selectList(new SqlCriteria().eq(key, value));
	}

	public List<T> selectListBy(String key, Collection<?> ids) {
		return selectList(new SqlCriteria().in(key, ids));
	}

	public T selectOne(SqlCriteria criteria) {
		if (primaryKey != null)
			criteria.desc(primaryKey);
		criteria.limit(1);
		return template.selectOne(selectStatement, criteria);
	}

	public Page<T> selectPage(SqlCriteria criteria, int offset, int count) {
		int sum = count(criteria);
		List<T> list;
		if (offset < 0 || offset >= sum)
			list = new LinkedList<>();
		else
			list = selectList(criteria.limit(offset, count));
		return new Page<>(list, sum);
	}

	public List<T> selectList(SqlCriteria criteria) {
		return template.selectList(selectStatement, criteria);
	}

	public Map<String, Object> selectMapOne(SqlCriteria criteria) {
		criteria.limit(1);
		return template.selectOne(selectMapStatement, criteria);
	}

	public List<Map<String, Object>> selectMapList(SqlCriteria criteria) {
		return template.selectList(selectMapStatement, criteria);
	}

	public int update(SqlCriteria criteria) {
		if (criteria.isUpdatesValid()) {
			if (modifyTimeCol != null)
				criteria.update(modifyTimeCol, System.currentTimeMillis());
			return template.update(updateStatement, criteria);
		}
		return 0;
	}

	public int updateById(SqlCriteria criteria, Object id) {
		return update(criteria.eq(primaryKey, id));
	}

	public int updateObj(final T update) {
		return updateObj(update, ignoreEmpty);
	}

	public int updateObj(final T update, boolean ignoreEmpty) {
		SqlCriteria criteria = new SqlCriteria();
		Object v;
		for (Field f : fields)
			try {
				if (f == primaryField)
					continue;
				if ((v = f.get(update)) == null)
					continue;
				if (ignoreEmpty && (v instanceof String))
					if (((String) v).isEmpty())
						continue;
				criteria.update(f.getName(), v);
			} catch (Throwable e) {
			}
		criteria.eq(primaryKey, getPrimaryValue(update));
		return update(criteria);
	}

	public int updateObj(final T update, SqlCriteria criteria) {
		return updateObj(update, criteria, ignoreEmpty);
	}

	public int updateObj(final T update, SqlCriteria criteria, boolean ignoreEmpty) {
		setUpdate(criteria, update);
		return update(criteria);
	}

	public int deleteById(Object value) {
		return deleteBy(primaryKey, value);
	}

	public int deleteBy(String key, Object value) {
		return template.delete(deleteStatement, new SqlCriteria().eq(key, value));
	}

	public int delete(SqlCriteria criteria) {
		return template.delete(deleteStatement, criteria);
	}

	public SqlCriteria setUpdate(SqlCriteria criteria, T update) {
		if (criteria == null)
			criteria = new SqlCriteria();
		if (update == null)
			return criteria;
		Object v;
		for (Field f : fields)
			try {
				if ((v = f.get(update)) == null)
					continue;
				if (ignoreEmpty && (v instanceof String))
					if (((String) v).isEmpty())
						continue;
				criteria.update(f.getName(), v);
			} catch (Throwable e) {
			}
		return criteria;
	}

	public SqlCriteria setCondition(SqlCriteria criteria, T condition) {
		if (criteria == null)
			criteria = new SqlCriteria();
		if (condition == null)
			return criteria;
		Object v;
		for (Field f : fields)
			try {
				if ((v = f.get(condition)) != null)
					criteria.eq(f.getName(), v);
			} catch (Throwable e) {
			}
		return criteria;
	}

	public int count(SqlCriteria criteria) {
		return template.selectOne(countStatement, criteria);
	}

	public Object getPrimaryValue(T obj) {
		try {
			return primaryField.get(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
