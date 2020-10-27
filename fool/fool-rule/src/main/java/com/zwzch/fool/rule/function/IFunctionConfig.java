package com.zwzch.fool.rule.function;

import com.zwzch.fool.rule.param.IRuleParam;
import com.zwzch.fool.rule.value.MultiRangeValue;

import java.util.List;

public interface IFunctionConfig {
    /**
     * <p>功能描述：获得obj中的值,算法会做一些转化,最终都会转化成整型 </p>
     *
     * @date   16/4/1 下午2:59
     * @param column    字段名
     * @param obj       字段值
     *
     * @return 用于分表算法的value
     */
    public long getValue(String column, Object obj);

    /**
     * <p>功能描述：获得sharding key的类型, 现在支持int String time </p>
     *
     * @date   16/4/1 下午3:00
     * @param column    字段名
     *
     * @return  类型
     */
    public IRuleParam.TYPE getParamType(String column);

    /**
     * <p>功能描述：判断column是否是算法的sharding key </p>
     *
     * @date   16/4/1 下午3:00
     * @param column    字段名
     *
     * @return 是否是分表字段
     */
    public boolean isShardingKey(String column);

    /**
     * <p>功能描述：构建range value</p>
     *
     * @date   16/4/1 下午3:01
     * @param
     *
     * @return 计算物理表下标的区间
     */
    public MultiRangeValue buildRangeValue(String column, long left, long right);

    /**
     * <p>功能描述：返回算法中sharding key的个数 </p>
     *
     * @date   16/4/1 下午3:02
     *
     * @return 分表字段个数
     */
    /* 返回算法中sharding key的个数 */
    public int getShardingKeyNum();

    /**
     * <p>功能描述：返回算法中sharding key </p>
     *
     * @date   16/4/1 下午3:02
     *
     */
    /* 返回算法中sharding key */
    public List<String> getShardingKey();


    public boolean isEqualMap(IFunctionConfig config);

    /**
     * <p>功能描述：一般化的接口函数, 用于后期增加新功能</p>
     *
     * @param method 方法名
     * @param paramList	参数列表
     *
     * @return
     * @throws
     */
    public Object process(String method, List<Object> paramList);
}
