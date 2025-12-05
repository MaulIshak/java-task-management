package com.taskmanager.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    // Singleton instance
    private static DBConnection instance;

    private Connection connection;

    private DBConnection() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/config.properties")) {
            prop.load(input);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:postgresql://"+prop.getProperty("DB_HOST")+":"+prop.getProperty("DB_PORT")+"/"+prop.getProperty("DB_NAME"),
                    prop.getProperty("DB_USER"),
                    prop.getProperty("DB_PASSWORD"));

        } catch ( SQLException e) {
            throw new IllegalStateException("Gagal membuat koneksi ke database", e);
        }
    }

    // get instance singleton
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    // Ambil koneksi
    public Connection getConnection() {
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
