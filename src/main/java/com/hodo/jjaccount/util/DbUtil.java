/**
 *
 */
package com.hodo.jjaccount.util;


import java.sql.Connection;

import java.sql.SQLException;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;


/**
 * @author Administrator
 */
public class DbUtil {
    private static DataSource dataSource = null;

    private static ThreadLocal<Connection> connlocal = new ThreadLocal<Connection>();

    static {
        Properties props = new Properties();

        try {
            props.setProperty("driverClassName", "oracle.jdbc.driver.OracleDriver");
            props.setProperty("url", "jdbc:oracle:thin:@192.158.8.13:1521:hodocw");
            props.setProperty("username", "jujia");
            props.setProperty("password", "jujia,/5987");
            props.setProperty("maxActive", "30");
            props.setProperty("initialSize", "5");
            props.setProperty("maxWait", "30000");
            dataSource = BasicDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static Connection getConnection() throws SQLException {
        Connection conn = connlocal.get();
        if (conn == null || conn.isClosed()) {
            conn = dataSource.getConnection();
            connlocal.set(conn);
        }
        return conn;
    }

    public static void closeConnection(Connection con) {
        con = connlocal.get();
        connlocal.set(null);
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
