package com.zwzch.fool.engine.processor;

import com.alibaba.cobar.parser.ast.stmt.SQLStatement;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.model.SqlType;
import com.zwzch.fool.engine.exception.NotAllowMultiDbAccessException;
import com.zwzch.fool.engine.exception.NotSupportSQLException;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.router.ParseManager;
import com.zwzch.fool.engine.router.model.ActualSql;
import com.zwzch.fool.engine.router.model.JoinOn;
import com.zwzch.fool.engine.router.model.ParseItem;
import com.zwzch.fool.engine.router.visitor.*;
import com.zwzch.fool.rule.RuleItem;
import com.zwzch.fool.rule.RuleResult;
import com.zwzch.fool.rule.param.IRuleParam;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProcessorUtil {
    /**
     *路由到SingleSlice
     */
    public static void notShardingRoute(String sliceId, SqlObject sqlObject) {

        List<ParseItem> parseItemList = ParseManager.getParseItems(sqlObject);
        RuleResult ruleResult = new RuleResult();
        RuleItem ruleItem = new RuleItem();
        ruleItem.setSliceName(sliceId);
        ActualSql actualSql = getActualForOriginSql(sqlObject, sliceId);
        actualSql.setSliceName(sliceId);
        ruleItem.addActualSql(actualSql);
        ruleResult.addRuleItem(ruleItem);
        sqlObject.setRuleResult(ruleResult);
    }

    public static void doRoute(SqlObject sqlObject)  throws SQLException {
        boolean hasShardingTable = false;
        String singleTableSliceId = null;
        for(String lTabName: sqlObject.getParseResult().getLogicTables()){
            if (!sqlObject.getConn().getRule().isShardingLogicTable(lTabName)) {
                if ( null == singleTableSliceId) {
                    singleTableSliceId = sqlObject.getConn().getRule().getSingleTableSliceId(lTabName);
                } else {
                    if(!sqlObject.getConn().getRule().getSingleTableSliceId(lTabName).equals(singleTableSliceId)){
                        throw new NotSupportSQLException("not sharding table must on same slice");
                    }
                }

            } else {
                hasShardingTable = true;
                break;
            }
        }
        if (!hasShardingTable) {
            ProcessorUtil.notShardingRoute(singleTableSliceId, sqlObject);
            return;
        }

        if (sqlObject.getRuleResult().getRuleParam() != null) {
            try {
                sqlObject.getRuleResult().addRuleItem(param2RuleMap(sqlObject));
            } catch (NotAllowMultiDbAccessException e) {
                throw new CommonExpection("not allow multDB access");
            }
        }

        if (sqlObject.getRuleResult().getMap().size() != 0){
            List<ParseItem> parseItems = ParseManager.getParseItems(sqlObject);
            for (ParseItem parseItem : parseItems) {
                for (String sliceName : sqlObject.getRuleResult().getMap().keySet()) {
                    for (RuleItem ruleItem : sqlObject.getRuleResult().getMap().get(sliceName)) {
                        ruleItem.addActualSql(getActualForChangeSql(sqlObject, parseItem, ruleItem));
                    }
                }
            }
            return;
        }

        List<ParseItem> parseItemList = ParseManager.getParseItems(sqlObject);
        for(ParseItem parseItem : parseItemList) {
            /* getRuleParam  */
            IRuleParam ruleParam = ParseManager.getRuleParam(parseItem);
            sqlObject.getRuleResult().setRuleParam(ruleParam);
            List<RuleItem> ruleItems = new ArrayList<RuleItem>();
            try {
                ruleItems = param2RuleMap(sqlObject);
            } catch (NotAllowMultiDbAccessException e) {
                throw new CommonExpection("not allow multDB access");
            }
            for (RuleItem ruleItem : ruleItems){
                ruleItem.addActualSql(getActualForChangeSql(sqlObject, parseItem,ruleItem));
            }
            sqlObject.getRuleResult().addRuleItem(ruleItems);
        }
    }


    public static ActualSql getActualForOriginSql(SqlObject sqlObject, String sliceId) {
        StringBuilder sb = new StringBuilder();
        String dbName = null;
        GetLogicTableNameVisitor gv = new GetLogicTableNameVisitor();
        sqlObject.getParseResult().getSqlStatement().accept(gv);
        DeleteDBNameOutputVisitor ov = new DeleteDBNameOutputVisitor(sb, sqlObject.getDataSource().getLogicDBName(), dbName, gv.getLtNames(), gv.getAliasNames());
        sqlObject.getParseResult().getSqlStatement().accept(ov);
        String actualSqlStr = ov.getSql();
        ActualSql actualSql = new ActualSql();
        actualSql.setNewSql(actualSqlStr);
        actualSql.setUpdateIndexs(null);
        actualSql.setSqlType(sqlObject.getParseResult().getSqlType());
        actualSql.setParameterMap(sqlObject.getParameterMap());
        actualSql.setPhyTableNames(sqlObject.getParseResult().getLogicTables());
        actualSql.setSqlObject(sqlObject);
        return actualSql;
    }

    public static List<RuleItem> param2RuleMap(SqlObject sqlObject) throws SQLException, NotAllowMultiDbAccessException {
        List<String> logicTableNameList = sqlObject.getParseResult().getLogicTables();
        if(logicTableNameList.size() == 1)
            return singleLogicTableRuleRoute(sqlObject);
        else
            return bindTableRuleRoute(sqlObject);
    }

    /**
     * single LogicTable route
     *
     */
    private static List<RuleItem> singleLogicTableRuleRoute(SqlObject sqlObject) throws SQLException, NotAllowMultiDbAccessException {
        List<RuleItem> ruleItems = sqlObject.getConn().getRule().route(sqlObject.getParseResult().getLogicTables().get(0), sqlObject.getParseResult().getSqlType(), sqlObject.getRuleResult().getRuleParam(), sqlObject.getParseResult().getAsTableMap());
        /* split后,单条insert,replace只能操作一个物理表 */
        SqlType sqlType = sqlObject.getParseResult().getSqlType();

        if(sqlType==SqlType.INSERT || sqlType==SqlType.REPLACE) {
            int size = 0;
            for (RuleItem ruleItem : ruleItems){
                size ++;
            }
            if (size > 1) {
                throw new NotAllowMultiDbAccessException("not multi table for splitted insert/replace sql");
            }
        }
        //sqlObject.getRuleResult().addRuleItem(ruleItems);
        return ruleItems;
    }

    /**
     * bind table route, 用于可以做本地join的select语句
     */
    private static List<RuleItem> bindTableRuleRoute(SqlObject sqlObject){
        //check bind route
        bindRouteCheck(sqlObject);
//        List<RuleItem> ruleItems = sqlObject.getConn().getRule().bindRoute(sqlObject.getParseResult().getLogicTables(), sqlObject.getParseResult().getSqlType(), sqlObject.getRuleResult().getRuleParam(),sqlObject.getParseResult().getAsTableMap());
        //sqlObject.getRuleResult().addRuleItem(ruleItems);
        //TODO join
        return null;
    }


    private static void bindRouteCheck(SqlObject sqlObject){
        //TODO join on check
        if(sqlObject.getParseResult().getLogicTables().size() != 2){
            throw new NotSupportSQLException("only support two table join when logicTable num >1");
        }
        JoinOn joinOn = getJoinOn(sqlObject.getParseResult().getSqlStatement());
        if(joinOn == null){
            throw new NotSupportSQLException("only support two table join use join clause");
        }

        if(sqlObject.getConn().getRule().isShardingLogicTable(joinOn.getLeftTableName())&&
                sqlObject.getConn().getRule().isShardingLogicTable(joinOn.getRightTableName())&&
                sqlObject.getConn().getRule().getShardingKey(joinOn.getLeftTableName()).size() == 1&&
                sqlObject.getConn().getRule().getShardingKey(joinOn.getLeftTableName()).get(0).equals(joinOn.getLeftColumnName())&&
                sqlObject.getConn().getRule().getShardingKey(joinOn.getRightTableName()).size() == 1&&
                sqlObject.getConn().getRule().getShardingKey(joinOn.getRightTableName()).get(0).equals(joinOn.getRightColumnName())){

        }else{
            throw new NotSupportSQLException("sql join can not bind join!");
        }

        if(!sqlObject.getConn().getRule().isEqualMap(sqlObject.getParseResult().getLogicTables())){
            throw new NotSupportSQLException("sql join can not bind join!");
        }
    }

    public static JoinOn getJoinOn(SQLStatement sqlStatement){
        GetJoinOnVisitor v = new GetJoinOnVisitor();
        sqlStatement.accept(v);
        return v.getJoinOn();
    }

    /**
     * sharding sql重写
     * @return
     */
    public static ActualSql getActualForChangeSql(SqlObject sqlObject, ParseItem parseItem, RuleItem ruleItem) {
        StringBuilder sb = new StringBuilder();
        String dbName = null;
        GetLogicTableNameVisitor gv = new GetLogicTableNameVisitor();
        parseItem.getSqlObject().getParseResult().getSqlStatement().accept(gv);
        OutputVisitor ov = new OutputVisitor(sb, ruleItem.getTableMap(), null,
                parseItem.getInSqlIndex(), sqlObject.getParameterMap(), sqlObject.getConn().getLdbName(), dbName, gv.getAliasNames());
        parseItem.getSqlObject().getParseResult().getSqlStatement().accept(ov);
        String actualSqlStr = ov.getSql();
        System.out.println(actualSqlStr);

        ActualSql actualSql = new ActualSql();
        actualSql.setNewSql(actualSqlStr);
        actualSql.setParamIndexs(parseItem.getParameterIndexs());
        actualSql.setUpdateIndexs(parseItem.getUpdateParamIndexs());

        actualSql.setSqlType(sqlObject.getParseResult().getSqlType());
        actualSql.setParameterMap(sqlObject.getParameterMap());
        actualSql.setPhyTableNames(new ArrayList<String>(ruleItem.getTableMap().values()));
        actualSql.setSliceName(ruleItem.getSliceName());
        actualSql.setSqlObject(sqlObject);
        return actualSql;
    }
}
