package com.taskmanager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Singleton instance
    private static DBConnection instance;

    // JDBC Connection
    private Connection connection;

    // Database config
    private static final String URL = "jdbc:mysql://localhost:3306/task_management_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Private constructor
    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);

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
