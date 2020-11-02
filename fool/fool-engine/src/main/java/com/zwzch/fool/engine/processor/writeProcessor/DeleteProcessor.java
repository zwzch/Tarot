package com.zwzch.fool.engine.processor.writeProcessor;

import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.processor.ProcessorUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteProcessor extends WriteProcessor {

    public DeleteProcessor(SqlObject sqlObject) {
        super(sqlObject);
    }

    @Override
    public void doRoute() throws SQLException, CommonExpection {
        ProcessorUtil.doRoute(sqlObject);
    }

    @Override
    public boolean isUpdate() {
        return true;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public long[] getLastInsertIds() throws SQLException {
        return noInsertId;
    }

    @Override
    public ResultSet getResultSet() {
        return currentResultSet;
    }
}
