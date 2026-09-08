# Báo Cáo Nghiệm Thu Hoàn Thành: Hệ Thống RESTAURANT OPS

Hệ thống **RESTAURANT OPS** đã được xây dựng, kết nối CSDL MySQL 8.0, tối ưu hóa và hoàn thiện 100% bám sát ma trận nghiệp vụ thực tế (Phục vụ, Thu ngân, Quản trị viên).

---

## 1. Cấu Trúc Dự Án (Kiến Trúc MVC & DAO Chuẩn Hóa)

Toàn bộ mã nguồn được tổ chức chặt chẽ tại `d:\BTLJAVA\src\main\java\com\restaurant`:

```
d:\BTLJAVA
├── database.sql                                    // Script tạo CSDL MySQL: Tables, Views, Stored Procedures, Seed Data
├── pom.xml                                         // FlatLaf 3.5.4, MySQL Connector 8.3.0, JUnit 5, Compiler Java 21
└── src
    ├── main\java\com\restaurant
    │   ├── App.java                                // Điểm khởi chạy: khởi động Look & Feel, mở LoginDialog
    │   ├── database
    │   │   └── DatabaseConnection.java             // Quản lý kết nối Singleton JDBC MySQL 8.0 (root / Pthao*1234)
    │   ├── dao                                     // [DAO Layer] Thao tác trực tiếp với CSDL MySQL
    │   │   ├── BanDAO.java                         // CRUD bàn ăn, cập nhật trạng thái bàn Trống/Có khách
    │   │   ├── DanhMucDAO.java                     // CRUD danh mục món ăn (ràng buộc toàn vẹn)
    │   │   ├── MonAnDAO.java                       // CRUD món ăn, tính năng Ẩn món / Hết hàng
    │   │   ├── NhanVienDAO.java                    // CRUD nhân viên, hàm authenticate đăng nhập phân quyền
    │   │   ├── HoaDonDAO.java                      // Mở hóa đơn, lưu chi tiết gọi món, thanh toán, chuyển bàn
    │   │   ├── VoucherDAO.java                     // CRUD mã khuyến mãi & xác thực mã giảm giá
    │   │   └── ThongKeDAO.java                     // Truy vấn View v_DoanhThuTheoNgay, v_TopMonBanChay & KPIs
    │   ├── model                                   // [MODEL] Entity ánh xạ CSDL MySQL
    │   │   ├── Ban.java, DanhMuc.java, MonAn.java, HoaDon.java, ChiTietHoaDon.java, NhanVien.java, Voucher.java
    │   ├── util
    │   │   ├── UIConstants.java                    // Bảng màu chuẩn UI/UX, Typography Segoe UI, Formatter tiền tệ
    │   │   └── DummyDataFactory.java               // Bộ đệm dữ liệu & cơ chế Fallback tự động khi CSDL offline
    │   └── view                                    // [VIEW] Giao diện Swing tách bạch theo từng module
    │       ├── MainFrame.java                      // Khung chính: Header báo trạng thái MySQL, Sidebar RBAC, CardLayout
    │       ├── auth
    │       │   └── LoginDialog.java                // Màn hình Đăng nhập xác thực và phân quyền truy cập
    │       ├── components
    │       │   ├── RoundedPanel.java, KpiCard.java, FunnelChartPanel.java, RevenueBarLineChartPanel.java, ModernTable.java
    │       ├── dashboard
    │       │   └── DashboardPanel.java             // Dashboard phân tích KPIs, 2 biểu đồ, Top món chạy từ MySQL
    │       ├── tablemap
    │       │   └── TableMapPanel.java              // Sơ đồ bàn: Lưới 20 bàn, đổi màu Xanh/Đỏ, mở bàn, xem order
    │       ├── order
    │       │   └── OrderPanel.java                 // Màn hình gọi món: Ghi chú bếp, hủy món, chuyển bàn, gửi bếp
    │       ├── checkout
    │       │   ├── CheckoutPanel.java              // Thu ngân: Chọn bàn đỏ, áp dụng Voucher/VAT, in bill K80, thanh toán
    │       │   └── SplitBillDialog.java            // Hộp thoại Tách Hóa Đơn độc lập
    │       └── management
    │           ├── BaseCrudPanel.java              // Lớp trừu tượng dùng chung cho các màn hình CRUD
    │           ├── UserManagementPanel.java        // Quản lý Nhân sự & Cấp tài khoản phân quyền
    │           ├── MenuManagementPanel.java        // Quản lý Thực đơn & Trạng thái Còn món/Hết hàng
    │           ├── CategoryManagementPanel.java    // Quản lý Danh mục món ăn
    │           ├── TableManagementPanel.java       // Quản lý Sơ đồ bàn & Khu vực
    │           └── VoucherManagementPanel.java     // Quản lý Khuyến mãi, Voucher, Chiết khấu
    └── test\java\com\restaurant
        ├── DatabaseConnectionTest.java             // Kiểm thử kết nối MySQL 8.0 và liệt kê các bảng
        ├── MainFrameTest.java                      // Kiểm thử tự động chuyển module, mở bàn, thanh toán
        └── CaptureUiScreenshotsTest.java           // Kiểm thử kết xuất ảnh giao diện 8 module
```

