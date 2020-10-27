package com.zwzch.fool.rule.param;


import com.zwzch.fool.rule.clac.CalcOr;
import com.zwzch.fool.rule.exception.RuleCalculationException;
import com.zwzch.fool.rule.function.IFunctionConfig;
import com.zwzch.fool.rule.value.CompositeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>功能描述：语法解析得到的or对象</p>
 */
public class RuleParamOr implements IRuleParam {
	private List<IRuleParam> args = new ArrayList<IRuleParam>();

	private OPERATION op = OPERATION.OR;

	public void addComparative(IRuleParam c) {
		this.args.add(c);
	}

	public List<IRuleParam> getArgs() {
		return args;
	}

	public void setArgs(List<IRuleParam> args) {
		this.args = args;
	}

	public OPERATION getOp() {
		return op;
	}

	public CompositeValue calc(IFunctionConfig config) throws RuleCalculationException {
		CalcOr ORCald = new CalcOr();
		return ORCald.calc(config, this);
	}

	@Override
	public void changeColumn(Map<String, String> columnMap) {
		for(IRuleParam param : args) {
			param.changeColumn(columnMap);
		}
	}

	@Override
	public boolean equals(Object v) {
		if(v instanceof RuleParamOr) {
			RuleParamOr rv = (RuleParamOr)v;

			if(this.op != rv.op) {
				return false;
			}

			if(this.args.size()!=rv.args.size()) {
				return false;
			}

			for(IRuleParam p1 : this.args) {
				boolean isMatch = false;
				for(IRuleParam p2 : rv.args) {
					if(p1.equals(p2)) {
						isMatch = true;
					}
				}

				if(!isMatch) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[type:or,value:");
        for(IRuleParam param : args) {
            sb.append(param);
            sb.append(",");
        }

        sb.append("]");
        return sb.toString();
    }
}
