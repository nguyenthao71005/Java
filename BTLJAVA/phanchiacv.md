# BẢNG PHÂN CÔNG NHIỆM VỤ & ĐÁNH GIÁ ĐÓNG GÓP THÀNH VIÊN
## ĐỀ TÀI: HỆ THỐNG QUẢN LÝ VẬN HÀNH NHÀ HÀNG ĐA PHÂN HỆ (RESTAURANT OPS)
**Học phần**: Lập trình Java / Bài tập lớn Lập trình Hướng đối tượng  
**Giảng viên hướng dẫn**: [Họ tên Giảng viên]  
**Nhóm sinh viên thực hiện**: Nhóm [Số nhóm]  

---

## I. THÔNG TIN THÀNH VIÊN & TỔNG QUAN PHÂN VAI

Khối lượng công việc trong dự án được chia đều cho **3 thành viên** tương ứng với 3 mảng kiến trúc kỹ thuật trụ cột: **Kiến trúc & Cơ sở dữ liệu**, **Giao diện & Nghiệp vụ Bán hàng**, **Điều hành Bếp, Dashboard & Quản trị**.

| STT | Họ và tên | Mã sinh viên | Vai trò trong nhóm | Trách nhiệm chính phụ trách | Khối lượng | Đánh giá |
| :---: | :--- | :---: | :---: | :--- | :---: | :---: |
| **1** | **Nguyễn Phương Thảo** | [Mã SV 1] | **Trưởng nhóm**<br>*(Team Leader / Architect)* | - Quản lý tiến độ, điều phối thành viên, phân rã WBS.<br>- Thiết kế CSDL MySQL 8.0, Kiến trúc MVC & 6 Interfaces.<br>- Phân hệ Quản lý Ca làm việc & Đối soát Két tiền mặt.<br>- Xác thực phân quyền (RBAC 5 vai trò), Hệ thống Audit Log & Mã hóa BCrypt. | **33.3%** | **100% (A)** |
| **2** | **Bạch Hoàng Dương** | [Mã SV 2] | **Thành viên**<br>*(Frontend Lead / UI/UX)* | - Thiết lập Design System FlatLaf, bảng màu UIConstants.<br>- Xây dựng Custom Components: `ModernTable`, `RoundedPanel`.<br>- Phân hệ Sơ đồ bàn ăn trực quan thời gian thực.<br>- Phân hệ Gọi món tại bàn (Order) & Chuyển bàn.<br>- Phân hệ Thu ngân (Checkout), Voucher, VAT & Tách bill. | **33.3%** | **100% (A)** |
| **3** | **Đào Phúc Việt** | [Mã SV 3] | **Thành viên**<br>*(Fullstack & QA Engineer)* | - Phân hệ Điều hành Bếp & Quầy Bar (Chọn nhiều món).<br>- Phân hệ Kho Thực đơn & Báo hết món phục vụ.<br>- Phân hệ Dashboard thống kê (Biểu đồ Spline & Phễu).<br>- Khung quản trị `BaseCrudPanel` & 5 màn hình Master Data.<br>- Cơ chế Offline Caching, Viết kịch bản Test JUnit 5. | **33.3%** | **100% (A)** |

---

## II. BẢNG PHÂN CÔNG CÔNG VIỆC CHI TIẾT THEO PHÂN HỆ (WORK BREAKDOWN)

