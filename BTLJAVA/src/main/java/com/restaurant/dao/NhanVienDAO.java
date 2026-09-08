package com.restaurant.dao;

import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.NhanVien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng `NhanVien`
 */
public class NhanVienDAO {

    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT MaNV, HoTen, TenDangNhap, MatKhau, VaiTro, TrangThai FROM NhanVien ORDER BY MaNV ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NhanVien nv = new NhanVien(
                        rs.getInt("MaNV"),
                        rs.getString("HoTen"),
                        rs.getString("TenDangNhap"),
                        rs.getString("MatKhau"),
                        rs.getString("VaiTro"),
                        rs.getString("TrangThai")
                );
                list.add(nv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi NhanVienDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    /**
     * Xác thực đăng nhập nhân viên từ MySQL
     */
    public NhanVien authenticate(String username, String password) {
        String sql = "SELECT MaNV, HoTen, TenDangNhap, MatKhau, VaiTro, TrangThai FROM NhanVien WHERE TenDangNhap = ? AND MatKhau = ? AND TrangThai = 'DangLam'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new NhanVien(
                            rs.getInt("MaNV"),
                            rs.getString("HoTen"),
                            rs.getString("TenDangNhap"),
                            rs.getString("MatKhau"),
                            rs.getString("VaiTro"),
                            rs.getString("TrangThai")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi NhanVienDAO.authenticate: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (HoTen, TenDangNhap, MatKhau, VaiTro, TrangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getTenDangNhap());
            ps.setString(3, nv.getMatKhau());
            ps.setString(4, nv.getVaiTro());
            ps.setString(5, nv.getTrangThai());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        nv.setMaNV(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi NhanVienDAO.insert: " + e.getMessage());
        }
        return false;
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET HoTen = ?, TenDangNhap = ?, VaiTro = ?, TrangThai = ? WHERE MaNV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getTenDangNhap());
            ps.setString(3, nv.getVaiTro());
            ps.setString(4, nv.getTrangThai());
            ps.setInt(5, nv.getMaNV());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi NhanVienDAO.update: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int maNV) {
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi NhanVienDAO.delete: " + e.getMessage());
            return false;
        }
    }
}
