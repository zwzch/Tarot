package com.zwzch.fool.rule;

import com.zwzch.fool.common.model.SqlType;
import com.zwzch.fool.rule.model.LogicTable;
import com.zwzch.fool.rule.param.IRuleParam;

import java.util.List;
import java.util.Map;

public interface IRule {
    /**
    * 是否分库
    * */
    boolean isSharding();

    /**
     * 是否分表
     * */
    boolean isShardingLogicTable(String logicTableName);

    /**
     * 获得默认的sliceName
     * */
    String getSingleSliceId();

    /**
     * 获得单表的sliceName
     * */
    String getSingleTableSliceId(String logicTableName);

    /**
     * 计算物理表名和sliceName
     * */
    List<RuleItem> route(String logicTableName, SqlType sqlType, IRuleParam comp, Map<String, String> asTableMap);

    /**
     * 获得逻辑表的全部物理表
     * */
    List<RuleItem> getFullScanRuleResult(String logicTableName);

    /**
     * 获得逻辑表和逻辑表对象的map
     * */
    Map<String, LogicTable> getTableMap();

    /**
     * 获得逻辑表的seqName
     * */
    String getSeqName(String logicTableName);

    /**
     * 获得逻辑表的主键
     * */
    String getPrimeKey(String logicTableName);

    /**
     * 获得逻辑表分表键
     * */
    List<String> getShardingKey(String logicTableName);

    /**
     * 判断两个逻辑表是否相同
     * */
    boolean isEqualMap(List<String> ltNameList);

    /**
     * 获得逻辑表的全部topo结构
     * */
    Map<String/*sliceName*/,List<String/*物理表名*/>> getTopo(String logicTableName);
}
