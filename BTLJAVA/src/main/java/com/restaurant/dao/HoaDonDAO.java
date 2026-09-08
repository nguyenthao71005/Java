package com.restaurant.dao;

import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.ChiTietHoaDon;
import com.restaurant.model.HoaDon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng `HoaDon` & `ChiTietHoaDon`
 */
public class HoaDonDAO {

    /**
     * Lấy hóa đơn chưa thanh toán của bàn kèm chi tiết các món đã gọi
     */
    public HoaDon getActiveInvoiceByTable(int maBan) {
        String sqlHd = "SELECT h.MaHD, h.MaBan, b.TenBan, h.GioVao, h.GiamGiaPhanTram, h.VatPhanTram, h.TongTien, h.HinhThucTT, h.TrangThai " +
                "FROM HoaDon h " +
                "JOIN Ban b ON h.MaBan = b.MaBan " +
                "WHERE h.MaBan = ? AND h.TrangThai = 'ChuaThanhToan' " +
                "ORDER BY h.MaHD DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlHd)) {

            ps.setInt(1, maBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HoaDon hd = new HoaDon(rs.getInt("MaHD"), rs.getInt("MaBan"), rs.getString("TenBan"));
                    hd.setNgayLap(rs.getTimestamp("GioVao"));
                    hd.setGiamGiaPhanTram(rs.getDouble("GiamGiaPhanTram"));
                    hd.setVatPhanTram(rs.getDouble("VatPhanTram"));
                    hd.setHinhThucTT(rs.getString("HinhThucTT"));
                    hd.setTrangThai(rs.getString("TrangThai"));

                    // Lấy danh sách chi tiết món
                    hd.setDanhSachChiTiet(getInvoiceItems(conn, hd.getMaHD()));
                    hd.tinhTongCong();
                    return hd;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi HoaDonDAO.getActiveInvoiceByTable: " + e.getMessage());
        }
        return null;
    }

