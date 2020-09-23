package com.zwzch.fool.engine.test;

import com.zwzch.fool.common.utils.FileUtils;
import com.zwzch.fool.engine.jdbc.DistributedDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


public class DataSourceTest {
    private static String logicDBName = "WD_USER_APP";
    private static String logicAccountName = "WD_USER_APP_user";
    private static String logicAccountPass = "root";
    static DistributedDataSource ds;

    @Test
    public void init() {
        String configPath = "db.json";
        ds = new DistributedDataSource();
        ds.setLogicDBName(logicDBName);
        ds.setLogicAccountName(logicAccountName);
        ds.setLogicAccountPass(logicAccountPass);
        ds.setConfigStr(configPath);
        ds.init();

    }
}
