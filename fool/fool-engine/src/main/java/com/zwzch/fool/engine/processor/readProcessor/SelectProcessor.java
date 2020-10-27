package com.zwzch.fool.engine.processor.readProcessor;

import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.processor.ProcessorUtil;

import java.sql.SQLException;

public class SelectProcessor extends ReadProcessor {
    public SelectProcessor(SqlObject sqlObject) throws SQLException {
        super(sqlObject);
    }

    @Override
    public void doRoute() throws SQLException, CommonExpection {
        ProcessorUtil.doRoute(sqlObject);
    }

    @Override
    public boolean isUpdate() {
        return false;
    }

    @Override
    public int getSingleUpdateCount() {
        return noUpdateCount;
    }

    @Override
    public long[] getLastInsertIds() throws SQLException {
        return noInsertId;
    }

    @Override
    public boolean getMoreResults() {
        return true;
    }
}
