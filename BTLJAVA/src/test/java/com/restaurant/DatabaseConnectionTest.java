package com.restaurant;

import com.restaurant.database.DatabaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    @Test
    public void testMySqlConnection() {
        Connection conn = DatabaseConnection.getConnection();
        assertNotNull(conn, "Kết nối MySQL không được null (Kiểm tra xem MySQL Server đã chạy và database restaurant_db đã được tạo chưa)");

        try {
            assertTrue(DatabaseConnection.isConnected(), "DatabaseConnection.isConnected() phải trả về true");

            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Kết nối thành công đến: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());

            // Kiểm tra truy vấn các bảng cốt lõi
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SHOW TABLES;")) {
                System.out.println("Các bảng hiện có trong restaurant_db:");
                while (rs.next()) {
                    System.out.println(" - " + rs.getString(1));
                }
            }
        } catch (Exception e) {
            fail("Lỗi khi truy vấn database: " + e.getMessage());
        }
    }
}
