package com.zwzch.fool.engine.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.config.IConfigLoader;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.constant.CommonConst;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.exception.ConfigException;
import com.zwzch.fool.common.lifecycle.AbstractLifecycle;
import com.zwzch.fool.common.utils.JsonUtils;
import com.zwzch.fool.engine.config.EngineConfig;
import com.zwzch.fool.repo.Repo;
import com.zwzch.fool.rule.Rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResourceContainer extends AbstractLifecycle implements IBase {
    private String ldbName = null;          /* 逻辑库名 */
    private String logicUser = null;        /* 逻辑用户名 */
    private String logicPassword = null;    /* 逻辑密码 */

    /* 从本地获得config*/
    private String configStr = null;
    private Map<String, String> pdbParam; /* 业务传入的pdb连接属性 */
    private boolean disConnMysql;         /* 是否连接mysql */
    private int batchLimit;               /* 批量限制 */
    private int waitOldTime = 10000;      /* 等待老连接的时间 */
    private  Map<String, IConfigLoader> confMap = new HashMap<String,IConfigLoader>();
    private Map<String,Object> container = new HashMap<String,Object>();    /* 存放具体的内部核心结构 */

    @Override
    protected void doInit() {
        String configStr = getConfigStr();
        log.info("load config:\n" + configStr);
        this.container = initWithStr(configStr, disConnMysql);
    }

    public Map<String, Object> initWithStr(String configStr, boolean isFastInit) throws CommonExpection {
        final JsonObject rootObject = JsonUtils.parseJsonStr(configStr);
        String configName = JsonUtils.getStringFromObject(rootObject, CommonConst.NAME_STR);
        CommonConfig commonConfig = new CommonConfig();
        commonConfig.setFastInit(isFastInit);
        commonConfig.setLdbName(ldbName);
        commonConfig.setPdbParam(pdbParam);
        commonConfig.setVersion(0);
        Map<String, Object> resource = new HashMap<String, Object>();

        for(String name : confMap.keySet()) {
            JsonElement jsonElement = JsonUtils.getElementFromObject(rootObject, name);
            Object obj = confMap.get(name).buildObject(jsonElement, ldbName, logicUser, logicPassword, container.get(name), commonConfig);
            if(obj == null) {
                throw new ConfigException("ResourceContainer initWithStr - buildObject return null - object:" + name);
            }
            resource.put(name, obj);
        }

        EngineConfig engineConfig = (EngineConfig) resource.get(CommonConst.ENG_STR);

        if (batchLimit >= 0){
            engineConfig.setBatchLimit(batchLimit);
        }
        setAndcheckConfig(resource);
        connectToBackend(resource);
        return resource;
    }

    private void connectToBackend(Map<String, Object> resource) {
        Repo repo = getRepo(resource);
        repo.connToBackend();
    }

    private void setAndcheckConfig(Map<String, Object> resource) {
        Repo repo = getRepo(resource);
        Rule rule = getRule(resource);
        Set<String> sliceNameSet = repo.getSliceNameSet();

    }

    public Repo getRepo(Map<String, Object> map) {
        Object obj = map.get(CommonConst.REPO_STR);
        if (obj != null && obj instanceof Repo) {
            return (Repo) obj;
        } else {
            throw new ConfigException("ResourceContainer getRepo - object is not REPO");
        }
    }

    public Rule getRule(Map<String, Object> map) {
        Object obj = map.get(CommonConst.RULE_STR);
        if (obj != null && obj instanceof Rule) {
            return (Rule) obj;
        } else {
            throw new ConfigException("ResourceContainer getRepo - object is not REPO");
        }
    }

    public EngineConfig getEngineConfig(Map<String, Object> map) {
        Object obj = map.get(CommonConst.ENG_STR);
        if(obj!=null && obj instanceof EngineConfig) {
            return (EngineConfig)obj;
        } else {
            throw new ConfigException("ResourceContainer getEngineConfig - object is not EngineConfig");
        }
    }

    public void addIConfig(String name, IConfigLoader config) {
        confMap.put(name, config);
    }
    public String getConfigStr() {
        return configStr;
    }

    public void setConfigStr(String configStr) {
        this.configStr = configStr;
    }
    public void setPdbParam(Map<String, String> pdbParam) {
        this.pdbParam = pdbParam;
    }

    public Map<String, String> getPdbParam() {
        return pdbParam;
    }

    public boolean isDisConnMysql() {
        return disConnMysql;
    }

    public void setDisConnMysql(boolean disConnMysql) {
        this.disConnMysql = disConnMysql;
    }

    public int getBatchLimit() {
        return batchLimit;
    }

    public void setBatchLimit(int batchLimit) {
        this.batchLimit = batchLimit;
    }

    public int getWaitOldTime() {
        return waitOldTime;
    }

    public void setWaitOldTime(int waitOldTime) {
        this.waitOldTime = waitOldTime;
    }

    public String getLdbName() {
        return ldbName;
    }

    public void setLdbName(String ldbName) {
        this.ldbName = ldbName;
    }

    public String getLogicUser() {
        return logicUser;
    }

    public void setLogicUser(String logicUser) {
        this.logicUser = logicUser;
    }

    public String getLogicPassword() {
        return logicPassword;
    }

    public void setLogicPassword(String logicPassword) {
        this.logicPassword = logicPassword;
    }

    public Map<String, IConfigLoader> getConfMap() {
        return confMap;
    }

    public void setConfMap(Map<String, IConfigLoader> confMap) {
        this.confMap = confMap;
    }

    public Map<String, Object> getContainer() {
        return container;
    }

    public void setContainer(Map<String, Object> container) {
        this.container = container;
    }

    public Map<String, Object> getResource() { return this.container; }

    public void closeResource() {
        getRepo(container).close();
    }
}
