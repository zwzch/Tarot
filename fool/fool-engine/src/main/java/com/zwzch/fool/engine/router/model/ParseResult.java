package com.zwzch.fool.engine.router.model;

import com.alibaba.cobar.parser.ast.stmt.SQLStatement;
import com.zwzch.fool.common.model.SqlType;

import java.util.*;

public class ParseResult {
    private SQLStatement sqlStatement;

    private SqlType sqlType;
    //逻辑表
    private List<String> logicTables = new ArrayList<>();
    //字段列表
    private Set<String> columnSet = new HashSet<>();
    //别名映射
    private Map<String, String> asTableMap = new HashMap<>();

    public ParseResult(SQLStatement sqlStatement) {
        this.sqlStatement = sqlStatement;
        this.sqlType = SqlType.valueOfSQLStatement(sqlStatement);
    }

    public ParseResult(SqlType sqlType) {
        this.sqlStatement = null;
        this.sqlType = sqlType;
    }

    public SQLStatement getSqlStatement() {
        return sqlStatement;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public List<String> getLogicTables() {
        return logicTables;
    }

    public void setLogicTables(List<String> logicTables) {
        this.logicTables = logicTables;
    }

    public Set<String> getColumnSet() {
        return columnSet;
    }

    public void setColumnSet(Set<String> columnSet) {
        this.columnSet = columnSet;
    }

    public Map<String, String> getAsTableMap() {
        return asTableMap;
    }

    public void setAsTableMap(Map<String, String> asTableMap) {
        this.asTableMap = asTableMap;
    }

    public void setSqlType(SqlType sqlType) {
        this.sqlType = sqlType;
    }

    public void setSqlStatement(SQLStatement sqlStatement) {
        this.sqlStatement = sqlStatement;
    }
}
