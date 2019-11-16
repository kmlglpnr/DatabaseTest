package com.example.jdbc;

import org.apache.derby.iapi.db.Database;
import org.apache.derby.jdbc.EmbeddedDataSource;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Random;

public class DBUtility {

    public Connection connectToDatabase() throws SQLException{
        Properties connectionProps = new Properties();
        System.out.println(DatabaseTest.globalProps.getProperty(DatabaseTest.DB_USER));
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

    public void createDatabaseTable(Connection conn) throws SQLException{
        try (
                // Java 7 and higher Statement object implements auto-closeable interface
                Statement s = conn.createStatement();
                ){
            s.executeUpdate("create table testData(" +
                    "num INT, " +
                    "dt TIMESTAMP, " +
                    "txt VARCHAR(256))");
        } // I do not need s.close() and finally after that
    }

    public void addData(Connection conn) throws SQLException{
        String sql = "insert into testdata values (?,?,?)";
        try(
                PreparedStatement ps = conn.prepareStatement(sql);
                )
        {
            Random rnd = new Random();
            long timenow = System.currentTimeMillis();

            for(int i = 0; i < 10; i++){
                int num = rnd.nextInt(1000);
                long time = timenow - (num ^ 2);

                ps.setInt(1, num);
                ps.setTimestamp(2, new Timestamp(time));
                ps.setString(3, time + ": " + new Date(time));
                ps.executeUpdate();
            }
        }
    }

    public void readData(Connection conn) throws SQLException{
        try(Statement query = conn.createStatement()){
            String sql = "select * from testdata";
            query.setFetchSize(100);

            try(
                    ResultSet rs = query.executeQuery(sql)
                    ){
                while(!rs.isClosed() && rs.next()){
                    int num = rs.getInt(1);
                    Timestamp dt = rs.getTimestamp(2);
                    String txt = rs.getString(3);
                    System.out.println(num + "; " + dt + ": " + txt);

                }
            }
        }
    }

    public void closeDatabaseConnection(Connection conn) throws SQLException{
        conn.close();

        try{
            // I do this because I am accessing a database file directly
            // if I was connecting to a database server I would never do this
            DriverManager.getConnection("jdbc:derby:test.db;shutdown=true");
        } catch (SQLException sqe){
            if((sqe.getErrorCode() == 45000) || (sqe.getErrorCode() == 50000)){
                //Ignore these error codes; Derby ALWAYS throws an error when
                // we shutdown a database: 08006(45000) if it is a single
                // database, XJ015(50000) if you close all databases
            } else {
                throw sqe;
            }
        }
    }

    public void testConnection() throws IOException, SQLException{
        DatabaseTest dt = new DatabaseTest();
        dt.writeProperties();
        dt.readProperties();
        Connection conn = connectToDatabase();

        try{
            createDatabaseTable(conn);
            addData(conn);
        } catch (SQLException e){
            System.out.println("Database table already exists");
        }

        readData(conn);
        closeDatabaseConnection(conn);
    }

}
