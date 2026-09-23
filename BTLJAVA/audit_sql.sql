USE `restaurant_db`;
-- =============================================================================
-- BẢNG LỊCH SỬ TRUY CẬP (Audit Trail - Access Log)
-- Theo dõi đăng nhập, đăng xuất, thời gian, địa chỉ IP
-- =============================================================================
CREATE TABLE IF NOT EXISTS `LichSuTruyCap` (
    `MaTruyCap` INT AUTO_INCREMENT PRIMARY KEY,
    `MaNV` INT NULL COMMENT 'Mã nhân viên đăng nhập',
    `TenDangNhap` VARCHAR(50) NOT NULL COMMENT 'Tên đăng nhập đã dùng',
    `HoTen` VARCHAR(100) NULL COMMENT 'Họ tên nhân viên',
    `HanhDong` ENUM('DangNhap', 'DangXuat', 'DangNhapThatBai') NOT NULL COMMENT 'Loại hành động',
    `ThoiGian` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm thực hiện',
    `DiaChiIP` VARCHAR(45) NULL COMMENT 'Địa chỉ IP (hỗ trợ IPv6)',
    `TrinhDuyet` VARCHAR(255) NULL COMMENT 'User-Agent / Trình duyệt',
    `TrangThai` ENUM('ThanhCong', 'ThatBai') NOT NULL DEFAULT 'ThanhCong' COMMENT 'Kết quả đăng nhập',
    `GhiChu` VARCHAR(255) NULL COMMENT 'Ghi chú thêm (nếu có)',
    CONSTRAINT `fk_lstruycap_nhanvien` FOREIGN KEY (`MaNV`) REFERENCES `NhanVien` (`MaNV`) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Index cho tìm kiếm nhanh theo nhân viên, thời gian
CREATE INDEX `idx_lstruycap_manv` ON `LichSuTruyCap` (`MaNV`);
CREATE INDEX `idx_lstruycap_thoigian` ON `LichSuTruyCap` (`ThoiGian`);
CREATE INDEX `idx_lstruycap_hanhdong` ON `LichSuTruyCap` (`HanhDong`, `TrangThai`);

-- =============================================================================
-- BẢNG LỊCH SỬ THAO TÁC (Audit Log - Operation History)
-- Ghi nhận mọi thao tác CRUD trên hệ thống: tạo, sửa, xóa dữ liệu
-- =============================================================================
CREATE TABLE IF NOT EXISTS `LichSuThaoTac` (
    `MaThaoTac` INT AUTO_INCREMENT PRIMARY KEY,
    `MaNV` INT NULL COMMENT 'Mã nhân viên thực hiện',
    `HoTen` VARCHAR(100) NULL COMMENT 'Họ tên nhân viên',
    `TenBang` VARCHAR(50) NOT NULL COMMENT 'Tên bảng bị tác động (NhanVien, Ban, MonAn...)',
    `HanhDong` ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL COMMENT 'Loại thao tác',
    `BanGhiCu` TEXT NULL COMMENT 'Dữ liệu cũ (JSON) trước khi thay đổi',
    `BanGhiMoi` TEXT NULL COMMENT 'Dữ liệu mới (JSON) sau khi thay đổi',
    `MoTa` VARCHAR(500) NULL COMMENT 'Mô tả ngắn gọn thao tác (VD: Cập nhật giá món Bò bít tết)',
    `ChiTiet` TEXT NULL COMMENT 'Chi tiết đầy đủ thao tác (VD: giá cũ: 450000, giá mới: 495000)',
    `ThoiGian` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm thực hiện',
    `DiaChiIP` VARCHAR(45) NULL COMMENT 'Địa chỉ IP thực hiện',
    `MayTinh` VARCHAR(100) NULL COMMENT 'Tên máy tính (hostname)',
    CONSTRAINT `fk_lsthaotac_nhanvien` FOREIGN KEY (`MaNV`) REFERENCES `NhanVien` (`MaNV`) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Index cho tìm kiếm nhanh
CREATE INDEX `idx_lsthaotac_manv` ON `LichSuThaoTac` (`MaNV`);
CREATE INDEX `idx_lsthaotac_bang` ON `LichSuThaoTac` (`TenBang`);
CREATE INDEX `idx_lsthaotac_hanhdong` ON `LichSuThaoTac` (`HanhDong`);
CREATE INDEX `idx_lsthaotac_thoigian` ON `LichSuThaoTac` (`ThoiGian`);

-- =============================================================================
-- VIEW TIỆN ÍCH: Lịch sử truy cập chi tiết
-- =============================================================================
CREATE OR REPLACE VIEW `v_LichSuTruyCap` AS
SELECT 
    l.`MaTruyCap`,
    l.`MaNV`,
    l.`TenDangNhap`,
    l.`HoTen`,
    l.`HanhDong`,
    l.`ThoiGian`,
    l.`DiaChiIP`,
    l.`TrangThai`,
    l.`GhiChu`
FROM `LichSuTruyCap` l
ORDER BY l.`ThoiGian` DESC;

-- =============================================================================
-- VIEW TIỆN ÍCH: Lịch sử thao tác chi tiết
-- =============================================================================
CREATE OR REPLACE VIEW `v_LichSuThaoTac` AS
SELECT 
    t.`MaThaoTac`,
    t.`MaNV`,
    t.`HoTen`,
    t.`TenBang`,
    t.`HanhDong`,
    t.`MoTa`,
    t.`ChiTiet`,
    t.`ThoiGian`,
    t.`DiaChiIP`,
    t.`MayTinh`
FROM `LichSuThaoTac` t
ORDER BY t.`ThoiGian` DESC;

-- =============================================================================
-- VIEW TIỆN ÍCH: Thống kê đăng nhập theo ngày
-- =============================================================================
CREATE OR REPLACE VIEW `v_ThongKeDangNhap` AS
SELECT 
    DATE(`ThoiGian`) AS `Ngay`,
    COUNT(*) AS `TongSoTruyCap`,
    SUM(CASE WHEN `HanhDong` = 'DangNhap' AND `TrangThai` = 'ThanhCong' THEN 1 ELSE 0 END) AS `DangNhapThanhCong`,
    SUM(CASE WHEN `HanhDong` = 'DangNhap' AND `TrangThai` = 'ThatBai' THEN 1 ELSE 0 END) AS `DangNhapThatBai`,
    SUM(CASE WHEN `HanhDong` = 'DangXuat' THEN 1 ELSE 0 END) AS `DangXuat`
FROM `LichSuTruyCap`
GROUP BY DATE(`ThoiGian`)
ORDER BY `Ngay` DESC;
