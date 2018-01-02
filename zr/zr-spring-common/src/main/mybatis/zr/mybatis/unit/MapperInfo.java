package zr.mybatis.unit;

import org.mybatis.spring.SqlSessionTemplate;

public final class MapperInfo {
	protected final SqlSessionTemplate template;
	protected final String table;
	protected final EntityInfo entityInfo;
	protected final boolean ignoreEmpty;

	public MapperInfo(SqlSessionTemplate template, String table, EntityInfo entity, boolean ignoreEmpty) {
		this.template = template;
		this.table = table;
		this.entityInfo = entity;
		this.ignoreEmpty = ignoreEmpty;
	}

	public SqlSessionTemplate getTemplate() {
		return template;
	}

	public String getTable() {
		return table;
	}

	public EntityInfo getEntityInfo() {
		return entityInfo;
	}

	public boolean isIgnoreEmpty() {
		return ignoreEmpty;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entityInfo == null) ? 0 : entityInfo.hashCode());
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
		if (entityInfo != other.entityInfo)
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
