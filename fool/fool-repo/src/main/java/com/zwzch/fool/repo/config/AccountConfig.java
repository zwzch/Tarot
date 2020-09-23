package com.zwzch.fool.repo.config;

import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.repo.model.AccountPair;

import java.util.Map;

public class AccountConfig {
    private String user;
    private String password;
    private Map<String, AccountPair> backend;
    public CommonConfig commonConfig;

}
