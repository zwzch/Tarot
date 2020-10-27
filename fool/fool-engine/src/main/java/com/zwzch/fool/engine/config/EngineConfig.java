package com.zwzch.fool.engine.config;

import com.alibaba.cobar.parser.ast.stmt.SQLStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLCreateTableStatement;
import com.google.gson.JsonObject;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.exception.ConfigException;
import com.zwzch.fool.common.utils.JsonUtils;
import com.zwzch.fool.engine.router.ParseManager;

import java.util.HashMap;
import java.util.Map;

public class EngineConfig {
    private CommonConfig commonConfig;

    private static String SQLCONFIGS_STR = "sqlConfigs";
    private static String THREADPOOLSIZE_STR = "threadPoolSize";
    private static String BATCHLIMIT = "batchLimit";
    private static String LOGORIGINSQL = "logOriginSql";
    private static String LOGACTUALSQL = "logActualSql";
    private static final String LOG_NON_STANDARD_SQL = "logNonStandardSql";
    //慢sql 执行时间设定   单位  ms
    private static String QUICKFUSE_EXEC_TIME = "quickFuseExecTime";
    //超过慢sql执行个数，占总执行数百分比
    private static String QUICKFUSE_FAIL_PERCENT = "quickFuseFailPercent";
    //默认值
    private int batchLimit = 0;

    private boolean logOriginSql = false;

    private boolean logActualSql = false;
    private Map<String, SQLConfig> sqlConfigMap = new HashMap<String, SQLConfig>();



    // 记录非标SQL
    private boolean logNonStandardSql = true;

    public EngineConfig(JsonObject rootObject, CommonConfig commonConfig) {
        this.commonConfig = commonConfig;

        if (JsonUtils.isExist(rootObject, BATCHLIMIT)) {
            batchLimit = JsonUtils.getIntFromObject(rootObject, BATCHLIMIT);
            if (batchLimit < 0) {
                throw new ConfigException("EngineConfig batchLimit smaller than 0, is:" + batchLimit);
            }
        }

        if (JsonUtils.isExist(rootObject, LOG_NON_STANDARD_SQL)) {
            this.logNonStandardSql = JsonUtils.getBoolFromObject(rootObject, LOG_NON_STANDARD_SQL);
        }

        if (JsonUtils.isExist(rootObject, LOGORIGINSQL)) {
            logOriginSql = JsonUtils.getBoolFromObject(rootObject, LOGORIGINSQL);
        }

        if (JsonUtils.isExist(rootObject, LOGACTUALSQL)) {
            logActualSql = JsonUtils.getBoolFromObject(rootObject, LOGACTUALSQL);
        }
    }

    /* 判断sql是否被配置 */
    public boolean isConfigSql(SQLStatement sqlStatement) {
        if(sqlStatement instanceof DDLCreateTableStatement) {   // cobar中的create命令output过程没有完善，所以这里直接跳过
            return false;
        }

        String sqlStr = ParseManager.getFormatSql(sqlStatement);
        return sqlConfigMap.containsKey(sqlStr);
    }

    public int getBatchLimit() {
        return batchLimit;
    }

    public void setBatchLimit(int batchLimit) {
        this.batchLimit = batchLimit;
    }

    public boolean isLogOriginSql() {
        return logOriginSql;
    }

    public void setLogOriginSql(boolean logOriginSql) {
        this.logOriginSql = logOriginSql;
    }

    public boolean isLogActualSql() {
        return logActualSql;
    }

    public void setLogActualSql(boolean logActualSql) {
        this.logActualSql = logActualSql;
    }

    public boolean isLogNonStandardSql() {
        return logNonStandardSql;
    }

    public void setLogNonStandardSql(boolean logNonStandardSql) {
        this.logNonStandardSql = logNonStandardSql;
    }
}
