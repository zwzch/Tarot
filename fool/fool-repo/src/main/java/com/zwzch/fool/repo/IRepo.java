package com.zwzch.fool.repo;

import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.repo.model.PhysicalDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public interface IRepo {
    /* 获得pdb */
    public PhysicalDB getPhysicalDB(String name) throws SQLException;

    /* 读写分离 */
    public String selectPDBId(String sliceName, boolean isUpdate) throws CommonExpection;

    /* 返回指定slice的master数据库标识 */
    public String getMasterPDBId(String sliceName) throws CommonExpection;

    /* 获得slave, 需要把writer剔除 */
    public String getSlavePDBId(String sliceName) throws CommonExpection;

    /* 获得pdb的数据库连接 */
    public Connection getPhysicalDBConn(String name) throws CommonExpection ;


    /* 获得silce名字列表 */
    public Set<String> getSliceNameSet() throws CommonExpection;

    /* 获得当前连接数目 */
    public Map<String, Long> getActivalConn() throws CommonExpection;

    /* 获得各个连接状态数目 */
    public Map<String, Map<String, Object>> getConnCount() throws CommonExpection;

    /* 获得pdb对应的非影子库的数据库名 */
    public String getDbNameFromSliceName(String sliceName);

    public void checkShadow(boolean isShadow) throws  CommonExpection;
}
