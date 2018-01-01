package zr.mybatis.util;

import java.lang.reflect.Field;

import zr.mybatis.unit.MapperInfo;
import zr.mybatis.unit.sql.SqlCondition;
import zr.mybatis.unit.sql.SqlCriteria;
import zr.mybatis.unit.sql.SqlUpdate;

public class MybatisXmlWriter {
	private static final String SQL = SqlCriteria.class.getName();

	public static final String writeMapperXml(String mapperName, MapperInfo info) {
		StringBuilder sb = new StringBuilder(1024 * 8);
		writeXmlHeader(mapperName, sb);
		writeTableSql(info, sb);
		writeSelectSql(sb);
		writeWhereSql(sb);
		writeInsertStatement(info, sb);
		writeSelectStatement(info, sb);
		writeSelectMapStatement(info, sb);
		writeCountStatement(sb);
		writeUpdateStatement(info, sb);
		writeDeleteStatement(info, sb);
		writeXmlFooter(sb);
		return sb.toString();
	}

	private static final void writeXmlHeader(String mapper, StringBuilder sb) {
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append(
				"<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
		sb.append("<mapper namespace=\"").append(mapper).append("\">\n");
	}

	private static final void writeXmlFooter(StringBuilder sb) {
		sb.append("</mapper>");
	}

	private static final void writeTableSql(MapperInfo info, StringBuilder sb) {
		sb.append("<sql id=\"_TABLE\">").append(info.getTable()).append("</sql>\n");
	}

	private static final void writeSelectSql(StringBuilder sb) {
		sb.append("<sql id=\"_SELECT\">\n");
		sb.append("<choose>\n");
		sb.append("<when test=\"selectValid\">${select}</when>\n");
		sb.append("<otherwise>*</otherwise>\n");
		sb.append("</choose>\n");
		sb.append("</sql>\n");
	}

	private static final void writeWhereSql(StringBuilder sb) {
		sb.append("<sql id=\"_WHERE\">\n");
		sb.append("<if test=\"wheresValid\">\n");
		sb.append("WHERE\n");
		sb.append("<foreach collection=\"wheres\" item=\"e\" open=\"(\" separator=\")OR(\" close=\")\">\n");
		sb.append("<foreach collection=\"e.conditions\" item=\"c\" separator=\"AND\">\n");
		sb.append("<choose>\n");

		sb.append("<when test=\"c.type==" + SqlCondition.TYPE_SINGLE + "\">\n");
		sb.append("${c.condition}#{c.value1}\n");
		sb.append("</when>\n");

		sb.append("<when test=\"c.type==" + SqlCondition.TYPE_BETWEEN + "\">\n");
		sb.append("${c.condition}#{c.value1} AND #{c.value2}\n");
		sb.append("</when>\n");

		sb.append("<when test=\"c.type==" + SqlCondition.TYPE_LIST + "\">${c.condition}\n");
		sb.append(
				"<foreach collection=\"c.value1\" item=\"i\" open=\"(\" separator=\",\" close=\")\">#{i}</foreach>\n");
		sb.append("</when>\n");

		sb.append("<when test=\"c.type==" + SqlCondition.TYPE_SPECIAL + "\">\n");
		sb.append("${c.condition}\n");
		sb.append("</when>\n");
		sb.append("</choose>\n");

		sb.append("</foreach>\n");
		sb.append("</foreach>\n");
		sb.append("</if>\n");

		sb.append("</sql>\n");
	}

	private static final void writeInsertStatement(MapperInfo info, StringBuilder sb) {
		sb.append("<insert id=\"insert\" parameterType=\"map\"");
		Field incField = info.getIncField();
		if (incField != null)
			sb.append(" useGeneratedKeys=\"true\" keyProperty=\"").append(incField.getName()).append('"');
		sb.append(">\n");
		sb.append("INSERT INTO <include refid=\"_TABLE\" />\n");
		sb.append(
				"<foreach collection=\"_parameter\" index=\"key\"  open=\"(\" separator=\",\" close=\")\">${key}</foreach>");
		sb.append("VALUES\n");
		sb.append(
				"<foreach collection=\"_parameter\" item=\"value\" open=\"(\" separator=\",\" close=\")\">#{value}</foreach>");
		sb.append("</insert>\n");
	}

	private static final void writeSelectStatement(MapperInfo info, StringBuilder sb) {
		sb.append("<select id=\"selectObj\" parameterType=\"").append(SQL).append("\" resultType=\"")
				.append(info.getEntityClz().getName()).append("\">\n");
		sb.append("SELECT\n");
		sb.append("<include refid=\"_SELECT\" />\n");
		sb.append("FROM\n");
		sb.append("<include refid=\"_TABLE\" />\n");
		sb.append("<include refid=\"_WHERE\" />\n");
		sb.append("<if test=\"orderByValid\">${orderBy}</if>\n");
		sb.append("<if test=\"limitValid\">${limit}</if>\n");
		sb.append("<if test=\"tailValid\">${tailSql}</if>\n");
		sb.append("</select>\n");
	}

	private static final void writeSelectMapStatement(MapperInfo info, StringBuilder sb) {
		sb.append("<select id=\"selectMap\" parameterType=\"").append(SQL).append("\" resultType=\"map\">\n");
		sb.append("SELECT\n");
		sb.append("<include refid=\"_SELECT\" />\n");
		sb.append("FROM\n");
		sb.append("<include refid=\"_TABLE\" />\n");
		sb.append("<include refid=\"_WHERE\" />\n");
		sb.append("<if test=\"orderByValid\">${orderBy}</if>\n");
		sb.append("<if test=\"limitValid\">${limit}</if>\n");
		sb.append("<if test=\"tailValid\">${tailSql}</if>\n");
		sb.append("</select>\n");
	}

	private static final void writeCountStatement(StringBuilder sb) {
		sb.append("<select id=\"count\" parameterType=\"").append(SQL).append("\" resultType=\"int\">\n");
		sb.append("SELECT COUNT(*) AS sum FROM\n");
		sb.append("<include refid=\"_TABLE\" />\n");
		sb.append("<include refid=\"_WHERE\" />\n");
		sb.append("<if test=\"tailValid\">${tailSql}</if>\n");
		sb.append("</select>\n");
	}

	private static final void writeUpdateStatement(MapperInfo info, StringBuilder sb) {
		sb.append("<update id=\"update\" parameterType=\"").append(SqlCriteria.class.getName()).append("\">\n");
		sb.append("UPDATE\n");
		sb.append("<include refid=\"_TABLE\" />\n");
		sb.append("SET\n");
		sb.append("<foreach collection=\"updates\" item=\"i\" separator=\",\">\n");
		sb.append("<choose>\n");
		sb.append("<when test=\"i.type==").append(SqlUpdate.TYPE_RAW).append("\">${i.key}</when>\n");
		sb.append("<when test=\"i.type==").append(SqlUpdate.TYPE_SET).append("\">${i.key}#{i.value}</when>\n");
		sb.append("<when test=\"i.type==").append(SqlUpdate.TYPE_INC)
				.append("\">${i.key}=${i.key}+(#{i.value})</when>\n");
		sb.append("<when test=\"i.type==").append(SqlUpdate.TYPE_MAX)
				.append("\">${i.key}=IF(${i.key}&gt;#{i.value},${i.key},#{i.value})</when>\n");
		sb.append("<when test=\"i.type==").append(SqlUpdate.TYPE_MIN)
				.append("\">${i.key}=IF(${i.key}&lt;#{i.value},${i.key},#{i.value})</when>\n");
		sb.append("<when test=\"i.type==").append(SqlUpdate.TYPE_APPEND)
				.append("\">${i.key}=CONCAT(${i.key},#{i.value})</when>\n");
		sb.append("</choose>\n");
		sb.append("</foreach>\n");
		sb.append("<include refid=\"_WHERE\" />\n");
		sb.append("<if test=\"tailValid\">${tailSql}</if>\n");
		sb.append("</update>\n");
	}

	private static final void writeDeleteStatement(MapperInfo info, StringBuilder sb) {
		sb.append("<delete id=\"delete\" parameterType=\"").append(SqlCriteria.class.getName()).append("\">\n");
		sb.append("DELETE FROM\n");
		sb.append("<include refid=\"_TABLE\" />\n");
		sb.append("<include refid=\"_WHERE\" />\n");
		sb.append("<if test=\"tailValid\">${tailSql}</if>\n");
		sb.append("</delete>\n");
	}

}
