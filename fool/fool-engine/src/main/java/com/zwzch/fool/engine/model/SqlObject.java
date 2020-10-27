package com.zwzch.fool.engine.model;

import com.zwzch.fool.common.utils.StringUtils;
import com.zwzch.fool.engine.jdbc.DistributedConnection;
import com.zwzch.fool.engine.jdbc.DistributedDataSource;
import com.zwzch.fool.engine.jdbc.DistributedStatement;
import com.zwzch.fool.engine.processor.Processor;
import com.zwzch.fool.engine.router.model.ActualSql;
import com.zwzch.fool.engine.router.model.ParseResult;
import com.zwzch.fool.rule.RuleItem;
import com.zwzch.fool.rule.RuleResult;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlObject {
    private DistributedStatement statement;
    private String sqlTemplate;
    private String sql;
    private final Map<Integer, Parameter> parameterMap;
    private ParseResult parseResult;
    private RuleResult ruleResult = new RuleResult();     /* 路由结果 */
    private Processor processor;
    private boolean isPressSql = false;
    private String traceId = null;
    private boolean haveHint = false;
    // commond
    private ActualSql actualSql;
    private boolean isQuickFuseFit = false;

    public SqlObject(DistributedStatement statement, String sql, Map<Integer, Parameter> parameterMap) {
        this.statement = statement;
        String tmpSql = sql.trim();
        if (tmpSql.charAt(tmpSql.length() - 1) == ';')
            this.sql = tmpSql.substring(0, tmpSql.length() - 1);
        else
            this.sql = sql.trim();
        this.parameterMap = parameterMap;
    }

    public List<ActualSql> getActualSqlList(){
        List<ActualSql> actualSqls = new ArrayList<ActualSql>();
        for (String sliceName : ruleResult.getMap().keySet()){
            for (RuleItem ruleItem : ruleResult.getMap().get(sliceName)){
                for(Object o : ruleItem.getSqlList()){
                    actualSqls.add((ActualSql)o);
                }
            }
        }
        return actualSqls;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public ParseResult getParseResult() {
        return parseResult;
    }

    public void setParseResult(ParseResult parseResult) {
        this.parseResult = parseResult;
    }

    public DistributedDataSource getDataSource() { return statement.getDataSource(); }
    public DistributedConnection getConn() { return statement.conn; }

    public DistributedStatement getStatement() {
        return statement;
    }

    public void setStatement(DistributedStatement statement) {
        this.statement = statement;
    }

    public String getSqlTemplate() {
        return sqlTemplate;
    }

    public void setSqlTemplate(String sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    public Map<Integer, Parameter> getParameterMap() {
        return parameterMap;
    }

    public RuleResult getRuleResult() {
        return ruleResult;
    }

    public void setRuleResult(RuleResult ruleResult) {
        this.ruleResult = ruleResult;
    }

    public Processor getProcessor() {
        return processor;
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    public boolean isPressSql() {
        return isPressSql;
    }

    public void setPressSql(boolean pressSql) {
        isPressSql = pressSql;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public boolean isHaveHint() {
        return haveHint;
    }

    public void setHaveHint(boolean haveHint) {
        this.haveHint = haveHint;
    }

    public ActualSql getActualSql() {
        return actualSql;
    }

    public void setActualSql(ActualSql actualSql) {
        this.actualSql = actualSql;
    }

    public boolean isQuickFuseFit() {
        return isQuickFuseFit;
    }

    public void setQuickFuseFit(boolean quickFuseFit) {
        isQuickFuseFit = quickFuseFit;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof SqlObject)) {
            return false;
        }

        SqlObject sqlObject = (SqlObject)obj;

        if(!StringUtils.isEquals(this.sql, sqlObject.sql)) {
            return false;
        }

        if(!StringUtils.isEquals(this.parameterMap, sqlObject.parameterMap)) {
            return false;
        }

        return true;
    }
}
