package com.zwzch.fool.engine.statisic;

public class SqlStatistics {
    public String sql;
    public String ldbName;
    public String ldbAccountName;
    public String tableName;
    public String type;
    public boolean autoCommit;

    public long allCostTime = 0;
    public long initCostTime = 0;
    public long routeCostTime = 0;
    public long getConnCostTime = 0;
    public long processCostTime = 0;

    public SqlStatistics(String sql, String ldbName, String ldbAccountName) {
        this.sql = sql;
        this.ldbAccountName = ldbName;
        this.ldbAccountName = ldbAccountName;
    }

}