### 1. Phân hệ Cơ sở hạ tầng, Kiến trúc & Cơ sở dữ liệu (Database & Architecture)
| Hạng mục công việc | Chi tiết kỹ thuật / File mã nguồn | Người phụ trách chính | Người phối hợp | Kết quả nghiệm thu |
| :--- | :--- | :---: | :---: | :---: |
| **Thiết kế CSDL MySQL 8.0** | - Tạo Schema CSDL `restaurant_db` với 7 bảng quan hệ.<br>- Thiết lập khóa ngoại (Foreign Keys), Triggers, Indexing.<br>- File: `database.sql`. | **Nguyễn Phương Thảo** | Đào Phúc Việt | Hoàn thành 100% |
| **Xây dựng Views & Procedures** | - Viết Views: `v_DoanhThuTheoNgay`, `v_TopMonBanChay`, `v_LichSuCaLamViec`.<br>- Tối ưu hóa truy vấn tổng hợp báo cáo. | **Nguyễn Phương Thảo** | Bạch Hoàng Dương | Hoàn thành 100% |
| **Quản trị Kết nối JDBC** | - Xây dựng lớp Singleton `DatabaseConnection.java`.<br>- Cơ chế kiểm tra kết nối `isConnected()`, bảo mật tài khoản root. | **Nguyễn Phương Thảo** | Đào Phúc Việt | Hoàn thành 100% |
| **Thiết kế 6 Java Interfaces** | - Đặc tả chuẩn OOP: `IBaseDAO<T>`, `IOrderService`, `IPaymentService`, `IShiftService`, `IKitchenService`, `IPrintableReceipt`. | **Nguyễn Phương Thảo** | Bạch Hoàng Dương | Hoàn thành 100% |
| **Cơ chế Bộ đệm ngoại tuyến** | - Xây dựng `DummyDataFactory.java` làm bộ đệm RAM lưu trữ khi CSDL bị ngắt kết nối đột xuất. | **Đào Phúc Việt** | Nguyễn Phương Thảo | Hoàn thành 100% |

---

### 2. Phân hệ Dịch vụ Khách hàng, Bàn ăn & Bán hàng (Front-of-House)
| Hạng mục công việc | Chi tiết kỹ thuật / File mã nguồn | Người phụ trách chính | Người phối hợp | Kết quả nghiệm thu |
| :--- | :--- | :---: | :---: | :---: |
| **Hệ thống Giao diện & Look & Feel** | - Cấu hình FlatLaf 3.5.4 Light Theme, Font Segoe UI.<br>- Định nghĩa `UIConstants.java`: Bảng màu, typography, format tiền tệ. | **Bạch Hoàng Dương** | Đào Phúc Việt | Hoàn thành 100% |
| **Lớp Kế thừa Bảng & Panel Bo góc** | - Xây dựng `RoundedPanel.java` (kế thừa `JPanel`).<br>- Xây dựng `ModernTable.java` (kế thừa `JTable` phân màu zebra, header navy). | **Bạch Hoàng Dương** | Nguyễn Phương Thảo | Hoàn thành 100% |
| **Sơ đồ Bàn ăn (Table Map)** | - `TableMapPanel.java`, `BanDAO.java`, Model `Ban.java`.<br>- Lưới 20 bàn ăn đổi màu Xanh/Đỏ thời gian thực, lọc khu vực. | **Bạch Hoàng Dương** | Nguyễn Phương Thảo | Hoàn thành 100% |
| **Màn hình Gọi món (Order)** | - `OrderPanel.java`, `ChiTietHoaDon.java`, `HoaDonDAO.java`.<br>- Thêm món, tăng/giảm SL, ghi chú bếp, chuyển bàn, gửi bếp. | **Bạch Hoàng Dương** | Đào Phúc Việt | Hoàn thành 100% |
| **Màn hình Thu ngân (Checkout)** | - `CheckoutPanel.java`, `VoucherDAO.java`, Model `Voucher.java`.<br>- Tính toán Subtotal, xác thực Voucher, thuế VAT 0-8-10%, thanh toán đa kênh. | **Bạch Hoàng Dương** | Nguyễn Phương Thảo | Hoàn thành 100% |
| **Hộp thoại Tách hóa đơn** | - `SplitBillDialog.java`: Tách danh sách món sang bill phụ để khách thanh toán riêng biệt. | **Bạch Hoàng Dương** | Đào Phúc Việt | Hoàn thành 100% |

---

