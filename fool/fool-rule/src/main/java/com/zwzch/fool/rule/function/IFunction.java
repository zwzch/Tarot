package com.zwzch.fool.rule.function;

import com.google.gson.JsonObject;
import com.zwzch.fool.rule.RuleItem;
import com.zwzch.fool.rule.model.LogicTable;
import com.zwzch.fool.rule.param.IRuleParam;

import java.util.List;

public interface IFunction {

    /**
     * 根据逻辑表和shardingId的计算表达式,获得后段的slice和物理表名
     *
     * */
    List<RuleItem> route(IRuleParam param);

    /**
     * 根据逻辑表名获得slice和物理表名
     *
     * */
    long getTableIndexByName(String tableName);

    /**
     * 根据slice和物理表名获得逻辑表名
     *
     * */
    String getTableNameByIndex(String sliceName, long tableIndex);

    /**
     * 构建分表算法配置对象
     *
     * */
    IFunctionConfig build(JsonObject obj);

    /**
     * 使用分表算法检测逻辑表中的配置是否ok
     *
     * */
    void check(LogicTable lt);


}
