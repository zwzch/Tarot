package com.zwzch.fool.engine.jdbc;

import com.zwzch.fool.common.IBase;
import com.zwzch.fool.engine.model.Parameter;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.processor.Processor;
import com.zwzch.fool.engine.router.model.ActualSql;
import com.zwzch.fool.engine.utils.PreParseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributedStatement implements Statement, IBase {

    public DistributedConnection conn;

    protected Processor processor;

    private List<Statement> statements = new ArrayList<Statement>();

    List<List<Object>> generatedKey = new ArrayList<List<Object>>();

    private boolean isPress = false;

    /**
     * 当前statment 是否是关闭的
     */
    protected boolean closed = false;
    protected List<SqlObject> sqlObjectList = new ArrayList<SqlObject>();    /* 传入内部处理的结构，表示单次execute的参数 */

    ResultSet resultSet;
    /**
     * 更新计数，如果执行了多次，那么这个值只会返回最后一次执行的结果。 如果是一个query，那么返回的数据应该是-1
     */
    protected int updateCount = -1;
    protected int[] updateCountForBatch = new int[0];
    protected int maxRows = -1;

    public DistributedStatement(DistributedConnection conn) {
        this.conn = conn;
    }

    public DistributedDataSource getDataSource() {
        return conn.dataSource;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        executeCore(sql, new HashMap<Integer, Parameter>());
        clearBatch();
        this.resultSet = processor.getResultSet();
        return this.resultSet;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        executeCore(sql, new HashMap<Integer, Parameter>());
        clearBatch();
        return updateCount = processor.getSingleUpdateCount();
    }


    @Override
    public void close() throws SQLException {
        if (!this.closed && conn != null && processor != null) {
            processor.release();
            this.closed = true;
        }
        releaseAllStatements();
        this.conn.removeStatement(this);
    }

    private void releaseAllStatements() {
        synchronized (this) {
            for (Statement statement : statements) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.warn("关闭statement异常", e);
                }
            }
            statements.clear();
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxRows() throws SQLException {
        return maxRows;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        if (max == 0) {
            max = -1;
        }
        this.maxRows = max;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {

    }

    @Override
    public void cancel() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        if (PreParseUtil.isUpdate(sql)) {
            executeUpdate(sql);
            return false;
        } else {
            executeQuery(sql);
            return true;
        }
    }

    public void  executeCore(String sql, Map<Integer, Parameter> parameterMap) throws SQLException {
        checkclosed();
//        ensureResultSetClosed();
        SqlObject sqlObject = new SqlObject(this, sql, parameterMap);
        this.sqlObjectList.add(sqlObject);
        this.conn.execute(this, isPress);
        for (ActualSql actualSql : sqlObject.getActualSqlList()) {
            addGeneratedKey(actualSql);
        }
    }

    private void addGeneratedKey(ActualSql actualSql) {
        List<Long> insertIdList = actualSql.getInsertIdList();
        if (insertIdList.size() > 0) {
            for (long id : insertIdList) {
                ArrayList<Object> row = new ArrayList<Object>();
                row.add(id);
                generatedKey.add(row);
            }
        } else if (actualSql.getInsertId() != -1) {
            ArrayList<Object> row = new ArrayList<Object>();
            row.add(actualSql.getInsertId());
            generatedKey.add(row);
        }
    }


    protected void checkclosed() throws SQLException {
        if (this.closed) {
            throw new SQLException("statement is closed!");
        }
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return this.resultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return this.updateCount;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        this.updateCount = -1;
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.conn;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        this.sqlObjectList.add(new SqlObject(this, sql, new HashMap<Integer, Parameter>()));
    }

    @Override
    public void clearBatch() throws SQLException {
        this.sqlObjectList = new ArrayList<SqlObject>();
    }

    public List<SqlObject> getSqlObjectList() {
        return sqlObjectList;
    }

    public Processor getProcessor() {
        return processor;
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void addStatement(Statement statement) {
        synchronized (this) {
            statements.add(statement);
        }
    }


}
