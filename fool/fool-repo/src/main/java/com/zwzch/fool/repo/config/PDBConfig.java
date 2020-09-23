package com.zwzch.fool.repo.config;

import com.sun.tools.javac.util.Assert;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.constant.CommonConst;

import java.util.HashMap;
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
        Assert.checkNonNull(param);
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
}
