package com.restaurant;

import com.restaurant.database.DatabaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseMigrationTest {

    @Test
    public void migrateDatabase() throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null || conn.isClosed()) {
            System.out.println("MySQL offline, skipping live migration.");
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            // 1. Tạo bảng CaLamViec nếu chưa có
            String sqlCreateCaLamViec = "CREATE TABLE IF NOT EXISTS `CaLamViec` (" +
                    "`MaCa` INT AUTO_INCREMENT PRIMARY KEY," +
                    "`MaNV` INT NOT NULL COMMENT 'Nhân viên phụ trách ca'," +
                    "`ThoiGianMo` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm bắt đầu mở ca'," +
                    "`ThoiGianDong` DATETIME NULL COMMENT 'Thời điểm đóng / bàn giao ca'," +
                    "`TienDauCa` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Tiền mặt ban đầu trong két'," +
                    "`DoanhThuTienMat` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Doanh thu tiền mặt ghi nhận trong ca'," +
                    "`DoanhThuChuyenKhoan` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Doanh thu chuyển khoản / thẻ trong ca'," +
                    "`TongDoanhThu` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng doanh thu trong ca'," +
                    "`TienLyThuyet` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Tiền mặt lý thuyết = TienDauCa + DoanhThuTienMat'," +
                    "`TienThucTe` DECIMAL(12, 2) NULL COMMENT 'Tiền mặt thực tế kiểm đếm khi kết ca'," +
                    "`ChenhLech` DECIMAL(12, 2) NULL COMMENT 'Chênh lệch = TienThucTe - TienLyThuyet'," +
                    "`TrangThai` ENUM('DangMo', 'DaDong') NOT NULL DEFAULT 'DangMo' COMMENT 'Trạng thái ca'," +
                    "`GhiChu` TEXT NULL COMMENT 'Ghi chú bàn giao / giải trình chênh lệch thừa thiếu'," +
                    "CONSTRAINT `fk_calamviec_nhanvien` FOREIGN KEY (`MaNV`) REFERENCES `NhanVien` (`MaNV`) ON DELETE RESTRICT" +
                    ") ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;";
            stmt.executeUpdate(sqlCreateCaLamViec);
            System.out.println("-> Bảng CaLamViec đã sẵn sàng!");

            // 2. Thêm cột MaCa vào bảng HoaDon nếu chưa có
            try (ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM `HoaDon` LIKE 'MaCa';")) {
                if (!rs.next()) {
                    stmt.executeUpdate("ALTER TABLE `HoaDon` ADD COLUMN `MaCa` INT NULL COMMENT 'Mã ca làm việc ghi nhận doanh thu';");
                    stmt.executeUpdate("ALTER TABLE `HoaDon` ADD CONSTRAINT `fk_hoadon_ca` FOREIGN KEY (`MaCa`) REFERENCES `CaLamViec` (`MaCa`) ON DELETE SET NULL;");
                    stmt.executeUpdate("CREATE INDEX `idx_hoadon_maca` ON `HoaDon` (`MaCa`);");
                    System.out.println("-> Đã bổ sung cột MaCa và khóa ngoại vào bảng HoaDon!");
                } else {
                    System.out.println("-> Bảng HoaDon đã có cột MaCa.");
                }
            }

            // 3. Tạo View v_LichSuCaLamViec
            String sqlCreateView = "CREATE OR REPLACE VIEW `v_LichSuCaLamViec` AS " +
                    "SELECT " +
                    "    c.`MaCa`, " +
                    "    c.`MaNV`, " +
                    "    nv.`HoTen` AS `TenNV`, " +
                    "    c.`ThoiGianMo`, " +
                    "    c.`ThoiGianDong`, " +
                    "    c.`TienDauCa`, " +
                    "    c.`DoanhThuTienMat`, " +
                    "    c.`DoanhThuChuyenKhoan`, " +
                    "    c.`TongDoanhThu`, " +
                    "    c.`TienLyThuyet`, " +
                    "    c.`TienThucTe`, " +
                    "    c.`ChenhLech`, " +
                    "    c.`TrangThai`, " +
                    "    c.`GhiChu`, " +
                    "    COUNT(h.`MaHD`) AS `SoHoaDon` " +
                    "FROM `CaLamViec` c " +
                    "JOIN `NhanVien` nv ON c.`MaNV` = nv.`MaNV` " +
                    "LEFT JOIN `HoaDon` h ON c.`MaCa` = h.`MaCa` " +
                    "GROUP BY c.`MaCa`, c.`MaNV`, nv.`HoTen`, c.`ThoiGianMo`, c.`ThoiGianDong`, c.`TienDauCa`, c.`DoanhThuTienMat`, c.`DoanhThuChuyenKhoan`, c.`TongDoanhThu`, c.`TienLyThuyet`, c.`TienThucTe`, c.`ChenhLech`, c.`TrangThai`, c.`GhiChu` " +
                    "ORDER BY c.`MaCa` DESC;";
            stmt.executeUpdate(sqlCreateView);
            System.out.println("-> View v_LichSuCaLamViec đã sẵn sàng!");

            // 4. Seed CaLamViec nếu chưa có bản ghi
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `CaLamViec`;")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO `CaLamViec` (`MaCa`, `MaNV`, `ThoiGianMo`, `ThoiGianDong`, `TienDauCa`, `DoanhThuTienMat`, `DoanhThuChuyenKhoan`, `TongDoanhThu`, `TienLyThuyet`, `TienThucTe`, `ChenhLech`, `TrangThai`, `GhiChu`) VALUES " +
                            "(1, 2, NOW() - INTERVAL 2 DAY + INTERVAL 7 HOUR, NOW() - INTERVAL 2 DAY + INTERVAL 15 HOUR, 1000000.00, 2450000.00, 0.00, 2450000.00, 3450000.00, 3450000.00, 0.00, 'DaDong', 'Ca làm việc khớp tiền 100%, bàn giao ca chiều suôn sẻ'), " +
                            "(2, 2, NOW() - INTERVAL 4 HOUR, NULL, 1500000.00, 3925800.00, 2494800.00, 6420600.00, 5425800.00, NULL, NULL, 'DangMo', 'Ca trực hiện tại của Thu ngân Trần Thị Mai');");

                    stmt.executeUpdate("UPDATE `HoaDon` SET `MaCa` = 1 WHERE `MaHD` IN (9001, 9002);");
                    stmt.executeUpdate("UPDATE `HoaDon` SET `MaCa` = 2 WHERE `MaHD` IN (1004, 1005, 1009, 1010, 1014, 1015);");
                    System.out.println("-> Đã nạp dữ liệu mẫu cho CaLamViec và liên kết HoaDon!");
                }
            }

            // 5. Cập nhật ENUM VaiTro để phân định Admin và QuanLy
            try {
                stmt.executeUpdate("ALTER TABLE `NhanVien` MODIFY COLUMN `VaiTro` ENUM('Admin', 'QuanLy', 'ThuNgan', 'PhucVu', 'Bep') NOT NULL DEFAULT 'PhucVu' COMMENT 'Phân quyền truy cập';");
                stmt.executeUpdate("UPDATE `NhanVien` SET `VaiTro` = 'Admin' WHERE `TenDangNhap` = 'admin';");

                // Thêm tài khoản quản lý nếu chưa có
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `NhanVien` WHERE `TenDangNhap` = 'quanly';")) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        String hashedPass = com.restaurant.util.PasswordUtils.hashPassword("quanly123");
                        stmt.executeUpdate("INSERT INTO `NhanVien` (`HoTen`, `TenDangNhap`, `MatKhau`, `SoDienThoai`, `VaiTro`, `TrangThai`) VALUES " +
                                "('Nguyễn Văn Quản Lý', 'quanly', '" + hashedPass + "', '0901234568', 'QuanLy', 'DangLam');");
                        System.out.println("-> Đã tạo tài khoản Quản lý: quanly / quanly123 (Role: QuanLy)");
                    }
                }

                // Thêm tài khoản Admin mới (admin02) nếu chưa có
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `NhanVien` WHERE `TenDangNhap` = 'admin02';")) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        String hashedPass = com.restaurant.util.PasswordUtils.hashPassword("admin123");
                        stmt.executeUpdate("INSERT INTO `NhanVien` (`HoTen`, `TenDangNhap`, `MatKhau`, `SoDienThoai`, `VaiTro`, `TrangThai`) VALUES " +
                                "('Nguyễn Phương Thảo (Admin)', 'admin02', '" + hashedPass + "', '0987654321', 'Admin', 'DangLam');");
                        System.out.println("-> Đã tạo tài khoản Admin mới: admin02 / admin123 (Role: Admin)");
                    }
                }
                System.out.println("-> Đã phân tách vai trò Admin và QuanLy trong CSDL!");
            } catch (Exception e) {
                System.err.println("Lỗi cập nhật vai trò NhanVien: " + e.getMessage());
            }

            // 6. Tạo bảng LichSuTruyCap (Audit Trail)
            String sqlCreateLichSuTruyCap = "CREATE TABLE IF NOT EXISTS `LichSuTruyCap` (" +
                    "`MaTruyCap` INT AUTO_INCREMENT PRIMARY KEY," +
                    "`MaNV` INT NULL COMMENT 'Mã nhân viên đăng nhập'," +
                    "`TenDangNhap` VARCHAR(50) NOT NULL COMMENT 'Tên đăng nhập đã dùng'," +
                    "`HoTen` VARCHAR(100) NULL COMMENT 'Họ tên nhân viên'," +
                    "`HanhDong` ENUM('DangNhap', 'DangXuat', 'DangNhapThatBai') NOT NULL COMMENT 'Loại hành động'," +
                    "`ThoiGian` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm thực hiện'," +
                    "`DiaChiIP` VARCHAR(45) NULL COMMENT 'Địa chỉ IP (hỗ trợ IPv6)'," +
                    "`TrinhDuyet` VARCHAR(255) NULL COMMENT 'User-Agent / Trình duyệt'," +
                    "`TrangThai` ENUM('ThanhCong', 'ThatBai') NOT NULL DEFAULT 'ThanhCong' COMMENT 'Kết quả đăng nhập'," +
                    "`GhiChu` VARCHAR(255) NULL COMMENT 'Ghi chú thêm (nếu có)'," +
                    "CONSTRAINT `fk_lstruycap_nhanvien` FOREIGN KEY (`MaNV`) REFERENCES `NhanVien` (`MaNV`) ON DELETE SET NULL" +
                    ") ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;";
            stmt.executeUpdate(sqlCreateLichSuTruyCap);

            // 7. Tạo bảng LichSuThaoTac (Audit Log)
            String sqlCreateLichSuThaoTac = "CREATE TABLE IF NOT EXISTS `LichSuThaoTac` (" +
                    "`MaThaoTac` INT AUTO_INCREMENT PRIMARY KEY," +
                    "`MaNV` INT NULL COMMENT 'Mã nhân viên thực hiện'," +
                    "`HoTen` VARCHAR(100) NULL COMMENT 'Họ tên nhân viên'," +
                    "`TenBang` VARCHAR(50) NOT NULL COMMENT 'Tên bảng bị tác động'," +
                    "`HanhDong` ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL COMMENT 'Loại thao tác'," +
                    "`BanGhiCu` TEXT NULL COMMENT 'Dữ liệu cũ (JSON) trước khi thay đổi'," +
                    "`BanGhiMoi` TEXT NULL COMMENT 'Dữ liệu mới (JSON) sau khi thay đổi'," +
                    "`MoTa` VARCHAR(500) NULL COMMENT 'Mô tả ngắn gọn thao tác'," +
                    "`ChiTiet` TEXT NULL COMMENT 'Chi tiết đầy đủ thao tác'," +
                    "`ThoiGian` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm thực hiện'," +
                    "`DiaChiIP` VARCHAR(45) NULL COMMENT 'Địa chỉ IP thực hiện'," +
                    "`MayTinh` VARCHAR(100) NULL COMMENT 'Tên máy tính (hostname)'," +
                    "CONSTRAINT `fk_lsthaotac_nhanvien` FOREIGN KEY (`MaNV`) REFERENCES `NhanVien` (`MaNV`) ON DELETE SET NULL" +
                    ") ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;";
            stmt.executeUpdate(sqlCreateLichSuThaoTac);

            // 8. Tạo Views cho Audit
            stmt.executeUpdate("CREATE OR REPLACE VIEW `v_LichSuTruyCap` AS " +
                    "SELECT `MaTruyCap`, `MaNV`, `TenDangNhap`, `HoTen`, `HanhDong`, `ThoiGian`, `DiaChiIP`, `TrangThai`, `GhiChu` " +
                    "FROM `LichSuTruyCap` ORDER BY `ThoiGian` DESC;");

            stmt.executeUpdate("CREATE OR REPLACE VIEW `v_LichSuThaoTac` AS " +
                    "SELECT `MaThaoTac`, `MaNV`, `HoTen`, `TenBang`, `HanhDong`, `MoTa`, `ChiTiet`, `ThoiGian`, `DiaChiIP`, `MayTinh` " +
                    "FROM `LichSuThaoTac` ORDER BY `ThoiGian` DESC;");

            System.out.println("-> Toàn bộ bảng và view AuditLog (LichSuTruyCap, LichSuThaoTac) đã sẵn sàng!");
        }

        assertTrue(true);
    }

    @Test
    public void testImportDulieuSql() throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null || conn.isClosed()) {
            System.out.println("MySQL offline, skipping dulieu.sql import test.");
            return;
        }

        java.io.File sqlFile = new java.io.File("d:/BTLJAVA/dulieu.sql");
        assertTrue(sqlFile.exists(), "File dulieu.sql phải tồn tại");

        System.out.println("-> Đang thực thi nạp file dulieu.sql vào MySQL...");
        String content = java.nio.file.Files.readString(sqlFile.toPath(), java.nio.charset.StandardCharsets.UTF_8);

        // Tách câu lệnh theo dấu chấm phẩy ;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            
            StringBuilder currentStmt = new StringBuilder();
            for (String line : content.split("\r?\n")) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.isEmpty()) {
                    continue;
                }
                currentStmt.append(line).append("\n");
                if (trimmed.endsWith(";")) {
                    String sqlToRun = currentStmt.toString().trim();
                    if (!sqlToRun.isEmpty()) {
                        try {
                            stmt.execute(sqlToRun);
                        } catch (Exception ex) {
                            System.err.println("Lỗi câu lệnh SQL: " + sqlToRun.substring(0, Math.min(80, sqlToRun.length())) + "... -> " + ex.getMessage());
                            throw ex;
                        }
                    }
                    currentStmt.setLength(0);
                }
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            System.out.println("-> Đã nạp thành công toàn bộ file dulieu.sql vào MySQL!");

            // Kiểm tra số lượng bản ghi thực tế từng bảng
            String[] tables = {"NhanVien", "Ban", "DanhMuc", "MonAn", "Voucher", "CaLamViec", "HoaDon", "ChiTietHoaDon", "LichSuTruyCap", "LichSuThaoTac"};
            for (String t : tables) {
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `" + t + "`")) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("   [BẢNG " + t + "] Số bản ghi: " + count);
                        if (t.equals("NhanVien") || t.equals("Ban")) {
                            assertTrue(count >= 10 && count <= 20, "Bảng " + t + " phải có 10-20 bản ghi (thực tế: " + count + ")");
                        } else {
                            assertTrue(count >= 100, "Bảng " + t + " phải có ít nhất 100 bản ghi (thực tế: " + count + ")");
                        }
                    }
                }
            }
        }
    }
}

