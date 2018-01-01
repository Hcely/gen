package com.mysql.genenrator.handler;

public interface FieldTypeHandler {
	public Class<?> handle(int columnType);
}
