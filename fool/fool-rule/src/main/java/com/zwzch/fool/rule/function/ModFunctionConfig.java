package com.zwzch.fool.rule.function;

import com.google.gson.JsonObject;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.utils.JsonUtils;
import com.zwzch.fool.rule.exception.RuleCalculationException;
import com.zwzch.fool.rule.model.LogicTable;
import com.zwzch.fool.rule.param.IRuleParam;
import com.zwzch.fool.rule.utils.FunctionUtils;
import com.zwzch.fool.rule.value.MultiRangeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

public class ModFunctionConfig implements IFunctionConfig{
    private ModFunction function;

    public String shardingKey;          /* 分表字段名 */
    public String tableNamePattern;     /* 物理表名模板 */
    public String sliceNamePattern;     /* slice名模板 */
    public IRuleParam.TYPE valueType;   /* 分表字段类型 */
    public int strStart,strEnd;         /* 如果是stirng类型,则计算val的字符串的起始下标*/
    public int numLen;                  /* 物理表名中数字的长度 */
    public int sliceNumLen;             /* slice名中数字的长度 */
    public int modNum;
    public int tableCount;              /* 分表数 */
    public int sliceCount;              /* slice数 */
    public ModType modType;             /* 分表类型 */

    JsonObject config;

    public ModFunctionConfig() {}
    public static final String TABLE_COUNT_STR = "tableCount";
    public static final String SLICE_COUNT_STR = "sliceCount";
    public static final String NUMBER_LEN_STR = "numLen";
    public static final String SLICE_NUM_LEN_STR = "sliceNumLen";
    public static final String MOD_NUM_STR = "modNum";
    public static final String SHARDING_KEY_STR = "shardingKey";
    public static final String TABLE_NAME_PATTERN_STR = "tableNamePattern";
    public static final String SLICE_NAME_PATTERN_STR = "sliceNamePattern";
    public static final String PARAM_TYPE_STR = "paramtype";
    public static final String STRING_START_STR = "strstart";
    public static final String STRING_END_STR = "strend";
    public static final String MOD_TYPE_STR = "modtype";

    public ModFunctionConfig(JsonObject obj, ModFunction modFunction) {
        this.function = modFunction;
        this.config = obj;

        if (JsonUtils.isExist(obj, MOD_TYPE_STR)) {
            modType = ModType.getModType(JsonUtils.getStringFromObject(obj, MOD_TYPE_STR));
        } else {
            modType = ModType.BYTABLE;
        }

        modNum = JsonUtils.getIntFromObject(obj, MOD_NUM_STR);
        shardingKey = JsonUtils.getStringFromObject(obj, SHARDING_KEY_STR);
        numLen = JsonUtils.getIntFromObject(obj, NUMBER_LEN_STR);
        tableCount = JsonUtils.getIntFromObject(obj, TABLE_COUNT_STR);
        tableNamePattern = JsonUtils.getStringFromObject(obj, TABLE_NAME_PATTERN_STR);

        if(JsonUtils.isExist(obj,SLICE_COUNT_STR)) {
            sliceCount = JsonUtils.getIntFromObject(obj, SLICE_COUNT_STR);
        } else {
            if(modType == ModType.BYSLICE) {
                throw new CommonExpection("no sliceCount in byslice mod");
            }
            sliceCount = 1;
        }

        if(JsonUtils.isExist(obj, SLICE_NAME_PATTERN_STR)) {
            sliceNamePattern = JsonUtils.getStringFromObject(obj, SLICE_NAME_PATTERN_STR);
        } else {
            if(modType == ModType.BYSLICE) {
                throw new CommonExpection("no sliceNamePattern in byslice mod");
            }
            sliceNamePattern = null;
        }

        if(JsonUtils.isExist(obj, SLICE_NUM_LEN_STR)) {
            sliceNumLen = JsonUtils.getIntFromObject(obj, SLICE_NUM_LEN_STR);
        } else {
            if(modType == ModType.BYSLICE) {
                throw new CommonExpection("no sliceNumLen in byslice mod");
            }
            sliceNumLen = -1;
        }

        String type = JsonUtils.getStringFromObject(obj, PARAM_TYPE_STR);
        if (type.equalsIgnoreCase("sting2")) {
            throw new CommonExpection("mod function do not support sting2");
        }
        if (type.equalsIgnoreCase("wdmember")){
            throw new CommonExpection("mod function do not support wdmember");
        }
        valueType = IRuleParam.TYPE.valueOfStr(type);
        if (valueType == IRuleParam.TYPE.STRING) {
            if (JsonUtils.isExist(obj, STRING_START_STR)) {
                strStart = JsonUtils.getIntFromObject(obj, STRING_START_STR);
                strEnd = JsonUtils.getIntFromObject(obj, STRING_END_STR);
            } else {
                strStart = -1;
                strEnd = -1;
            }
        }
    }

    @Override
    public long getValue(String column, Object obj) {
        try {
            if (null == obj) {
                throw new RuleCalculationException("ModFunctionConfig getValue - param is null");
            }
            String param = obj.toString();
            if (valueType == IRuleParam.TYPE.INT) {
                return Long.valueOf(param);
            }
            //sharding key是String 使用CRC计算
            if (valueType == IRuleParam.TYPE.STRING) {
                CRC32 crc = new CRC32();
                crc.reset();
                if (strStart == -1 && strEnd == -1) {
                    crc.update(param.getBytes());
                } else {
                    if (param.length() < strEnd) {
                        throw new RuleCalculationException("ModFunctionConfig getValue - param is too long - " +
                                "param:" + param + ",strEnd:" + strEnd);
                    } else {
                        crc.update(param.substring(strStart, strEnd).getBytes());
                    }
                }

                return crc.getValue();
            }

            throw new RuleCalculationException("ModFunctionConfig getValue - unknown type - param:" + param);
        } catch (Exception e) {
            throw new RuleCalculationException("ModFunctionConfig the value of sharding id is wrong, make sure set the right type", e);
        }
    }

    @Override
    public IRuleParam.TYPE getParamType(String column) {
        return this.valueType;
    }

    @Override
    public boolean isShardingKey(String column) {
        LogicTable lt = function.getLogicTable();
        return FunctionUtils.isShardingKeyEqual(column, lt, shardingKey);
    }

    @Override
    public MultiRangeValue buildRangeValue(String column, long left, long right) {
        MultiRangeValue mrv = new MultiRangeValue();
        mrv.addRangeValue(column, left, right);

        return mrv;
    }

    @Override
    public int getShardingKeyNum() {
        return 1;
    }

    @Override
    public List<String> getShardingKey() {
        List<String> ret = new ArrayList();
        ret.add(this.shardingKey);
        return ret;
    }

    @Override
    public boolean isEqualMap(IFunctionConfig config) {
        return false;
    }

    @Override
    public Object process(String method, List<Object> paramList) {
        return null;
    }

    public enum ModType {
        BYTABLE("bytable", 0), BYSLICE("byslice", 1);

        private String name;
        private int index;
        private ModType(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String toString() { return this.name; }

        public static ModType getModType(String str) {
            for(ModType type : values()) {
                if(type.name.equals(str)) {
                    return type;
                }
            }

            throw new CommonExpection("unknonw mod type, type:" + str);
        }
    }

}
