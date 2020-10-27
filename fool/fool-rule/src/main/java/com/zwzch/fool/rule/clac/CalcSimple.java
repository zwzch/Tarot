
package com.zwzch.fool.rule.clac;

import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.rule.exception.RuleCalculationException;
import com.zwzch.fool.rule.function.IFunctionConfig;
import com.zwzch.fool.rule.param.IRuleParam;
import com.zwzch.fool.rule.param.RuleParamSimple;
import com.zwzch.fool.rule.value.CompositeValue;
import com.zwzch.fool.rule.value.FullValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.zwzch.fool.rule.param.IRuleParam.OPERATION.*;

/**
 * <p>功能描述：计算一般的表达式</p>
 */
public class CalcSimple implements ICalc {
    @Override
    public CompositeValue calc(IFunctionConfig config, IRuleParam param) throws CommonExpection {
		RuleParamSimple sParam = null;
        if(param instanceof RuleParamSimple) {
			sParam = (RuleParamSimple)param;
        } else {
            throw new CommonExpection("CalcSimple calc - param is no RuleParamSimple - param:" + param);
        }

        // 存放结果
        CompositeValue reValue = new CompositeValue();

        // 判断是否存在shardingkey 或者全部都是常量
        if(sParam.getLeftType()== IRuleParam.TYPE.COLUMN) {
            if(sParam.getRightType()== IRuleParam.TYPE.COLUMN) {
                /* 没有常量 fullValue */
                reValue.addRuleValue(new FullValue());
                return reValue;

            } else {
                if(!config.isShardingKey((String)sParam.getLeftValue())) {
                    reValue.addRuleValue(new FullValue());
                    return reValue;
                }

            }
        } else {
            if(sParam.getRightType()== IRuleParam.TYPE.COLUMN) {
                if(!config.isShardingKey((String)sParam.getRightValue())) {
                    reValue.addRuleValue(new FullValue());
                    return reValue;
                } else {
                    sParam.exchangeValue();
                }
            } else {
                reValue.addRuleValue(new FullValue());
                return reValue;
            }
        }

        // 左操作数的值
        Object leftValue = sParam.getLeftValue();
        // 右操作数的值
        Object rightValue = sParam.getRightValue();
        // 左操作数的类型
        IRuleParam.TYPE leftType = sParam.getLeftType();
        // 右操作数的类型
        IRuleParam.TYPE rightType = sParam.getRightType();

        // 当前字段名
        String column = leftValue.toString();

		switch (sParam.getOp()) {
        case AND:
			throw new RuleCalculationException("CalcSimple calc - not support AND - param:" + param);
		case OR:
			throw new RuleCalculationException("CalcSimple calc - not support OR - param:" + param);
		case GT:
			// ">";
            if(config.getParamType(column) == IRuleParam.TYPE.STRING) {
                reValue.addRuleValue(new FullValue());
            } else {
                long val = config.getValue(column, rightValue);
                if(val==Long.MAX_VALUE) {
                    throw new RuleCalculationException("can not > LONG_MAX_VALUE");
                }

                reValue.addRuleValue(config.buildRangeValue(column, val+1, Long.MAX_VALUE));
            }
			return reValue;

		case LT:
			// "<";
            if(config.getParamType(column) == IRuleParam.TYPE.STRING) {
                reValue.addRuleValue(new FullValue());
            } else {
                long val = config.getValue(column, rightValue);
                if(val==Long.MIN_VALUE) {
                    throw new RuleCalculationException("can not < LONG_MIN_VALUE");
                }

                reValue.addRuleValue(config.buildRangeValue(column, Long.MIN_VALUE, val-1));
            }
			return reValue;

		case IN:
			// "IN";
            // 使用or连接的等于操作替代IN操作
			Set<Object> set = new HashSet<Object>();
			set.addAll((Collection<? extends Object>) rightValue);
			for (Object l : set) {
				reValue.addRuleValue(config.buildRangeValue(column, config.getValue(column, l), config.getValue(column, l)));
			}
			return reValue;

		case GT_EQ:
			// ">=";
            if(config.getParamType(column) == IRuleParam.TYPE.STRING) {
                reValue.addRuleValue(new FullValue());
            } else {
                reValue.addRuleValue(config.buildRangeValue(column, config.getValue(column, rightValue), Long.MAX_VALUE));
            }
            return reValue;

		case LT_EQ:
			// "<=";
            if(config.getParamType(column) == IRuleParam.TYPE.STRING) {
                reValue.addRuleValue(new FullValue());
            } else {
                reValue.addRuleValue(config.buildRangeValue(column, Long.MIN_VALUE, config.getValue(column, rightValue)));
            }
			return reValue;

		case EQ:
			// "=";
			reValue.addRuleValue(config.buildRangeValue(column, config.getValue(column, rightValue), config.getValue(column, rightValue)));
			return reValue;

		case LIKE:
			// "LIKE";
            // 不支持,直接返回fullValue
            if(config.getParamType(column) == IRuleParam.TYPE.STRING) {
                reValue.addRuleValue(new FullValue());
                return reValue;
            } else {
                throw new RuleCalculationException("CalcSimple calc - not support LIKE with INT - param" + param);
            }

		case IS_NULL:
			// "IS NULL";
            reValue.addRuleValue(new FullValue());
            return reValue;

		case IS:
			// "IS";
            reValue.addRuleValue(new FullValue());
            return reValue;

		case IS_NOT_NULL:
			// "IS NOT NULL";
            reValue.addRuleValue(new FullValue());
            return reValue;

		case NOT_EQ:
			// "!=";
            reValue.addRuleValue(new FullValue());
            return reValue;

		case CONSTANT:
			// "CONSTANT";
            reValue.addRuleValue(new FullValue());
            return reValue;

		case NULL_SAFE_EQUAL:
			// return "<=>";
		case XOR:
            throw new RuleCalculationException("CalcSimple calc - not support XOR - param" + param);
		default:
            throw new RuleCalculationException("CalcSimple calc - not support DEFAULT - param" + param);
		}
	}
}
