
package com.zwzch.fool.rule.clac;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.rule.exception.RuleCalculationException;
import com.zwzch.fool.rule.function.IFunctionConfig;
import com.zwzch.fool.rule.param.IRuleParam;
import com.zwzch.fool.rule.param.RuleParamAnd;
import com.zwzch.fool.rule.param.RuleParamOr;
import com.zwzch.fool.rule.value.*;


import java.util.Map;

/**
 * <p>功能描述：计算And</p>
 */
public class CalcAnd implements ICalc {
    @Override
    public CompositeValue calc(IFunctionConfig config, IRuleParam param) throws RuleCalculationException {
        RuleParamAnd andParam = null;
		//转化为RuleParamAnd类型
        if (param instanceof RuleParamAnd) {
            andParam = (RuleParamAnd) param;
        } else {
            throw new RuleCalculationException("CalcAnd calc - param is no RuleParamAnd - param:" + param);
        }

		//分表计算andParam中的param,将结果放入reValue中
		CompositeValue reValue = null;
		for (IRuleParam c : andParam.getArgs()) {
            if(reValue == null) {
                reValue = c.calc(config);
            } else {
                reValue = and(reValue, c.calc(config));
            }
		}

		return reValue;
	}

	/**
	 * <p>功能描述：用and将vs1和vs2合并</p>
	 * <p>其他说明： </p>
	 *
	 * @date   16/4/1 下午2:16
	 * @param	vs1	shardingId区间集合
	 * @param	vs2	shardingId区间集合
	 *
	 * @return 两个集合and的结果
	 */
	private CompositeValue and(CompositeValue vs1, CompositeValue vs2) {
        CompositeValue reValue = new CompositeValue();

        for(Value v1 : vs1.getValues()){
            for(Value v2 : vs2.getValues()) {
                reValue.addRuleValue(andValue(v1, v2));
            }
        }

        return reValue;
    }


	/**
	 * <p>功能描述：用and合并两个区间 </p>
	 *
	 * @date   16/4/1 下午2:18
	 * @param v1	shardingId区间
	 * @param v2	shardingId区间
	 *
	 * @return 两个区间合并的结果
	 */
	private Value andValue(Value v1, Value v2) {
		if(v1 instanceof EmptyValue || v2 instanceof EmptyValue) {
			return new EmptyValue();
		}

        if(v1 instanceof FullValue) {
            return v2;
        }

        if(v2 instanceof FullValue) {
            return v1;
        }


		Map<String, SingleRangeValue> m1 = ((MultiRangeValue) v1).getValueMap();
		Map<String, SingleRangeValue> m2 = ((MultiRangeValue) v2).getValueMap();
		if(m1.size()!=m2.size()) {
			throw new CommonExpection("andValue wrong value, v1" + v1.toString() + ", v2" + v2.toString());
		}

		MultiRangeValue mrv = new MultiRangeValue();

		// 对每一个singleRangeValue做and处理
		for(String k1 : m1.keySet()) {
			if (!m2.containsKey(k1)) {
				// FIXME 当逻辑表名.分表字段 和 单独分表字段 同时出现在where表达式中，这里会报错
				throw new CommonExpection("andValue wrong value, v1" + v1.toString() + ", v2" + v2.toString());
			}

			SingleRangeValue rv1 = m1.get(k1);
			SingleRangeValue rv2 = m2.get(k1);

			SingleRangeValue rl, rh;
			if (rv1.left <= rv2.left) {
				rl = rv1;
				rh = rv2;
			} else {
				rl = rv2;
				rh = rv1;
			}


			if (rl.right.longValue() < rh.left.longValue()) {
				return new EmptyValue();
			} else if (rl.right.longValue() == rh.left.longValue()) {
				mrv.addRangeValue(k1, rl.right, rl.right);
			} else {
				mrv.addRangeValue(k1, rh.left, (rl.right <= rh.right ? rl.right : rh.right));
			}
		}

		return mrv;
	}
}
