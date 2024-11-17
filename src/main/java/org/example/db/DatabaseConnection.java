package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    private static final String URL = "jdbc:oracle:thin:@orion.javeriana.edu.co:1521/LAB";
    private static final String USER = "is144807";
    private static final String PASSWORD = "EPXvEgBllgu1o2c";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}