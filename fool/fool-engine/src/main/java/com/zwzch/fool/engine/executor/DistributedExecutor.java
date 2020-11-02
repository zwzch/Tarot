package com.zwzch.fool.engine.executor;

import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.lifecycle.AbstractLifecycle;
import com.zwzch.fool.engine.jdbc.DistributedStatement;
import com.zwzch.fool.engine.model.Parameter;
import com.zwzch.fool.engine.router.model.ActualSql;
import com.zwzch.fool.engine.statisic.SqlStatistics;
import com.zwzch.fool.engine.executor.Session.Channel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DistributedExecutor extends AbstractLifecycle implements IBase {
    private ExecutorService pool;
    private int size;

    public DistributedExecutor(int size) {
        this.size = size;
    }

    @Override
    protected void doInit() {
        this.pool = Executors.newFixedThreadPool(size);
    }


    public void execute(List<ActualSql> actualSqls, final Session session, SqlStatistics sqlStatistics, final DistributedStatement statement) throws Exception {
        boolean haveUpdate = false;
        boolean haveSelect = false;
        try {
            Map<String, List<ActualSql>> actualSqlMap = getActualSqlMap(actualSqls);
            if (actualSqlMap.size() == 1 || !session.getConn().isParallelExecute()) {
                //单条SQL
                for (final String sliceName: actualSqlMap.keySet()) {
                    final List<ActualSql> actualSqlList = actualSqlMap.get(sliceName);
                    /*batch sql只能是更新语句（insert delete or update）所以actualSqlList要么是更新语句，要么不是*/
                    if (actualSqlList.get(0).getSqlType().isUpdate()) {
                        haveUpdate = true;
                    } else {
                        haveSelect = true;
                    }
                    //获得连接
                    final Channel channel = session.getChannel(sliceName, haveUpdate, actualSqlList.get(0));
                    executeInternal(channel, actualSqlList, statement);
                }
            } else {
                //多条SQL
            }
        } finally {
            if (!haveSelect && session.isAutoCommit()) {
                session.release();
            }
        }
    }

    private Map<String, List<ActualSql>> getActualSqlMap(List<ActualSql> actualSqls) {
        Map<String, List<ActualSql>> actualSqlMap = new HashMap<String, List<ActualSql>>();
        for (ActualSql actualSql : actualSqls) {
            String sliceName = actualSql.getSliceName();
            if (!actualSqlMap.containsKey(sliceName)) {
                actualSqlMap.put(sliceName, new ArrayList<ActualSql>());
            }
            actualSqlMap.get(sliceName).add(actualSql);
        }

        return actualSqlMap;
    }

    private void executeInternal(Channel channel, List<ActualSql> actualSqlList, DistributedStatement statement) throws Exception {
        List<List<ActualSql>> clustedActuals = clusterActualSqlByOriginSql(actualSqlList);
        List<ActualSql> singleAcutualSqls = new ArrayList<ActualSql>();
        for (List<ActualSql> actualSqls : clustedActuals) {
            if (actualSqls.size() == 0) {
                throw new CommonExpection("cluster actual sql error, clusetedAcutals:" + clustedActuals);
            }

            if (actualSqls.size() == 1) {
                singleAcutualSqls.add(actualSqls.get(0));
            } else {
                if (singleAcutualSqls.size() > 0) {
                    runActualSql(channel, singleAcutualSqls, statement);
                    singleAcutualSqls.clear();
                }

                runAcutalSqlInTransaction(channel, actualSqls, statement);
            }
        }

        if (singleAcutualSqls.size() != 0) {
            runActualSql(channel, singleAcutualSqls, statement);
            singleAcutualSqls.clear();
        }
    }

    private List<List<ActualSql>> clusterActualSqlByOriginSql(List<ActualSql> actualSqls) {
        List<List<ActualSql>> ret = new ArrayList<List<ActualSql>>();

        for (ActualSql actualSql : actualSqls) {
            int size = ret.size();

            if (actualSql == null) {
                throw new CommonExpection("actualsql have null parameter map, actualSql:" + actualSql);
            }

            if (size == 0) {
                // 第一个 直接放入
                ret.add(new ArrayList<ActualSql>());
                ret.get(0).add(actualSql);
                continue;
            }

            ActualSql pre = ret.get(size - 1).get(0);
            // 后续，跟前面不对，如果不对，则直接放入新的list中
            if (pre.getSqlObject() == actualSql.getSqlObject()) {
                ret.get(size - 1).add(actualSql);
            } else {
                ret.add(new ArrayList<ActualSql>());
                ret.get(size).add(actualSql);
            }
        }
        return ret;
    }

    private void runActualSql(Channel channel, List<ActualSql> actualSqls, DistributedStatement statement) throws SQLException {
        //记录物理DB
        for (ActualSql actualSql: actualSqls) {
            actualSql.setPdbName(channel.getDbKey());
        }

        //执行拼接SQL
        StringBuilder sb = new StringBuilder();
        for (ActualSql actualSql : actualSqls) {
            String sql = actualSql.getNewSql();
            sb.append(sql);
            if (!sql.endsWith(";")) {
                sb.append(";");
            }
        }

        PreparedStatement ps = channel.getC().prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
        statement.addStatement(ps);
        int paramIndex = 1;
        for (ActualSql actualSql : actualSqls) {
            paramIndex = addParameter(ps, paramIndex, actualSql);
        }
        boolean isRs = ps.execute();
        getResult(ps, isRs, actualSqls);
    }

    private int addParameter(PreparedStatement preparedStatement, int paramIndex, ActualSql actualSql) throws SQLException {

        Map<Integer, Parameter> parameterMap = actualSql.getParameterMap();
        if (parameterMap == null || parameterMap.size() == 0) {
            return paramIndex;
        }
        if (actualSql.getParamIndexs() != null) {
            for (int index : actualSql.getParamIndexs()) {
                Parameter parameter = parameterMap.get(index);
                parameter.getParameterMethod().setParameter(preparedStatement, paramIndex, parameter.getArgs());
                paramIndex++;
            }
        }
        if (actualSql.getUpdateIndexs() != null) {
            for (int index : actualSql.getUpdateIndexs()) {
                Parameter parameter = parameterMap.get(index);
                parameter.getParameterMethod().setParameter(preparedStatement, paramIndex, parameter.getArgs());
                paramIndex++;
            }
        }
        return paramIndex;
    }

    private void getResult(PreparedStatement ps, boolean isRs, List<ActualSql> actualSqls) throws SQLException {
        for (ActualSql actualSql: actualSqls) {
            if (isRs) {
                //select
                actualSql.setResultSet(ps.getResultSet());
            } else {
                //update
                int updateCount = ps.getUpdateCount();
                if (updateCount == -1) {
                    throw new SQLException("getResult actualSqls.size() > ps.size()");
                }
                actualSql.setUpdateCount(ps.getUpdateCount());
                if (actualSql.getInsertId() == -1) {
                    ResultSet rs = ps.getGeneratedKeys();
                    while (rs.next()) {
                        actualSql.addInsertId(rs.getLong(1));
                    }
                }
            }
            isRs = ps.getMoreResults(Statement.KEEP_CURRENT_RESULT);
        }
    }

    private void runAcutalSqlInTransaction(Channel c, List<ActualSql> actualSqls, DistributedStatement statement) throws Exception {
        c.getC().setAutoCommit(false);

        try {
            runActualSql(c, actualSqls, statement);

            c.getC().commit();
        } catch (Exception e) {
            try {
                c.getC().rollback();
            } catch (Exception ee) {
                log.error("distributed exector run actual sql in transaction rollback error", e);
            }
            throw e;
        } finally {
            c.getC().setAutoCommit(true);
        }

    }


}

