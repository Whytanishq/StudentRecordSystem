package com.pool.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final HikariDataSource dataSource;

    static {
        try(InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")){
            Properties prop = new Properties();
            prop.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(prop.getProperty("db.url"));
            config.setUsername(prop.getProperty("db.username"));
            config.setPassword(prop.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(prop.getProperty("db.pool.size","10")));
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource = new HikariDataSource(config);
        }catch(IOException e){
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }
    public static HikariDataSource getDataSource(){
        return dataSource;
    }
}