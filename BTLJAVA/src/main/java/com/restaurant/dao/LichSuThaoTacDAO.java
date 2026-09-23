package com.restaurant.dao;

import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.LichSuThaoTac;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho Lịch sử thao tác - Thao tác với bảng LichSuThaoTac
 */
public class LichSuThaoTacDAO {

    /**
     * Ghi nhận một thao tác (INSERT, UPDATE, DELETE)
     */
    public boolean ghiNhanThaoTac(LichSuThaoTac lichSu) {
        String sql = """
            INSERT INTO LichSuThaoTac 
            (MaNV, HoTen, TenBang, HanhDong, BanGhiCu, BanGhiMoi, MoTa, ChiTiet, ThoiGian, DiaChiIP, MayTinh)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, lichSu.getMaNV());
            stmt.setString(2, lichSu.getHoTen());
            stmt.setString(3, lichSu.getTenBang());
            stmt.setString(4, lichSu.getHanhDong().name());
            stmt.setString(5, lichSu.getBanGhiCu());
            stmt.setString(6, lichSu.getBanGhiMoi());
            stmt.setString(7, lichSu.getMoTa());
            stmt.setString(8, lichSu.getChiTiet());
            stmt.setObject(9, lichSu.getThoiGian() != null ? lichSu.getThoiGian() : LocalDateTime.now());
            stmt.setString(10, lichSu.getDiaChiIP());
            stmt.setString(11, lichSu.getMayTinh());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi ghi nhận thao tác: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy tất cả lịch sử thao tác
     */
    public List<LichSuThaoTac> getAll() {
        List<LichSuThaoTac> list = new ArrayList<>();
        String sql = "SELECT * FROM LichSuThaoTac ORDER BY ThoiGian DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToObject(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy lịch sử thao tác: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy lịch sử thao tác theo khoảng thời gian
     */
    public List<LichSuThaoTac> getByKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        List<LichSuThaoTac> list = new ArrayList<>();
        String sql = """
            SELECT * FROM LichSuThaoTac 
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
     * Lấy lịch sử thao tác theo bảng (NhanVien, Ban, MonAn...)
     */
    public List<LichSuThaoTac> getByBang(String tenBang) {
        List<LichSuThaoTac> list = new ArrayList<>();
        String sql = "SELECT * FROM LichSuThaoTac WHERE TenBang = ? ORDER BY ThoiGian DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tenBang);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToObject(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy lịch sử theo bảng: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy lịch sử thao tác theo nhân viên
     */
    public List<LichSuThaoTac> getByMaNV(int maNV) {
        List<LichSuThaoTac> list = new ArrayList<>();
        String sql = "SELECT * FROM LichSuThaoTac WHERE MaNV = ? ORDER BY ThoiGian DESC";

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
     * Lấy lịch sử thao tác theo loại hành động
     */
    public List<LichSuThaoTac> getByHanhDong(LichSuThaoTac.HanhDong hanhDong) {
        List<LichSuThaoTac> list = new ArrayList<>();
        String sql = "SELECT * FROM LichSuThaoTac WHERE HanhDong = ? ORDER BY ThoiGian DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hanhDong.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToObject(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy lịch sử theo hành động: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy lịch sử thao tác gần đây (mặc định 100 bản ghi)
     */
    public List<LichSuThaoTac> getRecent(int limit) {
        List<LichSuThaoTac> list = new ArrayList<>();
        String sql = "SELECT * FROM LichSuThaoTac ORDER BY ThoiGian DESC LIMIT ?";

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
     * Tìm kiếm theo từ khóa (họ tên, mô tả, chi tiết)
     */
    public List<LichSuThaoTac> search(String tuKhoa) {
        List<LichSuThaoTac> list = new ArrayList<>();
        String sql = """
            SELECT * FROM LichSuThaoTac 
            WHERE HoTen LIKE ? OR MoTa LIKE ? OR ChiTiet LIKE ? OR TenBang LIKE ?
            ORDER BY ThoiGian DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + tuKhoa + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
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
     * Lấy thao tác gần nhất trên một bảng (để hiển thị "vừa xảy ra")
     */
    public LichSuThaoTac getThaoTacGanNhat(String tenBang) {
        String sql = """
            SELECT * FROM LichSuThaoTac 
            WHERE TenBang = ?
            ORDER BY ThoiGian DESC LIMIT 1
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tenBang);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToObject(rs);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy thao tác gần nhất: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Đếm số thao tác theo ngày
     */
    public int demTheoNgay(Date ngay) {
        String sql = "SELECT COUNT(*) FROM LichSuThaoTac WHERE DATE(ThoiGian) = DATE(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, ngay);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi đếm thao tác: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Map ResultSet sang Object
     */
    private LichSuThaoTac mapResultSetToObject(ResultSet rs) throws SQLException {
        LichSuThaoTac ls = new LichSuThaoTac();

        ls.setMaThaoTac(rs.getInt("MaThaoTac"));
        ls.setMaNV(rs.getObject("MaNV") != null ? rs.getInt("MaNV") : null);
        ls.setHoTen(rs.getString("HoTen"));
        ls.setTenBang(rs.getString("TenBang"));

        String hanhDongStr = rs.getString("HanhDong");
        ls.setHanhDong(LichSuThaoTac.HanhDong.fromString(hanhDongStr));

        ls.setBanGhiCu(rs.getString("BanGhiCu"));
        ls.setBanGhiMoi(rs.getString("BanGhiMoi"));
        ls.setMoTa(rs.getString("MoTa"));
        ls.setChiTiet(rs.getString("ChiTiet"));

        Timestamp thoiGian = rs.getTimestamp("ThoiGian");
        if (thoiGian != null) {
            ls.setThoiGian(thoiGian.toLocalDateTime());
        }

        ls.setDiaChiIP(rs.getString("DiaChiIP"));
        ls.setMayTinh(rs.getString("MayTinh"));

        return ls;
    }
}
