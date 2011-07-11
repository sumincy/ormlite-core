package com.j256.ormlite.stmt;

import java.sql.SQLException;

import com.j256.ormlite.field.FieldType;

/**
 * Base class for other select argument classes.
 * 
 * @author graywatson
 */
public abstract class BaseSelectArg implements ArgumentHolder {

	private String columnName = null;
	private FieldType fieldType = null;

	public BaseSelectArg() {
		// no args
	}

	public BaseSelectArg(String columName) {
		this.columnName = columName;
	}

	/**
	 * Return the stored value.
	 */
	protected abstract Object getValue();

	public abstract void setValue(Object value);

	/**
	 * Return true if the value is set.
	 */
	protected abstract boolean isValueSet();

	public String getColumnName() {
		if (columnName == null) {
			throw new IllegalArgumentException("Column name has not been set");
		} else {
			return columnName;
		}
	}

	public void setMetaInfo(String columnName) {
		if (this.columnName == null) {
			// not set yet
		} else if (this.columnName.equals(columnName)) {
			// set to the same value as before
		} else {
			throw new IllegalArgumentException("Column name cannot be set twice from " + this.columnName + " to "
					+ columnName);
		}
		this.columnName = columnName;
	}

	public void setMetaInfo(FieldType fieldType) {
		if (this.fieldType == null) {
			// not set yet
		} else if (this.fieldType == fieldType) {
			// set to the same value as before
		} else {
			throw new IllegalArgumentException("FieldType name cannot be set twice from " + this.fieldType + " to "
					+ fieldType);
		}
		this.fieldType = fieldType;
	}

	public void setMetaInfo(String columnName, FieldType fieldType) {
		setMetaInfo(columnName);
		setMetaInfo(fieldType);
	}

	public Object getSqlArgValue() throws SQLException {
		if (!isValueSet()) {
			throw new SQLException("Column value has not been set for " + columnName);
		}
		Object value = getValue();
		if (value == null) {
			return null;
		} else if (fieldType == null) {
			return value;
		} else if (fieldType.isForeign() && fieldType.getFieldType() == value.getClass()) {
			FieldType idFieldType = fieldType.getForeignIdField();
			return idFieldType.extractJavaFieldValue(value);
		} else {
			return fieldType.convertJavaFieldToSqlArgValue(value);
		}
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	@Override
	public String toString() {
		if (!isValueSet()) {
			return "[unset]";
		}
		Object val;
		try {
			val = getSqlArgValue();
			if (val == null) {
				return "[null]";
			} else {
				return val.toString();
			}
		} catch (SQLException e) {
			return "[could not get value: " + e.getMessage() + "]";
		}
	}
}
