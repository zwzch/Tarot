package com.zwzch.fool.common.config;

import com.google.gson.JsonElement;
import com.zwzch.fool.common.constant.CommonConfig;

public interface IConfigLoader {
     Object buildObject(JsonElement jsonEle, String ldbName, String user, String password, Object old, CommonConfig commonConfig);
}
