package com.restaurant.dao;

import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.CaLamViec;
import com.restaurant.model.HoaDon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng `CaLamViec` & View `v_LichSuCaLamViec`
 */
public class CaLamViecDAO {

    /**
     * Lấy ca làm việc đang mở gần nhất (nếu có)
     */
    public CaLamViec getCaDangMo() {
        String sql = "SELECT c.*, nv.HoTen AS TenNV FROM CaLamViec c " +
                "JOIN NhanVien nv ON c.MaNV = nv.MaNV " +
                "WHERE c.TrangThai = 'DangMo' " +
                "ORDER BY c.MaCa DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                CaLamViec ca = mapResultSetToCaLamViec(rs);
                // Cập nhật lại doanh thu tức thời từ các hóa đơn trong ca
                capNhatDoanhThuTucThoi(conn, ca);
                return ca;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi CaLamViecDAO.getCaDangMo: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy ca làm việc đã đóng gần nhất (để ca sau biết số tiền bàn giao trong két)
     */
    public CaLamViec getCaTruocGanNhat() {
        String sql = "SELECT c.*, nv.HoTen AS TenNV FROM CaLamViec c " +
                "JOIN NhanVien nv ON c.MaNV = nv.MaNV " +
                "WHERE c.TrangThai = 'DaDong' " +
                "ORDER BY c.MaCa DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapResultSetToCaLamViec(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi CaLamViecDAO.getCaTruocGanNhat: " + e.getMessage());
        }
        return null;
    }

    /**
     * Mở ca làm việc mới
     */
    public boolean moCa(CaLamViec ca) {
        String sql = "INSERT INTO CaLamViec (MaNV, ThoiGianMo, TienDauCa, DoanhThuTienMat, DoanhThuChuyenKhoan, " +
                "TongDoanhThu, TienLyThuyet, TrangThai, GhiChu) " +
                "VALUES (?, ?, ?, 0, 0, 0, ?, 'DangMo', ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp openTime = ca.getThoiGianMo() != null ? ca.getThoiGianMo() : new Timestamp(System.currentTimeMillis());
            ca.setThoiGianMo(openTime);
            ca.tinhTienLyThuyet();

            ps.setInt(1, ca.getMaNV());
            ps.setTimestamp(2, openTime);
            ps.setDouble(3, ca.getTienDauCa());
            ps.setDouble(4, ca.getTienLyThuyet());
            ps.setString(5, ca.getGhiChu());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        ca.setMaCa(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi CaLamViecDAO.moCa: " + e.getMessage());
        }
        return false;
    }

    /**
     * Bàn giao ca / Đóng ca làm việc:
     * - Tổng hợp doanh thu thực tế từ hóa đơn trong ca
     * - Tính toán số tiền lý thuyết trong két
     * - Lưu số tiền thực tế kiểm đếm, số tiền chênh lệch thừa/thiếu và ghi chú giải trình
     */
    public boolean dongCa(CaLamViec ca) {
        String sql = "UPDATE CaLamViec SET " +
                "ThoiGianDong = NOW(), " +
                "DoanhThuTienMat = ?, " +
                "DoanhThuChuyenKhoan = ?, " +
                "TongDoanhThu = ?, " +
                "TienLyThuyet = ?, " +
                "TienThucTe = ?, " +
                "ChenhLech = ?, " +
                "TrangThai = 'DaDong', " +
                "GhiChu = ? " +
                "WHERE MaCa = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Tính toán lại doanh thu chuẩn xác từ các hóa đơn đã thanh toán
            capNhatDoanhThuTucThoi(conn, ca);
            ca.tinhChenhLech();
            if (ca.getThoiGianDong() == null) {
                ca.setThoiGianDong(new Timestamp(System.currentTimeMillis()));
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, ca.getDoanhThuTienMat());
                ps.setDouble(2, ca.getDoanhThuChuyenKhoan());
                ps.setDouble(3, ca.getTongDoanhThu());
                ps.setDouble(4, ca.getTienLyThuyet());
                if (ca.getTienThucTe() != null) {
                    ps.setDouble(5, ca.getTienThucTe());
                } else {
                    ps.setNull(5, Types.DECIMAL);
                }
                if (ca.getChenhLech() != null) {
                    ps.setDouble(6, ca.getChenhLech());
                } else {
                    ps.setNull(6, Types.DECIMAL);
                }
                ps.setString(7, ca.getGhiChu());
                ps.setInt(8, ca.getMaCa());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi CaLamViecDAO.dongCa: " + e.getMessage());
        }
        return false;
    }

    public void capNhatDoanhThuTucThoi(CaLamViec ca) {
        if (ca == null) return;
        try (Connection conn = DatabaseConnection.getConnection()) {
            capNhatDoanhThuTucThoi(conn, ca);
        } catch (SQLException e) {
            System.err.println("Lỗi CaLamViecDAO.capNhatDoanhThuTucThoi: " + e.getMessage());
        }
    }

    /**
     * Cập nhật doanh thu tức thời của ca dựa vào các hóa đơn đã thanh toán:
     * - Tự động đồng bộ các hóa đơn đã thanh toán phát sinh trong thời gian ca trực
     * - Tính toán chính xác doanh thu tiền mặt, chuyển khoản, thẻ và tổng tiền
     * - Ghi lại tức thời vào bảng CaLamViec để đồng bộ CSDL và view
     */
    public void capNhatDoanhThuTucThoi(Connection conn, CaLamViec ca) {
        if (ca == null || conn == null) return;

        // 1. Tự động đồng bộ các hóa đơn đã thanh toán kể từ lúc mở ca vào ca này nếu chưa được gán MaCa
        if (ca.getThoiGianMo() != null) {
            String sqlSync = "UPDATE HoaDon SET MaCa = ? " +
                    "WHERE (MaCa IS NULL OR MaCa = ?) " +
                    "AND GioRa >= ? " +
                    "AND TrangThai = 'DaThanhToan'";
            try (PreparedStatement psSync = conn.prepareStatement(sqlSync)) {
                psSync.setInt(1, ca.getMaCa());
                psSync.setInt(2, ca.getMaCa());
                psSync.setTimestamp(3, ca.getThoiGianMo());
                psSync.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Lỗi đồng bộ MaCa cho hóa đơn: " + e.getMessage());
            }
        }

        // 2. Tổng hợp doanh thu
        String sql = "SELECT " +
                "IFNULL(SUM(CASE WHEN HinhThucTT = 'TienMat' THEN TongTien ELSE 0 END), 0) AS DoanhThuTienMat, " +
                "IFNULL(SUM(CASE WHEN HinhThucTT IN ('ChuyenKhoan', 'The') THEN TongTien ELSE 0 END), 0) AS DoanhThuChuyenKhoan, " +
                "IFNULL(SUM(TongTien), 0) AS TongDoanhThu, " +
                "COUNT(MaHD) AS SoHoaDon " +
                "FROM HoaDon " +
                "WHERE (MaCa = ? OR (MaCa IS NULL AND GioRa >= ?)) AND TrangThai = 'DaThanhToan'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ca.getMaCa());
            ps.setTimestamp(2, ca.getThoiGianMo() != null ? ca.getThoiGianMo() : new Timestamp(0));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ca.setDoanhThuTienMat(rs.getDouble("DoanhThuTienMat"));
                    ca.setDoanhThuChuyenKhoan(rs.getDouble("DoanhThuChuyenKhoan"));
                    ca.setTongDoanhThu(rs.getDouble("TongDoanhThu"));
                    ca.setSoHoaDon(rs.getInt("SoHoaDon"));
                    ca.tinhTienLyThuyet();
                    ca.tinhChenhLech();

                    // Lưu trực tiếp vào CSDL MySQL để View và các truy vấn khác luôn cập nhật
                    String sqlUpdateCa = "UPDATE CaLamViec SET DoanhThuTienMat = ?, DoanhThuChuyenKhoan = ?, TongDoanhThu = ?, TienLyThuyet = ? WHERE MaCa = ?";
                    try (PreparedStatement psUp = conn.prepareStatement(sqlUpdateCa)) {
                        psUp.setDouble(1, ca.getDoanhThuTienMat());
                        psUp.setDouble(2, ca.getDoanhThuChuyenKhoan());
                        psUp.setDouble(3, ca.getTongDoanhThu());
                        psUp.setDouble(4, ca.getTienLyThuyet());
                        psUp.setInt(5, ca.getMaCa());
                        psUp.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi CaLamViecDAO.capNhatDoanhThuTucThoi: " + e.getMessage());
        }
    }

    /**
     * Lấy toàn bộ lịch sử các ca làm việc
     */
    public List<CaLamViec> getAll() {
        List<CaLamViec> list = new ArrayList<>();
        String sql = "SELECT * FROM v_LichSuCaLamViec ORDER BY MaCa DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CaLamViec ca = mapResultSetToCaLamViec(rs);
                try {
                    ca.setSoHoaDon(rs.getInt("SoHoaDon"));
                } catch (Exception ignored) {}
                list.add(ca);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi CaLamViecDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy danh sách hóa đơn đã phát sinh trong một ca làm việc cụ thể
     */
    public List<HoaDon> getHoaDonByMaCa(int maCa) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT h.MaHD, h.MaBan, b.TenBan, h.GioVao, h.GioRa, h.GiamGiaPhanTram, h.VatPhanTram, " +
                "h.TongTien, h.HinhThucTT, h.TrangThai, nv.HoTen AS TenNV " +
                "FROM HoaDon h " +
                "JOIN Ban b ON h.MaBan = b.MaBan " +
                "LEFT JOIN NhanVien nv ON h.MaNV = nv.MaNV " +
                "WHERE h.MaCa = ? " +
                "ORDER BY h.MaHD DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maCa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HoaDon hd = new HoaDon(rs.getInt("MaHD"), rs.getInt("MaBan"), rs.getString("TenBan"));
                    hd.setNgayLap(rs.getTimestamp("GioVao"));
                    hd.setGioRa(rs.getTimestamp("GioRa"));
                    hd.setGiamGiaPhanTram(rs.getDouble("GiamGiaPhanTram"));
                    hd.setVatPhanTram(rs.getDouble("VatPhanTram"));
                    hd.setTongTien(rs.getDouble("TongTien"));
                    hd.setHinhThucTT(rs.getString("HinhThucTT"));
                    hd.setTrangThai(rs.getString("TrangThai"));
                    hd.setTenNV(rs.getString("TenNV"));
                    list.add(hd);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi CaLamViecDAO.getHoaDonByMaCa: " + e.getMessage());
        }
        return list;
    }

    private CaLamViec mapResultSetToCaLamViec(ResultSet rs) throws SQLException {
        Double tienThucTe = rs.getObject("TienThucTe") != null ? rs.getDouble("TienThucTe") : null;
        Double chenhLech = rs.getObject("ChenhLech") != null ? rs.getDouble("ChenhLech") : null;

        return new CaLamViec(
                rs.getInt("MaCa"),
                rs.getInt("MaNV"),
                rs.getString("TenNV"),
                rs.getTimestamp("ThoiGianMo"),
                rs.getTimestamp("ThoiGianDong"),
                rs.getDouble("TienDauCa"),
                rs.getDouble("DoanhThuTienMat"),
                rs.getDouble("DoanhThuChuyenKhoan"),
                rs.getDouble("TongDoanhThu"),
                rs.getDouble("TienLyThuyet"),
                tienThucTe,
                chenhLech,
                rs.getString("TrangThai"),
                rs.getString("GhiChu")
        );
    }
}
