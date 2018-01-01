package zr.mybatis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import zr.mybatis.unit.Page;
import zr.mybatis.unit.sql.SqlCriteria;

public class BaseDaoImpl<T> implements BaseDao<T> {
	protected SimpleMapper<T> mapper;

	@Override
	public int insert(T e) {
		return mapper.insert(e);
	}

	@Override
	public void batchInsert(Collection<T> e) {
		mapper.batchInsert(e);
	}

	@Override
	public int updateById(T update) {
		return mapper.updateObj(update);
	}

	@Override
	public int update(T update, T condition) {
		return mapper.updateObj(update, mapper.setCondition(null, condition));
	}

	@Override
	public T findById(Object id) {
		return mapper.selectById(id);
	}

	@Override
	public T find(T condition) {
		return mapper.selectOne(mapper.setCondition(null, condition));
	}

	@Override
	public List<T> queryAll() {
		return mapper.selectList(new SqlCriteria());
	}

	@Override
	public List<T> queryByIds(Collection<?> ids) {
		return mapper.selectByIds(ids);
	}

	@Override
	public <K> Map<K, T> queryByIdsAsMap(Collection<K> ids) {
		return mapper.selectByIdsAsMap(ids);
	}

	@Override
	public List<T> query(T condition) {
		return mapper.selectList(mapper.setCondition(null, condition));
	}

	@Override
	public Page<T> query(int offset, int count) {
		return mapper.selectPage(new SqlCriteria(), offset, count);
	}

	@Override
	public Page<T> query(T condition, int offset, int count) {
		return mapper.selectPage(mapper.setCondition(null, condition), offset, count);
	}

	@Override
	public int deleteById(Object id) {
		return mapper.deleteById(id);
	}

	@Override
	public int delete(T condition) {
		return mapper.delete(mapper.setCondition(null, condition));
	}

	@Override
	public int count() {
		return mapper.count(new SqlCriteria());
	}

	@Override
	public int count(T condition) {
		return mapper.count(mapper.setCondition(null, condition));
	}

}
