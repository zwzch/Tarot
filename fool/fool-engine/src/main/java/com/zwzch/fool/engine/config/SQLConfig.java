package com.zwzch.fool.engine.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.utils.JsonUtils;
import com.zwzch.fool.engine.router.ParseManager;

public class SQLConfig implements IBase {
    private String originSQL = null;
    private String formatSQL = null;
    private boolean toSlave = false;


    private static final String SQL_STR = "sql";
    private static final String FORMATSQL_STR = "formatSql";
    private static final String TOSLAVE_STR = "toSlave";
    private static final String CONCURRENTCOUNT_STR = "concurrentCount";
    public SQLConfig(JsonObject jsonObject) throws Exception {
        this.originSQL = JsonUtils.getStringFromObject(jsonObject, SQL_STR);
        this.toSlave = JsonUtils.getBoolFromObject(jsonObject, TOSLAVE_STR);

        this.formatSQL = ParseManager.getFormatSql(ParseManager.parseSql(this.originSQL).get(0));

        log.info("SQLConfig init - originSQL:" + originSQL + ",formatSQL:" + formatSQL);
    }

    public String getOriginSQL() { return this.originSQL; }
    public String getFormatSQL() { return this.formatSQL; }
    public boolean getToSlave() { return this.toSlave; }

    @Override
    public String toString() {
        return toJsonObject().toString();
    }

    public JsonElement toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(SQL_STR, originSQL);
        jsonObject.addProperty(FORMATSQL_STR, formatSQL);
        jsonObject.addProperty(TOSLAVE_STR, toSlave);

        return jsonObject;
    }
}
