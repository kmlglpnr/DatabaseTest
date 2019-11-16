package com.example.jdbc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        prop.setProperty(DB_URL, "localhost");




    }

    public void readProperties() throws IOException{
        try(
                InputStream in = new FileInputStream(DB_PROPERTIES)
        ) {
            Properties prop = new Properties();
            prop.load(in);
            globalProps = new Properties();
            globalProps.load(in);
        }
    }

    public static void main(String[] args) {


    }
}
