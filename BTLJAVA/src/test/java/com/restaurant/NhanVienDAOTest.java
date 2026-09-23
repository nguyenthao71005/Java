package com.restaurant;

import com.restaurant.dao.NhanVienDAO;
import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.NhanVien;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NhanVienDAOTest {

    @Test
    public void testRoleAndStatusMapping() {
        // Test VaiTro to DB ENUM
        assertEquals("QuanLy", NhanVienDAO.toDbVaiTro("Quản lý"));
        assertEquals("QuanLy", NhanVienDAO.toDbVaiTro("quanly"));
        assertEquals("Admin", NhanVienDAO.toDbVaiTro("Admin"));
        assertEquals("Admin", NhanVienDAO.toDbVaiTro("Quản trị viên (Admin)"));
        assertEquals("ThuNgan", NhanVienDAO.toDbVaiTro("Thu ngân"));
        assertEquals("ThuNgan", NhanVienDAO.toDbVaiTro("thungan"));
        assertEquals("PhucVu", NhanVienDAO.toDbVaiTro("Phục vụ"));
        assertEquals("PhucVu", NhanVienDAO.toDbVaiTro("phucvu"));
        assertEquals("Bep", NhanVienDAO.toDbVaiTro("Đầu bếp"));
        assertEquals("Bep", NhanVienDAO.toDbVaiTro("Bếp"));
        assertEquals("Bep", NhanVienDAO.toDbVaiTro("chef"));

        // Test VaiTro to UI Display
        assertEquals("Quản lý", NhanVienDAO.toDisplayVaiTro("QuanLy"));
        assertEquals("Quản trị viên (Admin)", NhanVienDAO.toDisplayVaiTro("admin"));
        assertEquals("Quản trị viên (Admin)", NhanVienDAO.toDisplayVaiTro("Admin"));
        assertEquals("Thu ngân", NhanVienDAO.toDisplayVaiTro("ThuNgan"));
        assertEquals("Phục vụ", NhanVienDAO.toDisplayVaiTro("PhucVu"));
        assertEquals("Đầu bếp", NhanVienDAO.toDisplayVaiTro("Bep"));

        // Test TrangThai to DB ENUM
        assertEquals("DangLam", NhanVienDAO.toDbTrangThai("Đang làm việc"));
        assertEquals("DangLam", NhanVienDAO.toDbTrangThai("DangLam"));
        assertEquals("NghiViec", NhanVienDAO.toDbTrangThai("Đã nghỉ việc"));
        assertEquals("NghiViec", NhanVienDAO.toDbTrangThai("NghiViec"));

        // Test TrangThai to UI Display
        assertEquals("Đang làm việc", NhanVienDAO.toDisplayTrangThai("DangLam"));
        assertEquals("Đã nghỉ việc", NhanVienDAO.toDisplayTrangThai("NghiViec"));
    }

    @Test
    public void testInsertNhanVienWithVietnameseText() {
        if (!DatabaseConnection.isConnected()) {
            System.out.println("Bỏ qua test database vì CSDL MySQL chưa kết nối");
            return;
        }

        NhanVienDAO dao = new NhanVienDAO();
        String testUsername = "test_user_" + System.currentTimeMillis();
        // Nhập trực tiếp chuỗi tiếng Việt như khi bấm trên giao diện
        NhanVien nv = new NhanVien(0, "Nguyễn Kiểm Thử", testUsername, "pass123", "Quản lý", "Đang làm việc");

        boolean inserted = dao.insert(nv);
        assertTrue(inserted, "insert nhân viên với chuỗi tiếng Việt 'Quản lý' phải thành công và không bị truncated");
        assertTrue(nv.getMaNV() > 0, "Mã NV tự sinh phải lớn hơn 0");

        // Thử cập nhật
        nv.setHoTen("Nguyễn Kiểm Thử Đã Sửa");
        nv.setVaiTro("Thu ngân");
        nv.setTrangThai("Đã nghỉ việc");
        boolean updated = dao.update(nv);
        assertTrue(updated, "update nhân viên phải thành công");

        // Dọn dẹp sau khi test
        boolean deleted = dao.delete(nv.getMaNV());
        assertTrue(deleted, "delete nhân viên kiểm thử phải thành công");
    }
}
