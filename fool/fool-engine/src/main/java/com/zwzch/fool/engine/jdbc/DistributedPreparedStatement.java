package com.zwzch.fool.engine.jdbc;

import com.zwzch.fool.engine.model.Parameter;
import com.zwzch.fool.engine.model.ParameterMethod;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.processor.Processor;
import com.zwzch.fool.engine.router.model.ActualSql;
import com.zwzch.fool.engine.utils.PreParseUtil;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class DistributedPreparedStatement extends DistributedStatement implements PreparedStatement  {

    private String sql;
    private Map<Integer, Parameter> parameterMap = new HashMap<Integer, Parameter>();


    public DistributedPreparedStatement(String sql, DistributedConnection conn){
        super(conn);
        this.sql = sql;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        executeCore(sql, parameterMap);
        clearBatch();
        this.resultSet = processor.getResultSet();
        return this.resultSet;
    }

    @Override
    public int executeUpdate() throws SQLException {
        executeCore(sql,parameterMap);
        clearBatch();
        return updateCount = getProcessor().getSingleUpdateCount();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setNull1, parameterIndex, new Object[] {sqlType }));
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setBoolean, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setByte, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setShort, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setInt, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setLong, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setFloat, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setDouble, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setBigDecimal, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setString, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setBytes, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setDate1, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setTime1, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setTimestamp1, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setAsciiStream, parameterIndex, new Object[] {x ,length}));
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setUnicodeStream, parameterIndex, new Object[] {x, length}));
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setBinaryStream, parameterIndex, new Object[] {x, length}));
    }

    @Override
    public void clearParameters() throws SQLException {
        this.parameterMap.clear();
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setObject1, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setObject2, parameterIndex, new Object[] {x, targetSqlType}));
    }


    @Override
    public void addBatch() throws SQLException {
        int index = sqlObjectList.size();
        sqlObjectList.add(new SqlObject(this, this.sql, this.parameterMap));

        parameterMap = new HashMap<Integer, Parameter>();

    }


    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setCharacterStream, parameterIndex, new Object[] {reader, length }));
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setRef, parameterIndex, new Object[] {x }));
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setBlob1, parameterIndex, new Object[] {x}));
    }

    @Override
    public void setBlob(int parameterIndex, InputStream x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setBlob2, parameterIndex, new Object[] {x}));
    }

    @Override
    public void setBlob(int parameterIndex, InputStream x, long length) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setBlob3, parameterIndex, new Object[] {x, length}));
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setClob, parameterIndex, new Object[] {x}));
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setArray, parameterIndex, new Object[] {x}));
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setDate2, parameterIndex, new Object[] {x, cal }));
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        if(cal == null){
            parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setTime1, parameterIndex, new Object[] {x }));
        }else{
            parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setTime2, parameterIndex, new Object[] {x, cal }));
        }
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setTimestamp2, parameterIndex, new Object[] {x ,cal}));
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setNull2, parameterIndex, new Object[] {sqlType,typeName }));
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setURL, parameterIndex, new Object[] {x }));
    }


    /*************** 未实现的方法 ****************/
    @Override
    public void setCursorName(String name) throws SQLException {
        throw new UnsupportedOperationException();

    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        parameterMap.put(parameterIndex-1, new Parameter(ParameterMethod.setObject3, parameterIndex, new Object[] {x, targetSqlType, scaleOrLength }));
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
        parameterMap.put(parameterIndex, new Parameter(ParameterMethod.setObject2, parameterIndex, new Object[] {x, targetSqlType}));
    }



    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute() throws SQLException {
        if(PreParseUtil.isUpdate(sql)){
            executeUpdate();
            return false;
        }else{
            executeQuery();
            return true;
        }
    }

}