    private List<ChiTietHoaDon> getInvoiceItems(Connection conn, int maHD) {
        List<ChiTietHoaDon> items = new ArrayList<>();
        String sql = "SELECT ct.MaCTHD, ct.MaHD, ct.MaMon, m.TenMon, ct.SoLuong, ct.DonGia, ct.GhiChu, ct.TrangThaiMon " +
                "FROM ChiTietHoaDon ct " +
                "JOIN MonAn m ON ct.MaMon = m.MaMon " +
                "WHERE ct.MaHD = ? AND ct.TrangThaiMon != 'DaHuy' " +
                "ORDER BY ct.MaCTHD ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon ct = new ChiTietHoaDon(
                            rs.getInt("MaCTHD"),
                            rs.getInt("MaHD"),
                            rs.getInt("MaMon"),
                            rs.getString("TenMon"),
                            rs.getInt("SoLuong"),
                            rs.getDouble("DonGia"),
                            rs.getString("GhiChu"),
                            rs.getString("TrangThaiMon")
                    );
                    items.add(ct);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi HoaDonDAO.getInvoiceItems: " + e.getMessage());
        }
        return items;
    }

    /**
     * Mở hóa đơn mới khi khách ngồi vào bàn trống
     */
    public HoaDon createInvoiceForTable(int maBan, String tenBan) {
        String sqlInsertHd = "INSERT INTO HoaDon (MaBan, GioVao, TrangThai, HinhThucTT, MaCa) " +
                "VALUES (?, NOW(), 'ChuaThanhToan', 'TienMat', (SELECT c.MaCa FROM (SELECT MaCa FROM CaLamViec WHERE TrangThai = 'DangMo' ORDER BY MaCa DESC LIMIT 1) c))";
        String sqlUpdateBan = "UPDATE Ban SET TrangThai = 'CoKhach' WHERE MaBan = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psBan = conn.prepareStatement(sqlUpdateBan)) {
                psBan.setInt(1, maBan);
                psBan.executeUpdate();
            }

            int newHdId = 0;
            try (PreparedStatement psHd = conn.prepareStatement(sqlInsertHd, Statement.RETURN_GENERATED_KEYS)) {
                psHd.setInt(1, maBan);
                psHd.executeUpdate();
                try (ResultSet rs = psHd.getGeneratedKeys()) {
                    if (rs.next()) {
                        newHdId = rs.getInt(1);
                    }
                }
            }

            conn.commit();
            conn.setAutoCommit(true);

            HoaDon hd = new HoaDon(newHdId, maBan, tenBan);
            return hd;
        } catch (SQLException e) {
            System.err.println("Lỗi HoaDonDAO.createInvoiceForTable: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lưu danh sách order món ăn vào bảng ChiTietHoaDon
     */
    public boolean saveOrderItems(int maHD, List<ChiTietHoaDon> items) {
        String sqlDelete = "DELETE FROM ChiTietHoaDon WHERE MaHD = ?";
        String sqlInsert = "INSERT INTO ChiTietHoaDon (MaHD, MaMon, SoLuong, DonGia, GhiChu, TrangThaiMon) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psDel = conn.prepareStatement(sqlDelete)) {
                psDel.setInt(1, maHD);
                psDel.executeUpdate();
            }

            try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
                for (ChiTietHoaDon ct : items) {
                    psIns.setInt(1, maHD);
                    psIns.setInt(2, ct.getMaMon());
                    psIns.setInt(3, ct.getSoLuong());
                    psIns.setDouble(4, ct.getDonGia());
                    psIns.setString(5, ct.getGhiChu());
                    psIns.setString(6, normalizeDishStatus(ct.getTrangThaiMon()));
                    psIns.addBatch();
                }
                psIns.executeBatch();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi HoaDonDAO.saveOrderItems: " + e.getMessage());
            return false;
        }
    }

    private String normalizeDishStatus(String status) {
        if (status == null || "ChoLam".equalsIgnoreCase(status) || "DangCho".equalsIgnoreCase(status)) {
            return "DangCho";
        }
        if ("DangLam".equalsIgnoreCase(status)) return "DangLam";
        if ("DaRaMon".equalsIgnoreCase(status)) return "DaRaMon";
        if ("DaHuy".equalsIgnoreCase(status)) return "DaHuy";
        return "DangCho";
    }

    /**
     * Chuẩn hóa phương thức thanh toán sang enum CSDL: 'TienMat', 'ChuyenKhoan', 'The'
     */
    private String normalizePaymentMethod(String hinhThucTT) {
        if (hinhThucTT == null) return "TienMat";
        String lower = hinhThucTT.toLowerCase();
        if (lower.contains("chuyen") || lower.contains("khoan") || lower.contains("qr")) {
            return "ChuyenKhoan";
        }
        if (lower.contains("thẻ") || lower.contains("the") || lower.contains("pos") || lower.contains("card")) {
            return "The";
        }
        return "TienMat";
    }

    /**
     * Thanh toán hóa đơn và giải phóng bàn về trạng thái Trống
     */
    public boolean checkout(int maBan, int maHD, String hinhThucTT, double giamGia, double vat) {
        String validMethod = normalizePaymentMethod(hinhThucTT);
        String sql = "CALL sp_ThanhToanHoaDon(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, maHD);
                cs.setString(2, validMethod);
                cs.setDouble(3, giamGia);
                cs.setDouble(4, vat);
                cs.execute();
            }

            // Đảm bảo hóa đơn được gắn với ca làm việc đang mở gần nhất
            String sqlLinkShift = "UPDATE HoaDon SET " +
                    "MaCa = IFNULL(MaCa, (SELECT c.MaCa FROM (SELECT MaCa FROM CaLamViec WHERE TrangThai = 'DangMo' ORDER BY MaCa DESC LIMIT 1) c)) " +
                    "WHERE MaHD = ?";
            try (PreparedStatement psLink = conn.prepareStatement(sqlLinkShift)) {
                psLink.setInt(1, maHD);
                psLink.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            // Fallback nếu Stored Procedure chưa tạo
            return fallbackCheckout(maBan, maHD, validMethod, giamGia, vat);
        }
    }

    private boolean fallbackCheckout(int maBan, int maHD, String validMethod, double giamGia, double vat) {
        String sqlHd = "UPDATE HoaDon SET GioRa = NOW(), HinhThucTT = ?, GiamGiaPhanTram = ?, VatPhanTram = ?, " +
                "TrangThai = 'DaThanhToan', " +
                "MaCa = IFNULL(MaCa, (SELECT c.MaCa FROM (SELECT MaCa FROM CaLamViec WHERE TrangThai = 'DangMo' ORDER BY MaCa DESC LIMIT 1) c)) " +
                "WHERE MaHD = ?";
        String sqlBan = "UPDATE Ban SET TrangThai = 'Trong', MaNVPhuTrach = NULL WHERE MaBan = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psHd = conn.prepareStatement(sqlHd);
                 PreparedStatement psBan = conn.prepareStatement(sqlBan)) {

                psHd.setString(1, validMethod);
                psHd.setDouble(2, giamGia);
                psHd.setDouble(3, vat);
                psHd.setInt(4, maHD);
                psHd.executeUpdate();

                psBan.setInt(1, maBan);
                psBan.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException ex) {
            System.err.println("Lỗi fallbackCheckout: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Chuyển toàn bộ order từ bàn cũ sang bàn mới
     */
    public boolean transferTable(int maBanCu, int maBanMoi) {
        String sql = "CALL sp_ChuyenBan(?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, maBanCu);
            cs.setInt(2, maBanMoi);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi HoaDonDAO.transferTable: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy toàn bộ danh sách hóa đơn (cho module Lịch sử hóa đơn)
     */
    public List<HoaDon> getAllInvoices() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT h.MaHD, h.MaBan, b.TenBan, h.GioVao, h.GioRa, h.GiamGiaPhanTram, h.VatPhanTram, " +
                "h.TongTien, h.HinhThucTT, h.TrangThai, nv.HoTen AS TenNV " +
                "FROM HoaDon h " +
                "JOIN Ban b ON h.MaBan = b.MaBan " +
                "LEFT JOIN NhanVien nv ON h.MaNV = nv.MaNV " +
                "ORDER BY h.MaHD DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                HoaDon hd = new HoaDon(rs.getInt("MaHD"), rs.getInt("MaBan"), rs.getString("TenBan"));
                hd.setNgayLap(rs.getTimestamp("GioVao"));
                hd.setGioVao(rs.getTimestamp("GioVao"));
                hd.setGioRa(rs.getTimestamp("GioRa"));
                hd.setGiamGiaPhanTram(rs.getDouble("GiamGiaPhanTram"));
                hd.setVatPhanTram(rs.getDouble("VatPhanTram"));
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setHinhThucTT(rs.getString("HinhThucTT"));
                hd.setTrangThai(rs.getString("TrangThai"));
                hd.setTenNV(rs.getString("TenNV"));
                list.add(hd);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi HoaDonDAO.getAllInvoices: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy chi tiết món theo mã hóa đơn
     */
    public List<ChiTietHoaDon> getInvoiceItemsById(int maHD) {
        List<ChiTietHoaDon> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            return getInvoiceItems(conn, maHD);
        } catch (SQLException e) {
            System.err.println("Lỗi HoaDonDAO.getInvoiceItemsById: " + e.getMessage());
        }
        return items;
    }
}
