package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String CONFIG_FILE = "db.properties";
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try(FileInputStream in = new FileInputStream(CONFIG_FILE)){
            props.load(in);
        } catch(IOException e){
            System.out.println("Không đọc được " + CONFIG_FILE);
        }

        URL = pick("DB_URL", props.getProperty("db.url"));
        USER = pick("DB_USER", props.getProperty("db.user"));
        PASSWORD = pick("DB_PASSWORD", props.getProperty("db.password"));
    }

    private DBConnection(){}

    private static String pick(String envName, String fileValue){
        String env = System.getenv(envName);
        return (env != null && !env.isEmpty()) ? env : fileValue;
    }

    public static Connection getConnection() throws SQLException {
        if(URL == null || USER == null){
            throw new SQLException ("Thieu cau hinh DB: kiem tra db.properties hoac bien moi truong");
        }

        return DriverManager.getConnection(URL, USER, PASSWORD == null ? "" : PASSWORD);
    }
}
