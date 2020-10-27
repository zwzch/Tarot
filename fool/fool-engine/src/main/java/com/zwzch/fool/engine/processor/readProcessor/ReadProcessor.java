package com.zwzch.fool.engine.processor.readProcessor;

import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.processor.BaseProcessor;
import com.zwzch.fool.engine.router.visitor.GetLogicTableNameVisitor;
import com.zwzch.fool.engine.router.visitor.GetSqlInfoVisitor;
import com.zwzch.fool.engine.temlatesql.TemplateSql;

import java.sql.SQLException;
import java.util.List;

public abstract class ReadProcessor extends BaseProcessor {
    public ReadProcessor(SqlObject sqlObject) throws SQLException {
        super(sqlObject);
    }

    @Override
    public void parseSql(){
        super.parseSql();

        try {
            sqlObject.setSqlTemplate(TemplateSql.getSqlTemplate(sqlObject.getSql()).toString());
        } catch (Exception e){
            throw new CommonExpection("create template sql error :" + e);
        }

        GetLogicTableNameVisitor glnv = new GetLogicTableNameVisitor();
        sqlObject.getParseResult().getSqlStatement().accept(glnv);
        List<String> ltNames = glnv.getLtNames();

        /* 基础sql检查 获取LogicTableName,ColumnSet,isDefault */
        GetSqlInfoVisitor gsiv = new GetSqlInfoVisitor(sqlObject);
        sqlObject.getParseResult().getSqlStatement().accept(gsiv);
        if (sqlObject.getParseResult().getLogicTables().size() == 0) {
            throw new CommonExpection("logic table name is null");
        }
    }
}