### 3. Phân hệ Bếp, Ca làm việc, Dashboard & Quản trị (Back-of-House)
| Hạng mục công việc | Chi tiết kỹ thuật / File mã nguồn | Người phụ trách chính | Người phối hợp | Kết quả nghiệm thu |
| :--- | :--- | :---: | :---: | :---: |
| **Điều hành Bếp & Quầy Bar** | - `KitchenManagementPanel.java`, `IKitchenService.java`.<br>- Cảnh báo món chờ lâu (>15p), Multi-select chọn nhiều món nhận nấu/ra món hàng loạt. | **Đào Phúc Việt** | Bạch Hoàng Dương | Hoàn thành 100% |
| **Quản lý Kho Thực đơn Bếp** | - Tab Kho món ăn: Thao tác Báo Hết Hàng / Mở Bán Lại.<br>- Đồng bộ tức thì gạch ngang món hết trên màn hình Order. | **Đào Phúc Việt** | Bạch Hoàng Dương | Hoàn thành 100% |
| **Quản lý Ca làm việc & Két tiền** | - `ShiftManagementPanel.java`, `CaLamViecDAO.java`, Model `CaLamViec.java`.<br>- Banner ca hiện tại, tính tự động tiền lý thuyết trong két theo hóa đơn. | **Nguyễn Phương Thảo** | Đào Phúc Việt | Hoàn thành 100% |
| **Hộp thoại Mở ca & Đóng ca** | - `OpenShiftDialog.java`: Tự động điền số dư bàn giao từ ca trước.<br>- `CloseShiftDialog.java`: Kiểm đếm thực tế, tính chênh lệch thừa/thiếu, bắt buộc giải trình. | **Nguyễn Phương Thảo** | Bạch Hoàng Dương | Hoàn thành 100% |
| **In Biên bản Bàn giao & Bill K80** | - `IPrintableReceipt.java`: Định dạng văn bản in nhiệt K80 và in biên bản bàn giao ca ký xác nhận. | **Nguyễn Phương Thảo** | Bạch Hoàng Dương | Hoàn thành 100% |
| **Dashboard Phân tích Kinh doanh** | - `DashboardPanel.java`, `ThongKeDAO.java`, `KpiCard.java`.<br>- Kết nối dữ liệu thực tế CSDL, 4 thẻ KPI, bộ lọc thời gian. | **Đào Phúc Việt** | Nguyễn Phương Thảo | Hoàn thành 100% |
| **Đồ họa Biểu đồ Nâng cao** | - `RevenueBarLineChartPanel.java` (Đường cong Spline Bezier + Cột Gradient).<br>- `FunnelChartPanel.java` (Biểu đồ phễu vận hành phân tầng đa sắc). | **Đào Phúc Việt** | Bạch Hoàng Dương | Hoàn thành 100% |
| **Khung Quản trị CRUD (5 màn hình)** | - `BaseCrudPanel.java` (Lớp trừu tượng).<br>- 5 lớp con: `UserManagementPanel`, `MenuManagementPanel`, `TableManagementPanel`, `CategoryManagementPanel`, `VoucherManagementPanel`. | **Đào Phúc Việt** | Nguyễn Phương Thảo | Hoàn thành 100% |

---

