package com.restaurant.dao;

import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.MonAn;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng `MonAn`
 */
public class MonAnDAO {

    public List<MonAn> getAll() {
        List<MonAn> list = new ArrayList<>();
        String sql = "SELECT m.MaMon, m.TenMon, m.MaDM, IFNULL(d.TenDM, 'Mặc định') AS TenDM, m.DonGia, m.HinhAnh, m.TrangThai " +
                "FROM MonAn m " +
                "LEFT JOIN DanhMuc d ON m.MaDM = d.MaDM " +
                "ORDER BY m.MaMon ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                boolean conMon = "ConMon".equalsIgnoreCase(rs.getString("TrangThai"));
                MonAn mon = new MonAn(
                        rs.getInt("MaMon"),
                        rs.getString("TenMon"),
                        rs.getInt("MaDM"),
                        rs.getString("TenDM"),
                        rs.getDouble("DonGia"),
                        rs.getString("HinhAnh"),
                        conMon
                );
                list.add(mon);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi MonAnDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(MonAn mon) {
        String sql = "INSERT INTO MonAn (TenMon, MaDM, DonGia, HinhAnh, TrangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, mon.getTenMon());
            ps.setInt(2, mon.getMaDM());
            ps.setDouble(3, mon.getDonGia());
            ps.setString(4, mon.getHinhAnh());
            ps.setString(5, mon.isConMon() ? "ConMon" : "HetHang");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        mon.setMaMon(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi MonAnDAO.insert: " + e.getMessage());
        }
        return false;
    }

    public boolean update(MonAn mon) {
        String sql = "UPDATE MonAn SET TenMon = ?, MaDM = ?, DonGia = ?, HinhAnh = ?, TrangThai = ? WHERE MaMon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mon.getTenMon());
            ps.setInt(2, mon.getMaDM());
            ps.setDouble(3, mon.getDonGia());
            ps.setString(4, mon.getHinhAnh());
            ps.setString(5, mon.isConMon() ? "ConMon" : "HetHang");
            ps.setInt(6, mon.getMaMon());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi MonAnDAO.update: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int maMon) {
        String sql = "DELETE FROM MonAn WHERE MaMon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maMon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi MonAnDAO.delete: " + e.getMessage());
            return false;
        }
    }
}
