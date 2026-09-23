# Báo Cáo Cập Nhật Tài Khoản Admin Mới & Đồng Bộ Tài Liệu Hệ Thống

## 1. Cập Nhật Tài Khoản & Cơ Sở Dữ Liệu MySQL

Đã hoàn thiện cập nhật trực tiếp vào file [`database.sql`](file:///d:/BTLJAVA/database.sql), [`src/main/resources/database.sql`](file:///d:/BTLJAVA/src/main/resources/database.sql) và đồng bộ vào CSDL MySQL `restaurant_db`:

### Cấu trúc ENUM `VaiTro`
```sql
`VaiTro` ENUM('Admin', 'QuanLy', 'ThuNgan', 'PhucVu', 'Bep') NOT NULL DEFAULT 'PhucVu' COMMENT 'Phân quyền truy cập'
```

### Danh sách tài khoản người dùng mẫu trong CSDL

```sql
INSERT INTO `NhanVien` (`MaNV`, `HoTen`, `TenDangNhap`, `MatKhau`, `SoDienThoai`, `VaiTro`, `TrangThai`) VALUES
(1, 'Quản trị viên Hệ thống', 'admin', 'admin123', '0901234567', 'Admin', 'DangLam'),
(2, 'Trần Thị Mai', 'cashier01', 'mai123', '0912345678', 'ThuNgan', 'DangLam'),
(3, 'Lê Hoàng Nam', 'waiter01', 'nam123', '0923456789', 'PhucVu', 'DangLam'),
(4, 'Phạm Quốc Tuấn', 'chef01', 'tuan123', '0934567890', 'Bep', 'DangLam'),
(5, 'Đỗ Kim Ngân', 'cashier02', 'ngan123', '0945678901', 'ThuNgan', 'DangLam'),
(6, 'Vũ Đức Thịnh', 'waiter02', 'thinh123', '0956789012', 'PhucVu', 'NghiViec'),
(7, 'Nguyễn Văn Quản Lý', 'quanly', 'quanly123', '0901234568', 'QuanLy', 'DangLam'),
(8, 'Nguyễn Phương Thảo (Admin)', 'admin02', 'admin123', '0987654321', 'Admin', 'DangLam');
```

---

## 2. Bảng Phân Quyền Vai Trò (RBAC Matrix)

| Nhóm Menu / Module | Admin (Quản trị viên) | Quản lý (QuanLy) | Thu ngân (ThuNgan) | Phục vụ (PhucVu) | Bếp trưởng (Bep) |
|---|:---:|:---:|:---:|:---:|:---:|
| **Khởi đầu mặc định** | **Nhật ký hệ thống** | **Dashboard** | Sơ đồ bàn ăn | Sơ đồ bàn ăn | Quản lý Bếp & Bar |
| **Hệ thống** *(Audit Log, Dashboard)* | ✅ Có | ✅ Có | ❌ Ẩn | ❌ Ẩn | ❌ Ẩn |
| **Quản trị dữ liệu** *(Users, Menu, Bàn, Voucher)* | ✅ Có | ✅ Có | ❌ Ẩn | ❌ Ẩn | ❌ Ẩn |
| **Dịch vụ & Bàn ăn** *(Sơ đồ bàn, Order)* | ❌ Ẩn | ✅ Có | ✅ Có | ✅ Có | ❌ Ẩn |
| **Quản lý Bếp & Bar** | ❌ Ẩn | ✅ Có | ❌ Ẩn | ❌ Ẩn | ✅ Có |
| **Thu ngân & Hóa đơn** *(Checkout, Ca làm việc)* | ❌ Ẩn | ✅ Có | ✅ Có | ❌ Ẩn | ❌ Ẩn |
| **Báo cáo thống kê** *(Doanh thu, Top món)* | ❌ Ẩn | ✅ Có | ❌ Ẩn | ❌ Ẩn | ❌ Ẩn |
| **Quyền hạn tổng quát** | **Xem Audit Log & Quản trị hệ thống** | **Toàn quyền cả hệ thống (Full Access)** | Thu ngân, Ca làm việc | Bàn & Gọi món | Bếp & Báo hết món |

---

## 3. Các File Tài Liệu Markdown Đã Đồng Bộ

1. [`database.sql`](file:///d:/BTLJAVA/database.sql):
   - Mở rộng ENUM `VaiTro` có thêm `'Admin'`.
   - Bổ sung tài khoản admin mới `admin02` và tài khoản quản lý `quanly`.
   - Bổ sung DDL bảng `LichSuTruyCap`, `LichSuThaoTac` và các Views Audit Log.

2. [`README.md`](file:///d:/BTLJAVA/README.md):
   - Cập nhật ma trận phân quyền RBAC 5 vai trò.
   - Bổ sung bảng tài khoản mặc định chi tiết với tài khoản Admin mới (`admin02`) và Quản lý (`quanly`).
   - Cập nhật ghi chú mã hóa mật khẩu an toàn bằng **BCrypt**.

3. [`phanchiacv.md`](file:///d:/BTLJAVA/phanchiacv.md):
   - Cập nhật trách nhiệm của Trưởng nhóm (Nguyễn Phương Thảo) bao gồm: Xác thực phân quyền RBAC 5 vai trò, Hệ thống Audit Log và Mã hóa BCrypt.
   - Bổ sung hạng mục "Nhật ký hệ thống (Audit Log) & Bảo mật BCrypt" vào bảng phân công công việc chi tiết.

4. [`BAO_CAO_DE_TAI.md`](file:///d:/BTLJAVA/BAO_CAO_DE_TAI.md):
   - Cập nhật Mục 4.1: Ma trận Phân quyền Tác nhân (Actor / RBAC Matrix) từ 4 lên 5 vai trò với sơ đồ kiến trúc ASCII và bảng ma trận quyền hạn chi tiết.
   - Bổ sung bảng danh sách tài khoản kiểm thử hệ thống.
   - Cập nhật Mục 4.2.1: Chức năng Đăng nhập, phân luồng mở màn hình mặc định theo vai trò và ghi nhận sự kiện vào `LichSuTruyCap`.

5. [`DummyDataFactory.java`](file:///d:/BTLJAVA/src/main/java/com/restaurant/util/DummyDataFactory.java):
   - Đồng bộ tài khoản `admin02` vào danh sách nhân viên dự phòng ngoại tuyến.

---

## 4. Kết Quả Kiểm Thử (Build & Test Verification)

- Lệnh chạy: `mvn test`
- Kết quả: **11/11 bài test PASS** (0 Failure, 0 Error, `BUILD SUCCESS`).
