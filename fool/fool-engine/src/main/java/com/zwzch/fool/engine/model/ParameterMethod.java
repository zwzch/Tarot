package com.zwzch.fool.engine.model;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

public enum ParameterMethod {

    setArray, setAsciiStream, setBigDecimal, setBinaryStream, setBlob1, setBlob2, setBlob3, setBoolean, setByte, setBytes, //
    setCharacterStream, setClob, setDate1, setDate2, setDouble, setFloat, setInt, setLong, //
    setNull1, setNull2, setObject1, setObject2, setObject3, setRef, setShort, setString, //
    setTime1, setTime2, setTimestamp1, setTimestamp2, setURL, setUnicodeStream; //

    /**
     * args[0]: index args[1..n] 参数
     * 
     * @throws SQLException
     */
    public void setParameter(PreparedStatement stmt, int index, Object... args) throws SQLException {
        switch (this) {
            case setArray:
                stmt.setArray(index, (Array) args[0]);
                break;
            case setAsciiStream:
                stmt.setAsciiStream(index, (InputStream) args[0], (Integer) args[1]);
                break;
            case setBigDecimal:
                stmt.setBigDecimal(index, (BigDecimal) args[0]);
                break;
            case setBinaryStream:
                stmt.setBinaryStream(index, (InputStream) args[0], (Integer) args[1]);
                break;
            case setBlob1:
                stmt.setBlob(index, (Blob) args[0]);
                break;
            case setBlob2:
                stmt.setBlob(index, (InputStream) args[0]);
                break;
            case setBlob3:
                stmt.setBlob(index, (InputStream) args[0], (Long)args[1]);
                break;
            case setBoolean:
                stmt.setBoolean(index, (Boolean) args[0]);
                break;
            case setByte:
                stmt.setByte(index, (Byte) args[0]);
                break;
            case setBytes:
                stmt.setBytes(index, (byte[]) args[0]);
                break;
            case setCharacterStream:
                stmt.setCharacterStream(index, (Reader) args[0], (Integer) args[1]);
                break;
            case setClob:
                stmt.setClob(index, (Clob) args[0]);
                break;
            case setDate1:
                stmt.setDate(index, (Date) args[0]);
                break;
            case setDate2:
                stmt.setDate(index, (Date) args[0], (Calendar) args[1]);
                break;
            case setDouble:
                stmt.setDouble(index, (Double) args[0]);
                break;
            case setFloat:
                stmt.setFloat(index, (Float) args[0]);
                break;
            case setInt:
                stmt.setInt(index, (Integer) args[0]);
                break;
            case setLong:
                stmt.setLong(index, (Long) args[0]);
                break;
            case setNull1:
                stmt.setNull(index, (Integer) args[0]);
                break;
            case setNull2:
                stmt.setNull(index, (Integer) args[0], (String) args[1]);
                break;
            case setObject1:
                stmt.setObject(index, args[0]);
                break;
            case setObject2:
                stmt.setObject(index, args[0], (Integer) args[1]);
                break;
            case setObject3:
                stmt.setObject(index, args[0], (Integer) args[1], (Integer) args[2]);
                break;
            case setRef:
                stmt.setRef(index, (Ref) args[0]);
                break;
            case setShort:
                stmt.setShort(index, (Short) args[0]);
                break;
            case setString:
                stmt.setString(index, (String) args[0]);
                break;
            case setTime1:
                stmt.setTime(index, (Time) args[0]);
                break;
            case setTime2:
                stmt.setTime(index, (Time) args[0], (Calendar) args[1]);
                break;
            case setTimestamp1:
                stmt.setTimestamp(index, (Timestamp) args[0]);
                break;
            case setTimestamp2:
                stmt.setTimestamp(index, (Timestamp) args[0], (Calendar) args[1]);
                break;
            case setURL:
                stmt.setURL(index, (URL) args[0]);
                break;
            case setUnicodeStream:
                stmt.setUnicodeStream(index, (InputStream) args[0], (Integer) args[1]);
                break;
            default:
                throw new IllegalArgumentException("Unhandled ParameterMethod:" + this.name());
        }
    }
}
