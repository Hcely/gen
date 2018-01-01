package zr.mybatis.unit;

import java.lang.reflect.Field;

import org.mybatis.spring.SqlSessionTemplate;

public final class MapperInfo {
	protected final SqlSessionTemplate template;
	protected final String table;
	protected final EntityInfo entity;
	protected final boolean ignoreEmpty;

	public MapperInfo(SqlSessionTemplate template, String table, EntityInfo entity, boolean ignoreEmpty) {
		this.template = template;
		this.table = table;
		this.entity = entity;
		this.ignoreEmpty = ignoreEmpty;
	}

	public SqlSessionTemplate getTemplate() {
		return template;
	}

	public String getTable() {
		return table;
	}

	public EntityInfo getEntity() {
		return entity;
	}

	public boolean isIgnoreEmpty() {
		return ignoreEmpty;
	}

	public Field[] getFields() {
		return entity.fields;
	}

	public Field getPrimaryField() {
		return entity.primaryField;
	}

	public Field getIncField() {
		return entity.incField;
	}

	public String getCreateTimeCol() {
		return entity.createTimeCol;
	}

	public String getModifyTimeCol() {
		return entity.modifyTimeCol;
	}

	public Object newInstance() {
		return entity.newInstance();
	}

	public Class<?> getEntityClz() {
		return entity.clz;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + (ignoreEmpty ? 1231 : 1237);
		result = prime * result + ((template == null) ? 0 : template.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapperInfo other = (MapperInfo) obj;
		if (entity != other.entity)
			return false;
		if (ignoreEmpty != other.ignoreEmpty)
			return false;
		if (template != other.template)
			return false;
		if (!table.equals(other.table))
			return false;
		return true;
	}

}
