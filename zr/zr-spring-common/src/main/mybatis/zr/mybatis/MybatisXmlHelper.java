package zr.mybatis;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

import v.Destoryable;
import v.Initializable;
import zr.AppContext;
import zr.mybatis.annotation.MapperConfig;
import zr.mybatis.unit.EntityInfo;
import zr.mybatis.unit.MapperInfo;
import zr.mybatis.util.MybatisUtil;
import zr.mybatis.util.MybatisXmlWriter;
import zr.util.SpringUtil;

@SuppressWarnings("rawtypes")
public class MybatisXmlHelper implements Initializable, Destoryable, ApplicationContextAware {

	protected final Map<MapperInfo, SimpleMapper> mapperMap;
	protected final Map<Class<?>, EntityInfo> entityMap;

	protected ApplicationContext application;
	protected Map<String, SqlSessionTemplate> templateMap;
	protected SqlSessionTemplate defTemplate;
	protected Entity2TableHandler tableNameHandler;

	public MybatisXmlHelper() {
		this.mapperMap = new HashMap<>();
		this.entityMap = new HashMap<>();
		this.tableNameHandler = null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		application = applicationContext;
	}

	@PostConstruct
	public void init() {
		templateMap = SpringUtil.getBeansOfType(application, SqlSessionTemplate.class);
		if (templateMap.isEmpty())
			return;
		defTemplate = getDefTemplate(templateMap);

		if (tableNameHandler == null)
			tableNameHandler = DefEntity2TableHandler.INSTANCE;
		initBaseDaos();
		initMappers();
	}

	@SuppressWarnings("unchecked")
	private void initBaseDaos() {
		Map<String, BaseDaoImpl> daoMap = SpringUtil.getBeansOfType(application, BaseDaoImpl.class);
		for (BaseDaoImpl dao : daoMap.values()) {
			dao = SpringUtil.getRawObj(dao);
			MapperConfig config = dao.getClass().getAnnotation(MapperConfig.class);
			Class<?> entityClz = MybatisUtil.getDaoGenericType(dao.getClass());
			dao.mapper = getMapper(config, entityClz);
		}
	}

	private void initMappers() {
		Map<String, Object> repoMap = SpringUtil.getBeansWithAnnotation(application, Repository.class);
		for (Object repo : repoMap.values()) {
			repo = SpringUtil.getRawObj(repo);
			Field[] fields = MybatisUtil.getFields(repo.getClass());
			for (Field f : fields) {
				if (f.getType() != SimpleMapper.class)
					continue;
				MapperConfig config = f.getAnnotation(MapperConfig.class);
				if (config == null)
					continue;
				Class<?> entityClz = MybatisUtil.getFieldGenericType(f);
				SimpleMapper mapper = getMapper(config, entityClz);
				try {
					f.set(repo, mapper);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@PreDestroy
	public void destory() {
		mapperMap.clear();
		entityMap.clear();
		application = null;
		templateMap = null;
		defTemplate = null;
	}

	private synchronized EntityInfo getEntityInfo(Class<?> clz) {
		EntityInfo info = entityMap.get(clz);
		if (info == null) {
			info = EntityInfo.build(clz);
			entityMap.put(clz, info);
		}
		return info;
	}

	private SimpleMapper getMapper(MapperConfig config, Class<?> entityClz) {
		EntityInfo entity = getEntityInfo(entityClz);
		SqlSessionTemplate template = defTemplate;
		String table = tableNameHandler.handleTable(entity.getClz().getSimpleName());
		boolean ignoreEmpty = false;
		if (config != null) {
			if (!StringUtils.isBlank(config.sqlTemplate()))
				template = templateMap.get(config.sqlTemplate());
			ignoreEmpty = config.ignoreEmpty();
		}
		return getMapper(new MapperInfo(template, table, entity, ignoreEmpty));
	}

	private synchronized SimpleMapper getMapper(MapperInfo mapperInfo) {
		SimpleMapper mapper = mapperMap.get(mapperInfo);
		if (mapper != null)
			return mapper;
		String mapperName = MybatisUtil.getMapperName();
		String xml = MybatisXmlWriter.writeMapperXml(mapperName, mapperInfo);
		if (AppContext.isDebug())
			AppContext.logger.info(xml);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		SqlSessionTemplate template = mapperInfo.getTemplate();
		Configuration configuration = template.getConfiguration();
		XMLMapperBuilder builder = new XMLMapperBuilder(inputStream, configuration, mapperName + ".xml",
				configuration.getSqlFragments(), mapperName);
		builder.parse();
		mapper = new SimpleMapper<>(mapperName, mapperInfo);
		mapperMap.put(mapperInfo, mapper);
		return mapper;
	}

	private static final SqlSessionTemplate getDefTemplate(Map<String, SqlSessionTemplate> templateMap) {
		if (templateMap.size() == 1)
			return templateMap.values().iterator().next();
		SqlSessionTemplate hr = templateMap.get("org.mybatis.spring.SqlSessionTemplate#0");
		if (hr != null)
			return hr;
		hr = templateMap.get("sqlTemplate");
		if (hr != null)
			return hr;
		hr = templateMap.get("sqlTemplate0");
		if (hr != null)
			return hr;
		hr = templateMap.get("sqlTemplate-0");
		if (hr != null)
			return hr;
		hr = templateMap.get("sqlTemplate_0");
		if (hr != null)
			return hr;
		hr = templateMap.get("sqlSessionTemplate");
		if (hr != null)
			return hr;
		hr = templateMap.get("sqlSessionTemplate0");
		if (hr != null)
			return hr;
		hr = templateMap.get("sqlSessionTemplate-0");
		if (hr != null)
			return hr;
		hr = templateMap.get("sqlSessionTemplate_0");
		if (hr != null)
			return hr;
		return templateMap.values().iterator().next();
	}
}
