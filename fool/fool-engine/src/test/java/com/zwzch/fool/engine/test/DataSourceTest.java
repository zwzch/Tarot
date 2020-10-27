package com.zwzch.fool.engine.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.constant.CommonConst;
import com.zwzch.fool.common.utils.FileUtils;
import com.zwzch.fool.engine.jdbc.DistributedDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DataSourceTest {
    private static String logicDBName = "CALIBRATION_CENTER_APP";
    private static String logicAccountName = "calibration_";
    private static String logicAccountPass = "calibration_";
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

    @Test
    public void pool() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(CommonConst.MYSQL_DRIVER);
        hikariConfig.setUsername("calibration_");
        hikariConfig.setPassword("calibration_");
//        hikariConfig.setPoolName(name + CONNECTION_POOL_STR);
        hikariConfig.setJdbcUrl("jdbc:mysql://10.37.48.137:3363/calibration_center?connectTimeout=1000&socketTimeout=12000&allowMultiQueries=true&characterEncoding=utf8");
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        System.out.println(hikariConfig);
    }

    @Test
    public void testQuery() throws SQLException {
        String configPath = "db.json";
        ds = new DistributedDataSource();
        ds.setLogicDBName(logicDBName);
        ds.setLogicAccountName(logicAccountName);
        ds.setLogicAccountPass(logicAccountPass);
        ds.setConfigStr(configPath);
        ds.init();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //select
            String sql = "SELECT * FROM slide_image_record LIMIT 0, 100";

            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                //get result
                System.out.println(rs.getInt(1));
                System.out.println(rs.getString(2));
                System.out.println(rs.getString(3));
                System.out.println(rs.getString(4));
            }
        } catch (Exception e) {
            //exception process
            e.printStackTrace();
        } finally {
            rs.close();
            ps.close();
            conn.close();
        }
    }
}
