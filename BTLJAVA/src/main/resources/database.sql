-- =============================================================================
-- HỆ THỐNG QUẢN LÝ VẬN HÀNH NHÀ HÀNG (RESTAURANT OPS)
-- CƠ SỞ DỮ LIỆU: MySQL 8.0+
-- Thiết kế tối ưu, bám sát 100% nghiệp vụ thực tế: Phục vụ, Thu ngân, Quản trị
-- Bảng mã: utf8mb4 / utf8mb4_unicode_ci (Hỗ trợ tiếng Việt đầy đủ và chính xác)
-- =============================================================================

CREATE DATABASE IF NOT EXISTS `restaurant_db` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `restaurant_db`;

-- Tắt kiểm tra khóa ngoại tạm thời khi khởi tạo lại bảng
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `ChiTietHoaDon`;
DROP TABLE IF EXISTS `HoaDon`;
DROP TABLE IF EXISTS `CaLamViec`;
DROP TABLE IF EXISTS `Voucher`;
DROP TABLE IF EXISTS `MonAn`;
DROP TABLE IF EXISTS `DanhMuc`;
DROP TABLE IF EXISTS `Ban`;
DROP TABLE IF EXISTS `NhanVien`;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- 1. BẢNG NHÂN VIÊN (NhanVien)
-- Nghiệp vụ: Quản trị tài khoản, phân quyền (Admin, Thu ngân, Phục vụ, Bếp)
-- =============================================================================
CREATE TABLE `NhanVien` (
    `MaNV` INT AUTO_INCREMENT PRIMARY KEY,
    `HoTen` VARCHAR(100) NOT NULL COMMENT 'Họ và tên nhân viên',
    `TenDangNhap` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên đăng nhập hệ thống',
    `MatKhau` VARCHAR(255) NOT NULL COMMENT 'Mật khẩu (khuyến nghị mã hóa BCrypt/MD5)',
    `SoDienThoai` VARCHAR(20) NULL COMMENT 'Số điện thoại liên lạc',
    `VaiTro` ENUM('QuanLy', 'ThuNgan', 'PhucVu', 'Bep') NOT NULL DEFAULT 'PhucVu' COMMENT 'Phân quyền truy cập',
    `TrangThai` ENUM('DangLam', 'NghiViec') NOT NULL DEFAULT 'DangLam' COMMENT 'Trạng thái công việc',
    `NgayTao` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =============================================================================
-- 2. BẢNG BÀN ĂN (Ban)
-- Nghiệp vụ: Quản lý sơ đồ bàn trực quan (Xanh lá = Trống, Đỏ = Đang có khách),
-- hiển thị nhân viên đang phụ trách bàn, lọc theo khu vực (Tầng 1, Tầng 2, VIP).
-- =============================================================================
CREATE TABLE `Ban` (
    `MaBan` INT AUTO_INCREMENT PRIMARY KEY,
    `TenBan` VARCHAR(50) NOT NULL COMMENT 'Tên hiển thị: Bàn 1, Bàn 2...',
    `KhuVuc` VARCHAR(50) NOT NULL DEFAULT 'Tầng 1' COMMENT 'Khu vực: Tầng 1, Tầng 2, VIP, Sân vườn',
    `SucChua` INT NOT NULL DEFAULT 4 COMMENT 'Số lượng chỗ ngồi tối đa',
    `TrangThai` ENUM('Trong', 'CoKhach', 'DatTruoc') NOT NULL DEFAULT 'Trong' COMMENT 'Trạng thái vận hành',
    `MaNVPhuTrach` INT NULL COMMENT 'Nhân viên phục vụ đang phụ trách',
    CONSTRAINT `fk_ban_nhanvien` FOREIGN KEY (`MaNVPhuTrach`) REFERENCES `NhanVien` (`MaNV`) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =============================================================================
-- 3. BẢNG DANH MỤC MÓN ĂN (DanhMuc)
-- Nghiệp vụ: Phân loại thực đơn (Khai vị, Món chính, Hải sản, Đồ uống...).
-- Ràng buộc: Chặn xóa danh mục nếu đang có món ăn trực thuộc.
-- =============================================================================
CREATE TABLE `DanhMuc` (
    `MaDM` INT AUTO_INCREMENT PRIMARY KEY,
    `TenDM` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Tên danh mục món',
    `MoTa` VARCHAR(255) NULL COMMENT 'Mô tả chi tiết danh mục'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =============================================================================
-- 4. BẢNG MÓN ĂN (MonAn)
-- Nghiệp vụ: Quản lý món ăn, hình ảnh, giá bán.
-- Tính năng nghiệp vụ: "Ẩn món / Hết hàng" khi bếp hết nguyên liệu để Phục vụ không thể order.
-- =============================================================================
CREATE TABLE `MonAn` (
    `MaMon` INT AUTO_INCREMENT PRIMARY KEY,
    `TenMon` VARCHAR(150) NOT NULL COMMENT 'Tên món ăn hoặc thức uống',
    `MaDM` INT NOT NULL COMMENT 'Mã danh mục trực thuộc',
    `DonGia` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Đơn giá bán niêm yết (VND)',
    `HinhAnh` VARCHAR(255) NULL DEFAULT 'food_placeholder.png' COMMENT 'Tên file hoặc đường dẫn ảnh',
    `TrangThai` ENUM('ConMon', 'HetHang') NOT NULL DEFAULT 'ConMon' COMMENT 'Trạng thái phục vụ',
    CONSTRAINT `fk_monan_danhmuc` FOREIGN KEY (`MaDM`) REFERENCES `DanhMuc` (`MaDM`) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =============================================================================
-- 5. BẢNG VOUCHER / KHUYẾN MÃI (Voucher)
-- Nghiệp vụ: Thiết lập mã giảm giá theo chiến dịch, ngày bắt đầu/kết thúc,
-- giảm theo % và mức giảm tối đa.
-- =============================================================================
CREATE TABLE `Voucher` (
    `MaVoucher` VARCHAR(30) PRIMARY KEY COMMENT 'Mã code giảm giá: TET2026, VIP10, GIAM50K',
    `TenVoucher` VARCHAR(150) NOT NULL COMMENT 'Tên sự kiện / khuyến mãi',
    `PhanTramGiam` DECIMAL(5, 2) NOT NULL DEFAULT 0.00 COMMENT '% giảm giá (ví dụ: 10.00)',
    `GiamToiDa` DECIMAL(12, 2) NULL DEFAULT 0.00 COMMENT 'Số tiền giảm tối đa (VND)',
    `DonHangToiThieu` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Giá trị đơn tối thiểu để áp dụng',
    `NgayBatDau` DATE NOT NULL COMMENT 'Ngày bắt đầu áp dụng',
    `NgayKetThuc` DATE NOT NULL COMMENT 'Ngày hết hạn',
    `TrangThai` ENUM('HoatDong', 'TamDung', 'HetHan') NOT NULL DEFAULT 'HoatDong'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =============================================================================
-- 6. BẢNG CA LÀM VIỆC (CaLamViec)
-- Nghiệp vụ: Quản lý ca thu ngân, tiền đầu ca, doanh thu tiền mặt, chuyển khoản,
-- tiền lý thuyết, tiền thực tế kiểm đếm, chênh lệch thừa/thiếu và bàn giao ca.
-- =============================================================================
CREATE TABLE `CaLamViec` (
    `MaCa` INT AUTO_INCREMENT PRIMARY KEY,
    `MaNV` INT NOT NULL COMMENT 'Nhân viên phụ trách ca',
    `ThoiGianMo` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm bắt đầu mở ca',
    `ThoiGianDong` DATETIME NULL COMMENT 'Thời điểm đóng / bàn giao ca',
    `TienDauCa` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Tiền mặt ban đầu trong két',
    `DoanhThuTienMat` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Doanh thu tiền mặt ghi nhận trong ca',
    `DoanhThuChuyenKhoan` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Doanh thu chuyển khoản / thẻ trong ca',
    `TongDoanhThu` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng doanh thu trong ca',
    `TienLyThuyet` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Tiền mặt lý thuyết = TienDauCa + DoanhThuTienMat',
    `TienThucTe` DECIMAL(12, 2) NULL COMMENT 'Tiền mặt thực tế kiểm đếm khi kết ca',
    `ChenhLech` DECIMAL(12, 2) NULL COMMENT 'Chênh lệch = TienThucTe - TienLyThuyet',
    `TrangThai` ENUM('DangMo', 'DaDong') NOT NULL DEFAULT 'DangMo' COMMENT 'Trạng thái ca',
    `GhiChu` TEXT NULL COMMENT 'Ghi chú bàn giao / giải trình chênh lệch thừa thiếu',
    CONSTRAINT `fk_calamviec_nhanvien` FOREIGN KEY (`MaNV`) REFERENCES `NhanVien` (`MaNV`) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =============================================================================
-- 7. BẢNG HÓA ĐƠN (HoaDon)
-- Nghiệp vụ: Quản lý order bàn, lưu thời gian vào/ra, chiết khấu, VAT,
-- phương thức thanh toán, liên kết ca làm việc (MaCa).
-- Hỗ trợ NGHIỆP VỤ TÁCH HÓA ĐƠN qua `MaHD_Goc` (Hóa đơn cha/gốc).
-- =============================================================================
CREATE TABLE `HoaDon` (
    `MaHD` INT AUTO_INCREMENT PRIMARY KEY,
    `MaBan` INT NOT NULL COMMENT 'Bàn phát sinh hóa đơn',
    `MaNV` INT NULL COMMENT 'Nhân viên lập hóa đơn / thu ngân',
    `MaCa` INT NULL COMMENT 'Mã ca làm việc ghi nhận doanh thu',
    `MaVoucher` VARCHAR(30) NULL COMMENT 'Mã khuyến mãi áp dụng',
    `MaHD_Goc` INT NULL COMMENT 'ID hóa đơn gốc nếu đây là hóa đơn được tách ra',
    `GioVao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm mở bàn / nhận khách',
    `GioRa` DATETIME NULL COMMENT 'Thời điểm thanh toán hoàn tất',
    `TamTinh` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng tiền món ăn chưa trừ chiết khấu và VAT',
    `GiamGiaPhanTram` DECIMAL(5, 2) NOT NULL DEFAULT 0.00 COMMENT '% giảm giá',
    `TienGiamGia` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Số tiền được giảm',
    `VatPhanTram` DECIMAL(5, 2) NOT NULL DEFAULT 8.00 COMMENT '% thuế VAT',
    `TienVAT` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Số tiền thuế VAT',
    `TongTien` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng tiền thực tế khách phải trả',
    `HinhThucTT` ENUM('TienMat', 'ChuyenKhoan', 'The') NOT NULL DEFAULT 'TienMat' COMMENT 'Hình thức thanh toán',
    `TrangThai` ENUM('ChuaThanhToan', 'DaThanhToan', 'DaHuy') NOT NULL DEFAULT 'ChuaThanhToan' COMMENT 'Trạng thái hóa đơn',
    CONSTRAINT `fk_hoadon_ban` FOREIGN KEY (`MaBan`) REFERENCES `Ban` (`MaBan`) ON DELETE RESTRICT,
    CONSTRAINT `fk_hoadon_nhanvien` FOREIGN KEY (`MaNV`) REFERENCES `NhanVien` (`MaNV`) ON DELETE SET NULL,
    CONSTRAINT `fk_hoadon_ca` FOREIGN KEY (`MaCa`) REFERENCES `CaLamViec` (`MaCa`) ON DELETE SET NULL,
    CONSTRAINT `fk_hoadon_voucher` FOREIGN KEY (`MaVoucher`) REFERENCES `Voucher` (`MaVoucher`) ON DELETE SET NULL,
    CONSTRAINT `fk_hoadon_goc` FOREIGN KEY (`MaHD_Goc`) REFERENCES `HoaDon` (`MaHD`) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =============================================================================
-- 7. BẢNG CHI TIẾT HÓA ĐƠN (ChiTietHoaDon)
-- Nghiệp vụ: Danh sách từng món trong hóa đơn, số lượng, đơn giá tại thời điểm gọi,
-- ghi chú bếp ("Ít đường", "Không cay"), theo dõi trạng thái chế biến (DangCho, DangLam, DaRaMon, DaHuy).
-- =============================================================================
CREATE TABLE `ChiTietHoaDon` (
    `MaCTHD` INT AUTO_INCREMENT PRIMARY KEY,
    `MaHD` INT NOT NULL COMMENT 'Mã hóa đơn trực thuộc',
    `MaMon` INT NOT NULL COMMENT 'Món ăn được gọi',
    `SoLuong` INT NOT NULL DEFAULT 1 COMMENT 'Số lượng gọi',
    `DonGia` DECIMAL(12, 2) NOT NULL COMMENT 'Đơn giá lúc gọi (đảm bảo không bị đổi khi sửa giá món)',
    `ThanhTien` DECIMAL(12, 2) GENERATED ALWAYS AS (`SoLuong` * `DonGia`) STORED COMMENT 'Tự động tính thành tiền',
    `GhiChu` VARCHAR(255) NULL COMMENT 'Ghi chú cho bếp (Ít cay, không hành, làm chín kỹ...)',
    `TrangThaiMon` ENUM('DangCho', 'DangLam', 'DaRaMon', 'DaHuy') NOT NULL DEFAULT 'DangCho' COMMENT 'Trạng thái phục vụ món',
    `ThoiGianGoi` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_cthd_hoadon` FOREIGN KEY (`MaHD`) REFERENCES `HoaDon` (`MaHD`) ON DELETE CASCADE,
    CONSTRAINT `fk_cthd_monan` FOREIGN KEY (`MaMon`) REFERENCES `MonAn` (`MaMon`) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =============================================================================
-- TỐI ƯU HÓA: CÁC CHỈ MỤC INDEX CHO BÁO CÁO THỐNG KÊ & TÌM KIẾM NHANH
-- =============================================================================
-- 1. Index thống kê doanh thu theo thời gian và trạng thái thanh toán
CREATE INDEX `idx_hoadon_ngay_trangthai` ON `HoaDon` (`GioRa`, `TrangThai`, `TongTien`);
CREATE INDEX `idx_hoadon_maban` ON `HoaDon` (`MaBan`, `TrangThai`);
CREATE INDEX `idx_hoadon_maca` ON `HoaDon` (`MaCa`);

-- 2. Index lọc bàn theo khu vực và trạng thái
CREATE INDEX `idx_ban_khuvuc_trangthai` ON `Ban` (`KhuVuc`, `TrangThai`);

-- 3. Index tìm kiếm món ăn theo danh mục và trạng thái còn món
CREATE INDEX `idx_monan_danhmuc_trangthai` ON `MonAn` (`MaDM`, `TrangThai`);

-- 4. Index tổng hợp món ăn bán chạy trong chi tiết hóa đơn
CREATE INDEX `idx_cthd_mamon_trangthaimon` ON `ChiTietHoaDon` (`MaMon`, `TrangThaiMon`);


-- =============================================================================
-- DỮ LIỆU KHỞI TẠO MẪU (SEED DATA - ĂN KHỚP 100% VỚI ỨNG DỤNG JAVA SWING)
-- =============================================================================

-- 1. Nhân viên
INSERT INTO `NhanVien` (`MaNV`, `HoTen`, `TenDangNhap`, `MatKhau`, `SoDienThoai`, `VaiTro`, `TrangThai`) VALUES
(1, 'Nguyễn Văn An (Admin)', 'admin', 'admin123', '0901234567', 'QuanLy', 'DangLam'),
(2, 'Trần Thị Mai', 'cashier01', 'mai123', '0912345678', 'ThuNgan', 'DangLam'),
(3, 'Lê Hoàng Nam', 'waiter01', 'nam123', '0923456789', 'PhucVu', 'DangLam'),
(4, 'Phạm Quốc Tuấn', 'chef01', 'tuan123', '0934567890', 'Bep', 'DangLam'),
(5, 'Đỗ Kim Ngân', 'cashier02', 'ngan123', '0945678901', 'ThuNgan', 'DangLam'),
(6, 'Vũ Đức Thịnh', 'waiter02', 'thinh123', '0956789012', 'PhucVu', 'NghiViec');

-- 2. Danh mục món ăn
INSERT INTO `DanhMuc` (`MaDM`, `TenDM`, `MoTa`) VALUES
(1, 'Khai vị', 'Các món nhẹ mở đầu bữa tiệc: salad, súp, khai vị'),
(2, 'Món chính', 'Bò bít tết Wagyu, sườn nướng BBQ, cừu nướng'),
(3, 'Hải sản', 'Tôm hùm, cua sốt ớt, cá hồi cao cấp tươi sống'),
(4, 'Đồ uống', 'Rượu vang đỏ cao cấp, cocktail, trà, nước hoa quả'),
(5, 'Tráng miệng', 'Bánh ngọt phong cách Pháp, kem tươi, chè tráng miệng');

-- 3. Thực đơn món ăn
INSERT INTO `MonAn` (`MaMon`, `TenMon`, `MaDM`, `DonGia`, `HinhAnh`, `TrangThai`) VALUES
(101, 'Gỏi ngó sen tôm thịt', 1, 125000.00, 'salad.png', 'ConMon'),
(102, 'Súp vi cá bào ngư', 1, 245000.00, 'soup.png', 'ConMon'),
(103, 'Khoai tây chiên phô mai', 1, 65000.00, 'fries.png', 'ConMon'),
(201, 'Bò bít tết Wagyu sốt tiêu', 2, 495000.00, 'steak.png', 'ConMon'),
(202, 'Sườn heo nướng BBQ', 2, 320000.00, 'ribs.png', 'ConMon'),
(203, 'Cừu nướng thảo mộc', 2, 380000.00, 'lamb.png', 'ConMon'),
(204, 'Gà nướng lu thảo mộc', 2, 290000.00, 'chicken.png', 'ConMon'),
(301, 'Tôm hùm nướng bơ tỏi', 3, 850000.00, 'lobster.png', 'ConMon'),
(302, 'Cá hồi áp chảo sốt chanh dây', 3, 360000.00, 'salmon.png', 'ConMon'),
(303, 'Cua sốt ớt Singapore', 3, 550000.00, 'crab.png', 'ConMon'),
(401, 'Rượu vang đỏ Cabernet Sauvignon', 4, 680000.00, 'wine.png', 'ConMon'),
(402, 'Trà đào cam sả', 4, 550000.00, 'tea.png', 'ConMon'),
(403, 'Nước ép cam tươi', 4, 60000.00, 'juice.png', 'ConMon'),
(501, 'Bánh Tiramisu Ý', 5, 85000.00, 'cake.png', 'ConMon');

-- 4. Bàn ăn (20 bàn phân bổ theo 3 khu vực khớp TableMapPanel)
INSERT INTO `Ban` (`MaBan`, `TenBan`, `KhuVuc`, `SucChua`, `TrangThai`, `MaNVPhuTrach`) VALUES
(1, 'Bàn 1', 'Tầng 1', 4, 'Trong', NULL),
(2, 'Bàn 2', 'Tầng 1', 4, 'Trong', NULL),
(3, 'Bàn 3', 'Tầng 1', 6, 'Trong', NULL),
(4, 'Bàn 4', 'Tầng 1', 4, 'CoKhach', 3),
(5, 'Bàn 5', 'Tầng 1', 8, 'CoKhach', 3),
(6, 'Bàn 6', 'Tầng 1', 6, 'Trong', NULL),
(7, 'Bàn 7', 'Tầng 1', 4, 'Trong', NULL),
(8, 'Bàn 8', 'Tầng 1', 4, 'Trong', NULL),
(9, 'Bàn 9', 'Tầng 1', 6, 'CoKhach', 3),
(10, 'Bàn 10', 'Tầng 1', 8, 'CoKhach', 3),
(11, 'Bàn 11', 'Tầng 2', 4, 'Trong', NULL),
(12, 'Bàn 12', 'Tầng 2', 6, 'Trong', NULL),
(13, 'Bàn 13', 'Tầng 2', 4, 'Trong', NULL),
(14, 'Bàn 14', 'Tầng 2', 4, 'CoKhach', 6),
(15, 'Bàn 15', 'Tầng 2', 6, 'CoKhach', 6),
(16, 'Bàn 16', 'Tầng 2', 4, 'Trong', NULL),
(17, 'Bàn 17', 'Phòng VIP', 4, 'Trong', NULL),
(18, 'Bàn 18', 'Phòng VIP', 6, 'Trong', NULL),
(19, 'Bàn 19', 'Phòng VIP', 4, 'Trong', NULL),
(20, 'Bàn 20', 'Phòng VIP', 8, 'Trong', NULL);

-- 5. Voucher Khuyến mãi
INSERT INTO `Voucher` (`MaVoucher`, `TenVoucher`, `PhanTramGiam`, `GiamToiDa`, `DonHangToiThieu`, `NgayBatDau`, `NgayKetThuc`, `TrangThai`) VALUES
('LEHOI10', 'Khuyến mãi Lễ Hội giảm 10%', 10.00, 100000.00, 500000.00, '2026-01-01', '2026-12-31', 'HoatDong'),
('VIP20', 'Tri ân khách VIP thân thiết 20%', 20.00, 300000.00, 1000000.00, '2026-01-01', '2026-12-31', 'HoatDong'),
('GIAM50K', 'Chiết khấu trực tiếp 50k', 0.00, 50000.00, 300000.00, '2026-01-01', '2026-12-31', 'HoatDong');

-- 6. Ca làm việc mẫu
INSERT INTO `CaLamViec` (`MaCa`, `MaNV`, `ThoiGianMo`, `ThoiGianDong`, `TienDauCa`, `DoanhThuTienMat`, `DoanhThuChuyenKhoan`, `TongDoanhThu`, `TienLyThuyet`, `TienThucTe`, `ChenhLech`, `TrangThai`, `GhiChu`) VALUES
(1, 2, NOW() - INTERVAL 2 DAY + INTERVAL 7 HOUR, NOW() - INTERVAL 2 DAY + INTERVAL 15 HOUR, 1000000.00, 2450000.00, 0.00, 2450000.00, 3450000.00, 3450000.00, 0.00, 'DaDong', 'Ca làm việc khớp tiền 100%, bàn giao ca chiều suôn sẻ'),
(2, 2, NOW() - INTERVAL 4 HOUR, NULL, 1500000.00, 3925800.00, 2494800.00, 6420600.00, 5425800.00, NULL, NULL, 'DangMo', 'Ca trực hiện tại của Thu ngân Trần Thị Mai');

-- 7. Hóa đơn mẫu (Bàn 4, 5, 9, 10, 14, 15 đang phục vụ; Hóa đơn cũ đã thanh toán để thống kê)
INSERT INTO `HoaDon` (`MaHD`, `MaBan`, `MaNV`, `MaCa`, `MaVoucher`, `MaHD_Goc`, `GioVao`, `GioRa`, `TamTinh`, `GiamGiaPhanTram`, `TienGiamGia`, `VatPhanTram`, `TienVAT`, `TongTien`, `HinhThucTT`, `TrangThai`) VALUES
(1004, 4, 2, 2, NULL, NULL, NOW() - INTERVAL 45 MINUTE, NULL, 1795000.00, 0.00, 0.00, 8.00, 143600.00, 1938600.00, 'TienMat', 'ChuaThanhToan'),
(1005, 5, 2, 2, 'LEHOI10', NULL, NOW() - INTERVAL 60 MINUTE, NULL, 1370000.00, 10.00, 100000.00, 8.00, 101600.00, 1371600.00, 'ChuyenKhoan', 'ChuaThanhToan'),
(1009, 9, 2, 2, NULL, NULL, NOW() - INTERVAL 30 MINUTE, NULL, 850000.00, 0.00, 0.00, 8.00, 68000.00, 918000.00, 'TienMat', 'ChuaThanhToan'),
(1010, 10, 2, 2, NULL, NULL, NOW() - INTERVAL 75 MINUTE, NULL, 1270000.00, 0.00, 0.00, 8.00, 101600.00, 1371600.00, 'The', 'ChuaThanhToan'),
(1014, 14, 2, 2, NULL, NULL, NOW() - INTERVAL 20 MINUTE, NULL, 990000.00, 0.00, 0.00, 8.00, 79200.00, 1069200.00, 'TienMat', 'ChuaThanhToan'),
(1015, 15, 2, 2, NULL, NULL, NOW() - INTERVAL 90 MINUTE, NULL, 1040000.00, 0.00, 0.00, 8.00, 83200.00, 1123200.00, 'ChuyenKhoan', 'ChuaThanhToan'),
-- Hóa đơn lịch sử đã thanh toán phục vụ Báo cáo Doanh thu Dashboard
(9001, 1, 2, 1, NULL, NULL, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY + INTERVAL 60 MINUTE, 2450000.00, 0.00, 0.00, 8.00, 196000.00, 2646000.00, 'ChuyenKhoan', 'DaThanhToan'),
(9002, 2, 2, 1, 'LEHOI10', NULL, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY + INTERVAL 75 MINUTE, 3200000.00, 10.00, 100000.00, 8.00, 248000.00, 3348000.00, 'The', 'DaThanhToan');

-- 8. Chi tiết hóa đơn mẫu
INSERT INTO `ChiTietHoaDon` (`MaCTHD`, `MaHD`, `MaMon`, `SoLuong`, `DonGia`, `GhiChu`, `TrangThaiMon`, `ThoiGianGoi`) VALUES
-- Chi tiết Bàn 4 (Khớp hình chụp UI)
(1, 1004, 201, 2, 495000.00, 'Chín vừa (Medium Rare)', 'DaRaMon', NOW() - INTERVAL 40 MINUTE),
(2, 1004, 401, 1, 680000.00, 'Ướp lạnh trước khi rót', 'DaRaMon', NOW() - INTERVAL 35 MINUTE),
(3, 1004, 101, 1, 125000.00, 'Ít cay', 'DaRaMon', NOW() - INTERVAL 30 MINUTE),
-- Chi tiết Bàn 5
(4, 1005, 301, 1, 850000.00, 'Nhiều bơ tỏi', 'DangLam', NOW() - INTERVAL 55 MINUTE),
(5, 1005, 202, 1, 320000.00, 'Sốt BBQ cay đậm', 'DangCho', NOW() - INTERVAL 50 MINUTE),
(6, 1005, 103, 2, 65000.00, 'Phục vụ trước', 'DaRaMon', NOW() - INTERVAL 45 MINUTE),
-- Chi tiết Bàn 9
(7, 1009, 301, 1, 850000.00, '', 'DangLam', NOW() - INTERVAL 25 MINUTE),
-- Chi tiết Bàn 10
(8, 1010, 203, 2, 380000.00, 'Không lấy hành tây', 'DaRaMon', NOW() - INTERVAL 70 MINUTE),
(9, 1010, 303, 1, 550000.00, 'Ăn kèm bánh mì nóng', 'DangLam', NOW() - INTERVAL 65 MINUTE),
-- Chi tiết Bàn 14
(10, 1014, 201, 2, 495000.00, 'Làm chín kỹ (Well done)', 'DangCho', NOW() - INTERVAL 15 MINUTE),
-- Chi tiết Bàn 15
(11, 1015, 102, 2, 245000.00, '', 'DaRaMon', NOW() - INTERVAL 85 MINUTE),
(12, 1015, 303, 1, 550000.00, 'Ăn kèm bánh mì', 'DangLam', NOW() - INTERVAL 80 MINUTE),
-- Chi tiết hóa đơn lịch sử (9001, 9002)
(13, 9001, 201, 3, 495000.00, '', 'DaRaMon', NOW() - INTERVAL 2 DAY),
(14, 9001, 301, 1, 850000.00, '', 'DaRaMon', NOW() - INTERVAL 2 DAY),
(15, 9002, 303, 2, 550000.00, '', 'DaRaMon', NOW() - INTERVAL 1 DAY),
(16, 9002, 401, 3, 680000.00, '', 'DaRaMon', NOW() - INTERVAL 1 DAY);


-- =============================================================================
-- CÁC VIEW TIỆN ÍCH BÁO CÁO THỐNG KÊ DOANH THU, TOP MÓN ĂN & CA LÀM VIỆC
-- =============================================================================

-- 1. View Thống kê doanh thu theo ngày
CREATE OR REPLACE VIEW `v_DoanhThuTheoNgay` AS
SELECT 
    DATE(`GioRa`) AS `Ngay`,
    COUNT(`MaHD`) AS `TongSoHoaDon`,
    SUM(`TamTinh`) AS `TongTamTinh`,
    SUM(`TienGiamGia`) AS `TongTienGiam`,
    SUM(`TienVAT`) AS `TongTienVAT`,
    SUM(`TongTien`) AS `TongDoanhThuThucTe`
FROM `HoaDon`
WHERE `TrangThai` = 'DaThanhToan'
GROUP BY DATE(`GioRa`)
ORDER BY `Ngay` DESC;

-- 2. View Top 10 món ăn bán chạy nhất
CREATE OR REPLACE VIEW `v_TopMonBanChay` AS
SELECT 
    m.`MaMon`,
    m.`TenMon`,
    dm.`TenDM` AS `DanhMuc`,
    SUM(ct.`SoLuong`) AS `TongSoLuongBan`,
    SUM(ct.`SoLuong` * ct.`DonGia`) AS `TongDoanhThuMon`
FROM `ChiTietHoaDon` ct
JOIN `MonAn` m ON ct.`MaMon` = m.`MaMon`
JOIN `DanhMuc` dm ON m.`MaDM` = dm.`MaDM`
JOIN `HoaDon` hd ON ct.`MaHD` = hd.`MaHD`
WHERE ct.`TrangThaiMon` != 'DaHuy' AND hd.`TrangThai` = 'DaThanhToan'
GROUP BY m.`MaMon`, m.`TenMon`, dm.`TenDM`
ORDER BY `TongSoLuongBan` DESC
LIMIT 10;

-- 3. View Lịch sử ca làm việc và thống kê doanh thu theo ca
CREATE OR REPLACE VIEW `v_LichSuCaLamViec` AS
SELECT 
    c.`MaCa`,
    c.`MaNV`,
    nv.`HoTen` AS `TenNV`,
    c.`ThoiGianMo`,
    c.`ThoiGianDong`,
    c.`TienDauCa`,
    c.`DoanhThuTienMat`,
    c.`DoanhThuChuyenKhoan`,
    c.`TongDoanhThu`,
    c.`TienLyThuyet`,
    c.`TienThucTe`,
    c.`ChenhLech`,
    c.`TrangThai`,
    c.`GhiChu`,
    COUNT(h.`MaHD`) AS `SoHoaDon`
FROM `CaLamViec` c
JOIN `NhanVien` nv ON c.`MaNV` = nv.`MaNV`
LEFT JOIN `HoaDon` h ON c.`MaCa` = h.`MaCa`
GROUP BY c.`MaCa`, c.`MaNV`, nv.`HoTen`, c.`ThoiGianMo`, c.`ThoiGianDong`, c.`TienDauCa`, c.`DoanhThuTienMat`, c.`DoanhThuChuyenKhoan`, c.`TongDoanhThu`, c.`TienLyThuyet`, c.`TienThucTe`, c.`ChenhLech`, c.`TrangThai`, c.`GhiChu`
ORDER BY c.`MaCa` DESC;


-- =============================================================================
-- CÁC STORED PROCEDURE NGIỆP VỤ THỰC TẾ
-- =============================================================================

DELIMITER //

-- 1. Thủ tục Chuyển bàn / Ghép bàn (Chuyển order từ Bàn cũ sang Bàn mới)
CREATE PROCEDURE `sp_ChuyenBan`(
    IN `p_MaBanCu` INT,
    IN `p_MaBanMoi` INT
)
BEGIN
    DECLARE v_MaHD INT;
    
    -- Lấy mã hóa đơn chưa thanh toán của bàn cũ
    SELECT `MaHD` INTO v_MaHD 
    FROM `HoaDon` 
    WHERE `MaBan` = `p_MaBanCu` AND `TrangThai` = 'ChuaThanhToan' 
    LIMIT 1;
    
    IF v_MaHD IS NOT NULL THEN
        -- Cập nhật hóa đơn sang mã bàn mới
        UPDATE `HoaDon` SET `MaBan` = `p_MaBanMoi` WHERE `MaHD` = v_MaHD;
        -- Bàn cũ về Trống, Bàn mới thành Có khách
        UPDATE `Ban` SET `TrangThai` = 'Trong', `MaNVPhuTrach` = NULL WHERE `MaBan` = `p_MaBanCu`;
        UPDATE `Ban` SET `TrangThai` = 'CoKhach' WHERE `MaBan` = `p_MaBanMoi`;
    END IF;
END //

-- 2. Thủ tục Thanh toán Hóa đơn & Giải phóng bàn
CREATE PROCEDURE `sp_ThanhToanHoaDon`(
    IN `p_MaHD` INT,
    IN `p_HinhThucTT` ENUM('TienMat', 'ChuyenKhoan', 'The'),
    IN `p_GiamGiaPhanTram` DECIMAL(5,2),
    IN `p_VatPhanTram` DECIMAL(5,2)
)
BEGIN
    DECLARE v_MaBan INT;
    DECLARE v_TamTinh DECIMAL(12,2);
    DECLARE v_TienGiam DECIMAL(12,2);
    DECLARE v_TienVAT DECIMAL(12,2);
    DECLARE v_TongTien DECIMAL(12,2);
    DECLARE v_MaCa INT;
    
    -- Lấy mã ca làm việc đang mở gần nhất để gắn vào hóa đơn
    SELECT `MaCa` INTO v_MaCa 
    FROM `CaLamViec` 
    WHERE `TrangThai` = 'DangMo' 
    ORDER BY `MaCa` DESC LIMIT 1;
    
    -- Lấy mã bàn và tính lại tạm tính từ các món không bị hủy
    SELECT `MaBan` INTO v_MaBan FROM `HoaDon` WHERE `MaHD` = `p_MaHD`;
    
    SELECT IFNULL(SUM(`ThanhTien`), 0) INTO v_TamTinh 
    FROM `ChiTietHoaDon` 
    WHERE `MaHD` = `p_MaHD` AND `TrangThaiMon` != 'DaHuy';
    
    -- Tính tiền giảm giá, VAT và tổng cộng
    SET v_TienGiam = v_TamTinh * (p_GiamGiaPhanTram / 100);
    SET v_TienVAT = (v_TamTinh - v_TienGiam) * (p_VatPhanTram / 100);
    SET v_TongTien = (v_TamTinh - v_TienGiam) + v_TienVAT;
    
    -- Cập nhật hóa đơn thành Đã thanh toán và ghi nhận MaCa
    UPDATE `HoaDon` 
    SET `GioRa` = NOW(),
        `TamTinh` = v_TamTinh,
        `GiamGiaPhanTram` = p_GiamGiaPhanTram,
        `TienGiamGia` = v_TienGiam,
        `VatPhanTram` = p_VatPhanTram,
        `TienVAT` = v_TienVAT,
        `TongTien` = v_TongTien,
        `HinhThucTT` = p_HinhThucTT,
        `TrangThai` = 'DaThanhToan',
        `MaCa` = IFNULL(`MaCa`, v_MaCa)
    WHERE `MaHD` = `p_MaHD`;
    
    -- Trả bàn về trạng thái Trống
    UPDATE `Ban` 
    SET `TrangThai` = 'Trong', `MaNVPhuTrach` = NULL 
    WHERE `MaBan` = v_MaBan;
END //

DELIMITER ;
