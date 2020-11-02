package com.zwzch.fool.engine.processor.writeProcessor;

import com.zwzch.fool.engine.exception.NotSupportSQLException;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.processor.BaseProcessor;
import com.zwzch.fool.engine.router.visitor.GetLogicTableNameVisitor;
import com.zwzch.fool.engine.router.visitor.GetSqlInfoVisitor;

import java.util.List;

public abstract class WriteProcessor extends BaseProcessor {
    public WriteProcessor(SqlObject sqlObject) {
        super(sqlObject);
    }

    public WriteProcessor() {
    }

    @Override
    public void parseSql() {
        super.parseSql();
        GetLogicTableNameVisitor glnv = new GetLogicTableNameVisitor();
        sqlObject.getParseResult().getSqlStatement().accept(glnv);
        List<String> ltNames = glnv.getLtNames();
        String ltName = ltNames.get(0);
        if (!sqlObject.getConn().getRule().isShardingLogicTable(ltName)) {
            sqlObject.getParseResult().setLogicTables(ltNames);
            return;
        }
        GetSqlInfoVisitor gsiv = new GetSqlInfoVisitor(sqlObject);
        sqlObject.getParseResult().getSqlStatement().accept(gsiv);
        if (sqlObject.getParseResult().getLogicTables().size() == 0) {
            throw new NotSupportSQLException("logic table name is null");
        }
    }
}
