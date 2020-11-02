package com.zwzch.fool.rule;

import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.model.SqlType;
import com.zwzch.fool.rule.config.LogicTableConfig;
import com.zwzch.fool.rule.config.RuleConfig;
import com.zwzch.fool.rule.exception.RuleRuntimeException;
import com.zwzch.fool.rule.model.LogicTable;
import com.zwzch.fool.rule.param.IRuleParam;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Rule implements IRule, IBase {

    private String ldbName;

    private boolean isSharding = true;  /* 是否是单库模式 */

    private String singleSliceID = null;    /* 默认sliceName */

    private Map<String/*LogicTableName*/,LogicTable/*LogicTable*/> tables = new ConcurrentHashMap<String,LogicTable>();

    private RuleConfig config;
    private Set<String> equalLogicTablePairSet = new HashSet<String>();
    private Set<String> notEqualLogicTablePairSet = new HashSet<String>();

    public Rule(RuleConfig config) throws CommonExpection {
        if(config == null) {
            throw new CommonExpection("Rule - config is null");
        }

        this.config = config;
        this.ldbName = config.getConfigValue().getLdbName();
        if(this.ldbName==null || this.ldbName.trim().length()==0) {
            throw new CommonExpection("ldbName is null or emtpy");
        }

        this.isSharding = config.isSharding();
        this.singleSliceID = config.getSingleSliceID();

        this.tables = new HashMap<String, LogicTable>();
        for(LogicTableConfig c : config.getLtConfigList()) {
            this.tables.put(c.getLogicTableName(), new LogicTable(c));
        }


    }


    public boolean isSharding() {
        return false;
    }

    public boolean isShardingLogicTable(String logicTableName) {
        //单库单表
        if (!isSharding) {
            return false;
        }
        LogicTable lt = getLogicTableByName(logicTableName);
        if (null == lt) {
            return false;
        }
        return lt.isSharding();
    }

    @Override
    public String getSingleSliceId() {
        return null;
    }

    @Override
    public String getSingleTableSliceId(String logicTableName) {
        return null;
    }

    @Override
    public List<RuleItem> route(String logicTableName, SqlType sqlType, IRuleParam comp, Map<String, String> asTableMap) {
        if(!isSharding) {
            throw new RuleRuntimeException("Rule route - not sharding - tableName:" + logicTableName + ",param:" + comp);
        }
        try {
            LogicTable lt = null;
            if (logicTableName != null) {
                lt = getLogicTableByName(logicTableName);
                lt.setAsTableMap(asTableMap);
            }

            if (lt == null) {
                throw new CommonExpection("Rule route - table not exists - logicTableName:" + logicTableName);
            }

            List<RuleItem> rrList = null;

            if (!lt.isSharding()) {
                rrList = new ArrayList<RuleItem>();
                RuleItem rr = new RuleItem();
                rr.getTableMap().put(logicTableName, logicTableName);
                rr.setSliceName(lt.getSingleDbSliceID());
                rrList.add(rr);
            } else {
                if (comp == null) {
                    /* 当没有参数的时候，全表操作 */
                    rrList = lt.fullScanRuleResult();
                } else {
                    rrList = lt.getFunc().route(comp);
                }
            }

            if(sqlType.isDDL() && (rrList==null || rrList.size()==0)) {
                throw new RuleRuntimeException("router result is empty, table:" + logicTableName + ", rule param:" + comp);
            }

            // 一般sql语句，路由结果为空就返回一个随机的表
            if(rrList==null) {
                rrList = new ArrayList<RuleItem>();
            }

            if(rrList.size()==0) {
                RuleItem rr = new RuleItem();
                Map<String, List<String>> topo = lt.getTopo();
                for (String sliceName : topo.keySet()) {
                    rr.setSliceName(sliceName);
                    rr.getTableMap().put(lt.getLogicTableName(), topo.get(sliceName).get(0));
                    rrList.add(rr);
                    break;
                }
            }
            return rrList;
        } catch (Exception e) {
            throw new RuleRuntimeException("Rule route - get Exception - logicTableName:" + logicTableName + ",comp:" + comp, e);
        }
    }

    public List<RuleItem> getFullScanRuleResult(String logicTableName) {
        return null;
    }

    public Map<String, LogicTable> getTableMap() {
        return null;
    }

    public String getSeqName(String logicTableName) {
        return null;
    }

    public String getPrimeKey(String logicTableName) {
        return null;
    }

    public List<String> getShardingKey(String logicTableName) {
        return null;
    }

    public boolean isEqualMap(List<String> ltNameList) {
        return false;
    }

    public Map<String, List<String>> getTopo(String logicTableName) {
        return null;
    }

    private LogicTable getLogicTableByName(String logicTableName){
        return tables.get(logicTableName);
    }
}
