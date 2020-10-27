package com.zwzch.fool.rule.function;

import com.google.gson.JsonObject;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.rule.RuleItem;
import com.zwzch.fool.rule.model.LogicTable;
import com.zwzch.fool.rule.param.IRuleParam;
import com.zwzch.fool.rule.utils.FunctionUtils;
import com.zwzch.fool.rule.value.*;

import java.util.*;

public class ModFunction implements IFunction{
    private LogicTable logicTable;

    public ModFunction(LogicTable logicTable) {
        this.logicTable = logicTable;
    }

    public LogicTable getLogicTable() {
        return logicTable;
    }

    public List<RuleItem> route(IRuleParam param) {
        if (null == param) {
            return logicTable.fullScanRuleResult();
        }
        Map<String/*sliceName*/, Set<String/*物理表名*/>> retMap = new HashMap<String, Set<String>>();
        CompositeValue values = param.calc(logicTable.getFuncConfig());
        for (Value value: values.getValues()) {
            //full scan
            if (value instanceof FullValue) {
                return logicTable.fullScanRuleResult();
            }
            if (value instanceof MultiRangeValue) {
                SingleRangeValue srv = ((MultiRangeValue) value).getValueMap().get(((MultiRangeValue) value).getLastInsertKey());

                // full scan
                if(srv.right==Long.MAX_VALUE  || srv.left==Long.MIN_VALUE){
                    return logicTable.fullScanRuleResult();
                }

                ModFunctionConfig config = (ModFunctionConfig)logicTable.getFuncConfig();
                long start = srv.left;
                long end = srv.right;

                if(end-start >= config.modNum) {
                    return logicTable.fullScanRuleResult();
                }

                if(end < start) {
                    continue;
                }

                enumRange(logicTable, srv, retMap);
            }
        }
        return FunctionUtils.buildRuleResult(retMap,logicTable.getLogicTableName());
    }

    /**
     * 根据区间获得table的名字
     *
     * @param lt    逻辑表对象
     * @param val   区间
     * @param ret   返回结果
     */
    private void enumRange(LogicTable lt, SingleRangeValue val, Map<String, Set<String>> ret) {
        ModFunctionConfig config = (ModFunctionConfig) logicTable.getFuncConfig();
        long step = config.modNum/(config.tableCount * config.sliceCount);
        long start = val.left/step;
        long end = val.right/step;
        // 遍历区间,逐个计算物理表名,并放入ret中
        for(long i=start; i<=end; i++) {
            calcPhyicalTableNameAndadd(lt, i*step, ret);
        }
    }

    /**
     * <p>功能描述：获得物理表下标对应的sliceName和物理表名</p>
     *
     * @date   16/4/1 下午3:07
     * @param lt    逻辑表对象
     * @param val   物理表下标
     * @param ret   返回结果
     */
    private void calcPhyicalTableNameAndadd(LogicTable lt,Long val, Map<String, Set<String>> ret) {
        ModFunctionConfig config = (ModFunctionConfig) lt.getFuncConfig();

        String tableName=null, sliceName=null;
        long tableIndex = -1;

        // slice模式
        // div = modNum/(tableCount*sliceCount)
        // index = (abs(val)%modNum)/div
        // tableIndex = index%tableCount
        // sliceIndex = index/tableCount
        ModFunctionConfig.ModType type = config.modType;
        if (type == ModFunctionConfig.ModType.BYSLICE) {
            long div = config.modNum / (config.tableCount * config.sliceCount);
            long index = (Math.abs(val) % config.modNum) / div;

            tableIndex = index % config.tableCount;
            long sliceIndex = index / config.tableCount;

            tableName = config.tableNamePattern.replace("{?}", getStringFromNum(config.numLen, tableIndex));
            sliceName = config.sliceNamePattern.replace("{?}", getStringFromNum(config.sliceNumLen, sliceIndex));


            if(!lt.isExist(sliceName, tableName)) {
                throw new CommonExpection("do not find tale, ltName:" + lt.getLogicTableName() +
                        ", sliceName:" + sliceName +
                        ", tableName:" + tableName);
            }
        }

        // talbe模式
        // div = modNum/tableCount;
        // tableIndex = (abs(val)%modNum)/div
        if (type == ModFunctionConfig.ModType.BYTABLE) {
            long div = config.modNum / config.tableCount;
            tableIndex = (Math.abs(val) % config.modNum) / div;

            tableName = config.tableNamePattern.replace("{?}", getStringFromNum(config.numLen, tableIndex));
            sliceName = lt.getSliceNameByTableName(tableName);
        }

        if(sliceName==null || tableName==null) {
            throw new CommonExpection("mod type is wrong, type:" + type.toString());
        } else {
            if (!ret.containsKey(sliceName)) {
                ret.put(sliceName, new HashSet<String>());
            }

            ret.get(sliceName).add(tableName);
        }
    }

    public long getTableIndexByName(String tableName) {
        return 0;
    }

    public String getTableNameByIndex(String sliceName, long tableIndex) {
        return null;
    }

    public IFunctionConfig build(JsonObject obj) {
        return new ModFunctionConfig(obj, this);
    }

    public void check(LogicTable lt) {

    }

    /**
     * <p>功能描述：获得固定长度的整数字符串</p>
     *
     * @param numlen    长度
     * @param val       值
     *
     * @return 整数字符串
     */
    private String getStringFromNum(int numlen, long val) {

        Formatter fmt = new Formatter();
        if (numlen <= 0) {
            fmt.format("%d", val);
        } else {
            fmt.format("%0" + numlen + "d", val);
        }

        return fmt.toString();
    }

}