---

## 2. Chi Tiết Nghiệp Vụ & Hình Ảnh Trực Quan 8 Module

### Module 0: Màn hình Đăng Nhập & Phân Quyền (RBAC)
![Login Dialog](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_login.png)
- Xác thực tên đăng nhập và mật khẩu trực tiếp từ bảng `NhanVien` trong MySQL.
- **Phân quyền truy cập rõ ràng**:
  - **Quản lý / Admin (`admin` / `admin123`)**: Toàn quyền truy cập tất cả module (Dashboard, Sơ đồ bàn, Order, Thu ngân, Quản trị 5 bảng, Báo cáo thống kê).
  - **Nhân viên Phục vụ / Thu ngân (`waiter01` / `nam123`, `cashier01` / `mai123`)**: Chỉ hiển thị menu *Sơ đồ bàn*, *Gọi món (Order)*, *Thu ngân (Checkout)*; các chức năng Quản trị và Báo cáo tài chính bị ẩn hoàn toàn.
- Đèn báo trạng thái kết nối MySQL `● CSDL: MySQL Online` ngay trên màn hình đăng nhập.
- Nút **Đăng xuất hệ thống** trên Sidebar cho phép đăng xuất và chuyển đổi tài khoản/ca làm việc tức thì.

---

### Module 1: Dashboard Thống Kê Hoạt Động Kinh Doanh
![Dashboard](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_dashboard.png)
- **4 Thẻ KPI Card**: Tổng Doanh Thu, Tổng Đơn Hàng, Tỷ Lệ Lấp Đầy Bàn, Thời Gian Quay Vòng.
- **Biểu đồ phễu (Pipeline Bàn & Order)**: Thống kê số lượng bàn qua các bước: Tổng bàn (20) → Có khách (13) → Đã gọi món (11) → Đang phục vụ (8) → Chờ thanh toán (3).
- **Biểu đồ cột + đường (Doanh thu & Mục tiêu)**: Phân tích doanh thu 6 tháng gần nhất.
- **Bảng Top Món Ăn Bán Chạy**: Truy vấn dữ liệu thực tế từ MySQL View `v_TopMonBanChay`.

---

### Module 2: Sơ Đồ Bàn Ăn Trực Quan
![Table Map](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_table_map.png)
- Hiển thị lưới 20 bàn ăn trực quan theo màu sắc:
  - **Màu Xanh lá**: `Bàn trống • X chỗ`.
  - **Màu Đỏ**: `Đang có khách • X chỗ`.
- **Thao tác**:
  - Click bàn Xanh: Popup hỏi *"Mở bàn?"* -> Chuyển thành Đỏ và chuyển thẳng sang màn hình Order.
  - Click bàn Đỏ: Lựa chọn mở Order để thêm món hoặc chuyển nhanh sang màn hình Thu ngân.
- Bộ lọc khu vực: `Tất cả`, `Tầng 1`, `Tầng 2`, `Phòng VIP`.

---

### Module 3: Màn Hình Gọi Món (Order)
![Order Details](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_order_details.png)
- **Bên trái**: Chi tiết đơn hàng của bàn (Tên món, SL, Đơn giá, Thành tiền, Ghi chú, Trạng thái).
  - Nút điều chỉnh số lượng `+` / `-`.
  - Nút **Ghi chú món ăn**: Nhập ghi chú riêng cho từng món để báo bếp (*"Ít đường", "Không cay", "Làm chín kỹ"...*).
  - Nút **Hủy món (Void Item)**: Hủy món khách đã gọi khi chưa phục vụ.
  - Nút **Chuyển bàn / Ghép bàn**: Chuyển toàn bộ order sang bàn trống mới, tự động đồng bộ CSDL.
  - Nút **Lưu & Gửi Bếp**: Đẩy danh sách món xuống bảng `ChiTietHoaDon` trong CSDL, chuyển trạng thái sang `Đang làm`.
