# RESTAURANT OPS - Hệ Thống Quản Lý Nhà Hàng

## 📋 Mục lục
- [Giới thiệu](#giới-thiệu)
- [Tính năng](#tính-năng)
- [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
- [Cài đặt](#cài-đặt)
  - [Tạo bảng Audit Log](#3-chạy-file-sql-tạo-bảng)
- [Cấu hình Cơ sở dữ liệu](#cấu-hình-cơ-sở-dữ-liệu)
- [Hướng dẫn sử dụng](#hướng-dẫn-sử-dụng)
- [Tài khoản mặc định](#tài-khoản-mặc-định)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Lịch sử Phát triển](#lịch-sử-phát-triển)

---

## 🎯 Giới thiệu

**RESTAURANT OPS** là hệ thống quản lý vận hành nhà hàng được phát triển bằng Java Swing, kết nối MySQL. Hệ thống hỗ trợ:

- Quản lý nhân viên (phân quyền)
- Quản lý món ăn, danh mục
- Quản lý bàn và đặt bàn
- Tính tiền và in hóa đơn
- Thống kê doanh thu
- **Nhật ký hệ thống (Audit Log)**

---

## ✨ Tính năng

### 1. Quản lý Nhân viên
- Thêm, sửa, xóa nhân viên
- Phân quyền: Quản lý, Thu ngân, Phục vụ, Đầu bếp
- **Mã hóa mật khẩu BCrypt** - Bảo mật cao
- Khóa/Mở tài khoản nhân viên

### 2. Quản lý Món ăn
- Thêm, sửa, xóa món ăn
- Phân loại theo danh mục
- Cập nhật giá và trạng thái (Còn món/Hết món)

### 3. Quản lý Bàn
- Hiển thị trạng thái bàn trực quan
- Đặt bàn trực tuyến
- Ghép bàn, tách bàn

### 4. Tính tiền & Hóa đơn
- Tính tiền theo bàn
- In hóa đơn (có thể mở rộng Printer)
- Áp dụng khuyến mãi

### 5. Thống kê Doanh thu
- Doanh thu theo ngày/tuần/tháng
- Biểu đồ trực quan
- Xuất báo cáo Excel

### 6. Nhật ký Hệ thống (Audit Log) ⭐
- **Lịch sử truy cập**: Đăng nhập, đăng xuất, đăng nhập thất bại
- **Lịch sử thao tác**: INSERT, UPDATE, DELETE trên tất cả các bảng
- Theo dõi ai làm gì, lúc nào
- Lọc theo ngày, hành động, bảng
- Màu sắc phân biệt hành động:
  - 🟢 **Xanh**: Tạo mới (INSERT)
  - 🟠 **Cam**: Cập nhật (UPDATE)
  - 🔴 **Đỏ**: Xóa (DELETE)

### 7. Bảo mật
- **Mã hóa BCrypt**: Mật khẩu được hash an toàn
- Phân quyền người dùng
- Session management

---

## 💻 Yêu cầu hệ thống

| Yêu cầu | Phiên bản |
|---------|-----------|
| Java | 21+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |

---

## 🚀 Cài đặt

### 1. Clone/Download project

```bash
cd e:\BTLJAVA
```

### 2. Cài đặt MySQL

Đảm bảo MySQL đang chạy và tạo database:

```sql
CREATE DATABASE IF NOT EXISTS restaurant_db;
```

### 3. Chạy file SQL tạo bảng

#### Cách 1: Dùng Command Line

```bash
# Tạo database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS restaurant_db;"

# Chạy file SQL tạo bảng chính
mysql -u root -p restaurant_db < audit_sql.sql
```

#### Cách 2: Dùng MySQL Workbench hoặc phpMyAdmin

1. Mở MySQL Workbench / phpMyAdmin
2. Chọn database `restaurant_db`
3. Import file `audit_sql.sql`

#### Các bảng Audit Log được tạo

| Bảng | Mô tả |
|------|--------|
| `LichSuTruyCap` | Lưu lịch sử đăng nhập/đăng xuất |
| `LichSuThaoTac` | Lưu lịch sử INSERT/UPDATE/DELETE |
| `v_LichSuTruyCap` | View: Lịch sử truy cập chi tiết |
| `v_LichSuThaoTac` | View: Lịch sử thao tác chi tiết |
| `v_ThongKeDangNhap` | View: Thống kê đăng nhập theo ngày |

### 4. Cấu hình kết nối

Mở file `src/main/java/com/restaurant/database/DatabaseConnection.java` và kiểm tra:

```java
private static final String USER = "root";
private static final String PASSWORD = "Pthao*1234";  // Đổi theo password MySQL của bạn
```

### 5. Build và Run

```bash
mvn clean compile exec:java
```

---

## 🗄️ Cấu hình Cơ sở dữ liệu

### Database Connection

**File:** `src/main/java/com/restaurant/database/DatabaseConnection.java`

```java
private static final String HOST = "localhost";
private static final String PORT = "3306";
private static final String DATABASE = "restaurant_db";
private static final String USER = "root";
private static final String PASSWORD = "your_password_here";
```

### Các bảng trong CSDL

| Bảng | Mô tả |
|------|--------|
| `NhanVien` | Thông tin nhân viên |
| `DanhMuc` | Danh mục món ăn |
| `MonAn` | Món ăn |
| `Ban` | Bàn nhà hàng |
| `HoaDon` | Hóa đơn |
| `ChiTietHoaDon` | Chi tiết hóa đơn |
| `DatBan` | Đặt bàn |
| `KhuyenMai` | Khuyến mãi |
| `LichSuTruyCap` | Nhật ký truy cập |
| `LichSuThaoTac` | Nhật ký thao tác |

---

## 📖 Hướng dẫn sử dụng

### Đăng nhập

1. Chạy ứng dụng
2. Nhập tên đăng nhập và mật khẩu
3. Hệ thống sẽ ghi nhận đăng nhập vào Audit Log

### Phân quyền theo vai trò (RBAC)

| Nhóm Menu | Admin (Quản trị viên) | Quản lý (QuanLy) | Thu ngân (ThuNgan) | Phục vụ (PhucVu) | Bếp trưởng (Bep) |
|---|:---:|:---:|:---:|:---:|:---:|
| **Hệ thống** *(Audit Log, Dashboard)* | ✅ Có | ✅ Có | ❌ Ẩn | ❌ Ẩn | ❌ Ẩn |
| **Quản trị dữ liệu** *(Nhân viên, Món, Bàn, Voucher)* | ✅ Có | ✅ Có | ❌ Ẩn | ❌ Ẩn | ❌ Ẩn |
| **Dịch vụ & Bàn ăn** *(Sơ đồ bàn, Order)* | ❌ Ẩn | ✅ Có | ✅ Có | ✅ Có | ❌ Ẩn |
| **Quản lý Bếp & Bar** | ❌ Ẩn | ✅ Có | ❌ Ẩn | ❌ Ẩn | ✅ Có |
| **Thu ngân & Hóa đơn** *(Checkout, Ca làm việc)* | ❌ Ẩn | ✅ Có | ✅ Có | ❌ Ẩn | ❌ Ẩn |
| **Báo cáo thống kê** *(Doanh thu, Top món)* | ❌ Ẩn | ✅ Có | ❌ Ẩn | ❌ Ẩn | ❌ Ẩn |

---

## 🔐 Tài khoản mặc định

| Tài khoản | Mật khẩu | Vai trò | Quyền hạn chi tiết |
|-----------|-----------|---------|-------------------|
| `admin` | `admin123` | **Admin** (Quản trị viên) | Xem Audit Log (Nhật ký hệ thống) & Quản trị dữ liệu hệ thống |
| `admin02` | `admin123` | **Admin** (Quản trị viên mới) | Xem Audit Log (Nhật ký hệ thống) & Quản trị dữ liệu hệ thống |
| `quanly` | `quanly123` | **Quản lý** (QuanLy) | **Toàn quyền cả hệ thống** (Vận hành, Thu ngân, Bếp, Quản trị, Báo cáo) |
| `cashier01` | `mai123` | **Thu ngân** (ThuNgan) | Thu ngân (Checkout), Lịch sử hóa đơn, Quản lý ca làm việc, Sơ đồ bàn |
| `waiter01` | `nam123` | **Phục vụ** (PhucVu) | Sơ đồ bàn ăn, Gọi món (Order tại bàn), Chuyển bàn |
| `chef01` | `tuan123` | **Đầu bếp** (Bep) | Điều hành chế biến Bếp & Bar, Báo hết hàng / Mở bán lại món |

> ⚠️ **Lưu ý:** Tất cả mật khẩu nhân viên đều được mã hóa bằng thuật toán **BCrypt** an toàn trong CSDL! Đổi mật khẩu mặc định sau khi đăng nhập lần đầu.

---

## 📁 Cấu trúc dự án

```
BTLJAVA/
├── src/main/java/com/restaurant/
│   ├── App.java                          # Main class
│   ├── database/
│   │   └── DatabaseConnection.java        # Kết nối MySQL
│   ├── model/
│   │   ├── NhanVien.java                 # Model nhân viên
│   │   ├── MonAn.java                    # Model món ăn
│   │   ├── HoaDon.java                   # Model hóa đơn
│   │   └── ...
│   ├── dao/
│   │   ├── NhanVienDAO.java              # CRUD nhân viên + BCrypt
│   │   ├── MonAnDAO.java                 # CRUD món ăn
│   │   ├── LichSuTruyCapDAO.java          # Lưu log truy cập
│   │   └── LichSuThaoTacDAO.java         # Lưu log thao tác
│   ├── service/
│   │   ├── AuditService.java             # Service ghi audit
│   │   └── AuditSwingWorker.java         # Xử lý bất đồng bộ
│   ├── util/
│   │   ├── PasswordUtils.java            # Mã hóa BCrypt
│   │   └── ...
│   └── view/
│       ├── MainFrame.java                # Cửa sổ chính
│       ├── auth/
│       │   └── LoginDialog.java          # Hộp thoại đăng nhập
│       ├── management/
│       │   ├── UserManagementPanel.java  # Quản lý nhân viên
│       │   ├── FoodManagementPanel.java  # Quản lý món ăn
│       │   └── ...
│       └── audit/
│           └── AuditLogPanel.java        # Giao diện audit log
├── src/main/resources/
│   ├── database.sql                      # Tạo bảng chính
│   └── audit_sql.sql                     # Tạo bảng audit
├── pom.xml                              # Maven dependencies
└── README.md                            # File này
```

---

## 🔒 Bảo mật

### Mã hóa Mật khẩu BCrypt

Mật khẩu được mã hóa bằng thuật toán BCrypt (Blowfish-based):

```java
// Mã hóa khi tạo user mới
String hashedPassword = PasswordUtils.hashPassword("matkhau123");
// Output: $2a$10$N9qo8uLOickgx2ZMRZoMy...

// Verify khi đăng nhập
boolean isValid = PasswordUtils.verifyPassword(inputPassword, hashedPassword);
```

### Tính năng BCrypt
- ✅ Tự động tạo salt ngẫu nhiên
- ✅ Chống brute force (cost factor = 10)
- ✅ Salt được nhúng trong hash
- ✅ Hỗ trợ dữ liệu cũ (legacy plain text)

---

## 📅 Lịch sử Phát triển

| Phiên bản | Ngày | Nội dung |
|-----------|------|----------|
| v1.0 | 15/09/2026 | Khởi tạo dự án, cấu trúc cơ bản |
| v1.1 | 16/09/2026 | Thêm module đăng nhập, phân quyền |
| v1.2 | 17/09/2026 | Module quản lý nhân viên + BCrypt |
| v1.3 | 18/09/2026 | Module quản lý món ăn, danh mục |
| v1.4 | 19/09/2026 | Module bán hàng, tính tiền |
| v1.5 | 20/09/2026 | Thêm Audit Log, README |

---

## 📊 Audit Log

### Cấu trúc bảng Audit

#### Bảng `LichSuTruyCap` - Lịch sử Truy cập

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|--------|
| `id` | INT AUTO_INCREMENT | ID duy nhất |
| `maNV` | INT | Mã nhân viên |
| `tenDangNhap` | VARCHAR(50) | Tên đăng nhập |
| `hoTen` | VARCHAR(100) | Họ tên (có vai trò) |
| `hanhDong` | ENUM | Đăng nhập, Đăng xuất, Đăng nhập thất bại |
| `trangThai` | ENUM | Thành công, Thất bại |
| `diaChiIP` | VARCHAR(45) | IP máy trạm |
| `trinhDuyet` | VARCHAR(100) | Phiên bản app |
| `mayTinh` | VARCHAR(100) | Tên máy |
| `ghiChu` | TEXT | Ghi chú thêm |
| `thoiGian` | DATETIME | Thời gian thực hiện |

#### Bảng `LichSuThaoTac` - Lịch sử Thao tác

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|--------|
| `id` | INT AUTO_INCREMENT | ID duy nhất |
| `maNV` | INT | Mã nhân viên |
| `hoTen` | VARCHAR(100) | Họ tên |
| `tenBang` | VARCHAR(50) | Tên bảng bị tác động |
| `hanhDong` | ENUM | INSERT, UPDATE, DELETE |
| `duLieuCu` | TEXT | Dữ liệu trước khi thay đổi |
| `duLieuMoi` | TEXT | Dữ liệu sau khi thay đổi |
| `moTa` | TEXT | Mô tả thao tác |
| `thoiGian` | DATETIME | Thời gian thực hiện |

### Lịch sử Truy cập

Ghi nhận:
- Đăng nhập thành công
- Đăng nhập thất bại (sai mật khẩu)
- Đăng xuất

**Hiển thị:**
- Thời gian
- Người dùng (Họ tên + Vai trò)
- Hành động
- Trạng thái
- IP Address
- Ghi chú

### Lịch sử Thao tác

Ghi nhận tất cả CRUD:
- **INSERT**: Tạo mới bản ghi (màu xanh)
- **UPDATE**: Cập nhật bản ghi (màu cam)
- **DELETE**: Xóa bản ghi (màu đỏ)

**Theo dõi:**
- Ai thực hiện
- Bảng nào bị tác động
- Dữ liệu cũ và mới
- Thời gian

---

## 🛠️ Công nghệ sử dụng

| Công nghệ | Phiên bản |
|-----------|-----------|
| Java | 21 |
| Swing | Native |
| FlatLaf | 3.5.4 |
| MySQL Connector | 8.3.0 |
| BCrypt (jbcrypt) | 0.4 |
| Maven | 3.8+ |

---

## 📝 License

Đồ án BTL Java - Quản lý Nhà hàng

---

## 👤 Tác giả

[Thông tin sinh viên]

---

## 📞 Hỗ trợ

Nếu gặp lỗi, kiểm tra:
1. MySQL đã chạy chưa?
2. Database `restaurant_db` đã tạo chưa?
3. Password MySQL đã đúng chưa?
4. Đã chạy file SQL chưa?

---

**Cập nhật lần cuối:** 20/09/2026
