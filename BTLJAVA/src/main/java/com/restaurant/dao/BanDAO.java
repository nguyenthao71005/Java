package com.restaurant.dao;

import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.Ban;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng `Ban`
 */
public class BanDAO implements IBaseDAO<Ban> {

    public List<Ban> getAll() {
        List<Ban> list = new ArrayList<>();
        String sql = "SELECT MaBan, TenBan, KhuVuc, SucChua, TrangThai FROM Ban ORDER BY MaBan ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ban ban = new Ban(
                        rs.getInt("MaBan"),
                        rs.getString("TenBan"),
                        rs.getString("KhuVuc"),
                        rs.getInt("SucChua"),
                        rs.getString("TrangThai")
                );
                list.add(ban);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi BanDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    public boolean updateStatus(int maBan, String trangThai) {
        String sql = "UPDATE Ban SET TrangThai = ? WHERE MaBan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThai);
            ps.setInt(2, maBan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi BanDAO.updateStatus: " + e.getMessage());
            return false;
        }
    }

    public boolean insert(Ban ban) {
        String sql = "INSERT INTO Ban (TenBan, KhuVuc, SucChua, TrangThai) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ban.getTenBan());
            ps.setString(2, ban.getKhuVuc());
            ps.setInt(3, ban.getSucChua());
            ps.setString(4, ban.getTrangThai());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        ban.setMaBan(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi BanDAO.insert: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Ban ban) {
        String sql = "UPDATE Ban SET TenBan = ?, KhuVuc = ?, SucChua = ?, TrangThai = ? WHERE MaBan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ban.getTenBan());
            ps.setString(2, ban.getKhuVuc());
            ps.setInt(3, ban.getSucChua());
            ps.setString(4, ban.getTrangThai());
            ps.setInt(5, ban.getMaBan());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi BanDAO.update: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int maBan) {
        String sql = "DELETE FROM Ban WHERE MaBan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maBan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi BanDAO.delete: " + e.getMessage());
            return false;
        }
    }
}
