package com.zwzch.fool.rule.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.utils.JsonUtils;
import com.zwzch.fool.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicTableConfig implements IBase {

    private String logicTableName;

    private boolean isSharding;

    //单表时表slice
    private String singleDbSliceID;
    //分表算法
    private String funcClassName;
    //分表算法配置
    private JsonObject funcConfig;
    //单次最大操作表个数
    private int maxOpTableNum;
    //seq名
    private String seqName;
    //主键名
    private String primeKey;

    private Map<String/*sliceName*/, List<String/*物理表名*/>> topo; /* 对应的物理表结构拓扑 */

    private CommonConfig commonConfig;

    public String getLogicTableName()  { return this.logicTableName; }
    public boolean getIsSharding() { return this.isSharding; }
    public String getSingleDbSliceID() { return this.singleDbSliceID; }
    public String getFuncClassName() { return this.funcClassName; }
    public JsonObject getFuncConfig() { return this.funcConfig; }
    public int getMaxOpTablenNum() { return this.maxOpTableNum; }
    public String getSeqName() { return this.seqName; }
    public String getPrimeKey() { return this.primeKey; }
    public Map<String,List<String>> getTopo() { return this.topo; }

    public void setLogicTableName(String logicTableName) {
        this.logicTableName = logicTableName;
    }

    public boolean isSharding() {
        return isSharding;
    }

    public void setSharding(boolean sharding) {
        isSharding = sharding;
    }

    public void setSingleDbSliceID(String singleDbSliceID) {
        this.singleDbSliceID = singleDbSliceID;
    }

    public void setFuncClassName(String funcClassName) {
        this.funcClassName = funcClassName;
    }

    public void setFuncConfig(JsonObject funcConfig) {
        this.funcConfig = funcConfig;
    }

    public int getMaxOpTableNum() {
        return maxOpTableNum;
    }

    public void setMaxOpTableNum(int maxOpTableNum) {
        this.maxOpTableNum = maxOpTableNum;
    }

    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }

    public void setPrimeKey(String primeKey) {
        this.primeKey = primeKey;
    }

    public void setTopo(Map<String, List<String>> topo) {
        this.topo = topo;
    }

    public CommonConfig getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    public static final String NAME_STR = "logicTableName";
    public static final String ISSHARDING_STR = "isSharding";
    public static final String SHARDINGFUNCTION_STR = "shardingFunction";
    public static final String SHARDINGFUNCTIONDATA_STR = "shardingFunctionData";
    public static final String MAXOPTABLENUM_STR = "maxoptablenum";
    public static final String TOPO_STR = "topo";
    public static final String SLICEID_STR = "sliceID";
    public static final String TABLES_STR = "tables";
    public static final String SINGLEDBSLICEID_STR = "singleDBSliceID";
    public static final String SEQNAME_STR = "seqname";
    public static final String PRIMEKEY_STR = "primekey";
    public static final String TABLE_PREFIX_STR = "tableprefix";
    public static final String TABLE_NUMLEN_STR = "tablenumlen";
    public LogicTableConfig(JsonElement jsonElement, CommonConfig configValue) {
        this.commonConfig = configValue;

        JsonObject jsonObject = null;
        if (jsonElement != null && jsonElement instanceof JsonObject) {
            jsonObject = (JsonObject) jsonElement;
        }

        if (jsonObject == null) {
            throw new CommonExpection("LogicTableConfig - jsonElement is wrong - jsonElement:" + jsonElement);
        }

        this.logicTableName = JsonUtils.getStringFromObject(jsonObject, NAME_STR);
        this.isSharding = JsonUtils.getBoolFromObject(jsonObject, ISSHARDING_STR);

        // 解析分表属性
        try {
            this.funcClassName = JsonUtils.getStringFromObject(jsonObject, SHARDINGFUNCTION_STR);
            this.funcConfig = JsonUtils.getObjectFromObject(jsonObject, SHARDINGFUNCTIONDATA_STR);
            this.maxOpTableNum = JsonUtils.getIntFromObject(jsonObject, MAXOPTABLENUM_STR);
            this.topo = new HashMap<String, List<String>>();

            String tableprefix = "";
            int numlen = -1;
            if(JsonUtils.isExist(jsonObject, TABLE_PREFIX_STR)) {
                tableprefix = JsonUtils.getStringFromObject(jsonObject, TABLE_PREFIX_STR);
                numlen = JsonUtils.getIntFromObject(jsonObject, TABLE_NUMLEN_STR);
            }

            JsonArray topoArray = JsonUtils.getArrayFromObject(jsonObject, TOPO_STR);
            for (JsonElement j : topoArray) {
                JsonObject jobj = JsonUtils.getAsObject(j);

                String sliceName = JsonUtils.getStringFromObject(jobj, SLICEID_STR);

                List<String> tabs = new ArrayList<String>();
                JsonArray tableArray = JsonUtils.getArrayFromObject(jobj, TABLES_STR);
                for (JsonElement j1 : tableArray) {
                    List<String> tnList = StringUtils.getNumRange(JsonUtils.getStringFromElement(j1), numlen);

                    for(String tableName : tnList) {
                        tableName = tableprefix + tableName;
                        if (tabs.contains(tableName)) {
                            throw new CommonExpection("LogicTableConfig - same table name - name:" + tableName);
                        }
                        tabs.add(tableName);
                    }
                }

                if(topo.containsKey(sliceName))  {
                    throw new CommonExpection("LogicTableConfig - same slice name in two tabs - name:" + sliceName);
                }

                topo.put(sliceName, tabs);
            }
        } catch (Exception e) {
            if(this.isSharding) {
                throw new CommonExpection(e);
            } else {
                if(JsonUtils.isExist(jsonObject, SHARDINGFUNCTION_STR)) {
                    log.warn("LogicTableConfig error - used config wrong - isharding = " + this.isSharding +
                            "config:" + jsonElement.toString());
                }
            }
        }

        // 解析单表属性
        try {
            this.singleDbSliceID = JsonUtils.getStringFromObject(jsonObject, SINGLEDBSLICEID_STR);
        } catch (Exception e) {
            if(!this.isSharding) {
                throw new CommonExpection(e);
            }
        }

        // 解析seq属性
        if(JsonUtils.isExist(jsonObject, SEQNAME_STR)) {
            this.seqName = JsonUtils.getStringFromObject(jsonObject, SEQNAME_STR);
            this.primeKey = JsonUtils.getStringFromObject(jsonObject, PRIMEKEY_STR);
        }

    }

}
