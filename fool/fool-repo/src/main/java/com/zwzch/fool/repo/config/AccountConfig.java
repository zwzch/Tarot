package com.zwzch.fool.repo.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.utils.JsonUtils;
import com.zwzch.fool.repo.model.AccountPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountConfig {
    private String user;
    private String password;
    private Map<String, AccountPair> backend;
    public CommonConfig commonConfig;

    public AccountConfig(String user, String password, Map<String, AccountPair> backend, CommonConfig configValue) {
        this.user = user;
        this.password = password;
        this.backend = backend;
        this.commonConfig = configValue;

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, AccountPair> getBackend() {
        return backend;
    }

    public void setBackend(Map<String, AccountPair> backend) {
        this.backend = backend;
    }

    public static final String ACCOUNT_LUSER = "user";
    public static final String ACCOUNT_LPASSWORD = "password";
    public static final String ACCOUNT_BACKEND = "backend";
    public static final String ACCOUNT_PNAME = "db";
    public static final String ACCOUNT_PUSER = "user";
    public static final String ACCOUNT_PPASSWORD = "password";

    public static List<AccountConfig> loadConfig(JsonArray accountJsonArray, CommonConfig configValue) throws CommonExpection {
        if(accountJsonArray == null) {
            throw new CommonExpection("AccountConfig loadConfig - JsonArray is null");
        }

        List<AccountConfig> accountConfigList =  new ArrayList<AccountConfig>();

        for(JsonElement accountJsonElement : accountJsonArray) {
            JsonObject accountJsonObject = JsonUtils.getAsObject(accountJsonElement);

            /* 获得逻辑帐号 */
            String logicUser = JsonUtils.getStringFromObject(accountJsonObject, ACCOUNT_LUSER);
            String logicPassword = JsonUtils.getStringFromObject(accountJsonObject, ACCOUNT_LPASSWORD);

            /* 获得物理实例的帐号 */
            Map<String, AccountPair> pdbAccounts = new HashMap<String, AccountPair>();
            JsonArray pdbAccountJsonArray = JsonUtils.getArrayFromObject(accountJsonObject, ACCOUNT_BACKEND);

            for(JsonElement pdbAccountJsonElement : pdbAccountJsonArray) {
                JsonObject pdbAccountJsonObject = JsonUtils.getAsObject(pdbAccountJsonElement);

                String pdbName = JsonUtils.getStringFromObject(pdbAccountJsonObject, ACCOUNT_PNAME);
                String pdbUser = JsonUtils.getStringFromObject(pdbAccountJsonObject, ACCOUNT_PUSER);
                String pdbPassword = JsonUtils.getStringFromObject(pdbAccountJsonObject, ACCOUNT_PPASSWORD);

                if(pdbAccounts.containsKey(pdbName)) {
                    throw new CommonExpection("AccountConfig loadConfig - two pdb have same name - name:" + pdbName);
                }
                pdbAccounts.put(pdbName, new AccountPair(pdbUser, pdbPassword));
            }

            for(AccountConfig c : accountConfigList) {
                if(c.getUser().equals(logicUser)) {
                    throw new CommonExpection("AccountConfig loadConfig - two logic account have same name - name:" + logicUser);
                }
            }
            accountConfigList.add(new AccountConfig(logicUser, logicPassword, pdbAccounts, configValue));
        }

        return accountConfigList;
    }
}
