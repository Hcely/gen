package zr.mybatis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import zr.mybatis.unit.Page;

public interface BaseDao<T> {
	public int insert(T e);

	public void batchInsert(Collection<T> e);

	public int updateById(T update);

	public int update(T update, T condition);

	public T findById(Object id);

	public T find(T condition);

	public List<T> queryByIds(Collection<?> ids);

	public <K> Map<K, T> queryByIdsAsMap(Collection<K> ids);

	public List<T> queryAll();

	public List<T> query(T condition);

	public Page<T> query(int offset, int count);

	public Page<T> query(T condition, int offset, int count);

	public int deleteById(Object id);

	public int delete(T condition);

	public int count();

	public int count(T condition);

}
