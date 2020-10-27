package com.zwzch.fool.repo.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.utils.JsonUtils;

import java.util.List;

public class RepoConfig implements IBase {
    public static final String PDB_CONFIG = "pdb";
    public static final String SLICE_CONFIG = "slice";
    public static final String ACCOUNT_CONFIG = "account";

    private List<PDBConfig> pdbConfigList;
    private List<SliceConfig> sliceConfigList;
    private List<AccountConfig> accountConfigList;
    private CommonConfig commonConfig;
    private JsonObject obj;
    public RepoConfig(List<PDBConfig> pdbConfigList, List<SliceConfig> sliceConfigList, List<AccountConfig> accountConfigList, CommonConfig commonConfig, JsonObject obj) {
        this.pdbConfigList = pdbConfigList;
        this.sliceConfigList = sliceConfigList;
        this.accountConfigList = accountConfigList;
        this.commonConfig = commonConfig;
        this.obj = obj;
    }


    public static RepoConfig loadConfig(JsonObject rootObject, CommonConfig commonConfig) {
        if(rootObject==null) {
            throw new CommonExpection("RepoConfig loadConfig - param is null");
        }
        JsonArray pdb, slice, account;
        String name;
        pdb = JsonUtils.getArrayFromObject(rootObject, PDB_CONFIG);
        slice = JsonUtils.getArrayFromObject(rootObject, SLICE_CONFIG);
        account = JsonUtils.getArrayFromObject(rootObject, ACCOUNT_CONFIG);
        return new RepoConfig(PDBConfig.loadConfg(pdb, commonConfig),
                              SliceConfig.loadConfig(slice, commonConfig),
                              AccountConfig.loadConfig(account, commonConfig),
                              commonConfig,rootObject);
    }

    public List<PDBConfig> getPdbConfigList() {
        return pdbConfigList;
    }

    public void setPdbConfigList(List<PDBConfig> pdbConfigList) {
        this.pdbConfigList = pdbConfigList;
    }

    public List<SliceConfig> getSliceConfigList() {
        return sliceConfigList;
    }

    public void setSliceConfigList(List<SliceConfig> sliceConfigList) {
        this.sliceConfigList = sliceConfigList;
    }

    public List<AccountConfig> getAccountConfigList() {
        return accountConfigList;
    }

    public void setAccountConfigList(List<AccountConfig> accountConfigList) {
        this.accountConfigList = accountConfigList;
    }

    public CommonConfig getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    public List<PDBConfig> getPDBConfig() {
        return this.pdbConfigList;
    }


}
