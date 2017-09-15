package com.skspruce.ism.detect.spark.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * mysql帮助工具类
 */
public class SQLHelper {

    private static String MYSQL_IAS = "mysqlIas";
    private static String MYSQL_PROBE = "mysqlDetect";

    private ComboPooledDataSource mysqlIasDS;
    private ComboPooledDataSource mysqlMonitorDS;

    private static SQLHelper helper = null;

    private SQLHelper(){
        mysqlIasDS = new ComboPooledDataSource(MYSQL_IAS);
        mysqlMonitorDS = new ComboPooledDataSource(MYSQL_PROBE);
    }

    public synchronized static SQLHelper getInstance(){
        if(helper == null){
            helper = new SQLHelper();
        }
        return helper;
    }

    public Connection getIasConnection() throws SQLException{

        return mysqlIasDS.getConnection();
    }

    public Connection getDetectConnection() throws SQLException{
        return mysqlMonitorDS.getConnection();
    }
}
