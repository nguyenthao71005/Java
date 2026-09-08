package com.restaurant.dao;

import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.Voucher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng `Voucher`
 */
public class VoucherDAO {

    public List<Voucher> getAll() {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT MaVoucher, TenVoucher, PhanTramGiam, GiamToiDa, DonHangToiThieu, NgayBatDau, NgayKetThuc, TrangThai FROM Voucher ORDER BY NgayKetThuc DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Voucher v = new Voucher(
                        rs.getString("MaVoucher"),
                        rs.getString("TenVoucher"),
                        rs.getDouble("PhanTramGiam"),
                        rs.getDouble("GiamToiDa"),
                        rs.getDouble("DonHangToiThieu"),
                        rs.getString("NgayBatDau"),
                        rs.getString("NgayKetThuc"),
                        rs.getString("TrangThai")
                );
                list.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi VoucherDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    public Voucher getByCode(String code) {
        String sql = "SELECT MaVoucher, TenVoucher, PhanTramGiam, GiamToiDa, DonHangToiThieu, NgayBatDau, NgayKetThuc, TrangThai FROM Voucher WHERE MaVoucher = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Voucher(
                            rs.getString("MaVoucher"),
                            rs.getString("TenVoucher"),
                            rs.getDouble("PhanTramGiam"),
                            rs.getDouble("GiamToiDa"),
                            rs.getDouble("DonHangToiThieu"),
                            rs.getString("NgayBatDau"),
                            rs.getString("NgayKetThuc"),
                            rs.getString("TrangThai")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi VoucherDAO.getByCode: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(Voucher v) {
        String sql = "INSERT INTO Voucher (MaVoucher, TenVoucher, PhanTramGiam, GiamToiDa, DonHangToiThieu, NgayBatDau, NgayKetThuc, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, v.getMaVoucher());
            ps.setString(2, v.getTenVoucher());
            ps.setDouble(3, v.getPhanTramGiam());
            ps.setDouble(4, v.getGiamToiDa());
            ps.setDouble(5, v.getDonHangToiThieu());
            ps.setString(6, v.getNgayBatDau());
            ps.setString(7, v.getNgayKetThuc());
            ps.setString(8, v.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi VoucherDAO.insert: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Voucher v) {
        String sql = "UPDATE Voucher SET TenVoucher = ?, PhanTramGiam = ?, GiamToiDa = ?, DonHangToiThieu = ?, NgayBatDau = ?, NgayKetThuc = ?, TrangThai = ? WHERE MaVoucher = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, v.getTenVoucher());
            ps.setDouble(2, v.getPhanTramGiam());
            ps.setDouble(3, v.getGiamToiDa());
            ps.setDouble(4, v.getDonHangToiThieu());
            ps.setString(5, v.getNgayBatDau());
            ps.setString(6, v.getNgayKetThuc());
            ps.setString(7, v.getTrangThai());
            ps.setString(8, v.getMaVoucher());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi VoucherDAO.update: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String code) {
        String sql = "DELETE FROM Voucher WHERE MaVoucher = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi VoucherDAO.delete: " + e.getMessage());
            return false;
        }
    }
}
