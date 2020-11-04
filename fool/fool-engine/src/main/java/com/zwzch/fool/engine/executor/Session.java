package com.zwzch.fool.engine.executor;

import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.engine.exception.NotAllowMultiDbAccessException;
import com.zwzch.fool.engine.jdbc.DistributedConnection;
import com.zwzch.fool.engine.processor.Processor;
import com.zwzch.fool.engine.router.model.ActualSql;
import com.zwzch.fool.engine.statisic.SqlStatistics;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Session implements IBase {
    private DistributedConnection conn;

    private String transactionGroup = null;

    protected boolean autoCommit = true;

    public Session(DistributedConnection conn) {
        this.conn = conn;
    }

    private Map<String/*dbKey*/, Channel/*conn*/> target = new HashMap<String, Channel>();

    public DistributedConnection getConn() {
        return conn;
    }


    public void setConn(DistributedConnection conn) {
        this.conn = conn;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void execute(Processor processor, SqlStatistics sqlStatistics) throws Exception {
        List<ActualSql> actualSqls = processor.getActualSqls();
        if (null != actualSqls) {
            processor.checkTransaction(this);
            try {
                this.getConn().getExecutor().execute(actualSqls, this, sqlStatistics, processor.getStatement());
            } catch (SQLException e) {
                throw new SQLException(e);
            }
        } else {
            throw new SQLException("quick fuse back");
        }
    }

    /* 一个sql语句执行完成,或者 一个事务完成 执行一次 */
    public synchronized void release() throws SQLException {
        /* 释放链接 */
        SQLException exception = null;

        for (Map.Entry<String, Channel> entry : target.entrySet()) {
            try {
                entry.getValue().getC().close();
            } catch (SQLException e) {
                exception = e;
            }
        }
        target.clear();

        /* 重置事务数据 */
        transactionGroup = null;

        /* 刷新配置 */
        conn.flush();

        if (exception != null) {
            throw exception;
        }
    }

    public class Channel {
        private String dbGroupKey;
        private String dbKey;
        private String dbName;
        private Connection c;

        public String getDbGroupKey() {
            return dbGroupKey;
        }

        public void setDbGroupKey(String dbGroupKey) {
            this.dbGroupKey = dbGroupKey;
        }

        public String getDbKey() {
            return dbKey;
        }

        public void setDbKey(String dbKey) {
            this.dbKey = dbKey;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public Connection getC() {
            return c;
        }

        public void setC(Connection c) {
            this.c = c;
        }

        public boolean isLogActualSql() {
            return conn.getEngineConfig().isLogActualSql();
        }

    }

    public Channel getChannel(String dbGroupKey, boolean isUpdate, ActualSql actualSql) throws SQLException {
        String dbKey = getDBkey(dbGroupKey, isUpdate, actualSql);
        Channel channel = target.get(dbKey);
        if (null == channel) {
            channel = new Channel();
            Connection c = conn.getRepo().getPhysicalDBConn(dbKey);
            if (c.getAutoCommit() != this.autoCommit) {
                c.setAutoCommit(this.autoCommit);
            }
            channel.setDbGroupKey(dbGroupKey);
            channel.setDbKey(dbKey);
            channel.setDbName(conn.getRepo().getPhysicalDB(dbKey).getDbName());
            channel.setC(c);
            target.put(dbKey, channel);
        }
        return channel;
    }

    public Channel getChannel(String dbKey) throws SQLException {
        Channel channel = this.target.get(dbKey);
        Connection connection = channel.getC();
        channel.setC(connection);

        return channel;
    }


    public String getDBkey(String dbGroupKey, boolean isUpdate, ActualSql actualSql) {
        boolean toSlave = false;
        String dbKey = null;
        if (actualSql.isToSlave()) {
            toSlave = true;
            dbKey = conn.getRepo().getSlavePDBId(dbGroupKey);
        } else {
            dbKey = conn.getRepo().getMasterPDBId(dbGroupKey);
        }
        return dbKey;
    }

    public void commit() throws SQLException {
        // commit
        if (!this.autoCommit) {

            SQLException exception = null;
            for (Map.Entry<String, Channel> entry : target.entrySet()) {
                try {
                    entry.getValue().getC().commit();
                } catch (SQLException e) {
                    exception = e;
                }
            }

            try {
                this.release();
            } catch (SQLException e) {
                log.error("session release error!", e);
            }

            if (exception != null) {
                throw exception;
            }
        }

    }

    public void rollback() throws SQLException {
        // rollback
        if (!this.autoCommit) {

            SQLException exception = null;
            for (Map.Entry<String, Channel> entry : target.entrySet()) {
                try {
                    entry.getValue().getC().rollback();
                } catch (SQLException e) {
                    exception = e;
                }
            }

            try {
                this.release();
            } catch (SQLException e) {
                log.error("session release error!", e);
            }

            if (exception != null) {
                throw exception;
            }
        }

    }
    public Set<String> getChannelKeys() {
        return this.target.keySet();
    }

    public void releaseChannel(String dbKey) {
        Channel channel = this.target.get(dbKey);
        if (channel != null) {
            try {
                channel.getC().close();
            } catch (Exception e) {
                log.warn("releaseChannel error, channel:" + channel, e);
            }

            this.target.remove(dbKey);
        }
    }
}
