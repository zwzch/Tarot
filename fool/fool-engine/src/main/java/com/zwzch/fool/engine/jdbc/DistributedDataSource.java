package com.zwzch.fool.engine.jdbc;

import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.lifecycle.AbstractLifecycle;
import com.zwzch.fool.common.utils.FileUtils;
import com.zwzch.fool.engine.resource.ResourceContainer;

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
    private Map<String, String> pdbParam = new HashMap<String, String>();

    protected void doInit() {
        ResourceContainer rc = new ResourceContainer();

        rc.setConfigStr(configStr);
        rc.setPdbParam(pdbParam);
        rc.setBatchLimit(batchLimit);
        rc.setDisConnMysql(disConnMysql);
        rc.setWaitOldTime(waitOldTime);
        rc.init();
    }

    public Connection getConnection() throws SQLException {
        return null;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public void setLoginTimeout(int seconds) throws SQLException {

    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
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
}
