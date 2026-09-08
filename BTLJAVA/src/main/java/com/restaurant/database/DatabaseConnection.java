package com.restaurant.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp quản lý kết nối Cơ sở dữ liệu MySQL (Singleton Pattern)
 * =========================================================================
 * Thông tin cấu hình:
 * - Host: localhost:3306
 * - Database: restaurant_db
 * - Username: root
 * - Password: Pthao*1234
 * =========================================================================
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/restaurant_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "Pthao*1234";

    private static Connection connection = null;

    // Khởi tạo Driver MySQL
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Chưa tìm thấy MySQL JDBC Driver! Kiểm tra dependency pom.xml: " + e.getMessage());
        }
    }

    private DatabaseConnection() {}

    /**
     * Lấy kết nối duy nhất (Singleton Connection)
     * Tự động kiểm tra và kết nối lại nếu bị đóng
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối CSDL MySQL: " + e.getMessage());
            return null;
        }
    }

    /**
     * Kiểm tra trạng thái kết nối MySQL hiện tại
     * @return true nếu kết nối thành công, false nếu thất bại
     */
    public static boolean isConnected() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed() && conn.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Đóng kết nối an toàn khi tắt ứng dụng
     */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Đã đóng kết nối MySQL.");
                }
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