- **Bên phải**: Danh mục món ăn dạng thẻ trực quan (Khai vị, Món chính, Hải sản, Đồ uống, Tráng miệng) kèm ô tìm kiếm nhanh.

---

### Module 4: Màn Hình Thu Ngân & Thanh Toán Hóa Đơn
![Checkout](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_checkout.png)
- **Danh sách bàn đang có khách**: Click chọn bàn để load chi tiết các món đã dùng.
- **Tách hóa đơn (Split Bill)**: Popup cho phép tích chọn các món cụ thể để tách thành hóa đơn phụ độc lập.
- **Áp dụng Mã Voucher & Giảm giá**: Nhập mã Voucher (ví dụ: `LEHOI10`, `VIP20`, `GIAM50K`) nhấn "Áp dụng" -> hệ thống tự động kiểm tra CSDL và áp dụng % chiết khấu.
- **Tính toán tài chính tự động**: Tạm tính, Tiền chiết khấu, Thuế VAT (8%), Tổng tiền phải trả.
- **In hóa đơn (Bill K80)**: Xem trước hóa đơn đầy đủ thông tin: Tên nhà hàng, Số bàn, Giờ vào/ra, Danh sách món, Tổng tiền.
- **Thanh toán & Phương thức**: Chọn Tiền mặt, Chuyển khoản QR, hoặc Thẻ POS -> Lưu doanh thu vào CSDL và tự động reset bàn về màu XANH (Đang trống).

---

### Module 5: Nhóm Màn Hình Quản Trị Dữ Liệu (CRUD)

#### 5A. Quản lý Nhân sự & Phân quyền
![Users Management](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_users.png)
- Cấp tài khoản nhân viên (Username, Password, Họ tên, Vai trò: `QuanLy`, `ThuNgan`, `PhucVu`, `Bep`, Trạng thái: `DangLam`, `NghiViec`).
- Thao tác Thêm, Sửa, Xóa, Tìm kiếm thực thi trực tiếp vào bảng `NhanVien` trong MySQL.

#### 5B. Quản lý Thực đơn Món ăn
![Menu Items Management](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_menu_items.png)
- Quản lý Tên món, Danh mục, Đơn giá bán, Hình ảnh.
- **Tính năng nghiệp vụ "Ẩn món / Hết hàng"**: Checkbox đánh dấu món còn phục vụ hay tạm hết khi bếp hết nguyên liệu.

#### 5C. Quản lý Khuyến Mãi & Voucher
![Vouchers Management](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_vouchers.png)
- Thiết lập các chiến dịch Voucher: Mã code, Tên chương trình, % giảm giá, Giảm tối đa, Đơn tối thiểu, Ngày bắt đầu, Ngày kết thúc, Trạng thái.
- Lưu trữ đồng bộ trực tiếp vào bảng `Voucher` trong MySQL.

---

### Module 6: Lịch Sử Hóa Đơn & Tra Cứu Chi Tiết
![Invoice History](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_invoice_history.png)
- **Tra cứu và bộ lọc đa tiêu chí**:
  - Tìm kiếm theo Mã Hóa đơn hoặc Tên bàn.
  - Lọc theo trạng thái (*Đã thanh toán / Chưa thanh toán*).
  - Lọc theo phương thức thanh toán (*Tiền mặt, Chuyển khoản QR, Thẻ POS*).
- **Xem chi tiết các món trong hóa đơn**: Click vào bất kỳ hóa đơn nào ở bảng trên -> Bảng dưới lập tức hiển thị chi tiết các món đã dùng, số lượng, giá bán, ghi chú và trạng thái.
- **Tính năng In lại Hóa đơn (Reprint K80)**: Xem trước và in lại hóa đơn nhiệt K80 chuẩn cho khách khi có yêu cầu.

---

### Module 7: Báo Cáo Doanh Thu Chuyên Sâu & Top Món Bán Chạy

