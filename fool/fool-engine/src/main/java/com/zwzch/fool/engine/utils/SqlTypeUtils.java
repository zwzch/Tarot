package com.zwzch.fool.engine.utils;
import com.zwzch.fool.engine.model.ParameterMethod;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

public class SqlTypeUtils {
    public static ParameterMethod convertSetObjectByType(Object obj) {
		if (obj instanceof Boolean) {
			return ParameterMethod.setBoolean;
		} else if (obj instanceof Date) {
			return ParameterMethod.setDate1;
		} else if (obj instanceof Double) {
			return ParameterMethod.setDouble;
		} else if (obj instanceof Float) {
			return ParameterMethod.setFloat;
		} else if (obj instanceof Integer) {
			return ParameterMethod.setInt;
		} else if (obj instanceof Long) {
			return ParameterMethod.setLong;
		} else if (obj instanceof Short) {
			return ParameterMethod.setShort;
		} else if (obj instanceof String) {
			return ParameterMethod.setString;
		} else if (obj instanceof Time) {
			return ParameterMethod.setTime1;
		} else if (obj instanceof Timestamp) {
			return ParameterMethod.setTimestamp1;
		} else if (obj instanceof Byte) {
			return ParameterMethod.setByte;
		}

		return ParameterMethod.setObject1;
	}

    public static ParameterMethod converSetObjechhtByTargetSqlType(int targetSqlType) {
		switch (targetSqlType) {
			case Types.BOOLEAN:
				return ParameterMethod.setBoolean;
			case Types.DATE:
				return ParameterMethod.setDate1;
			case Types.DOUBLE:
				return ParameterMethod.setDouble;
			case Types.FLOAT:
				return ParameterMethod.setFloat;
			case Types.INTEGER:
				return ParameterMethod.setInt;
			case Types.BIGINT:
				return ParameterMethod.setLong;
			case Types.TINYINT:
				return ParameterMethod.setByte;
			case Types.SMALLINT:
				return ParameterMethod.setShort;
			case Types.VARCHAR:
				return ParameterMethod.setString;
			case Types.TIME:
				return ParameterMethod.setTime1;
			case Types.TIMESTAMP:
				return ParameterMethod.setTimestamp1;
			default:
				return ParameterMethod.setObject2;
		}
    }

}
