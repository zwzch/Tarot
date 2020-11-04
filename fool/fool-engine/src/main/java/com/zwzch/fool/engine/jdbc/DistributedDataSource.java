package com.zwzch.fool.engine.jdbc;

import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.constant.CommonConst;
import com.zwzch.fool.common.lifecycle.AbstractLifecycle;
import com.zwzch.fool.common.utils.FileUtils;
import com.zwzch.fool.engine.config.EngineConfigLoader;
import com.zwzch.fool.engine.executor.DistributedExecutor;
import com.zwzch.fool.engine.resource.ResourceContainer;
import com.zwzch.fool.repo.config.RepoConfigLoader;
import com.zwzch.fool.rule.config.RuleConfigLoader;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DistributedDataSource extends AbstractLifecycle implements DataSource, IBase {
    private String logicDBName;
    private String logicAccountName;
    private String logicAccountPass;
    /* 从本地获得config*/
    private String configStr = null;
    private int waitOldTime = 10000;
    private boolean disConnMysql = false;
    private int batchLimit = -1;
    private int threadPoolSize = 100;
    private boolean parallelExecute = false;
    private int slaveFirstPeriod = 0;
    private int slaveFirstThresholdValue = 2;

    private Map<String, String> pdbParam = new HashMap<String, String>();
    private ResourceContainer resourceContainer=null;
    private DistributedExecutor executor = null;
    private boolean isClosed = false;
    @Override
    protected void doInit() {
        ResourceContainer rc = new ResourceContainer();
        rc.addIConfig(CommonConst.RULE_STR, new RuleConfigLoader());
        rc.addIConfig(CommonConst.REPO_STR, new RepoConfigLoader());
        rc.addIConfig(CommonConst.ENG_STR, new EngineConfigLoader());
        rc.setConfigStr(configStr);
        rc.setPdbParam(pdbParam);
        rc.setBatchLimit(batchLimit);
        rc.setDisConnMysql(disConnMysql);
        rc.setWaitOldTime(waitOldTime);
        rc.setLogicUser(logicAccountName);
        rc.setLogicPassword(logicAccountPass);
        rc.setLdbName(logicDBName);
        rc.setPdbParam(pdbParam);
        rc.setWaitOldTime(waitOldTime);
        rc.setDisConnMysql(disConnMysql);
        rc.setBatchLimit(batchLimit);
        rc.init();
        this.resourceContainer = rc;
        //TOCO 统计连接池和线程池
        executor = new DistributedExecutor(threadPoolSize);
        executor.init();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new DistributedConnection(this, executor);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }

    public void setConfigStr(String fileName) {
        this.configStr = FileUtils.readFile(fileName);
    }

    public void addPdbParam(String key, String value) {
        this.pdbParam.put(key, value);
    }

    public Map<String, String> getPdbParam() {
        return this.pdbParam;
    }

    public void setPdbParam(Map<String, String> pdbParam) {
        this.pdbParam = pdbParam;
    }

    public String getConfigStr() {
        return configStr;
    }

    public int getWaitOldTime() {
        return waitOldTime;
    }

    public void setWaitOldTime(int waitOldTime) {
        this.waitOldTime = waitOldTime;
    }

    public boolean isDisConnMysql() {
        return disConnMysql;
    }

    public void setDisConnMysql(boolean disConnMysql) {
        this.disConnMysql = disConnMysql;
    }

    public int getBatchLimit() {
        return batchLimit;
    }

    public void setBatchLimit(int batchLimit) {
        this.batchLimit = batchLimit;
    }

    public String getLogicDBName() {
        return logicDBName;
    }

    public void setLogicDBName(String logicDBName) {
        this.logicDBName = logicDBName;
    }

    public String getLogicAccountName() {
        return logicAccountName;
    }

    public void setLogicAccountName(String logicAccountName) {
        this.logicAccountName = logicAccountName;
    }

    public String getLogicAccountPass() {
        return logicAccountPass;
    }

    public void setLogicAccountPass(String logicAccountPass) {
        this.logicAccountPass = logicAccountPass;
    }

    public ResourceContainer getResourceContainer() {
        return resourceContainer;
    }

    public void setResourceContainer(ResourceContainer resourceContainer) {
        this.resourceContainer = resourceContainer;
    }
    public boolean isParallelExecute() { return parallelExecute; }

    @Override
    protected void doDestory() {
        this.isClosed = true;
        if(this.resourceContainer!=null) {
            this.resourceContainer.closeResource();
        }

        if(this.executor!=null) {
            this.executor.destroy();
        }

    }


}
