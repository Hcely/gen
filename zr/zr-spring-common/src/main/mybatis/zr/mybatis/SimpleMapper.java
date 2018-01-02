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

import zr.mybatis.unit.DateBuilder;
import zr.mybatis.unit.EntityInfo;
import zr.mybatis.unit.MapperInfo;
import zr.mybatis.unit.Page;
import zr.mybatis.unit.sql.SqlCriteria;
import zr.mybatis.util.MybatisUtil;

public final class SimpleMapper<T> {
	private final MapperInfo mapperInfo;
	private final EntityInfo entityInfo;

	private final String insertStatement;
	private final String selectStatement;
	private final String selectMapStatement;
	private final String countStatement;
	private final String updateStatement;
	private final String deleteStatement;

	SimpleMapper(String mapperName, MapperInfo info) {
		this.mapperInfo = info;
		this.entityInfo = info.getEntityInfo();

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
		return mapperInfo.getTemplate();
	}

	public int insert(T e) {
		Map<String, Object> map = objToMap(e);
		int hr = mapperInfo.getTemplate().insert(insertStatement, map);
		if (hr > 0)
			setIncField(map, e);
		return hr;
	}

	public void batchInsert(Collection<T> list) {
		if (list == null || list.isEmpty())
			return;
		for (T e : list)
			insert(e);
	}

	public T selectById(Object value) {
		return mapperInfo.getTemplate().selectOne(selectStatement,
				new SqlCriteria().eq(entityInfo.getPrimaryKey(), value).limit(1));
	}

	public List<T> selectByIds(Collection<?> value) {
		return selectListBy(entityInfo.getPrimaryKey(), value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <K> Map<K, T> selectByIdsAsMap(Collection<K> value) {
		Map map = new HashMap();
		if (value.isEmpty())
			return map;
		Cursor<T> curtor = mapperInfo.getTemplate().selectCursor(selectStatement,
				new SqlCriteria().in(entityInfo.getPrimaryKey(), value));
		for (Iterator<T> it = curtor.iterator(); it.hasNext();) {
			T e = it.next();
			map.put(getPrimaryValue(e), e);
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
		if (entityInfo.getPrimaryKey() != null)
			criteria.desc(entityInfo.getPrimaryKey());
		criteria.limit(1);
		return mapperInfo.getTemplate().selectOne(selectStatement, criteria);
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
		return mapperInfo.getTemplate().selectList(selectStatement, criteria);
	}

	public Map<String, Object> selectMapOne(SqlCriteria criteria) {
		criteria.limit(1);
		return mapperInfo.getTemplate().selectOne(selectMapStatement, criteria);
	}

	public List<Map<String, Object>> selectMapList(SqlCriteria criteria) {
		return mapperInfo.getTemplate().selectList(selectMapStatement, criteria);
	}

	public int update(SqlCriteria criteria) {
		if (criteria.isUpdatesValid()) {
			updateModifyTime(criteria);
			return mapperInfo.getTemplate().update(updateStatement, criteria);
		}
		return 0;
	}

	public int updateById(SqlCriteria criteria, Object id) {
		return update(criteria.eq(entityInfo.getPrimaryKey(), id));
	}

	public int updateObj(final T update) {
		return updateObj(update, mapperInfo.isIgnoreEmpty());
	}

	public int updateObj(final T update, boolean ignoreEmpty) {
		SqlCriteria criteria = setUpdate(null, update, entityInfo.getPrimaryField(), ignoreEmpty);
		criteria.eq(entityInfo.getPrimaryKey(), getPrimaryValue(update));
		return update(criteria);
	}

	public int updateObj(final T update, SqlCriteria criteria) {
		return updateObj(update, criteria, mapperInfo.isIgnoreEmpty());
	}

	public int updateObj(final T update, SqlCriteria criteria, boolean ignoreEmpty) {
		setUpdate(criteria, update);
		return update(criteria);
	}

	public int deleteById(Object value) {
		return deleteBy(entityInfo.getPrimaryKey(), value);
	}

	public int deleteBy(String key, Object value) {
		return mapperInfo.getTemplate().delete(deleteStatement, new SqlCriteria().eq(key, value));
	}

	public int delete(SqlCriteria criteria) {
		return mapperInfo.getTemplate().delete(deleteStatement, criteria);
	}

	public SqlCriteria setUpdate(SqlCriteria criteria, T update) {
		return setUpdate(criteria, update, null, mapperInfo.isIgnoreEmpty());
	}

	private SqlCriteria setUpdate(SqlCriteria criteria, T update, Field ignoreField, boolean ignoreEmpty) {
		if (criteria == null)
			criteria = new SqlCriteria();
		if (update == null)
			return criteria;
		Object v;
		for (Field f : entityInfo.getFields())
			try {
				if (f == ignoreField)
					continue;
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
		for (Field f : entityInfo.getFields())
			try {
				if ((v = f.get(condition)) != null)
					criteria.eq(f.getName(), v);
			} catch (Throwable e) {
			}
		return criteria;
	}

	public int count(SqlCriteria criteria) {
		return mapperInfo.getTemplate().selectOne(countStatement, criteria);
	}

	private void setIncField(Map<String, Object> map, T obj) {
		Field incField = entityInfo.getIncField();
		if (incField == null)
			return;
		Number value = (Number) map.get(incField.getName());
		if (value == null)
			return;
		try {
			incField.set(obj, MybatisUtil.getNumber(value, incField.getType()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateModifyTime(SqlCriteria criteria) {
		DateBuilder dateBuilder = entityInfo.getModifyTimeBuilder();
		if (dateBuilder == null)
			return;
		if (criteria.containUpdate(dateBuilder.getName()))
			return;
		long time = System.currentTimeMillis();
		criteria.update(dateBuilder.getName(), dateBuilder.getDate(time));
	}

	private Map<String, Object> objToMap(T obj) {
		Map<String, Object> map = new LinkedHashMap<>();
		for (Field f : entityInfo.getFields()) {
			try {
				Object value = f.get(obj);
				if (value == null)
					continue;
				map.put(f.getName(), value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		long time = System.currentTimeMillis();
		
		DateBuilder dateBuilder = entityInfo.getCreateTimeBuilder();
		if (dateBuilder != null && !map.containsKey(dateBuilder.getName()))
			map.put(dateBuilder.getName(), dateBuilder.getDate(time));
		dateBuilder = entityInfo.getModifyTimeBuilder();
		if (dateBuilder != null && !map.containsKey(dateBuilder.getName()))
			map.put(dateBuilder.getName(), dateBuilder.getDate(time));
		return map;
	}

	private Object getPrimaryValue(T obj) {
		Field f = entityInfo.getPrimaryField();
		if (f != null)
			try {
				return f.get(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}

}
