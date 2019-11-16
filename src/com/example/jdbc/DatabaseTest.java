package com.example.jdbc;

import java.io.*;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseTest {


    public static final String DB_PROPERTIES = "db.properties";
    public static final String DB_PASSWORD = "dbPassword";
    public static final String DB_URL = "dbUrl";
    public static final String DB_USER = "dbUser";

    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public static Properties globalProps;

    // the reason I added global Properties object is
    // dbUrl, dbUser, dbPassword paramteres can be more during the program
    // everytime I added property I need to create a local variable
    // this is not a horrible process but readProperties and writeProperties are two way
    // so it is better to create a object and access those parameters
    // through that object


    public void writeProperties() throws IOException {
        // TODO - write properties to  project.properties file
        Properties prop = new Properties();
        prop.setProperty(DB_URL, "jdbc:derby:test.db;create=true");
        prop.setProperty(DB_USER, "jsmith");
        prop.setProperty(DB_PASSWORD, "pwd");

        try(OutputStream out = new FileOutputStream("db.properties")){
            prop.store(out, "Database Properties File");
        }

    }

    public void readProperties() throws IOException{
        try(
                InputStream in = new FileInputStream(DB_PROPERTIES)
        ) {
            globalProps = new Properties();
            globalProps.load(in);
            System.out.println(globalProps.getProperty(DB_USER));
        }
    }

    public static void main(String[] args) {
        DBUtility du = new DBUtility();

        try {
            du.testConnection();
        } catch (IOException | SQLException e){
            e.printStackTrace();
        }





    }
}