### 4. Đăng nhập, Bảo mật, Kiểm thử & Tài liệu (Testing & Documentation)
| Hạng mục công việc | Chi tiết kỹ thuật / File mã nguồn | Người phụ trách chính | Người phối hợp | Kết quả nghiệm thu |
| :--- | :--- | :---: | :---: | :---: |
| **Đăng nhập & Phân quyền (RBAC 5 Vai trò)** | - `LoginDialog.java`, `NhanVienDAO.java`, `MainFrame.java`.<br>- Phân quyền 5 vai trò: Admin (chỉ xem AuditLog & quản trị hệ thống), Quản lý (toàn quyền hệ thống), Thu ngân, Phục vụ, Đầu bếp.<br>- Tài khoản admin mới: `admin`, `admin02` và quản lý: `quanly`. | **Nguyễn Phương Thảo** | Bạch Hoàng Dương | Hoàn thành 100% |
| **Nhật ký Hệ thống (Audit Log) & Bảo mật BCrypt** | - `AuditDAO.java`, `AuditService.java`, `AuditSwingWorker.java`, `AuditLogPanel.java`, `PasswordUtils.java`.<br>- Lưu vết đăng nhập/đăng xuất, ghi lịch sử INSERT/UPDATE/DELETE, mã hóa mật khẩu BCrypt. | **Nguyễn Phương Thảo** | Đào Phúc Việt | Hoàn thành 100% |
| **Kiểm thử Tự động JUnit 5** | - Viết 11 test cases: `DatabaseConnectionTest`, `DatabaseMigrationTest`, `KitchenAndShiftTest`, `MainFrameTest`, `NhanVienDAOTest`, `ShiftPaymentTest`.<br>- Chạy `mvn test` đạt `BUILD SUCCESS`. | **Đào Phúc Việt** | Nguyễn Phương Thảo | Hoàn thành 100% |
| **Chụp ảnh Giao diện Tự động** | - Viết `CaptureUiScreenshotsTest.java` tự động render và chụp màn hình toàn bộ các module (Audit Log, Dashboard, Table Map...). | **Bạch Hoàng Dương** | Đào Phúc Việt | Hoàn thành 100% |
| **Soạn thảo Báo cáo Đề tài** | - Viết tài liệu `BAO_CAO_DE_TAI.md` (850+ dòng, 7 chương đầy đủ thuật toán, kiến trúc, phân tích yêu cầu). | **Nguyễn Phương Thảo** | Cả nhóm | Hoàn thành 100% |
| **Chuẩn bị Slide & Kịch bản Demo** | - Xây dựng kịch bản thuyết trình, chuẩn bị dữ liệu mẫu và video demo luồng vận hành nhà hàng. | **Bạch Hoàng Dương** | Đào Phúc Việt | Hoàn thành 100% |

---

## III. TIẾN ĐỘ THỰC HIỆN THEO TUẦN (TIMELINE & MILESTONES)

```
Tuần:  [W1-W2]      [W3-W4]       [W5-W6]       [W7-W8]       [W9-W10]      [W11-W12]
Thảo:  [Khảo sát] ── [DB Schema] ── [Shift DAO] ── [Shift UI] ─── [Bàn giao ca] ─ [Báo cáo/Test]
Dương: [Đặc tả GUI] ─ [Design Sys] ─ [Table Map] ─ [Order/Bill] ─ [Voucher/VAT] ─ [Demo/Slide]
Việt:  [Nghiệp vụ] ─ [Entities] ─── [Dummy/DB] ─── [Kitchen Ops] ─ [Dashboard] ─── [JUnit Test]
```

