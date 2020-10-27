package com.zwzch.fool.repo.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.config.IConfigLoader;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.repo.Repo;
import org.apache.commons.lang3.StringUtils;

public class RepoConfigLoader implements IConfigLoader, IBase {


    public Object buildObject(JsonElement rootEle, String ldbName, String user, String password, Object old, CommonConfig commonConfig) {
        checkElseThrow(StringUtils.isNoneBlank(ldbName, user, password), new CommonExpection("RepoConfig buildObject - param is null"));
        Repo oldRepo = null;
        if(null != old && old instanceof Repo) {
            oldRepo = (Repo)old;
            checkElseThrow(null != oldRepo, new CommonExpection("oldRepo type error"));
        }
        JsonObject rootObject = (JsonObject) rootEle;
        RepoConfig repoConfig = RepoConfig.loadConfig(rootObject, commonConfig);
        Repo repo = new Repo();
        repo.init(repoConfig, user, password, oldRepo);

        return repo;
    }
}
