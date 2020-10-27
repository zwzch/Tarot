package com.zwzch.fool.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleItem {
    public String sliceName;

    private final Map<String/*逻辑表*/, String/*物理表*/> tableMap = new HashMap<String, String>();

    /* sql语句集合,要发送给 */
    public List<Object> sqlList = new ArrayList<Object>();

    public String getSliceName() {
        return sliceName;
    }

    public void setSliceName(String sliceName) {
        this.sliceName = sliceName;
    }

    public Map<String, String> getTableMap() {
        return tableMap;
    }

    public List<Object> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<Object> sqlList) {
        this.sqlList = sqlList;
    }

    public void addActualSql(Object actualSql){
        sqlList.add(actualSql);
    }

}
