package com.mysql.genenrator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.mysql.genenrator.handler.DefEntityNameHandler;
import com.mysql.genenrator.handler.DefFieldTypeHandler;
import com.mysql.genenrator.handler.EntityNameHandler;
import com.mysql.genenrator.handler.FieldTypeHandler;
import com.mysql.genenrator.unit.ColumnInfo;
import com.mysql.genenrator.unit.TableInfo;
import com.mysql.genenrator.util.GenenratorUtil;

import zr.entity.annotation.AutoIncrement;
import zr.entity.annotation.PrimaryKey;

public class EntityWriter {
	protected EntityNameHandler entityHandler;
	protected FieldTypeHandler fieldTypeHandler;

	public EntityWriter(String tail) {
		DefEntityNameHandler entityHandler = new DefEntityNameHandler();
		entityHandler.setTail(tail);
		this.entityHandler = entityHandler;
		DefFieldTypeHandler fieldTypeHandler = new DefFieldTypeHandler();
		this.fieldTypeHandler = fieldTypeHandler;
	}

	public EntityNameHandler getEntityHandler() {
		return entityHandler;
	}

	public void setEntityHandler(EntityNameHandler entityHandler) {
		this.entityHandler = entityHandler;
	}

	public FieldTypeHandler getFieldTypeHandler() {
		return fieldTypeHandler;
	}

	public void setFieldTypeHandler(FieldTypeHandler fieldTypeHandler) {
		this.fieldTypeHandler = fieldTypeHandler;
	}

	public void write(String outputFolder, String packageName, TableInfo info) throws IOException {
		String folderPath = outputFolder + "/" + packageName.replace('.', '/');
		File folder = new File(folderPath);
		if (!folder.exists())
			folder.mkdirs();
		String entityName = entityHandler.handle(info.getTableName());

		File entityFile = new File(folder, entityName + ".java");
		Set<String> importSet = new HashSet<>();
		List<ColumnInfo> cols = info.getColumns();
		getImports(cols, importSet);
		List<String> imports = new LinkedList<>(importSet);

		PrintWriter writer = new PrintWriter(new FileOutputStream(entityFile));

		// write package
		writer.println("package " + packageName + ";");
		writer.println();

		// write imports
		writeImports(imports, writer);

		// write class start
		writer.println("public class " + entityName + " implements Cloneable, Serializable {");

		// write fields
		writeFields(cols, writer);

		// write construct clone
		writeConstructAndClone(entityName, writer);

		// write get set method
		for (ColumnInfo e : cols)
			writeGetSet(entityName, e, writer);

		// write end
		writer.println('}');
		writer.flush();
		writer.close();
	}

	private void writeImports(List<String> imports, PrintWriter writer) {
		Collections.sort(imports);
		for (String e : imports)
			writer.println("import " + e + ";");
		writer.println();
	}

	private void writeFields(List<ColumnInfo> cols, PrintWriter writer) {
		writer.println("\tprivate static final long serialVersionUID = 1L;");
		writer.println();

		for (ColumnInfo e : cols) {
			if (e.isAutoInc())
				writer.println("\t@" + AutoIncrement.class.getSimpleName());
			if (e.isPri())
				writer.println("\t@" + PrimaryKey.class.getSimpleName());
			writer.println("\tprotected " + e.getFieldType().getSimpleName() + " " + e.getColumnName() + ";");
		}
		writer.println();
	}

	private void writeConstructAndClone(String entityName, PrintWriter writer) {
		writer.println("\tpublic " + entityName + "() {");
		writer.println("\t}");
		writer.println();

		writer.println("\t@Override");
		writer.println("\tpublic " + entityName + " clone() {");
		writer.println("\t\ttry {");
		writer.println("\t\t\treturn (" + entityName + ") super.clone();");
		writer.println("\t\t} catch (CloneNotSupportedException e) {");
		writer.println("\t\t\tthrow new RuntimeException(e);");
		writer.println("\t\t}");
		writer.println("\t}");
		writer.println();
	}

	private void writeGetSet(String entityName, ColumnInfo col, PrintWriter writer) {
		String columnName = col.getColumnName();
		String name = GenenratorUtil.toHighLow(columnName);
		String type = col.getFieldType().getSimpleName();
		writer.println("\tpublic " + type + " get" + name + "() {");
		writer.println("\t\treturn " + columnName + ";");
		writer.println("\t}");
		writer.println();
		writer.println("\tpublic " + entityName + " set" + name + "(" + type + " " + columnName + ") {");
		writer.println("\t\tthis." + columnName + " = " + columnName + ";");
		writer.println("\t\treturn this;");
		writer.println("\t}");
		writer.println();
	}

	private void getImports(List<ColumnInfo> columns, Set<String> importSet) {
		for (ColumnInfo e : columns) {
			Class<?> clz = fieldTypeHandler.handle(e.getColumnType());
			if (Object.class.isAssignableFrom(clz)) {
				String clazzName = clz.getName();
				if (!clazzName.startsWith("java.lang."))
					importSet.add(clazzName);
			}
			if (e.isAutoInc())
				importSet.add(AutoIncrement.class.getName());
			if (e.isPri())
				importSet.add(PrimaryKey.class.getName());
			e.setFieldType(clz);
		}
		importSet.add("java.io.Serializable");
	}

}
