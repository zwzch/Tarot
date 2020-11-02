package com.zwzch.fool.engine.jdbc;

import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.engine.config.EngineConfig;
import com.zwzch.fool.engine.executor.DistributedExecutor;
import com.zwzch.fool.engine.executor.Session;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.processor.Processor;
import com.zwzch.fool.engine.processor.ProcessorFactory;
import com.zwzch.fool.engine.resource.ResourceContainer;
import com.zwzch.fool.engine.statisic.SqlStatistics;
import com.zwzch.fool.engine.utils.PreParseUtil;
import com.zwzch.fool.repo.IRepo;
import com.zwzch.fool.rule.IRule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class DistributedConnection implements Connection, IBase {
    public DistributedDataSource dataSource = null;
    private IRepo repo = null;
    private IRule rule = null;
    private EngineConfig engineConfig = null;
    private List<DistributedStatement> distributeStatements = new ArrayList<DistributedStatement>();
    private Session session;
    private DistributedExecutor executor;
    private boolean closed = false;
    //上次执行的sql
    private Processor lastProcess = null;


    public DistributedConnection(DistributedDataSource dataSource, DistributedExecutor executor) {
        this.dataSource = dataSource;
        this.session = new Session(this);
        this.executor = executor;
        flush();
    }

    public void flush() {
        ResourceContainer rc = this.dataSource.getResourceContainer();
        Map<String, Object> resource = rc.getResource();
        this.repo = rc.getRepo(resource);
        this.rule = rc.getRule(resource);
        this.engineConfig = rc.getEngineConfig(resource);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkIfclosed();
        DistributedPreparedStatement s = new DistributedPreparedStatement(sql, this);
        synchronized (this) {
            distributeStatements.add(s);
        }
        return s;
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return null;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return null;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    @Override
    public void commit() throws SQLException {

    }

    @Override
    public void rollback() throws SQLException {

    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {

    }

    @Override
    public String getCatalog() throws SQLException {
        return null;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {

    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return 0;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {

    }

    @Override
    public String getSchema() throws SQLException {
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {

    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public void execute(DistributedStatement statement, boolean isPress) throws SQLException {
        List<SqlObject> sqlObjects = statement.getSqlObjectList();
        sqlObjects.forEach(sqlObject -> {
            checkElseThrow(!PreParseUtil.isDDL(sqlObject.getSql()), new CommonExpection("not support DDL"));
            checkElseThrow(!PreParseUtil.isDDL(sqlObject.getSql()), new CommonExpection("not support LOCK"));
        });
        boolean isException = false;
        SqlStatistics sqlStatistics = null;
        try {
            sqlStatistics = new SqlStatistics(sqlObjects.get(0).getSql(), dataSource.getLogicDBName(), dataSource.getLogicAccountName());
            statement.setProcessor(ProcessorFactory.makeProcess(sqlObjects));
            Processor processor = statement.getProcessor();
            //解析SQL
            processor.parseSql();
            //路由
            processor.doRoute();
            //执行
            session.execute(processor, sqlStatistics);

        } catch (Throwable e) {
            log.error("Error! LdbName:" + dataSource.getLogicDBName(), e);
            SQLException sqlException = new SQLException(e);
            throw sqlException;
        }
    }

    private void checkIfclosed() throws SQLException {
        if (this.closed) {
            throw new SQLException();
        }
    }

    public void removeStatement(DistributedStatement statement) {
        synchronized (this) {
            distributeStatements.remove(statement);
        }
    }

    public DistributedDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DistributedDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public IRepo getRepo() {
        return repo;
    }

    public void setRepo(IRepo repo) {
        this.repo = repo;
    }

    public IRule getRule() {
        return rule;
    }

    public void setRule(IRule rule) {
        this.rule = rule;
    }

    public EngineConfig getEngineConfig() {
        return engineConfig;
    }

    public void setEngineConfig(EngineConfig engineConfig) {
        this.engineConfig = engineConfig;
    }

    public List<DistributedStatement> getDistributeStatements() {
        return distributeStatements;
    }

    public void setDistributeStatements(List<DistributedStatement> distributeStatements) {
        this.distributeStatements = distributeStatements;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public DistributedExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(DistributedExecutor executor) {
        this.executor = executor;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Processor getLastProcess() {
        return lastProcess;
    }

    public void setLastProcess(Processor lastProcess) {
        this.lastProcess = lastProcess;
    }

    public String getLdbName() {
        return dataSource.getLogicDBName();
    }

    public boolean isParallelExecute() {
        return dataSource.isParallelExecute();
    }
}
