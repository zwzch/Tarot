package com.zwzch.fool.rule.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class RuleConfig implements IBase {
    //是否分库
    private boolean isSharding;
    //单库的sliceName
    private String singleSliceID;
    //逻辑表列表
    private List<LogicTableConfig> ltConfigList;

    private CommonConfig configValue;
    private JsonObject obj;

    public static final String ISSHARDING_STR = "isSharding";
    public static final String SINGLESLICEID_STR = "singleSliceID";
    public static final String LOGICTABLES_STR = "logicTables";
    public RuleConfig(JsonObject obj, CommonConfig commonConfig) {
        this.configValue = commonConfig;
        this.obj = obj;
        this.isSharding = JsonUtils.getBoolFromObject(obj, ISSHARDING_STR);
        this.singleSliceID = JsonUtils.getStringFromObject(obj, SINGLESLICEID_STR);
        ltConfigList = new ArrayList<LogicTableConfig>();
        try {
            JsonArray tables = JsonUtils.getArrayFromObject(obj, LOGICTABLES_STR);
            for (JsonElement je : tables) {
                JsonObject o = JsonUtils.getAsObject(je);
                LogicTableConfig lt = new LogicTableConfig(o, configValue);

                for(LogicTableConfig c : ltConfigList) {
                    if(c.getLogicTableName().equals(lt.getLogicTableName())) {
                        throw new CommonExpection("RuleConfig - two logictable have same name - name:" + lt.getLogicTableName());
                    }
                }
                ltConfigList.add(lt);
            }
        } catch(Exception e) {
            if(isSharding) {
                throw new CommonExpection(e);
            } else {
                log.warn("RuleConfig error - unused config wrong - config:" + obj.toString());
            }
        }

    }

    public boolean isSharding() {
        return isSharding;
    }

    public void setSharding(boolean sharding) {
        isSharding = sharding;
    }

    public String getSingleSliceID() {
        return singleSliceID;
    }

    public void setSingleSliceID(String singleSliceID) {
        this.singleSliceID = singleSliceID;
    }

    public List<LogicTableConfig> getLtConfigList() {
        return ltConfigList;
    }

    public void setLtConfigList(List<LogicTableConfig> ltConfigList) {
        this.ltConfigList = ltConfigList;
    }

    public CommonConfig getConfigValue() {
        return configValue;
    }

    public void setConfigValue(CommonConfig configValue) {
        this.configValue = configValue;
    }

    public JsonObject getObj() {
        return obj;
    }

    public void setObj(JsonObject obj) {
        this.obj = obj;
    }
}
