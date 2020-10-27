package com.zwzch.fool.rule.model;

import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.utils.ClassUtils;
import com.zwzch.fool.rule.Rule;
import com.zwzch.fool.rule.RuleItem;
import com.zwzch.fool.rule.config.LogicTableConfig;
import com.zwzch.fool.rule.function.IFunction;
import com.zwzch.fool.rule.function.IFunctionConfig;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicTable implements IBase {
    private String ldbName;
    private String logicTableName;      /* 逻辑表名 */

    private boolean isSharding;            /* 是否分表 */
    private String singleDbSliceID;     /* 单表情况下,sliceName */

    private IFunction func;              /* 分表算法 */
    private IFunctionConfig funcConfig;  /* 分表算法配置 */
    private int maxOpTablenNum;         /* 最大操作物理表数目 */
    private String seqName;             /* seq名 */
    private String primeKey;            /* 主键名 */

    private Map<String/*sliceName*/, List<String/*物理表名*/>> topo;  /* 对应的物理表结构拓扑 */
    private Map<String/* 物理表名 */, String/*sliceName*/> ptnToSliceName; /* topo的反向 */

    Map<String, String> asTableMap;

    private LogicTableConfig config;

    public static String RULE_CLASS_STR = "rule-class";
    public LogicTable(LogicTableConfig config) {
        checkElseThrow(null != config, new CommonExpection("LogicTable - config is null"));
        this.config = config;
        this.ldbName = config.getCommonConfig().getLdbName();
        checkElseThrow(this.ldbName != null && this.ldbName.trim().length() > 0, new CommonExpection("ldbName is null"));
        this.logicTableName = config.getLogicTableName();
        this.isSharding = config.getIsSharding();
        if (this.isSharding) {
            this.maxOpTablenNum = config.getMaxOpTablenNum();
            this.seqName = config.getSeqName();
            this.primeKey = config.getPrimeKey();
            this.topo = config.getTopo();
            setPtnToSLiceName();

            String clazz = config.getFuncClassName();
            try {
                Class myClass = ClassUtils.getObjectByClassName(clazz, RULE_CLASS_STR);
                Class[] paramTypes = {LogicTable.class};
                Object[] params = {this};
                Constructor constructor = myClass.getConstructor(paramTypes);
                func = (IFunction) constructor.newInstance(params);
            } catch (Exception e) {
                throw new CommonExpection("LogicTable - load function class error - class:" + clazz, e);
            }
            funcConfig = func.build(config.getFuncConfig());
        } else {
            this.singleDbSliceID = config.getSingleDbSliceID();
        }
    }

    private void setPtnToSLiceName() {
        ptnToSliceName = new HashMap<String, String>();
        for (String sliceName : topo.keySet()) {
            for (String ptName : topo.get(sliceName)) {
                ptnToSliceName.put(ptName, sliceName);
            }
        }
    }

    public List<RuleItem> fullScanRuleResult() {
        List<RuleItem> ruleItems = new ArrayList<RuleItem>();
        topo.keySet().forEach(sliceName->{
            List<RuleItem> ruleItemList = new ArrayList<RuleItem>();
            topo.get(sliceName).forEach(ptb -> {
                RuleItem rr = new RuleItem();
                rr.setSliceName(sliceName);
                rr.getTableMap().put(logicTableName, ptb);
                ruleItemList.add(rr);
            });
            ruleItems.addAll(ruleItemList);
        });
        return ruleItems;
    }

    public String getLdbName() {
        return ldbName;
    }

    public void setLdbName(String ldbName) {
        this.ldbName = ldbName;
    }

    public String getLogicTableName() {
        return logicTableName;
    }

    public void setLogicTableName(String logicTableName) {
        this.logicTableName = logicTableName;
    }

    public boolean isSharding() {
        return isSharding;
    }

    public void setSharding(boolean sharding) {
        isSharding = sharding;
    }

    public String getSingleDbSliceID() {
        return singleDbSliceID;
    }

    public void setSingleDbSliceID(String singleDbSliceID) {
        this.singleDbSliceID = singleDbSliceID;
    }

    public IFunction getFunc() {
        return func;
    }

    public void setFunc(IFunction func) {
        this.func = func;
    }

    public IFunctionConfig getFuncConfig() {
        return funcConfig;
    }

    public void setFuncConfig(IFunctionConfig funcConfig) {
        this.funcConfig = funcConfig;
    }

    public int getMaxOpTablenNum() {
        return maxOpTablenNum;
    }

    public void setMaxOpTablenNum(int maxOpTablenNum) {
        this.maxOpTablenNum = maxOpTablenNum;
    }

    public String getSeqName() {
        return seqName;
    }

    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }

    public String getPrimeKey() {
        return primeKey;
    }

    public void setPrimeKey(String primeKey) {
        this.primeKey = primeKey;
    }

    public Map<String, List<String>> getTopo() {
        return topo;
    }

    public void setTopo(Map<String, List<String>> topo) {
        this.topo = topo;
    }

    public Map<String, String> getPtnToSliceName() {
        return ptnToSliceName;
    }

    public void setPtnToSliceName(Map<String, String> ptnToSliceName) {
        this.ptnToSliceName = ptnToSliceName;
    }

    public Map<String, String> getAsTableMap() {
        return asTableMap;
    }

    public void setAsTableMap(Map<String, String> asTableMap) {
        this.asTableMap = asTableMap;
    }

    public LogicTableConfig getConfig() {
        return config;
    }

    public void setConfig(LogicTableConfig config) {
        this.config = config;
    }

    public boolean isExist(String sliceName, String tableName) {
        return topo.containsKey(sliceName)&&topo.get(sliceName).contains(tableName);
    }

    public String getSliceNameByTableName(String tableName) {
        if(ptnToSliceName.containsKey(tableName)) {
            return ptnToSliceName.get(tableName);
        }
        throw new CommonExpection("no slice have table:" + tableName);
    }
}
