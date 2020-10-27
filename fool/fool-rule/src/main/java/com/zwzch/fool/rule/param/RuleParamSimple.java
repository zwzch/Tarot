package com.zwzch.fool.rule.param;

import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.rule.clac.CalcSimple;
import com.zwzch.fool.rule.function.IFunctionConfig;
import com.zwzch.fool.rule.value.CompositeValue;

import java.util.Map;

public class RuleParamSimple implements IRuleParam{
    private OPERATION op;

    private Object leftValue;
    private TYPE leftType;

    private Object rightValue;
    private TYPE rightType;

    public OPERATION getOp() { return op; }
    public void setOp(OPERATION op) { this.op = op; }

    public Object getLeftValue() { return leftValue; }
    public TYPE getLeftType() { return leftType; }
    public void setLeft(Object leftValue, TYPE leftType) {
        this.leftValue = leftValue;
        this.leftType = leftType;
    }

    public Object getRightValue() { return rightValue; }
    public TYPE getRightType() { return rightType; }
    public void setRight(Object rightValue, TYPE rightType) {
        this.rightValue = rightValue;
        this.rightType = rightType;
    }

    public void exchangeValue() {
        Object tmpValue = leftValue;
        TYPE tmpType = leftType;

        leftValue = rightValue;
        leftType = rightType;

        rightValue = tmpValue;
        rightType = tmpType;

        op = op.getInverse();
    }


    @Override
    public CompositeValue calc(IFunctionConfig config) throws CommonExpection {
        CalcSimple simpleCalc = new CalcSimple();
        return simpleCalc.calc(config, this);
    }

    @Override
    public void changeColumn(Map<String, String> columnMap) {
        if(leftType==TYPE.COLUMN) {
            String column = leftValue.toString().toUpperCase();
            if(columnMap.containsKey(column)) {
                leftValue = columnMap.get(column);
            }
        }

        if(rightType==TYPE.COLUMN) {
            String column = rightValue.toString().toUpperCase();
            if(columnMap.containsKey(column)) {
                rightValue = columnMap.get(column);
            }
        }
    }


}
