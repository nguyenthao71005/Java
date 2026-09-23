package com.restaurant.dao;

import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.NhanVien;
import com.restaurant.util.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng `NhanVien`
 */
public class NhanVienDAO {

    /**
     * Chuyển đổi từ text giao diện hoặc chuỗi nhập vào sang mã ENUM chuẩn trong CSDL MySQL:
     * 'Admin', 'QuanLy', 'ThuNgan', 'PhucVu', 'Bep'
     */
    public static String toDbVaiTro(String vt) {
        if (vt == null || vt.trim().isEmpty()) {
            return "PhucVu";
        }
        String lower = vt.trim().toLowerCase();
        if (lower.equals("admin") || lower.contains("quản trị")) {
            return "Admin";
        }
        if (lower.contains("quản lý") || lower.contains("quanly") || lower.contains("manager")) {
            return "QuanLy";
        }
        if (lower.contains("thu ngân") || lower.contains("thungan") || lower.contains("cashier")) {
            return "ThuNgan";
        }
        if (lower.contains("bếp") || lower.contains("bep") || lower.contains("chef") || lower.contains("đầu bếp")) {
            return "Bep";
        }
        return "PhucVu";
    }

    /**
     * Chuyển đổi mã ENUM CSDL MySQL sang tên tiếng Việt hiển thị trên giao diện người dùng
     */
    public static String toDisplayVaiTro(String vt) {
        if (vt == null || vt.trim().isEmpty()) {
            return "Phục vụ";
        }
        String lower = vt.trim().toLowerCase();
        if (lower.equals("admin") || lower.contains("quản trị")) {
            return "Quản trị viên (Admin)";
        }
        if (lower.contains("quanly") || lower.contains("quản lý") || lower.contains("manager")) {
            return "Quản lý";
        }
        if (lower.contains("thungan") || lower.contains("cashier") || lower.contains("thu ngân")) {
            return "Thu ngân";
        }
        if (lower.contains("bep") || lower.contains("chef") || lower.contains("bếp") || lower.contains("đầu bếp")) {
            return "Đầu bếp";
        }
        return "Phục vụ";
    }

    /**
     * Chuyển đổi trạng thái từ giao diện sang mã ENUM chuẩn trong CSDL MySQL:
     * 'DangLam', 'NghiViec'
     */
    public static String toDbTrangThai(String tt) {
        if (tt == null || tt.trim().isEmpty()) {
            return "DangLam";
        }
        String lower = tt.trim().toLowerCase();
        if (lower.contains("nghỉ") || lower.contains("nghiviec") || lower.contains("inactive")) {
            return "NghiViec";
        }
        return "DangLam";
    }

    /**
     * Chuyển đổi mã trạng thái CSDL MySQL sang tên tiếng Việt hiển thị
     */
    public static String toDisplayTrangThai(String tt) {
        if (tt == null || tt.trim().isEmpty()) {
            return "Đang làm việc";
        }
        String lower = tt.trim().toLowerCase();
        if (lower.contains("nghiviec") || lower.contains("nghỉ") || lower.contains("inactive")) {
            return "Đã nghỉ việc";
        }
        return "Đang làm việc";
    }

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
     * Xác thực đăng nhập nhân viên từ MySQL (dùng BCrypt)
     */
    public NhanVien authenticate(String username, String plainPassword) {
        String sql = "SELECT MaNV, HoTen, TenDangNhap, MatKhau, VaiTro, TrangThai FROM NhanVien WHERE TenDangNhap = ? AND TrangThai = 'DangLam'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("MatKhau");
                    
                    // Kiểm tra: nếu là BCrypt hash thì verify, nếu là plain text thì so sánh trực tiếp
                    if (PasswordUtils.isBCryptHash(hashedPassword)) {
                        // Dùng BCrypt verify
                        if (!PasswordUtils.verifyPassword(plainPassword, hashedPassword)) {
                            return null; // Sai mật khẩu
                        }
                    } else {
                        // Legacy: so sánh trực tiếp (để hỗ trợ dữ liệu cũ)
                        if (!hashedPassword.equals(plainPassword)) {
                            return null; // Sai mật khẩu
                        }
                    }
                    
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
            // Mã hóa password bằng BCrypt
            ps.setString(3, PasswordUtils.hashPassword(nv.getMatKhau()));
            ps.setString(4, toDbVaiTro(nv.getVaiTro()));
            ps.setString(5, toDbTrangThai(nv.getTrangThai()));

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
        boolean hasNewPass = nv.getMatKhau() != null && !nv.getMatKhau().trim().isEmpty() && !nv.getMatKhau().equals("******");
        String sql = hasNewPass
                ? "UPDATE NhanVien SET HoTen = ?, TenDangNhap = ?, MatKhau = ?, VaiTro = ?, TrangThai = ? WHERE MaNV = ?"
                : "UPDATE NhanVien SET HoTen = ?, TenDangNhap = ?, VaiTro = ?, TrangThai = ? WHERE MaNV = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (hasNewPass) {
                ps.setString(1, nv.getHoTen());
                ps.setString(2, nv.getTenDangNhap());
                // Mã hóa password mới bằng BCrypt
                ps.setString(3, PasswordUtils.hashPassword(nv.getMatKhau()));
                ps.setString(4, toDbVaiTro(nv.getVaiTro()));
                ps.setString(5, toDbTrangThai(nv.getTrangThai()));
                ps.setInt(6, nv.getMaNV());
            } else {
                ps.setString(1, nv.getHoTen());
                ps.setString(2, nv.getTenDangNhap());
                ps.setString(3, toDbVaiTro(nv.getVaiTro()));
                ps.setString(4, toDbTrangThai(nv.getTrangThai()));
                ps.setInt(5, nv.getMaNV());
            }

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
