package org.zzx.gen.util;

import org.zzx.gen.entity.DbInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DbUtil {

    /**
     * 连接数据库
     */
    public static Connection getConnection(DbInfo dbInfo) {
        try {
            Class.forName(dbInfo.getDriver()).newInstance();
            Connection  conn = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUserName(), dbInfo.getPassword());
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Statement
     */
    public static Statement getStatement(DbInfo dbInfo){
        try {
            return getConnection(dbInfo).createStatement();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




}
