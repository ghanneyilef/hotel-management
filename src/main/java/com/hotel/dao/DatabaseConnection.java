package com.hotel.dao;

import java.sql.*;

public class DatabaseConnection {

    private static final String URL  = "jdbc:postgresql://localhost:5432/hotel_db";
    private static final String USER = "postgres";
    private static final String PASS = "loflof";

    private static Connection instance = null;

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("✅ Connexion PostgreSQL réussie !");
        }
        return instance;
    }
}