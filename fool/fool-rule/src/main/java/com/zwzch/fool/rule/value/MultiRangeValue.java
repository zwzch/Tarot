package com.zwzch.fool.rule.value;

import com.zwzch.fool.common.exception.CommonExpection;

import java.util.HashMap;
import java.util.Map;

public class MultiRangeValue implements Value {
    private final Map<String, SingleRangeValue> valueMap = new HashMap<String, SingleRangeValue>();
    private String lastInsertKey = null;

    public void addRangeValue(String column, long left, long right) {
        if(valueMap.containsKey(column)) {
            throw new CommonExpection("column alread in valueMap - column:" + column + ", left:" + left + ",right:" + right);
        }

        lastInsertKey = column;
        valueMap.put(column, new SingleRangeValue(column, left, right));
    }

    public Map<String, SingleRangeValue> getValueMap() { return this.valueMap; }
    public String getLastInsertKey() { return this.lastInsertKey; }
}
