package com.zwzch.fool.engine.jdbc;

import com.zwzch.fool.engine.executor.Session;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DistributedResultSet implements ResultSet {
    private List<ResultSet> resultList;

    private Session session;

    private ResultSet currentRS = null;

    private int currentRSIndex = 0;

    private boolean closed = false;
    public DistributedResultSet(List<ResultSet> resultList, Session session){
        this.resultList = resultList;
        this.session = session;
        this.currentRSIndex = 0;
        this.currentRS = this.resultList.get(this.currentRSIndex++);
    }

    public DistributedResultSet(ResultSet result, Session session){
        this.resultList = new ArrayList<ResultSet>();
        resultList.add(result);
        this.session = session;
        this.currentRSIndex = 0;
        this.currentRS = this.resultList.get(this.currentRSIndex++);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return (T) this;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.getClass().isAssignableFrom(iface);
    }

    public boolean next() throws SQLException {

        boolean r = currentRS.next();
        while(r == false){
            if(currentRSIndex < resultList.size()){
                currentRS = resultList.get(currentRSIndex++);
                r=currentRS.next();
            }else{
                return false;
            }
        }
        return r;
    }

    public void close() throws SQLException {
        for(ResultSet rs : resultList) {
            if(rs==null) {
                continue;
            }
            rs.close();
        }

        /* 释放resultSet不需要释放channel */
//		if(session.isAutoCommit()){
//			session.release();
//		}
        this.closed = true;
    }

    public boolean isClosed() throws SQLException {
        return this.closed;
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return this.currentRS.getMetaData();
    }

    public void setFetchDirection(int direction) throws SQLException {
        return;
    }

    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    public void setFetchSize(int rows) throws SQLException {
        return;
    }

    public int getFetchSize() throws SQLException {
        return 0;
    }

    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    public boolean wasNull() throws SQLException {
        return this.currentRS.wasNull();
    }

    public String getString(int columnIndex) throws SQLException {
        return this.currentRS.getString(columnIndex);
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        return this.currentRS.getBoolean(columnIndex);
    }

    public byte getByte(int columnIndex) throws SQLException {
        return this.currentRS.getByte(columnIndex);
    }

    public short getShort(int columnIndex) throws SQLException {
        return this.currentRS.getShort(columnIndex);
    }

    public int getInt(int columnIndex) throws SQLException {
        return this.currentRS.getInt(columnIndex);
    }

    public long getLong(int columnIndex) throws SQLException {
        return this.currentRS.getLong(columnIndex);
    }

    public float getFloat(int columnIndex) throws SQLException {
        return this.currentRS.getFloat(columnIndex);
    }

    public double getDouble(int columnIndex) throws SQLException {
        return this.currentRS.getDouble(columnIndex);
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return this.currentRS.getBigDecimal(columnIndex, scale);
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        return this.currentRS.getBytes(columnIndex);
    }

    public Date getDate(int columnIndex) throws SQLException {
        return this.currentRS.getDate(columnIndex);
    }

    public Time getTime(int columnIndex) throws SQLException {
        return this.currentRS.getTime(columnIndex);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return this.currentRS.getTimestamp(columnIndex);
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return this.currentRS.getAsciiStream(columnIndex);
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return this.currentRS.getAsciiStream(columnIndex);
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return this.currentRS.getBinaryStream(columnIndex);
    }

    public String getString(String columnLabel) throws SQLException {
        return this.currentRS.getString(columnLabel);
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        return this.currentRS.getBoolean(columnLabel);
    }

    public byte getByte(String columnLabel) throws SQLException {
        return this.currentRS.getByte(columnLabel);
    }

    public short getShort(String columnLabel) throws SQLException {
        return this.currentRS.getShort(columnLabel);
    }

    public int getInt(String columnLabel) throws SQLException {
        return this.currentRS.getInt(columnLabel);
    }

    public long getLong(String columnLabel) throws SQLException {
        return this.currentRS.getLong(columnLabel);
    }

    public float getFloat(String columnLabel) throws SQLException {
        return this.currentRS.getFloat(columnLabel);
    }

    public double getDouble(String columnLabel) throws SQLException {
        return this.currentRS.getDouble(columnLabel);
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        return this.currentRS.getBytes(columnLabel);
    }

    public Date getDate(String columnLabel) throws SQLException {
        return this.currentRS.getDate(columnLabel);
    }

    public Time getTime(String columnLabel) throws SQLException {
        return this.currentRS.getTime(columnLabel);
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return this.currentRS.getTimestamp(columnLabel);
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return this.currentRS.getAsciiStream(columnLabel);
    }

    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return this.currentRS.getUnicodeStream(columnLabel);
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return this.currentRS.getBinaryStream(columnLabel);
    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    public void clearWarnings() throws SQLException {

    }

    public Object getObject(int columnIndex) throws SQLException {
        return this.currentRS.getObject(columnIndex);
    }

    public Object getObject(String columnLabel) throws SQLException {
        return this.currentRS.getObject(columnLabel);
    }


    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return this.currentRS.getBigDecimal(columnIndex);
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return this.currentRS.getBigDecimal(columnLabel);
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return this.currentRS.getTime(columnIndex, cal);
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return this.currentRS.getTime(columnLabel, cal);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return this.currentRS.getTimestamp(columnIndex,cal);
    }

    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return this.currentRS.getTimestamp(columnLabel, cal);
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return this.currentRS.getObject(columnIndex, type);
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return this.currentRS.getObject(columnLabel, type);
    }

    public Clob getClob(String columnLabel) throws SQLException {
        return this.currentRS.getClob(columnLabel);
    }
    public Clob getClob(int columnIndex) throws SQLException {
        return this.currentRS.getClob(columnIndex);
    }

    public Blob getBlob(String columnLabel) throws SQLException {
        return this.currentRS.getBlob(columnLabel);
    }
    public Blob getBlob(int columnIndex) throws SQLException {
        return this.currentRS.getBlob(columnIndex);
    }



    /****************** 未实现的方法 ******************/

    public URL getURL(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public URL getURL(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Ref getRef(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Array getArray(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Ref getRef(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Array getArray(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int findColumn(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public RowId getRowId(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public RowId getRowId(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean absolute(int row) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean relative(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean previous() throws SQLException {
        throw new UnsupportedOperationException();
    }
    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isBeforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isAfterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isLast() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void beforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void afterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean first() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean last() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNull(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateString(int columnIndex, String x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateNull(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateString(String columnLabel, String x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException();

    }

    public Statement getStatement() throws SQLException {
        throw new UnsupportedOperationException();
    }
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new UnsupportedOperationException();

    }
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public NClob getNClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public NClob getNClob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public String getNString(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getNString(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();

    }

    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new UnsupportedOperationException();
    }

}
