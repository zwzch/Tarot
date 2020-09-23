package com.zwzch.fool.repo;

import com.zwzch.fool.repo.expection.PdbRuntimeException;
import com.zwzch.fool.repo.model.PhysicalDB;

import java.util.HashMap;
import java.util.Map;

public class Repo implements IRepo {
    private String logicUser;/** 逻辑帐号名 和逻辑数据库名一起决定具体的LogicDB实例 */
    private Map<String, PhysicalDB> pdbMap = new HashMap<String, PhysicalDB>();

    public PhysicalDB getPhysicalDB(String name) throws Exception {
        PhysicalDB pdb = pdbMap.get(name);
        if (null == pdb) {
            throw new PdbRuntimeException("Repo getPhysicalDBConn - pdbName wrong - pdbName:" + name);
        }
        return pdb;
    }
}
