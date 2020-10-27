package com.zwzch.fool.repo;

import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.repo.config.PDBConfig;
import com.zwzch.fool.repo.config.RepoConfig;
import com.zwzch.fool.repo.config.SliceConfig;
import com.zwzch.fool.repo.expection.PdbRuntimeException;
import com.zwzch.fool.repo.model.AccountPair;
import com.zwzch.fool.repo.model.PhysicalDB;
import com.zwzch.fool.repo.model.Slice;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Repo implements IRepo, IBase {
    private String logicUser;/** 逻辑帐号名 和逻辑数据库名一起决定具体的LogicDB实例 */
    private Map<String, PhysicalDB> pdbMap = new HashMap<String, PhysicalDB>();
    private Map<String, Slice> sliceMap = new HashMap<String, Slice>();
    private Map<String, String> slice2db = new ConcurrentHashMap<String, String>();
    private RepoConfig config;
    private List<String> initNameList = new ArrayList<String>();
    private AccountPair accountPair;

    /**
     * 获取物理表实体
     *
     * @param sliceName slice名称
     * @param isUpdate 是否是更新语句
     * */
    public String selectPDBId(String sliceName, boolean isUpdate) throws CommonExpection {
        if (null != sliceName && this.sliceMap.containsKey(sliceName)) {
            String pdbName = ((Slice)this.sliceMap.get(sliceName)).getPDBName(isUpdate);
            if (pdbName != null && this.pdbMap.containsKey(pdbName)) {
                return pdbName;
            } else {
                throw new CommonExpection("Repo selectPDBId - pdbName is not in map - pdbName:" + pdbName);
            }
        } else {
            throw new CommonExpection("Repo selectPDBId - sliceName wrong - sliceName:" + sliceName);
        }
    }

    public void init(RepoConfig config, String logicUser, String logicPassword, Repo oldRepo) {
        this.config = config;
        this.accountPair = new AccountPair(logicUser, logicPassword);
        keepUnchangePDB(oldRepo);
        initPDB();
        initSlice();
        log.info("Repo init - init start - \ncontent:" + this);
    }

    /* 将oldRepo中需要保留下来的PhysicalDB加入到newRepo的pdbMap中 */
    private void keepUnchangePDB(Repo oldRepo) throws CommonExpection {
        if (oldRepo == null) {
            return;
        }
        Map<String, PhysicalDB> oldPdbMap = oldRepo.pdbMap;
        List<PDBConfig> newPDBConfig = config.getPdbConfigList();
        for (PDBConfig c : newPDBConfig) {
            String name = c.getName();
            if (oldPdbMap.containsKey(name)) {
                pdbMap.put(name, oldPdbMap.get(name));
                log.info("Repo keepUnchangeItem - update config - physical do not need change - name:" + name);
            }
        }
    }

    private void initPDB() throws CommonExpection {
        List<PDBConfig> pdbConfigList = config.getPdbConfigList();
        for (PDBConfig config: pdbConfigList) {
            String name = config.getName();
            if (pdbMap.containsKey(name)) {
                continue;
            }
            PhysicalDB pdb = new PhysicalDB();
            pdb.init(config, accountPair);
            pdbMap.put(name, pdb);
            initNameList.add(name);
        }
    }

    private void initSlice() throws CommonExpection {
        List<SliceConfig> sliceConfigList = config.getSliceConfigList();
        for (SliceConfig sliceConfig: sliceConfigList) {
            String name = sliceConfig.getName();
            if (sliceMap.containsKey(name)) {
                continue;
            }
            Slice slice = new Slice();
            slice.init(sliceConfig);
            sliceMap.put(name, slice);

            String dbname = pdbMap.get(slice.getPDBName(true)).getDbName();
            slice2db.put(name, dbname);
        }
    }

    public Set<String> getSliceNameSet() throws CommonExpection {
        return sliceMap.keySet();
    }

    public Map<String, Long> getActivalConn() throws CommonExpection {
        return null;
    }

    public Map<String, Map<String, Object>> getConnCount() throws CommonExpection {
        return null;
    }

    public String getDbNameFromSliceName(String sliceName) {
        return null;
    }

    public void checkShadow(boolean isShadow) throws CommonExpection {

    }

    public void connToBackend() throws CommonExpection {
        List<Thread> threadList = new ArrayList<Thread>();
        for (String name : pdbMap.keySet()) {
            PhysicalDB pdb = pdbMap.get(name);
            Thread thread = new Thread(new connThread(pdb));

            thread.start();
            threadList.add(thread);
        }

        Exception exception = null;
        for (Thread t : threadList) {
            try {
                t.join();
            } catch (InterruptedException e) {
                exception = new CommonExpection("connect to backend", e);
            }
        }

        if (null == exception) {
            for (String name: pdbMap.keySet()) {
                PhysicalDB pdb = pdbMap.get(name);
                if (pdb.exception != null) {
                    exception = pdb.exception;
                    break;
                }
                if (!pdb.isConnected()) {
                    exception = new CommonExpection("Repo init - can not connect to server - name:" + name);
                    break;
                }
            }
        }

        /* 只关闭新建的pdb */
        if (exception != null) {
            for (String name : initNameList) {
                PhysicalDB pdb = pdbMap.get(name);
                pdb.close();
            }

            throw new CommonExpection("Repo init - can not connect server", exception);
        }

    }

    public class connThread implements Runnable {
        PhysicalDB pdb;

        public connThread(PhysicalDB pdb) {
            this.pdb = pdb;
        }

        public void run() {
            pdb.connectToServer(true);
        }
    }

    @Override
    public String getMasterPDBId(String sliceName) throws CommonExpection {
        return selectPDBId(sliceName, true);
    }

    public String getSlavePDBId(String sliceName) throws CommonExpection {
        if (sliceName != null && this.sliceMap.containsKey(sliceName)) {
            Slice slice = (Slice)this.sliceMap.get(sliceName);
            String pdbName = slice.getSlave();
            if (pdbName != null && this.pdbMap.containsKey(pdbName)) {
                return pdbName;
            } else {
                throw new CommonExpection("Repo getSlavePDBId - pdbName is not in map - pdbName:" + pdbName);
            }
        } else {
            throw new CommonExpection("Repo getSlavePDBId - sliceName wrong - sliceName:" + sliceName);
        }
    }

    public Connection getPhysicalDBConn(String name) throws CommonExpection {
        PhysicalDB pdb = this.getPhysicalDB(name);

        try {
            return pdb.getConnection();
        } catch (Exception e) {
            throw new PdbRuntimeException("Repo getPhysicalDBConn - get Exception - pdbName:" + name, e);
        }
    }

    public PhysicalDB getPhysicalDB(String name) {
        PhysicalDB pdb = pdbMap.get(name);
        if (null == pdb && (null == name || !this.pdbMap.containsKey(name))) {
            int timeOut = 2000;
            if (((PDBConfig)this.config.getPDBConfig().get(0)).getParam().containsKey("ConnectionTimeout")) {
                timeOut = Integer.valueOf((String)((PDBConfig)this.config.getPDBConfig().get(0)).getParam().get("ConnectionTimeout"));
            } else if (((PDBConfig)this.config.getPDBConfig().get(0)).getParam().containsKey("POOL_ConnectionTimeout")) {
                timeOut = Integer.valueOf((String)((PDBConfig)this.config.getPDBConfig().get(0)).getParam().get("POOL_ConnectionTimeout"));
            }

            for(int i = 0; i < timeOut; i += 100) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException var6) {
                    throw new PdbRuntimeException("Repo getPhysicalDBConn, wait time - pdbName wrong - pdbName:" + name);
                }

                if (this.pdbMap.containsKey(name)) {
                    pdb = (PhysicalDB)this.pdbMap.get(name);
                }
            }
        }
        return pdb;
    }


}
