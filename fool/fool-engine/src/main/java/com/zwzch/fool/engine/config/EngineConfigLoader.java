package com.zwzch.fool.engine.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zwzch.fool.common.config.IConfigLoader;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.exception.ConfigException;

public class EngineConfigLoader implements IConfigLoader {

    @Override
    public Object buildObject(JsonElement jsonEle, String ldbName, String user, String password, Object old, CommonConfig commonConfig) {
        if(jsonEle==null) {
            throw new ConfigException("EngineConfigLoader buildObject - param is null");
        }

        JsonObject rootObj = null;
        if(jsonEle instanceof JsonObject) {
            rootObj = (JsonObject)jsonEle;
        } else {
            throw new ConfigException("EngineConfigLoader buildObject - jsonEle type wrong");
        }
        return new EngineConfig(rootObj, commonConfig);
    }
}
