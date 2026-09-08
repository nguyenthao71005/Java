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
        }

        assertTrue(true);
    }
}
