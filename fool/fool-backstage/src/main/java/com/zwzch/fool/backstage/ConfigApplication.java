package com.zwzch.fool.backstage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.rowset.JdbcRowSetImpl;
import com.zwzch.fool.common.config.IConfigLoader;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.constant.CommonConst;
import com.zwzch.fool.common.exception.ConfigException;
import com.zwzch.fool.common.utils.FileUtils;
import com.zwzch.fool.common.utils.JsonUtils;
import com.zwzch.fool.repo.Repo;
import com.zwzch.fool.repo.config.AccountConfig;
import com.zwzch.fool.repo.config.RepoConfig;
import com.zwzch.fool.repo.config.RepoConfigLoader;
import com.zwzch.fool.repo.model.AccountPair;
import com.zwzch.fool.rule.Rule;
import com.zwzch.fool.rule.config.RuleConfig;
import com.zwzch.fool.rule.config.RuleConfigLoader;
import com.zwzch.fool.rule.model.LogicTable;

import javax.sql.rowset.JdbcRowSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigApplication {

    private static Map<String, IConfigLoader> confMap = new HashMap<String,IConfigLoader>();
    private static CommonConfig configValue = new CommonConfig();
    private static Map<String, Object> resource = new HashMap<String, Object>();

    public static void main(String[] args) {
        String configName = "test.json";
        String sqlName = "create.json";
        confMap.put(CommonConst.RULE_STR, new RuleConfigLoader());
        confMap.put(CommonConst.REPO_STR, new RepoConfigLoader());
        JsonObject rootObject = JsonUtils.parseJsonStr(FileUtils.readFile(configName));
        configValue.setLdbName(JsonUtils.getStringFromObject(rootObject, "name"));
        JsonObject sqlObject = JsonUtils.parseJsonStr(FileUtils.readFile(sqlName));

        for(String name : confMap.keySet()) {
            JsonElement jsonElement = JsonUtils.getElementFromObject(rootObject, name);
            Object obj = confMap.get(name).buildObject(jsonElement, null, null, null, null, configValue);
            if(obj == null) {
                throw new ConfigException("ResourceContainer initWithStr - buildObject return null - object:" + name);
            }
            resource.put(name, obj);
        }

        Rule rule = (Rule) resource.get(CommonConst.RULE_STR);
        RuleConfig ruleConfig = rule.getConfig();
        Repo repo = (Repo) resource.get(CommonConst.REPO_STR);
        RepoConfig repoConfig = repo.getConfig();
        List<AccountConfig> accountConfigList = repoConfig.getAccountConfigList();
        Map<String, AccountPair> accountPairMap = new HashMap<>();
        accountConfigList.forEach(accountConfig -> {
            accountPairMap.putAll(accountConfig.getBackend());
        });

        repoConfig.getPdbConfigList().stream().forEach(config ->{
            String url = buildURL(config.getIp(), config.getPort(), config.getDbName(), null);
            AccountPair accountPair = accountPairMap.get(config.getName());
            Connection connection = null;
            System.out.println(url);
            try {
                connection = DriverManager.getConnection(url, accountPair.getUser(), accountPair.getPassword());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            System.out.println(connection);
            try {
                JdbcRowSet jrs=new JdbcRowSetImpl(connection);
                jrs.setCommand("show databases");
                jrs.execute();
                List<String> arrs = new ArrayList<>();
                while(jrs.next()) {
                    arrs.add(jrs.getString(1));
                }
                if (!arrs.contains(config.getDbName())) {
                    String createSql = "create database " + config.getDbName();
                    System.out.println(createSql);
                    Statement statement = connection.createStatement();
                    statement.execute(createSql);
                }
                connection.createStatement().execute("use "+config.getDbName());
                Map<String, LogicTable> tableMap = rule.getTableMap();
                System.out.println(tableMap);
                Map<String, String> slice2db = repo.getSlice2db();
                System.out.println(slice2db);
                for (String ltable: tableMap.keySet()) {
                    String sqlStr = JsonUtils.getStringFromObject(sqlObject, ltable);
                    LogicTable logicTable = tableMap.get(ltable);
                    System.out.println(logicTable);
                    System.out.println(sqlStr);
                    for (String pdbName: logicTable.getPtnToSliceName().keySet()) {
                        System.out.println(pdbName);
                        String actualSql = sqlStr.replace("TABLE_TEMPLATE", pdbName);
                        System.out.println(actualSql);
                        connection.createStatement().execute(actualSql);
                    }
                }
//                JsonUtils.getStringFromObject(sqlObject, )
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }finally {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }

    private static String buildURL(String ip, String port, String dbName, Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:mysql://");
        sb.append(ip).append(":").append(port);
        return sb.toString();
    }

}