#### 7A. Báo cáo Doanh thu & Hiệu quả tài chính
![Revenue Report](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_revenue_report.png)
- **4 Thẻ KPI Tài chính**: Tổng Doanh Thu Thực Tế, Số Hóa Đơn Hoàn Tất, Doanh Thu Trung Bình / Đơn (AOV), Hình Thức Thanh Toán Chiếm Tỷ Trọng Cao Nhất.
- **Bảng doanh thu theo từng ngày**: Đọc trực tiếp từ MySQL View `v_DoanhThuTheoNgay` (Ngày, Số đơn, Doanh thu ngày, Doanh thu TB/đơn).
- **Cơ cấu hình thức thanh toán**: Thống kê số lượng đơn và tỷ trọng % giữa Tiền mặt, Chuyển khoản QR, Thẻ POS.
- **Nút Xuất báo cáo (CSV/Print)** phục vụ kế toán và chủ nhà hàng.

#### 7B. Báo cáo Xếp hạng Top Món Ăn Bán Chạy
![Top Dishes Report](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_top_dishes.png)
- Vinh danh Top 3 món có doanh số cao nhất trên bục quán quân/á quân/hạng ba.
- Bảng xếp hạng chi tiết thứ bậc các món theo doanh thu từ View `v_TopMonBanChay`.

---

### Module 8: Trung Tâm Điều Hành Bếp & Bar (Kitchen Ops)
![Kitchen Management](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_kitchen_management.png)
- **Tab 1: Điều Hành Bếp & Chế Biến**:
  - **4 Thẻ KPI thời gian thực**: *Chờ chế biến*, *Đang nấu*, *Đã xong / Ra món*, *Tổng order*.
  - **Nhận Order thời gian thực**: Danh sách các món ăn được gọi từ các bàn qua bảng `ModernTable` với thông tin: Mã CTHD, Bàn, Khu vực, Tên món, Số lượng, Thời gian gọi, **Thời gian chờ (phút)** (tự động cảnh báo màu đỏ nếu chờ quá 15 phút), Ghi chú chế biến, Trạng thái (Pill Badge màu sắc).
  - **Xử lý trạng thái món**:
    + Nút **`🍳 Bắt đầu nấu`**: Chuyển trạng thái sang `Đang nấu` (`DangLam`).
    + Nút **`✅ Hoàn thành (Ra món)`**: Chuyển trạng thái sang `Đã nấu xong` (`DaRaMon`).
    + **Hỗ trợ chọn nhiều món (Multi-select)**: Giữ phím `Ctrl` hoặc `Shift` để chọn nhiều món và cập nhật hàng loạt cùng lúc.
    + Nút **`❌ Báo hủy món`**: Hủy món kèm lý do (*Hết nguyên liệu, lỗi kỹ thuật...*).
  - **Bộ lọc & Tự động làm mới**:
    + Lọc theo trạng thái (*Tất cả, Chờ nấu, Đang nấu, Đã nấu xong*).
    + Lọc theo Bàn / Khu vực (*Tầng 1, Tầng 2, VIP*), tìm kiếm nhanh tên món.
    + Checkbox **Tự động làm mới (10s)**: Sử dụng `Swing Timer` tự động đồng bộ order mới nhất từ CSDL.
- **Tab 2: Kho Thực Đơn & Báo Hết Món**:
  - Danh sách toàn bộ thực đơn món ăn kèm tình trạng: `🟢 Còn món phục vụ` / `🔴 Hết hàng`.
  - Nút thao tác nhanh: **`🔴 Báo Hết Hàng`** và **`🟢 Mở Bán Lại`**, cập nhật trực tiếp vào bảng `MonAn` trong MySQL.
  - **Đồng bộ với màn hình Order**: Món ăn khi bị bếp báo hết hàng sẽ hiển thị nhãn `[HẾT HÀNG]` gạch ngang và vô hiệu hóa nút thêm món, ngăn phục vụ gọi nhầm món đã hết.

---

### Module 9: Quản Lý Ca Làm Việc & Đối Soát Thu Ngân
![Shift Management](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_shift_management.png)
- **Thanh Banner Ca Làm Việc Hiện Tại (Tự động cập nhật thời gian thực)**:
  - Tự động đồng bộ và hiển thị chính xác mọi hóa đơn thanh toán phát sinh từ màn hình Thu ngân (`CheckoutPanel`).
  - Hiển thị trực quan ca làm việc đang mở: Mã ca, Nhân viên trực ca, Giờ mở, Tiền đầu ca, Doanh thu tiền mặt, **Tiền mặt lý thuyết trong két** (tự động cộng dồn theo thời gian thực).
  - Nút thao tác nhanh **`🔴 Bàn Giao Ca (Kết Ca)`** hoặc **`🟢 Mở Ca Làm Việc Mới`**.
