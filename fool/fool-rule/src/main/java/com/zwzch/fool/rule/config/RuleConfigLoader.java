package com.zwzch.fool.rule.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.config.IConfigLoader;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.rule.Rule;
import org.apache.commons.lang3.StringUtils;

public class RuleConfigLoader implements IConfigLoader, IBase {

    public Object buildObject(JsonElement jsonEle, String ldbName, String user, String password, Object old, CommonConfig commonConfig) {
        checkElseThrow(StringUtils.isNoneBlank(ldbName, user, password), new CommonExpection("RuleConfig buildObject - param is null"));
        JsonObject root = (JsonObject) jsonEle;
        RuleConfig config = new RuleConfig(root, commonConfig);
        return new Rule(config);
    }
}
