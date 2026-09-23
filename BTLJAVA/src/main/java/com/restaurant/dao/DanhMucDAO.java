package com.restaurant.dao;

import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.DanhMuc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng `DanhMuc`
 */
public class DanhMucDAO implements IBaseDAO<DanhMuc> {

    public List<DanhMuc> getAll() {
        List<DanhMuc> list = new ArrayList<>();
        String sql = "SELECT MaDM, TenDM, MoTa FROM DanhMuc ORDER BY MaDM ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DanhMuc dm = new DanhMuc(
                        rs.getInt("MaDM"),
                        rs.getString("TenDM"),
                        rs.getString("MoTa")
                );
                list.add(dm);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DanhMucDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(DanhMuc dm) {
        String sql = "INSERT INTO DanhMuc (TenDM, MoTa) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dm.getTenDM());
            ps.setString(2, dm.getMoTa());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        dm.setMaDM(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DanhMucDAO.insert: " + e.getMessage());
        }
        return false;
    }

    public boolean update(DanhMuc dm) {
        String sql = "UPDATE DanhMuc SET TenDM = ?, MoTa = ? WHERE MaDM = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dm.getTenDM());
            ps.setString(2, dm.getMoTa());
            ps.setInt(3, dm.getMaDM());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DanhMucDAO.update: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int maDM) {
        String sql = "DELETE FROM DanhMuc WHERE MaDM = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maDM);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DanhMucDAO.delete (có thể do ràng buộc đang chứa món): " + e.getMessage());
            return false;
        }
    }
}
