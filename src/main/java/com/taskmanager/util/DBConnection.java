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

    // JDBC Connection
    private Connection connection;

    // Private constructor
    private DBConnection() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/config.properties")) {
            prop.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Class.forName("org.postgresql.Driver");

            this.connection = DriverManager.getConnection(
                    "jdbc:postgresql://"+prop.getProperty("DB_HOST")+":"+prop.getProperty("DB_PORT")+"/"+prop.getProperty("DB_NAME"),
                    prop.getProperty("DB_USER"),
                    prop.getProperty("DB_PASSWORD"));

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Gagal membuat koneksi ke database", e);
        }
    }

    // Ambil instance singleton
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
                instance = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
