package com.restaurant.dao;

import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.KitchenOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) cho phân hệ Quản lý Bếp & Bar (Kitchen Management)
 */
public class KitchenDAO {

    /**
     * Lấy danh sách các món ăn cần chế biến từ các bàn chưa thanh toán
     */
    public List<KitchenOrder> getKitchenOrders(String statusFilter, Integer maBanFilter, String khuVucFilter) {
        List<KitchenOrder> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT ct.MaCTHD, ct.MaHD, h.MaBan, b.TenBan, b.KhuVuc, " +
                "ct.MaMon, m.TenMon, ct.SoLuong, ct.DonGia, ct.GhiChu, ct.TrangThaiMon, ct.ThoiGianGoi " +
                "FROM ChiTietHoaDon ct " +
                "JOIN HoaDon h ON ct.MaHD = h.MaHD " +
                "JOIN Ban b ON h.MaBan = b.MaBan " +
                "JOIN MonAn m ON ct.MaMon = m.MaMon " +
                "WHERE h.TrangThai = 'ChuaThanhToan' "
        );

        List<Object> params = new ArrayList<>();

        // Bộ lọc trạng thái món
        if (statusFilter != null && !statusFilter.isEmpty() && !"Tất cả".equalsIgnoreCase(statusFilter)) {
            if ("DangCho".equalsIgnoreCase(statusFilter) || "Chờ nấu".equalsIgnoreCase(statusFilter) || "ChoLam".equalsIgnoreCase(statusFilter)) {
                sql.append("AND ct.TrangThaiMon IN ('DangCho', 'ChoLam') ");
            } else if ("DangLam".equalsIgnoreCase(statusFilter) || "Đang nấu".equalsIgnoreCase(statusFilter)) {
                sql.append("AND ct.TrangThaiMon = 'DangLam' ");
            } else if ("DaRaMon".equalsIgnoreCase(statusFilter) || "Đã nấu xong".equalsIgnoreCase(statusFilter) || "Hoàn thành".equalsIgnoreCase(statusFilter)) {
                sql.append("AND ct.TrangThaiMon = 'DaRaMon' ");
            } else if ("DaHuy".equalsIgnoreCase(statusFilter) || "Đã hủy".equalsIgnoreCase(statusFilter)) {
                sql.append("AND ct.TrangThaiMon = 'DaHuy' ");
            }
        } else {
            // Mặc định không hiển thị món đã hủy trong danh sách chế biến
            sql.append("AND ct.TrangThaiMon != 'DaHuy' ");
        }

        // Bộ lọc theo bàn
        if (maBanFilter != null && maBanFilter > 0) {
            sql.append("AND h.MaBan = ? ");
            params.add(maBanFilter);
        }

        // Bộ lọc theo khu vực
        if (khuVucFilter != null && !khuVucFilter.isEmpty() && !"Tất cả".equalsIgnoreCase(khuVucFilter)) {
            sql.append("AND b.KhuVuc = ? ");
            params.add(khuVucFilter);
        }

        // Ưu tiên món đang chờ lên trước, sau đó đến đang nấu, và theo thời gian gọi
        sql.append("ORDER BY CASE " +
                "WHEN ct.TrangThaiMon IN ('DangCho', 'ChoLam') THEN 1 " +
                "WHEN ct.TrangThaiMon = 'DangLam' THEN 2 " +
                "WHEN ct.TrangThaiMon = 'DaRaMon' THEN 3 " +
                "ELSE 4 END ASC, ct.ThoiGianGoi ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KitchenOrder item = new KitchenOrder(
                            rs.getInt("MaCTHD"),
                            rs.getInt("MaHD"),
                            rs.getInt("MaBan"),
                            rs.getString("TenBan"),
                            rs.getString("KhuVuc"),
                            rs.getInt("MaMon"),
                            rs.getString("TenMon"),
                            rs.getInt("SoLuong"),
                            rs.getDouble("DonGia"),
                            rs.getString("GhiChu"),
                            rs.getString("TrangThaiMon"),
                            rs.getTimestamp("ThoiGianGoi")
                    );
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi KitchenDAO.getKitchenOrders: " + e.getMessage());
        }
        return list;
    }

    /**
     * Cập nhật trạng thái chế biến của 1 món ăn
     */
    public boolean updateItemStatus(int maCTHD, String newStatus) {
        String sql = "UPDATE ChiTietHoaDon SET TrangThaiMon = ? WHERE MaCTHD = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, maCTHD);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi KitchenDAO.updateItemStatus: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật đồng loạt trạng thái nhiều món ăn (Multi-select)
     */
    public boolean updateBatchItemStatus(List<Integer> maCTHDList, String newStatus) {
        if (maCTHDList == null || maCTHDList.isEmpty()) return false;

        String sql = "UPDATE ChiTietHoaDon SET TrangThaiMon = ? WHERE MaCTHD = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int maCTHD : maCTHDList) {
                    ps.setString(1, newStatus);
                    ps.setInt(2, maCTHD);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi KitchenDAO.updateBatchItemStatus: " + e.getMessage());
            return false;
        }
    }

    /**
     * Bếp báo hủy món ăn do hết nguyên liệu hoặc lý do khác
     */
    public boolean cancelItem(int maCTHD, String reason) {
        String sql = "UPDATE ChiTietHoaDon SET TrangThaiMon = 'DaHuy', " +
                "GhiChu = CONCAT(IFNULL(GhiChu, ''), ' [HỦY BẾP: ', ?, ']') " +
                "WHERE MaCTHD = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reason != null ? reason : "Hết nguyên liệu");
            ps.setInt(2, maCTHD);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi KitchenDAO.cancelItem: " + e.getMessage());
            return false;
        }
    }

    /**
     * Bếp trưởng bật/tắt tình trạng còn món / hết món
     */
    public boolean updateDishAvailability(int maMon, boolean conMon) {
        String sql = "UPDATE MonAn SET TrangThai = ? WHERE MaMon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, conMon ? "ConMon" : "HetHang");
            ps.setInt(2, maMon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi KitchenDAO.updateDishAvailability: " + e.getMessage());
            return false;
        }
    }
}
