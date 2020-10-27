
package com.zwzch.fool.rule.clac;


import com.zwzch.fool.rule.exception.RuleCalculationException;
import com.zwzch.fool.rule.function.IFunctionConfig;
import com.zwzch.fool.rule.param.IRuleParam;
import com.zwzch.fool.rule.param.RuleParamOr;
import com.zwzch.fool.rule.value.CompositeValue;

/**
 * <p>功能描述：计算Or</p>
 */
public class CalcOr implements ICalc {
    @Override
    public CompositeValue calc(IFunctionConfig config, IRuleParam param) throws RuleCalculationException {
        RuleParamOr orParam = null;
        if (param instanceof RuleParamOr) {
            orParam = (RuleParamOr) param;
        } else {
            throw new RuleCalculationException("CalcOr calc - param is no RuleParamOr - param:" + param);
        }

        CompositeValue reValue = new CompositeValue();

		for (IRuleParam c : orParam.getArgs()) {
				CompositeValue re = c.calc(config);
				reValue = or(reValue, re);
			}
		return reValue;
    }

	/**
	 * <p>功能描述：用or将vs1和vs2合并</p>
	 * <p>其他说明： </p>
	 *
	 * @date   16/4/1 下午2:16
	 * @param	v1	shardingId区间集合
	 * @param	v2	shardingId区间集合
	 *
	 * @return 两个集合or的结果
	 */
    private CompositeValue or(CompositeValue v1, CompositeValue v2) {
        CompositeValue val = new CompositeValue();

        //TODO 整理合并
        val.getValues().addAll(v1.getValues());
        val.getValues().addAll(v2.getValues());
        return val;
    }
}
