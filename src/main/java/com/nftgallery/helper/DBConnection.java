package com.nftgallery.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/nft_gallery";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Thay đổi mật khẩu tùy theo cài đặt

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}