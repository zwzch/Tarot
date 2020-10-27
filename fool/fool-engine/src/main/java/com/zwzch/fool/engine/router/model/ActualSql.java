package com.zwzch.fool.engine.router.model;

import com.zwzch.fool.common.model.SqlType;
import com.zwzch.fool.engine.model.Parameter;
import com.zwzch.fool.engine.model.SqlObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActualSql {
    private String newSql;
    private List<Long> insertIdList = new ArrayList<Long>();
    private long insertId = -1;
    private ResultSet resultSet;
    private int updateCount;
    private String sliceName;
    private SqlObject sqlObject;
    private List<Integer> paramIndexs;
    private List<Integer> updateIndexs;
    private boolean toSlave = false;
    private boolean autoDirect = false;
    private Map<Integer, Parameter> parameterMap;
    private SqlType sqlType = null;
    private PreparedStatement ps;
    private  String pdbName;
    private List<String> phyTableNames;

    public void addInsertId(long id) { this.insertIdList.add(id);}

    public List<Long> getInsertIdList() { return this.insertIdList; }

    public void setInsertId(long insertId) { this.insertId = insertId; }
    public long getInsertId() { return insertId; }

    public void setInsertIdList(List<Long> insertIdList) {
        this.insertIdList = insertIdList;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public String getNewSql() {
        return newSql;
    }

    public void setNewSql(String newSql) {
        this.newSql = newSql;
    }

    public String getSliceName() {
        return sliceName;
    }

    public void setSliceName(String sliceName) {
        this.sliceName = sliceName;
    }

    public SqlObject getSqlObject() {
        return sqlObject;
    }

    public void setSqlObject(SqlObject sqlObject) {
        this.sqlObject = sqlObject;
    }

    public List<Integer> getParamIndexs() {
        return paramIndexs;
    }

    public void setParamIndexs(List<Integer> paramIndexs) {
        this.paramIndexs = paramIndexs;
    }

    public List<Integer> getUpdateIndexs() {
        return updateIndexs;
    }

    public void setUpdateIndexs(List<Integer> updateIndexs) {
        this.updateIndexs = updateIndexs;
    }

    public boolean isToSlave() {
        return toSlave;
    }

    public void setToSlave(boolean toSlave) {
        this.toSlave = toSlave;
    }

    public boolean isAutoDirect() {
        return autoDirect;
    }

    public void setAutoDirect(boolean autoDirect) {
        this.autoDirect = autoDirect;
    }

    public Map<Integer, Parameter> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<Integer, Parameter> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public void setSqlType(SqlType sqlType) {
        this.sqlType = sqlType;
    }

    public PreparedStatement getPs() {
        return ps;
    }

    public void setPs(PreparedStatement ps) {
        this.ps = ps;
    }

    public String getPdbName() {
        return pdbName;
    }

    public void setPdbName(String pdbName) {
        this.pdbName = pdbName;
    }

    public List<String> getPhyTableNames() {
        return phyTableNames;
    }

    public void setPhyTableNames(List<String> phyTableNames) {
        this.phyTableNames = phyTableNames;
    }
}
