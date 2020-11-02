package com.zwzch.fool.engine.processor;

import com.alibaba.cobar.parser.ast.stmt.SQLStatement;
import com.alibaba.cobar.parser.recognizer.SQLParserDelegate;
import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.constant.CommonConst;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.model.SqlType;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.processor.readProcessor.SelectProcessor;
import com.zwzch.fool.engine.processor.writeProcessor.DeleteProcessor;
import com.zwzch.fool.engine.processor.writeProcessor.InsertProcessor;
import com.zwzch.fool.engine.processor.writeProcessor.UpdateProcessor;
import com.zwzch.fool.engine.router.model.ParseResult;

import java.sql.SQLException;
import java.util.List;

public class ProcessorFactory {

    public static Processor makeProcess(List<SqlObject> sqlObjects) throws SQLException {
        Processor processor;
        if(sqlObjects.size() > 1){
            processor = null;
//            processor = new BatchProcessor(sqlObjectList);
        } else {
            processor = makeProcess(sqlObjects.get(0));
            sqlObjects.get(0);
        }
        return processor;
    }

    public static Processor makeProcess(SqlObject sqlObject) throws SQLException {
        SqlType sqlType = getCommandSqlType(sqlObject);
        if (sqlType == null){
            initParseResultType(sqlObject);
            sqlType = sqlObject.getParseResult().getSqlType();
        } else {
            sqlObject.setParseResult(new ParseResult(sqlType));
        }

        switch (sqlType) {
            case SELECT:
                return new SelectProcessor(sqlObject);
            case INSERT:
                return new InsertProcessor(sqlObject);
            case UPDATE:
                return new UpdateProcessor(sqlObject);
            case DELETE:
                return new DeleteProcessor(sqlObject);
            default:
                throw new SQLException("unsupported sql type : " + sqlType.toString());

        }
    }

    public static SqlType getCommandSqlType(SqlObject sqlObject) throws SQLException {
        String sql = sqlObject.getSql().trim();
        if(sql.startsWith("explain")||sql.startsWith("EXPLAIN")){
            return SqlType.EXPLAIN;
        }
        //删除空白符
        String[] strs = sql.split("\\s+");
        if(strs.length<=1) {
            return null;
        }
        if(strs[0].equalsIgnoreCase("show")) {
            if(strs[1].equalsIgnoreCase("tables")) {
                return SqlType.SHOW_TABLES;
            }

            if(strs[1].equalsIgnoreCase("create") && strs[2].equalsIgnoreCase("table")) {
                return SqlType.SHOW_CREATE_TABLE;
            }
        }

        if(sql.toUpperCase().startsWith(CommonConst.COMMAND_ALTER_TABLE_STR)) {
            return SqlType.ALTER_TABLE;
        }
        return null;
    }

    public static void initParseResultType(SqlObject sqlObject) {
        try {
            //cobar 解析SQL类型
            List<SQLStatement> sqlStatements = SQLParserDelegate.parse(sqlObject.getSql());
            if(sqlStatements.size()>1) {
                throw new CommonExpection("not support multi sql with append with ';'");
            }
            ParseResult parseResult = new ParseResult(sqlStatements.get(0));
            sqlObject.setParseResult(parseResult);
        } catch (Throwable t) {
            throw new CommonExpection(" parser sql error, sqlObject:" + sqlObject, t);
        }
    }
}
