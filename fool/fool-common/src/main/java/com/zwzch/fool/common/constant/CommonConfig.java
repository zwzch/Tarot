package com.zwzch.fool.common.constant;

import java.util.Map;

public class CommonConfig {
    private boolean isFastInit = false;
    private int version = 0;
    //逻辑DB名称
    private String ldbName = null;
    private boolean needDeepCheck = true;
    private Map<String, String> pdbParam;
    private String zkServerHost;

    public boolean isFastInit() {
        return isFastInit;
    }

    public void setFastInit(boolean fastInit) {
        isFastInit = fastInit;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getLdbName() {
        return ldbName;
    }

    public void setLdbName(String ldbName) {
        this.ldbName = ldbName;
    }

    public boolean isNeedDeepCheck() {
        return needDeepCheck;
    }

    public void setNeedDeepCheck(boolean needDeepCheck) {
        this.needDeepCheck = needDeepCheck;
    }

    public Map<String, String> getPdbParam() {
        return pdbParam;
    }

    public void setPdbParam(Map<String, String> pdbParam) {
        this.pdbParam = pdbParam;
    }

    public String getZkServerHost() {
        return zkServerHost;
    }

    public void setZkServerHost(String zkServerHost) {
        this.zkServerHost = zkServerHost;
    }
}
