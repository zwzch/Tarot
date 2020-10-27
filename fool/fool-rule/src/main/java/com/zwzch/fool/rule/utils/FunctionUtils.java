package com.zwzch.fool.rule.utils;

import com.zwzch.fool.rule.RuleItem;
import com.zwzch.fool.rule.model.LogicTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FunctionUtils {
    public static boolean isShardingKeyEqual(String column, LogicTable lt, String shardingKey) {
        //列名
        if(shardingKey.equalsIgnoreCase(column)) {
            return true;
        }
        //物理表名+列名
        String withTableName = lt.getLogicTableName()+"."+shardingKey;
        if(withTableName.equalsIgnoreCase(column)) {
            return true;
        }
        //逻辑表名+列名
        String withLdbName = lt.getLdbName()+"."+withTableName;
        if(withLdbName.equalsIgnoreCase(column)) {
            return true;
        }
        //别名+列名
        if (lt.getAsTableMap() != null ){
            for(Map.Entry tt: lt.getAsTableMap().entrySet()){

                if (tt.getKey() == null || tt.getValue() == null){
                    break;
                }
                String withAsName = tt.getKey().toString() + "." + withTableName;
                String ltname = lt.getLogicTableName();
                if (ltname.equalsIgnoreCase(tt.getValue().toString()) && withAsName.equalsIgnoreCase(column)){
                    return true;
                }
            }
        }
        return false;
    }

    public static List<RuleItem> buildRuleResult(Map<String/*sliceName*/, Set<String/*物理表名*/>> m, String ltb) {
        List<RuleItem> ret = new ArrayList<>();
        for (String sliceName : m.keySet()) {
            for (String ptb : m.get(sliceName)){
                RuleItem ruleItem = new RuleItem();
                ruleItem.setSliceName(sliceName);
                ruleItem.getTableMap().put(ltb, ptb);
                ret.add(ruleItem);
            }
        }
        return ret;
    }
}
