package com.zwzch.fool.engine.router;

import com.alibaba.cobar.parser.ast.stmt.SQLStatement;
import com.alibaba.cobar.parser.ast.stmt.dml.*;
import com.alibaba.cobar.parser.recognizer.SQLParserDelegate;
import com.zwzch.fool.engine.exception.NotSupportSQLException;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.router.model.ParseItem;
import com.zwzch.fool.engine.router.visitor.GetFormatSQLVisitor;
import com.zwzch.fool.engine.router.visitor.GetParseItemsVisitor;
import com.zwzch.fool.engine.router.visitor.GetRuleParamVisitor;
import com.zwzch.fool.rule.param.IRuleParam;

import java.sql.SQLException;
import java.util.List;

public class ParseManager {
    public static String getFormatSql(SQLStatement sqlStatement) {
        StringBuilder sb = new StringBuilder() ;
        GetFormatSQLVisitor gfsv = new GetFormatSQLVisitor(sb);
        sqlStatement.accept(gfsv);
        return gfsv.getSql();
    }
    /* 解析sql语句 */
    public static List<SQLStatement> parseSql(String sql) throws SQLException {
        /* SQL解析 */
        return SQLParserDelegate.parse(sql);
    }

    public static List<ParseItem> getParseItems(SqlObject sqlObject) {
        GetParseItemsVisitor ssv = new GetParseItemsVisitor(sqlObject);
        sqlObject.getParseResult().getSqlStatement().accept(ssv);
        return ssv.getParseItemList();
    }

    /*
     * 解析sql语句，生成规则参数
     */
    public static IRuleParam getRuleParam(ParseItem parseItem) throws SQLException {
        checkSqlTypeOkForSharding(parseItem.getSqlObject().getParseResult().getSqlStatement());
        GetRuleParamVisitor grpv = new GetRuleParamVisitor(parseItem);
        parseItem.getSqlObject().getParseResult().getSqlStatement().accept(grpv);
        return grpv.getRuleParams();
    }

    /* check sql类型 for sharding */
    public static void checkSqlTypeOkForSharding(SQLStatement sqlStatement) {
        /* 支持的sql类型判断 */
        if (sqlStatement instanceof DMLInsertStatement) {
            return;
        } else if (sqlStatement instanceof DMLSelectStatement) {
            return;
        } else if (sqlStatement instanceof DMLUpdateStatement) {
            return;
        } else if (sqlStatement instanceof DMLDeleteStatement) {
            return;
        } else if (sqlStatement instanceof DMLReplaceStatement) {
            return;
        } else {
            throw new NotSupportSQLException("only support select,insert,delete,update,replace statements when sharding");
        }
    }

}
