package com.zwzch.fool.rule.clac;

import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.rule.function.IFunctionConfig;
import com.zwzch.fool.rule.param.IRuleParam;
import com.zwzch.fool.rule.value.CompositeValue;

public interface ICalc {
    /**
     * <p>功能描述：根据配置,计算解析得到的计算表达式对象,返回shardingId的区间集合</p>
     *
     *
     * @param config    分表算法配置
     * @param param     语法解析得到的计算表达式对象
     *
     * @return shardingId的区间集合
     * @throws com.zwzch.fool.common.exception.CommonExpection
     */
    public CompositeValue calc(IFunctionConfig config, IRuleParam param) throws CommonExpection;
}
