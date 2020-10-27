package com.zwzch.fool.engine.processor;

import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.engine.exception.NotAllowMultiDbAccessException;
import com.zwzch.fool.engine.executor.Session;
import com.zwzch.fool.engine.jdbc.DistributedConnection;
import com.zwzch.fool.engine.jdbc.DistributedStatement;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.router.model.ActualSql;
import com.zwzch.fool.engine.statisic.SqlStatistics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface Processor {

    DistributedConnection getConn();

    DistributedStatement getStatement();

    List<ActualSql> getActualSqls();

    public void execute(boolean isUpdate, SqlStatistics sqlStatistics) throws Exception;

    /**
     * 获得路由结果
     * */
    void doRoute() throws SQLException, CommonExpection;

    void parseSql();

    /**
     * 当前操作是否是写操作
     * */
    boolean isUpdate();

    /**
     * 获得result结果
     * */
    ResultSet getResultSet() throws SQLException;

    /**
    * 获得更新个数
    * */
    int getSingleUpdateCount() throws SQLException;

    /**
     * 获得上次写入的id, 以long[]的形式返回
     * */
    long[] getLastInsertIds() throws SQLException;

    /**
    * 指向下个sql结果
    * */
    boolean getMoreResults() throws SQLException;

    /**
     * 释放相关资源,主要是resultSet
     * */
    void release() throws SQLException;

    List<SqlObject> getSqlObjectList();

    void setLastProcessor(DistributedConnection conn);

    void checkTransaction(Session session) throws NotAllowMultiDbAccessException;

    int[] getUpdateCount() throws SQLException;
}