| Tuần | Nội dung công việc theo thành viên | Mốc hoàn thành (Milestone) |
| :---: | :--- | :--- |
| **Tuần 1 - 2** | - **Thảo**: Khảo sát quy trình vận hành nhà hàng thực tế, lập kế hoạch WBS.<br>- **Dương**: Thu thập yêu cầu giao diện người dùng, phác thảo wireframe các màn hình.<br>- **Việt**: Khảo sát nghiệp vụ nhà bếp, quầy bar và quy trình kiểm đếm két tiền. | Hoàn thành tài liệu Phân tích yêu cầu bài toán (SRS). |
| **Tuần 3 - 4** | - **Thảo**: Thiết kế ERD, viết script `database.sql`, tạo Views và Triggers MySQL 8.0.<br>- **Dương**: Thiết lập cấu hình Maven `pom.xml`, tích hợp FlatLaf, xây dựng `UIConstants`.<br>- **Việt**: Xây dựng các lớp Entity Model (`Ban`, `MonAn`, `HoaDon`, `CaLamViec`...). | Database sẵn sàng; Khung dự án chuẩn hóa. |
| **Tuần 5 - 6** | - **Thảo**: Xây dựng `DatabaseConnection`, `CaLamViecDAO`, đặc tả 6 Interface hệ thống.<br>- **Dương**: Xây dựng `RoundedPanel`, `ModernTable`, phân hệ `TableMapPanel` & `BanDAO`.<br>- **Việt**: Xây dựng `DummyDataFactory` (Offline Fallback) và các DAO danh mục, món ăn. | Hoàn thành tầng Model, DAO và kết nối JDBC. |
| **Tuần 7 - 8** | - **Thảo**: Phát triển `LoginDialog` phân quyền RBAC và khung sườn `MainFrame`.<br>- **Dương**: Phát triển màn hình `OrderPanel` (chuyển bàn, gửi bếp) và `CheckoutPanel`.<br>- **Việt**: Phát triển màn hình `KitchenManagementPanel` tab điều hành chế biến. | Vận hành thông suốt luồng: Bàn $\to$ Gọi món $\to$ Bếp $\to$ Thu ngân. |
| **Tuần 9 - 10** | - **Thảo**: Hoàn thiện phân hệ `ShiftManagementPanel`, `OpenShiftDialog`, `CloseShiftDialog`.<br>- **Dương**: Xây dựng chức năng Voucher, tính thuế VAT, in bill K80, `SplitBillDialog`.<br>- **Việt**: Xây dựng `DashboardPanel`, vẽ biểu đồ Spline Bezier và 5 màn hình `BaseCrudPanel`. | Toàn bộ 10 module chức năng hoàn thiện 100%. |
| **Tuần 11 - 12** | - **Thảo**: Tổng hợp báo cáo `BAO_CAO_DE_TAI.md`, kiểm tra đối soát két ca.<br>- **Dương**: Chạy `CaptureUiScreenshotsTest`, chuẩn bị slide thuyết trình PowerPoint.<br>- **Việt**: Viết trọn bộ 8 bài Unit Test JUnit 5, tối ưu hiệu năng, đóng gói `.jar`. | Dự án đạt chuẩn nghiệm thu; Sẵn sàng bảo vệ. |

---

## IV. TỔNG KẾT ĐÁNH GIÁ ĐÓNG GÓP & KÝ BIÊN BẢN

### 1. Bảng điểm tự đánh giá nội bộ nhóm

| STT | Thành viên | Điểm chuyên cần (10%) | Khối lượng hoàn thành (50%) | Chất lượng kỹ thuật (40%) | Tổng điểm tự đánh giá | Xếp loại đóng góp |
| :---: | :--- | :---: | :---: | :---: | :---: | :---: |
| 1 | **Nguyễn Phương Thảo** (Trưởng nhóm) | 10 / 10 | 50 / 50 | 40 / 40 | **10 / 10** | **Xuất sắc (A)** |
| 2 | **Bạch Hoàng Dương** | 10 / 10 | 50 / 50 | 40 / 40 | **10 / 10** | **Xuất sắc (A)** |
| 3 | **Đào Phúc Việt** | 10 / 10 | 50 / 50 | 40 / 40 | **10 / 10** | **Xuất sắc (A)** |

### 2. Nhận xét của Trưởng nhóm
- Cả 3 thành viên đều có tinh thần trách nhiệm cao, tham gia đầy đủ các buổi họp nhóm và hoàn thành đúng hạn các nhiệm vụ được giao.
- Khối lượng công việc kỹ thuật được san sẻ đồng đều, phối hợp chặt chẽ giữa Backend CSDL, Nghiệp vụ bàn/thu ngân và Điều hành bếp/Dashboard.
- Toàn bộ sản phẩm mã nguồn chạy ổn định, không có lỗi, đạt chuẩn chất lượng cao cả về giao diện lẫn kiến trúc lập trình hướng đối tượng.

---

### 3. Ký xác nhận của các thành viên trong nhóm

*Hà Nội, ngày 08 tháng 09 năm 2026*

| Trưởng nhóm | Thành viên 1 | Thành viên 2 |
| :---: | :---: | :---: |
| *(Đã ký)* | *(Đã ký)* | *(Đã ký)* |
| <br><br>**Nguyễn Phương Thảo** | <br><br>**Bạch Hoàng Dương** | <br><br>**Đào Phúc Việt** |
