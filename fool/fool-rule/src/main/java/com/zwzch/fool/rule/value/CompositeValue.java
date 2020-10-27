package com.zwzch.fool.rule.value;

import java.util.ArrayList;
import java.util.List;

public class CompositeValue implements Value {
    private final List<Value> valueList = new ArrayList<Value>();

    public List<Value> getValues() { return this.valueList; }
    public void addRuleValue(Value val) { this.valueList.add(val); }


}
