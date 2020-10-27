package com.zwzch.fool.repo.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.constant.CommonConst;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物理库连接
 *
 * */
public class PDBConfig {
    /* 属性定义*/
    public static final String NAME_STR = "name";
    public static final String IP_STR = "ip";
    public static final String PORT_STR = "port";
    public static final String DBNAME_STR = "dbname";

    private String name;
    private String ip;
    private String port;
    private String dbName;
    private Map<String, String> paramMap;
    //是否需要连接
    private boolean needToConnect = false;

    public CommonConst commonConst;

    public PDBConfig(Map<String, String> param, CommonConfig commonConfig) {
        this.paramMap = new HashMap<String, String>();

        for(String key : param.keySet()) {
            String value = param.get(key);

            if(key.equals(NAME_STR)) {
                name = value;
                continue;
            }

            if(key.equals(IP_STR)) {
                ip = value;
                continue;
            }

            if(key.equals(PORT_STR)) {
                port = value;
                continue;
            }

            if(key.equals(DBNAME_STR)) {
                dbName = value;
                continue;
            }

            paramMap.put(key, value);
        }
        this.commonConst = commonConst;

        // CommonConfig 优先设置
        if (null != commonConfig.getPdbParam()) {
            for (Map.Entry<String, String> entry : commonConfig.getPdbParam().entrySet()) {
                paramMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public PDBConfig() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public boolean isNeedToConnect() {
        return needToConnect;
    }

    public void setNeedToConnect(boolean needToConnect) {
        this.needToConnect = needToConnect;
    }

    public static List<PDBConfig> loadConfg(JsonArray pdbJsonArray, CommonConfig commonConfig) {
        List<PDBConfig> pdbConfigList = new ArrayList<PDBConfig>();
        for (JsonElement pdbJsonElement: pdbJsonArray) {
            JsonObject pdbJsonObject = JsonUtils.getAsObject(pdbJsonElement);

            /* 获得配置文件中一个pdb配置项目 */
            Map<String, String> param = new HashMap<String, String>();
            for(Map.Entry<String, JsonElement> entry : pdbJsonObject.entrySet()) {
                String value = JsonUtils.getStringFromElement(entry.getValue());
                String key = entry.getKey();

                param.put(key, value);
            }

            PDBConfig pdbConfig = new PDBConfig(param, commonConfig);
            /* 检测是否存在同名的后端 */
            for(PDBConfig pc : pdbConfigList) {
                if(pc.getName().equals(pdbConfig.getName())) {
                    throw new CommonExpection("PDBConfig loadConfig - two pdb have same name - name:" + pc.getName());
                }
            }
            pdbConfigList.add(pdbConfig);
        }
        return pdbConfigList;
    }

    public Map<String, String> getParam() {
        return this.paramMap;
    }
}
