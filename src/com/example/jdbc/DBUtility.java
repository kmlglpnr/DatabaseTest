package com.example.jdbc;

import org.apache.derby.jdbc.EmbeddedDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtility {

    public Connection connectToDatabase() throws SQLException{
        Properties connectionProps = new Properties();
        connectionProps.put("user", DatabaseTest.globalProps.getProperty(DatabaseTest.DB_USER));
        connectionProps.put("password", DatabaseTest.globalProps.getProperty(DatabaseTest.DB_PASSWORD));

        // registerDriver is optional for some drivers, required for others
        DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
        Connection conn = DriverManager.getConnection(DatabaseTest.globalProps.getProperty(DatabaseTest.DB_URL)
                , connectionProps);
        return conn;
    }

    // this uses data source - we dont need but it is more efficient on long span apps
    public Connection connectUsingDataSource() throws SQLException{
        EmbeddedDataSource ds = new org.apache.derby.jdbc.EmbeddedDataSource();
        ds.setDatabaseName("test.db;create=true");
        ds.setUser(DatabaseTest.globalProps.getProperty(DatabaseTest.DB_USER));
        ds.setPassword(DatabaseTest.globalProps.getProperty(DatabaseTest.DB_PASSWORD));
        Connection conn = ds.getConnection();

        // Paranoid mode for connection pools; make sure autoCommit is on
        // initially it is auto commit, this is just to be safe.
        conn.setAutoCommit(true);
        return conn;
    }

}
