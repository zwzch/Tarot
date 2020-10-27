package com.zwzch.fool.rule;

import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.rule.param.IRuleParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuleResult {
    private Map<String/*sliceName*/, List<RuleItem>> map = new ConcurrentHashMap<String, List<RuleItem>>();

    private IRuleParam ruleParam = null;
    private String lastAddSliceName;

    public void addRuleItem(RuleItem ruleItem){
        String sliceName = ruleItem.getSliceName();
        if (sliceName == null){
            throw new CommonExpection("slicename is null");
        }
        if (map.containsKey(sliceName)){
            map.get(sliceName).add(ruleItem);
        } else {
            List<RuleItem> ruleItems = new ArrayList<RuleItem>();
            ruleItems.add(ruleItem);
            map.put(sliceName, ruleItems);
            lastAddSliceName = sliceName;
        }
    }

    public void addRuleItem(List<RuleItem> ruleItemList){
        if (ruleItemList == null || ruleItemList.size() == 0){
            return;
        }
        for (RuleItem ruleItem : ruleItemList){
            if (map.containsKey(ruleItem.getSliceName())){
                map.get(ruleItem.getSliceName()).add(ruleItem);
                lastAddSliceName = ruleItem.getSliceName();
            } else {
                map.put(ruleItem.getSliceName(), new ArrayList<RuleItem>());
                map.get(ruleItem.getSliceName()).add(ruleItem);
                lastAddSliceName = ruleItem.getSliceName();
            }
        }
    }

    public Map<String, List<RuleItem>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<RuleItem>> map) {
        this.map = map;
    }

    public IRuleParam getRuleParam() {
        return ruleParam;
    }

    public void setRuleParam(IRuleParam ruleParam) {
        this.ruleParam = ruleParam;
    }

    public String getLastAddSliceName() {
        return lastAddSliceName;
    }

    public void setLastAddSliceName(String lastAddSliceName) {
        this.lastAddSliceName = lastAddSliceName;
    }
}

