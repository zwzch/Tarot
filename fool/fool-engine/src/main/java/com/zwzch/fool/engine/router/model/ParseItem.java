package com.zwzch.fool.engine.router.model;

import com.zwzch.fool.engine.model.SqlObject;

import java.util.ArrayList;
import java.util.List;

public class ParseItem {
    public SqlObject sqlObject;
    public List<Integer> parameterIndexs = new ArrayList<Integer>();;
    public List<Integer> updateParamIndexs = new ArrayList<Integer>();
    private int inSqlIndex=0;	/* sql拆分后,在原先sql中的下标 */

    public ParseItem(SqlObject sqlObject) {
        this.sqlObject = sqlObject;
    }

    public SqlObject getSqlObject() {
        return sqlObject;
    }

    public void setSqlObject(SqlObject sqlObject) {
        this.sqlObject = sqlObject;
    }

    public List<Integer> getParameterIndexs() {
        return parameterIndexs;
    }

    public void setParameterIndexs(List<Integer> parameterIndexs) {
        this.parameterIndexs = parameterIndexs;
    }

    public List<Integer> getUpdateParamIndexs() {
        return updateParamIndexs;
    }

    public void setUpdateParamIndexs(List<Integer> updateParamIndexs) {
        this.updateParamIndexs = updateParamIndexs;
    }

    public int getInSqlIndex() {
        return inSqlIndex;
    }

    public void setInSqlIndex(int inSqlIndex) {
        this.inSqlIndex = inSqlIndex;
    }
}
