package com.zwzch.fool.engine.processor;

import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.engine.config.EngineConfig;
import com.zwzch.fool.engine.exception.NotAllowMultiDbAccessException;
import com.zwzch.fool.engine.executor.Session;
import com.zwzch.fool.engine.jdbc.DistributedConnection;
import com.zwzch.fool.engine.jdbc.DistributedResultSet;
import com.zwzch.fool.engine.jdbc.DistributedStatement;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.router.model.ActualSql;
import com.zwzch.fool.engine.statisic.SqlStatistics;
import com.zwzch.fool.rule.IRule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseProcessor implements Processor {
    protected SqlObject sqlObject;

    protected ResultSet currentResultSet = null;
    protected static final int noUpdateCount = -1;
    protected static final long[] noInsertId = new long[0];

    public BaseProcessor() {
    }

    public BaseProcessor(SqlObject sqlObject) {
        this.sqlObject = sqlObject;
    }

    @Override
    public void execute(boolean isUpdate, SqlStatistics sqlStatistics) throws Exception {
    }

    @Override
    abstract public boolean isUpdate();


    @Override
    public DistributedConnection getConn() {
        return sqlObject.getConn();
    }

    @Override
    public DistributedStatement getStatement() {
        return sqlObject.getStatement();
    }

    @Override
    public ResultSet getResultSet() {
        List<ResultSet> resultList = new ArrayList<>();
        List<ActualSql> actualSqlList = sqlObject.getActualSqlList();
        actualSqlList.forEach(actualSql -> {
            ResultSet rs = actualSql.getResultSet();
            if (rs == null) {
                throw new CommonExpection("select processor - ResultSet is null - actualSql:" + actualSql.getNewSql());
            }
            resultList.add(rs);
        });
        return currentResultSet = new DistributedResultSet(resultList, sqlObject.getConn().getSession());
    }

    @Override
    public int getSingleUpdateCount() {
        int updateCount = 0;
        List<ActualSql> actualSqlList = sqlObject.getActualSqlList();
        for (ActualSql actualSql : actualSqlList) {
            updateCount += actualSql.getUpdateCount();
        }
        return updateCount;
    }

    public long[] getLastInsertIds() throws SQLException {
        List<ActualSql> actualSqlList = sqlObject.getActualSqlList();
        long[] ret = new long[actualSqlList.size()];
        for (int i = 0; i < actualSqlList.size(); i++) {
            ret[i] = actualSqlList.get(i).getInsertId();
        }

        return ret;
    }

    @Override
    abstract public boolean getMoreResults() throws SQLException;

    @Override
    public void release() throws SQLException {
        if (currentResultSet != null && !currentResultSet.isClosed()) {
            currentResultSet.close();
        }
    }

    @Override
    public int[] getUpdateCount() throws SQLException {
        int[] ret = new int[1];
        ret[0] = sqlObject.getProcessor().getSingleUpdateCount();
        return ret;
    }

    public SqlObject getSqlObject() {
        return sqlObject;
    }

    public void setSqlObject(SqlObject sqlObject) {
        this.sqlObject = sqlObject;
    }

    @Override
    public List<SqlObject> getSqlObjectList() {
        List<SqlObject> sqlObjects = new ArrayList<SqlObject>();
        sqlObjects.add(sqlObject);
        return sqlObjects;
    }

    @Override
    public List<ActualSql> getActualSqls() {
        return sqlObject.getActualSqlList();
    }

    public String getSameSlice(List<String> ltNames, IRule rule) {
        String sliceName = null;

        for (String ltName : ltNames) {
            if (rule.isShardingLogicTable(ltName)) {
                return null;
            }

            if (sliceName == null) {
                sliceName = rule.getSingleTableSliceId(ltName);
                continue;
            }

            if (!sliceName.equals(rule.getSingleTableSliceId(ltName))) {
                return null;
            }
        }

        return sliceName;
    }

    @Override
    public void setLastProcessor(DistributedConnection conn) {
        conn.setLastProcess(this);
    }

    @Override
    public void parseSql() {
        EngineConfig engineConfig = sqlObject.getConn().getEngineConfig();
    }


    @Override
    public void checkTransaction(Session session) throws NotAllowMultiDbAccessException {
        List<ActualSql> actualSqls = sqlObject.getActualSqlList();
        if (actualSqls == null) {
            throw new NotAllowMultiDbAccessException("actual sqls is null");
        }
        /* transaction check 检查下是否跨库。这里判断逻辑比较多，容易出错 */
        Set<String> groupNameSet = new HashSet<String>();

        for (ActualSql actualSql : actualSqls) {
            groupNameSet.add(actualSql.getSliceName());
        }
        if (!session.isAutoCommit()) {

        } else {

        }
    }
}
