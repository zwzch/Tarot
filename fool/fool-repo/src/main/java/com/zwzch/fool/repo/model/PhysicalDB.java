package com.zwzch.fool.repo.model;

import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zwzch.fool.common.constant.CommonConst;
import com.zwzch.fool.common.exception.ConfigException;
import com.zwzch.fool.repo.config.PDBConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zwzch.fool.common.constant.CommonConst.*;


/**
 * 物理库实体
 *
 * */
public class PhysicalDB {
    private final static String CONNECTION_POOL_SUFFIX = "-connection-pool";
    private static String POOL_PREFIX = "POOL_";
    private static String JDBC_PREFIX = "JDBC_";
    private static String SET_PREFIX = "set";
    public static final AtomicInteger INDEX = new AtomicInteger(0);

    private String name;
    private String user = "test";
    private String password = "test";
    private String urlStr;

    private HikariConfig hikariConfig;
    private MetricRegistry metricRegistry;
    private HikariDataSource connPool = null;

    /** 连接当前状态*/
    private Object lock = new Object();
    private static final int TO_CONNECT = 1;
    private static final int NOT_TO_CONNECT = 2;
    private static final int CONNECTED = 3;
    private static final int CLOSED = 4;
    private int status = TO_CONNECT;

    /** 配置相关*/
    private PDBConfig config;

    private Map<String, String> jdbcParamMap = new HashMap<String, String>();

    private Map<String, String> poolParamMap = new HashMap<String, String>();

    public Exception exception = null;

    public PhysicalDB() {
        //jdbc 超时
        jdbcParamMap.put(URL_CONNECTTIMEOUT, DEFAULT_URL_CONNECTTIMEOUT);
        //jdbc 编码
        jdbcParamMap.put(URL_CHARACTERENCODING, DEFAULT_URL_CHARACTERENCODING);
        //socket 超时
        jdbcParamMap.put(URL_SOCKETTIMEOUT, DEFAULT_URL_SOCKETTIMEOUT);
        //允许批量sql
        jdbcParamMap.put(URL_ALLOWMULTIQUERIES, DEFAULT_URL_ALLOWMULTIQUERIES);

        poolParamMap.put(POOL_CONNECTION_TIMEOUT, DEFAULT_POOL_CONNECTIONTIMEOUT);
        poolParamMap.put(POOL_IDLETIMEOUT, DEFAULT_POOL_IDLETIMEOUT);
        poolParamMap.put(POOL_MINIMUMIDLE, DEFAULT_POOL_MINIMUMIDLE);
        poolParamMap.put(POOL_MAXIMUMPOOLSIZE, DEFAULT_POOL_MAXIMUMPOOLSIZE);
        //连接池最长停留时间
        poolParamMap.put(POOL_MAXLIFETIME, DEFAULT_POOL_MAXLIFETIME);
    }

    public void init(PDBConfig config) {
        this.config = config;
        this.name = config.getName();
        this.status = config.isNeedToConnect()? TO_CONNECT: NOT_TO_CONNECT;

        //构建连接池
        hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(CommonConst.MYSQL_DRIVER);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setPoolName(name + CONNECTION_POOL_SUFFIX);
        urlStr = buildURL(config.getIp(), config.getPort(), config.getDbName(), jdbcParamMap);

        //连接池配置
        setPoolConfig(hikariConfig, poolParamMap);
        hikariConfig.setJdbcUrl(urlStr);
        metricRegistry = new MetricRegistry();
    }

    private String buildURL(String ip, String port, String dbName, Map<String, String> param) {
        StringBuilder sb = new StringBuilder();

        sb.append("jdbc:mysql://");
        sb.append(ip).append(":").append(port).append("/").append(dbName);
        sb.append("?");

        for(Map.Entry<String, String> entry : param.entrySet()) {
            String configName = entry.getKey();
            String configValue = entry.getValue();

            sb.append(configName).append("=").append(configValue).append("&");
        }

        return sb.substring(0, sb.length()-1);
    }

    private void setPoolConfig(HikariConfig config, Map<String, String> param) {
        Class<?> c = null;
        try {
            c = Class.forName("com.zaxxer.hikari.HikariConfig");
            Method[] methodArray = c.getMethods();
            Map<String, Method> methodMap = new HashMap<String, Method>();
            for (Method method: methodArray) {
                String name = method.getName();
                if(name.startsWith("set")) {
                    Class[] typeArray = method.getParameterTypes();
                    if (typeArray.length == 1) {
                        methodMap.put(name, method);
                    }
                }
            }
            for(String paramName : param.keySet()) {
                Method m = methodMap.get(SET_PREFIX + paramName);
                String value = param.get(paramName);
                Class paramTypeClass = m.getParameterTypes()[0];
                if (paramTypeClass.getName().equals(String.class.getName())) {
                    m.invoke(config, value);
                    continue;
                }

                if (paramTypeClass.getName().equals(int.class.getName())) {
                    m.invoke(config, Integer.valueOf(value));
                    continue;
                }

                if (paramTypeClass.getName().equals(long.class.getName())) {
                    m.invoke(config, Long.valueOf(value));
                    continue;
                }

                if (paramTypeClass.getName().equals(boolean.class.getName())) {
                    if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                        m.invoke(config, Boolean.valueOf(value));
                    } else {
                        throw new ConfigException("param value error - value Type:boolean - value:" + value);
                    }
                    continue;
                }

                throw new ConfigException("PhysicalDB setPoolConfig - wrong pool config name, wrong param type - " +
                        "param:" + name + ", value:" + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
