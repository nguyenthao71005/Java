package com.restaurant.dao;

import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.LichSuTruyCap;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho Lịch sử truy cập - Thao tác với bảng LichSuTruyCap
 */
public class LichSuTruyCapDAO {

    /**
     * Ghi nhận một lượt truy cập (đăng nhập/đăng xuất)
     */
    public boolean ghiNhanTruyCap(LichSuTruyCap lichSu) {
        String sql = """
            INSERT INTO LichSuTruyCap 
            (MaNV, TenDangNhap, HoTen, HanhDong, ThoiGian, DiaChiIP, TrinhDuyet, TrangThai, GhiChu)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, lichSu.getMaNV());
            stmt.setString(2, lichSu.getTenDangNhap());
            stmt.setString(3, lichSu.getHoTen());
            stmt.setString(4, lichSu.getHanhDong().name());
            stmt.setObject(5, lichSu.getThoiGian() != null ? lichSu.getThoiGian() : LocalDateTime.now());
            stmt.setString(6, lichSu.getDiaChiIP());
            stmt.setString(7, lichSu.getTrinhDuyet());
            stmt.setString(8, lichSu.getTrangThai().name());
            stmt.setString(9, lichSu.getGhiChu());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi ghi nhận truy cập: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy tất cả lịch sử truy cập, sắp xếp theo thời gian giảm dần
     */
    public List<LichSuTruyCap> getAll() {
        List<LichSuTruyCap> list = new ArrayList<>();
        String sql = "SELECT * FROM LichSuTruyCap ORDER BY ThoiGian DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToObject(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy lịch sử truy cập: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy lịch sử truy cập theo ngày
     */
    public List<LichSuTruyCap> getByNgay(Date ngay) {
        List<LichSuTruyCap> list = new ArrayList<>();
        String sql = """
            SELECT * FROM LichSuTruyCap 
            WHERE DATE(ThoiGian) = DATE(?) 
            ORDER BY ThoiGian DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, ngay);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToObject(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy lịch sử theo ngày: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy lịch sử truy cập trong khoảng thời gian
     */
    public List<LichSuTruyCap> getByKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        List<LichSuTruyCap> list = new ArrayList<>();
        String sql = """
            SELECT * FROM LichSuTruyCap 
            WHERE ThoiGian BETWEEN ? AND ? 
            ORDER BY ThoiGian DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, tuNgay);
            stmt.setObject(2, denNgay);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToObject(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy lịch sử theo khoảng thời gian: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy lịch sử truy cập theo nhân viên
     */
    public List<LichSuTruyCap> getByMaNV(int maNV) {
        List<LichSuTruyCap> list = new ArrayList<>();
        String sql = "SELECT * FROM LichSuTruyCap WHERE MaNV = ? ORDER BY ThoiGian DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maNV);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToObject(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy lịch sử theo NV: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Đếm số lần đăng nhập thất bại của một tài khoản trong ngày
     */
    public int demDangNhapThatBai(String tenDangNhap, Date ngay) {
        String sql = """
            SELECT COUNT(*) FROM LichSuTruyCap 
            WHERE TenDangNhap = ? 
            AND DATE(ThoiGian) = DATE(?) 
            AND HanhDong = 'DangNhapThatBai'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tenDangNhap);
            stmt.setDate(2, ngay);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi đếm đăng nhập thất bại: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Lấy lịch sử truy cập gần đây (mặc định 50 bản ghi)
     */
    public List<LichSuTruyCap> getRecent(int limit) {
        List<LichSuTruyCap> list = new ArrayList<>();
        String sql = "SELECT * FROM LichSuTruyCap ORDER BY ThoiGian DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToObject(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy lịch sử gần đây: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Tìm kiếm theo từ khóa (tên đăng nhập, họ tên)
     */
    public List<LichSuTruyCap> search(String tuKhoa) {
        List<LichSuTruyCap> list = new ArrayList<>();
        String sql = """
            SELECT * FROM LichSuTruyCap 
            WHERE TenDangNhap LIKE ? OR HoTen LIKE ?
            ORDER BY ThoiGian DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + tuKhoa + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToObject(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy lượt đăng nhập cuối cùng của một nhân viên
     */
    public LichSuTruyCap getLanDangNhapCuoi(int maNV) {
        String sql = """
            SELECT * FROM LichSuTruyCap 
            WHERE MaNV = ? AND HanhDong = 'DangNhap'
            ORDER BY ThoiGian DESC LIMIT 1
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maNV);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToObject(rs);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy lần đăng nhập cuối: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Map ResultSet sang Object
     */
    private LichSuTruyCap mapResultSetToObject(ResultSet rs) throws SQLException {
        LichSuTruyCap ls = new LichSuTruyCap();

        ls.setMaTruyCap(rs.getInt("MaTruyCap"));
        ls.setMaNV(rs.getObject("MaNV") != null ? rs.getInt("MaNV") : null);
        ls.setTenDangNhap(rs.getString("TenDangNhap"));
        ls.setHoTen(rs.getString("HoTen"));

        String hanhDongStr = rs.getString("HanhDong");
        ls.setHanhDong(LichSuTruyCap.HanhDong.valueOf(hanhDongStr));

        Timestamp thoiGian = rs.getTimestamp("ThoiGian");
        if (thoiGian != null) {
            ls.setThoiGian(thoiGian.toLocalDateTime());
        }

        ls.setDiaChiIP(rs.getString("DiaChiIP"));
        ls.setTrinhDuyet(rs.getString("TrinhDuyet"));

        String trangThaiStr = rs.getString("TrangThai");
        ls.setTrangThai(LichSuTruyCap.TrangThai.valueOf(trangThaiStr));

        ls.setGhiChu(rs.getString("GhiChu"));

        return ls;
    }
}
