package zr.mybatis;

import zr.mybatis.util.MybatisUtil;

final class DefEntity2TableHandler implements Entity2TableHandler {
	public static final DefEntity2TableHandler INSTANCE = new DefEntity2TableHandler();

	private DefEntity2TableHandler() {
	}

	@Override
	public String handleTable(String entityName) {
		StringBuilder sb = new StringBuilder(entityName.length() + 16);
		if (entityName.endsWith("Bean")) {
			entityName = entityName.substring(0, entityName.length() - 4);
			sb.append("t_");
		} else if (entityName.endsWith("Entity")) {
			entityName = entityName.substring(0, entityName.length() - 6);
			sb.append("t_");
		} else if (entityName.endsWith("View")) {
			entityName = entityName.substring(0, entityName.length() - 4);
			sb.append("v_");
		} else
			sb.append("t_");
		MybatisUtil.hump2Underline(sb, entityName);
		return sb.toString();
	}

}