- **Mở Ca (Bắt đầu ca làm việc) - Hiển thị số dư bàn giao ca trước**:
  ![Open Shift Dialog](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_open_shift_dialog.png)
  - Hộp thoại `OpenShiftDialog` tích hợp thẻ nổi bật **📋 THÔNG TIN BÀN GIAO TỪ CA LIỀN TRƯỚC**:
    + Mã ca trước (`Ca #...`), Nhân viên bàn giao, Giờ đóng ca.
    + **Số tiền mặt thực tế bàn giao trong két** (màu xanh lá nổi bật).
    + Tình trạng đối soát két ca trước (`✅ Khớp 100%`, thừa hoặc thiếu tiền) và ghi chú bàn giao ca trước.
    + **Tự động điền số tiền trong két** vào ô *Tiền mặt đầu ca (VND)* để ca sau kiểm đếm và nhận bàn giao liền mạch.
    + Nút tiện ích **`↺ Ca trước`** để khôi phục lại nhanh số tiền bàn giao bất kỳ lúc nào.
  - Lưu vào bảng `CaLamViec` với trạng thái `DangMo`.
- **Bàn Giao Ca (Kết ca & Xử lý chênh lệch tiền két)**:
  ![Close Shift Dialog](C:/Users/PC/.gemini/antigravity-ide/brain/08fe2abe-31e2-4cd8-b9f9-feb54d3ac262/ui_close_shift_dialog.png)
  - Hộp thoại `CloseShiftDialog` tự động tổng hợp số liệu chuẩn xác:
    + Số lượng hóa đơn thanh toán trong ca (ví dụ: *3 đơn hàng*).
    + Tiền đầu ca (ví dụ: *1,500,000 VND*).
    + Doanh thu tiền mặt phát sinh trong ca (ví dụ: *2,856,600 VND*).
    + Doanh thu chuyển khoản / thẻ POS.
    + Tổng doanh thu bán hàng.
    + **★ TIỀN MẶT LÝ THUYẾT TRONG KÉT** (ví dụ: *4,356,600 VND*).
  - Ô nhập **Tiền mặt thực tế kiểm đếm**: Hệ thống tự động điền sẵn số tiền lý thuyết và tự động so sánh, tính chênh lệch:
    + `✅ Khớp tiền 100% (Chênh lệch: 0 VND)` (Khung xanh lá).
    + `⚠️ Thừa tiền trong két: +X VND` (Khung xanh dương).
    + `❌ Thiếu tiền trong két: -X VND` (Khung đỏ cảnh báo).
  - Bắt buộc nhập lý do giải trình khi két tiền có chênh lệch thừa/thiếu trước khi đóng ca.
- **Lịch Sử Ca Làm Việc & Tra Cứu**:
  - Bảng `ModernTable` danh sách toàn bộ các ca làm việc đã thực hiện, tô màu trực quan dòng chênh lệch.
  - Bộ lọc theo trạng thái (*Đang mở / Đã đóng*), tìm kiếm theo nhân viên, mã ca.
  - Hộp thoại **`📄 Xem Chi Tiết`** (`ShiftDetailDialog`): Xem danh sách đầy đủ tất cả các hóa đơn phát sinh trong ca đó.
  - Nút **`🖨️ In Biên Bản Bàn Giao Ca`**: Kết xuất phiếu bàn giao ca chi tiết, có chữ ký người bàn giao và người nhận ca.

---

## 3. Hướng Dẫn Vận Hành Ứng Dụng

### Khởi chạy ứng dụng:
```powershell
mvn exec:java
```

### Danh sách tài khoản thử nghiệm:
| Tên đăng nhập | Mật khẩu | Vai trò | Quyền hạn trong hệ thống |
| :--- | :--- | :--- | :--- |
| **`admin`** | `admin123` | Quản lý (Admin) | **Toàn quyền**: Dashboard, Sơ đồ bàn, Order, Thu ngân, Bếp, Ca làm việc, 5 màn hình Quản trị, Báo cáo |
| **`cashier01`** | `mai123` | Thu ngân (Cashier) | Sơ đồ bàn, Thu ngân (Checkout), Mở/Đóng ca làm việc, Lịch sử hóa đơn, In biên bản bàn giao |
| **`chef01`** | `tuan123` | Bếp trưởng (Chef) | Điều hành Bếp & Bar, Cập nhật trạng thái món, Báo hết hàng / Mở bán lại thực đơn |
| **`waiter01`** | `nam123` | Phục vụ (Waiter) | Sơ đồ bàn ăn, Mở bàn, Gọi món (Order), Ghi chú, Chuyển bàn, Gửi bếp |
