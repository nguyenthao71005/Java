# BÁO CÁO CHI TIẾT ĐỀ TÀI BÀI TẬP LỚN LẬP TRÌNH JAVA
## ĐỀ TÀI: HỆ THỐNG QUẢN LÝ VẬN HÀNH NHÀ HÀNG ĐA PHÂN HỆ (RESTAURANT OPS)

---

## MỤC LỤC
1. [KẾ HOẠCH LÀM ĐỀ TÀI](#1-kế-hoạch-làm-đề-tài)
   - 1.1. Tổng quan đề tài & Mục tiêu
   - 1.2. Kế hoạch phân chia giai đoạn (WBS - Work Breakdown Structure)
   - 1.3. Bảng tiến độ và phân công trách nhiệm
   - 1.4. Quản lý rủi ro và phương án xử lý
2. [THUẬT TOÁN GIẢI QUYẾT VẤN ĐỀ](#2-thuật-toán-giải-quyết-vấn-đề)
   - 2.1. Thuật toán Tính toán Hóa đơn Đa tầng & Xác thực Voucher/VAT
   - 2.2. Thuật toán Bàn giao & Đối soát Két tiền Mặt Ca làm việc
   - 2.3. Thuật toán Nội suy Đường cong Spline Bezier cho Biểu đồ Doanh thu
   - 2.4. Thuật toán Máy trạng thái Hữu hạn (FSM) Điều phối Bếp & Order hàng loạt
   - 2.5. Thuật toán Lọc & Tìm kiếm Thực đơn Đa tiêu chí không dấu Tiếng Việt
3. [CÁC CÔNG NGHỆ SỬ DỤNG](#3-các-công-nghệ-sử-dụng)
   - 3.1. Ngôn ngữ lập trình: Java 21 LTS
   - 3.2. Giao diện đồ họa (Desktop GUI): Java Swing & Thư viện FlatLaf Look & Feel
   - 3.3. Cơ sở dữ liệu: MySQL 8.0 Server & JDBC Driver 8.3.0
   - 3.4. Quản lý dự án & Kiểm thử: Apache Maven & JUnit 5 Jupiter
   - 3.5. Các mẫu thiết kế (Design Patterns) áp dụng
4. [PHÂN TÍCH YÊU CẦU BÀI TOÁN](#4-phân-tích-yêu-cầu-bài-toán)
   - 4.1. Ma trận Phân quyền Tác nhân (Actor / RBAC Matrix)
   - 4.2. Đặc tả chi tiết 10 phân hệ nghiệp vụ cốt lõi
5. [KIẾN TRÚC HỆ THỐNG](#5-kiến-trúc-hệ-thống)
   - 5.1. Mô hình tổng thể 3 tầng kết hợp MVC & DAO
   - 5.2. Đặc tả chi tiết các Lớp (Class) trong hệ thống (Thuộc tính & Phương thức)
   - 5.3. Hiện thực Tính Kế thừa (Inheritance) - Tối thiểu 3 lớp kế thừa
   - 5.4. Mô tả chi tiết phương thức hoạt động trên 6 màn hình giao diện chính
6. [THIẾT KẾ GIAO DIỆN (INTERFACE DESIGN)](#6-thiết-kế-giao-diện-interface-design)
   - 6.1. Thiết kế Giao diện Lập trình Hướng đối tượng (Tối thiểu 5 Java Interfaces)
     - Interface 1: `IBaseDAO<T>`
     - Interface 2: `IOrderService`
     - Interface 3: `IPaymentService`
     - Interface 4: `IShiftService`
     - Interface 5: `IKitchenService`
     - Interface 6: `IPrintableReceipt`
   - 6.2. Thiết kế Giao diện Đồ họa Người dùng (GUI Components & Event Driven)
7. [KẾT LUẬN & HƯỚNG PHÁT TRIỂN](#7-kết-luận--hướng-phát-triển)

---

# 1. KẾ HOẠCH LÀM ĐỀ TÀI

### 1.1. Tổng quan đề tài & Mục tiêu
- **Tên đề tài**: Hệ thống Quản lý Vận hành Nhà hàng Đa phân hệ (**RESTAURANT OPS**).
- **Tính cấp thiết**: Trong kỷ nguyên chuyển đổi số ngành F&B (Food & Beverage), các nhà hàng hiện đại đòi hỏi sự phối hợp nhịp nhàng theo thời gian thực giữa 4 bộ phận: **Phục vụ bàn**, **Nhà bếp/Quầy bar**, **Thu ngân** và **Ban quản lý**. Việc ghi chép thủ công bằng giấy dễ dẫn đến thất thoát doanh thu, nhầm lẫn đơn gọi món, chậm trễ chế biến và sai lệch két tiền khi giao ca.
- **Mục tiêu đề tài**:
  1. Xây dựng ứng dụng Desktop hoàn chỉnh bằng ngôn ngữ Java theo chuẩn thiết kế hướng đối tượng (OOP) và mô hình kiến trúc MVC.
  2. Kết nối và thao tác dữ liệu an toàn với CSDL quan hệ **MySQL 8.0** qua JDBC, có cơ chế bộ đệm dữ liệu (Caching/Dummy fallback) khi ngắt kết nối mạng.
  3. Hiện thực hóa các nghiệp vụ nhà hàng phức tạp: Sơ đồ bàn ăn thời gian thực, điều phối gọi món đa trạng thái, quản lý hàng chờ nhà bếp đa chọn (multi-select), đối soát ca làm việc tự động tính chênh lệch thừa/thiếu, tách hóa đơn và áp dụng mã khuyến mãi (Voucher).
  4. Cung cấp màn hình Dashboard thống kê chuyên sâu với đồ họa hiện đại (biểu đồ đường cong Spline Bezier, biểu đồ phễu phân tầng gradient và thẻ chỉ số KPI).

---

### 1.2. Kế hoạch phân chia giai đoạn (WBS - Work Breakdown Structure)

Quy trình phát triển đề tài được hoạch định theo mô hình lặp (Iterative Waterfall) trong vòng **12 tuần**:

| Giai đoạn | Thời gian | Nhiệm vụ chính | Sản phẩm bàn giao (Deliverables) |
| :--- | :--- | :--- | :--- |
| **Giai đoạn 1**<br>Khảo sát & Phân tích | Tuần 1 - Tuần 2 | - Khảo sát quy trình vận hành nhà hàng thực tế.<br>- Thu thập và đặc tả yêu cầu chức năng (FR) và phi chức năng (NFR).<br>- Xác định các tác nhân và ma trận phân quyền (RBAC). | - Tài liệu Đặc tả Yêu cầu Phần mềm (SRS).<br>- Biểu đồ Use Case tổng quát. |
| **Giai đoạn 2**<br>Thiết kế Hệ thống & CSDL | Tuần 3 - Tuần 4 | - Thiết kế mô hình dữ liệu quan hệ (ERD).<br>- Thiết kế Schema MySQL (`Tables`, `Views`, `Foreign Keys`, `Triggers`).<br>- Thiết kế kiến trúc lớp (Class Diagram), Interface và Package. | - Script CSDL `database.sql`.<br>- Sơ đồ kiến trúc MVC + DAO.<br>- Định nghĩa các Java Interface lõi. |
| **Giai đoạn 3**<br>Hiện thực Model & Tầng DAO | Tuần 5 - Tuần 6 | - Xây dựng các Entity Model (`Ban`, `MonAn`, `HoaDon`, `CaLamViec`...).<br>- Xây dựng kết nối Singleton `DatabaseConnection`.<br>- Hiện thực các lớp DAO thực thi `IBaseDAO`. | - Bộ mã nguồn Model & DAO hoàn chỉnh.<br>- Các bài kiểm thử Unit Test kết nối CSDL (`DatabaseConnectionTest`). |
| **Giai đoạn 4**<br>Thiết kế GUI & Nghiệp vụ Cốt lõi | Tuần 7 - Tuần 8 | - Thiết lập Design System với FlatLaf & UIConstants.<br>- Xây dựng Màn hình Đăng nhập (`LoginDialog`).<br>- Xây dựng Màn hình Sơ đồ bàn (`TableMapPanel`) & Gọi món (`OrderPanel`).<br>- Xây dựng Màn hình Thu ngân (`CheckoutPanel`) & Tách hóa đơn. | - Bộ giao diện tương tác cốt lõi.<br>- Tích hợp sự kiện chuyển bàn, gửi bếp, thanh toán. |
| **Giai đoạn 5**<br>Nghiệp vụ Nâng cao & Dashboard | Tuần 9 - Tuần 10 | - Phát triển Trung tâm Điều hành Bếp (`KitchenManagementPanel`).<br>- Phát triển Quản lý Ca & Bàn giao Két tiền (`ShiftManagementPanel`).<br>- Xây dựng Dashboard phân tích với đồ họa Spline & Funnel.<br>- Xây dựng 5 màn hình Quản trị dữ liệu kế thừa `BaseCrudPanel`. | - Hoàn thiện 10 phân hệ nghiệp vụ.<br>- Báo cáo doanh thu và top món ăn chạy trực tiếp từ CSDL. |
| **Giai đoạn 6**<br>Kiểm thử, Đóng gói & Báo cáo | Tuần 11 - Tuần 12 | - Viết tự động hóa kịch bản kiểm thử (`ShiftPaymentTest`, `KitchenAndShiftTest`).<br>- Tối ưu hóa bộ nhớ, chống rò rỉ kết nối JDBC.<br>- Chụp ảnh giao diện tự động (`CaptureUiScreenshotsTest`).<br>- Hoàn thiện báo cáo tổng kết và đóng gói file chạy `.jar`. | - Bộ 8/8 bài kiểm thử tự động đạt 100% PASS.<br>- File báo cáo chi tiết đề tài `BAO_CAO_DE_TAI.md`.<br>- File thực thi đóng gói. |

---

### 1.3. Bảng tiến độ và phân công trách nhiệm

| Thành viên / Vai trò | Phụ trách kỹ thuật | Nhiệm vụ chi tiết | Tỷ lệ hoàn thành |
| :--- | :--- | :--- | :--- |
| **Nhóm trưởng / Kiến trúc sư phần mềm** | Architecture, Database & Shift Management | - Thiết kế kiến trúc MVC, DAO và Interface.<br>- Thiết kế Schema CSDL MySQL và Stored Views.<br>- Hiện thực phân hệ Quản lý Ca làm việc & Đối soát két tiền. | 100% |
| **Lập trình viên Backend / Data Layer** | JDBC, Services & Kitchen Ops | - Xây dựng các lớp DAO và Transaction thanh toán.<br>- Phát triển phân hệ Bếp & Bar và xử lý chọn nhiều món.<br>- Tích hợp bộ đệm ngoại tuyến `DummyDataFactory`. | 100% |
| **Lập trình viên Frontend / UI Designer** | Swing GUI, FlatLaf & Graphics2D | - Thiết kế giao diện Sơ đồ bàn, Order và Thu ngân.<br>- Vẽ biểu đồ tuỳ biến Spline Bezier và Funnel bằng `Graphics2D`.<br>- Tùy biến bảng dữ liệu `ModernTable` và `RoundedPanel`. | 100% |
| **Kỹ sư Đảm bảo chất lượng (QA / QC)** | Automated Testing & Documentation | - Viết kịch bản kiểm thử JUnit 5 (Kết nối DB, chu trình ca, thanh toán).<br>- Kiểm thử tải và kiểm tra an toàn luồng Swing.<br>- Soạn thảo tài liệu đặc tả và hướng dẫn sử dụng. | 100% |

---

### 1.4. Quản lý rủi ro và phương án xử lý (Risk Management)

1. **Rủi ro CSDL MySQL mất kết nối mạng hoặc server gián đoạn**:
   - *Tác động*: Nghiêm trọng. Nhân viên không thể order hoặc thanh toán, làm gián đoạn toàn bộ hoạt động của nhà hàng.
   - *Giải pháp*: Xây dựng cơ chế **Offline Caching Fallback** thông qua `DummyDataFactory`. Khi `DatabaseConnection.isConnected()` trả về `false`, hệ thống tự động chuyển vùng lưu trữ tạm thời sang RAM và hiển thị cảnh báo `CSDL: Ngoại tuyến (Bộ nhớ tạm)` trên Header. Khi CSDL online trở lại, hệ thống tiếp tục hoạt động liền mạch mà không bị văng ứng dụng (`Crash`).
2. **Rủi ro sai lệch dòng tiền két giữa các ca làm việc**:
   - *Tác động*: Cao. Thất thoát tài chính, nhân viên đùn đẩy trách nhiệm.
   - *Giải pháp*: Áp dụng thuật toán **Khóa ca & Kế thừa số dư**: Ca sau bắt buộc nhận số tiền mặt thực tế bàn giao từ ca trước làm tiền đầu ca. Khi đóng ca, hệ thống tính toán tiền lý thuyết độc lập từ hóa đơn và bắt buộc giải trình nguyên nhân nếu phát sinh chênh lệch.
3. **Rủi ro lỗi luồng giao diện Swing (Swing UI Freezing)**:
   - *Tác động*: Trung bình. Ứng dụng bị đơ (not responding) khi tải lượng lớn dữ liệu hoặc vẽ biểu đồ phức tạp.
   - *Giải pháp*: Tất cả các thao tác cập nhật giao diện đều được bọc trong luồng `SwingUtilities.invokeLater()` hoặc `Event Dispatch Thread (EDT)`. Các câu lệnh truy vấn CSDL đều dùng `PreparedStatement` được index hóa, giới hạn số bản ghi `LIMIT`.

---

# 2. THUẬT TOÁN GIẢI QUYẾT VẤN ĐỀ

Trong hệ thống **RESTAURANT OPS**, các thuật toán được nghiên cứu và cài đặt tỉ mỉ nhằm giải quyết triệt để các bài toán thực tế trong vận hành nhà hàng:

---

### 2.1. Thuật toán Tính toán Hóa đơn Đa tầng & Xác thực Voucher/VAT

#### Bài toán đặt ra:
Khi thanh toán, hóa đơn không chỉ đơn thuần là cộng tiền các món, mà phải tính toán qua nhiều tầng chiết khấu: giá gốc, giảm giá voucher theo % hoặc số tiền cố định (có khống chế trần tối đa và điều kiện đơn tối thiểu), sau đó mới áp dụng tỷ lệ thuế giá trị gia tăng (VAT).

#### Lưu đồ & Công thức toán học:
1. **Bước 1: Tính tổng tiền món ăn gốc (Subtotal)**:
   $$\text{Subtotal} = \sum_{i=1}^{n} (\text{DonGia}_i \times \text{SoLuong}_i)$$
2. **Bước 2: Xác thực & Tính giá trị chiết khấu Voucher ($V$)**:
   - Điều kiện áp dụng:
     $$\text{IsEligible} = (\text{Voucher.TrangThai} == \text{"ConHan"}) \land (\text{Subtotal} \ge \text{Voucher.DonHangToiThieu})$$
   - Nếu không thỏa mãn $\text{IsEligible} \implies \text{TienGiam} = 0$.
   - Nếu thỏa mãn:
     $$\text{TienGiamTamTinh} = \text{Subtotal} \times \frac{\text{Voucher.PhanTramGiam}}{100}$$
     $$\text{TienGiam} = \min(\text{TienGiamTamTinh}, \text{Voucher.GiamToiDa})$$
3. **Bước 3: Tính tiền chịu thuế & Thuế VAT**:
   $$\text{TienChiuThue} = \max(0, \text{Subtotal} - \text{TienGiam})$$
   $$\text{TienVAT} = \text{TienChiuThue} \times \frac{\text{VAT\_Percent}}{100}$$
4. **Bước 4: Tính tổng số tiền khách cần thanh toán (Final Total)**:
   $$\text{TongThanhToan} = \text{TienChiuThue} + \text{TienVAT}$$

#### Mã giả thuật toán (Pseudocode):
```text
FUNCTION TinhToanHoaDon(danhSachMon, voucher, vatPercent):
    subtotal = 0
    FOR EACH mon IN danhSachMon:
        subtotal = subtotal + (mon.donGia * mon.soLuong)
    
    tienGiam = 0
    IF voucher != NULL AND voucher.trangThai == "HoatDong" THEN:
        IF subtotal >= voucher.donHangToiThieu THEN:
            tienGiam = subtotal * (voucher.phanTramGiam / 100.0)
            IF tienGiam > voucher.giamToiDa THEN:
                tienGiam = voucher.giamToiDa
            END IF
        END IF
    END IF
    
    tienSauGiam = MAX(0, subtotal - tienGiam)
    tienVAT = tienSauGiam * (vatPercent / 100.0)
    tongThanhToan = tienSauGiam + tienVAT
    
    RETURN Map {
        "Subtotal": subtotal,
        "Discount": tienGiam,
        "VAT": tienVAT,
        "Total": tongThanhToan
    }
END FUNCTION
```

---

### 2.2. Thuật toán Bàn giao & Đối soát Két tiền Mặt Ca làm việc

#### Bài toán đặt ra:
Trong một ca làm việc, khách hàng thanh toán bằng nhiều hình thức: **Tiền mặt (Cash)**, **Chuyển khoản ngân hàng (QR Code)**, **Thẻ thanh toán (POS)**. Chỉ có doanh thu tiền mặt được lưu giữ trong két vật lý tại quầy thu ngân. Khi đóng ca, thu ngân phải kiểm đếm tiền mặt thực tế trong két. Hệ thống phải tự động tính toán số tiền mặt "lý thuyết" phải có và phát hiện chênh lệch thừa hoặc thiếu.

#### Thuật toán kiểm soát dòng tiền:
1. **Tiền mặt lý thuyết trong két ($\text{Cash}_{\text{theoretical}}$)**:
   $$\text{Cash}_{\text{theoretical}} = \text{TienDauCa} + \text{DoanhThuTienMat}$$
   *(Lưu ý: Doanh thu Chuyển khoản và Thẻ POS chảy trực tiếp vào tài khoản ngân hàng của nhà hàng, tuyệt đối không được cộng vào két tiền mặt).*
2. **Tính toán độ lệch két ($\Delta$)**:
   $$\Delta = \text{TienThucTe} - \text{Cash}_{\text{theoretical}}$$
3. **Phân loại xử lý nghiệp vụ**:
   - **Trường hợp 1 ($\Delta = 0$)**: Két tiền khớp 100%. Đóng ca thành công, lưu trạng thái `DaDong`.
   - **Trường hợp 2 ($\Delta > 0$)**: Thừa tiền trong két. Hệ thống cảnh báo màu xanh dương `⚠️ Thừa tiền trong két: +X VND`. Thu ngân bắt buộc nhập ghi chú nguyên nhân (ví dụ: khách không lấy tiền thối, tiền tip nhân viên chưa rút...).
   - **Trường hợp 3 ($\Delta < 0$)**: Thiếu tiền trong két. Hệ thống kích hoạt cảnh báo màu đỏ `❌ Thiếu tiền trong két: -X VND`. Thu ngân bắt buộc lập biên bản giải trình trước khi hệ thống cho phép hoàn tất kết ca.
4. **Kế thừa số dư ca liên tiếp**:
   - Khi ca mới mở, hệ thống truy vấn ca `DaDong` gần nhất:
     $$\text{TienDauCa}_{\text{CaMoi}} = \text{TienThucTe}_{\text{CaTruoc}}$$
   - Đảm bảo tính minh bạch, người nhận ca ký nhận đúng số tiền người giao ca đã bàn giao.

---

### 2.3. Thuật toán Nội suy Đường cong Spline Bezier cho Biểu đồ Doanh thu

#### Bài toán đặt ra:
Các biểu đồ mặc định thường nối các điểm dữ liệu doanh thu theo từng tháng bằng các đoạn thẳng gấp khúc thô cứng, gây khó chịu cho mắt nhìn. Để đạt tính thẩm mỹ cao cấp (Modern Rich Aesthetics), hệ thống sử dụng thuật toán nội suy đường cong mượt mà **Cubic Bezier Spline** đi qua các điểm dữ liệu $(x_i, y_i)$, đồng thời tạo vùng đổ bóng kính mờ (Glassmorphism Gradient Fill) phía dưới đường cong.

#### Nguyên lý toán học:
Giữa hai điểm dữ liệu liên tiếp $P_i(x_i, y_i)$ và $P_{i+1}(x_{i+1}, y_{i+1})$, ta cần tìm 2 điểm điều khiển $C1_i(x_{c1}, y_{c1})$ và $C2_i(x_{c2}, y_{c2})$ sao cho tiếp tuyến tại các điểm nối liên tục:
$$x_{c1} = x_i + \frac{x_{i+1} - x_{i-1}}{6} \cdot \text{smoothFactor}$$
$$y_{c1} = y_i + \frac{y_{i+1} - y_{i-1}}{6} \cdot \text{smoothFactor}$$
$$x_{c2} = x_{i+1} - \frac{x_{i+2} - x_i}{6} \cdot \text{smoothFactor}$$
$$y_{c2} = y_{i+1} - \frac{y_{i+2} - y_i}{6} \cdot \text{smoothFactor}$$
Với $\text{smoothFactor} \approx 0.8$, đường cong đi qua chính xác các điểm dữ liệu mà không bị uốn lượn quá mức.

```
       C1 (Control Point 1)          C2 (Control Point 2)
           o----------------------------o
          /                              \
         /     Smooth Bezier Spline       \
  P_i  *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~* P_{i+1}
```

Sau khi vẽ đường cong bằng `GeneralPath.curveTo(x_{c1}, y_{c1}, x_{c2}, y_{c2}, x_{i+1}, y_{i+1})`, đường dẫn được khép kín xuống đáy trục hoành và tô bằng `GradientPaint` từ màu Cyan bán trong suốt (`Alpha = 70`) về hoàn toàn trong suốt (`Alpha = 0`), tạo hiệu ứng ánh sáng phát quang hiện đại.

---

### 2.4. Thuật toán Máy trạng thái Hữu hạn (FSM) Điều phối Bếp & Order hàng loạt

#### Bài toán đặt ra:
Một bàn ăn có thể gọi nhiều món ăn cùng lúc. Bếp cần xử lý linh hoạt: có thể chọn 1 món hoặc giữ `Ctrl`/`Shift` để chọn hàng loạt món (Batch Multi-select) và bấm chuyển trạng thái cùng một lúc. Trạng thái của món phải chuyển tiếp tuần tự, không được nhảy cóc.

#### Máy trạng thái (Finite State Machine):
```
        [ Khách gọi món ]
               │
               ▼
         ┌───────────┐
         │  ChoNau   │ ◄── (Mới gửi vào bếp)
         └─────┬─────┘
               │  Bắt đầu nấu (Nhận nấu hàng loạt)
               ▼
         ┌───────────┐
         │  DangLam  │ ◄── (Bếp đang xào nấu / pha chế)
         └─────┬─────┘
               │  Hoàn thành (Ra món hàng loạt)
               ▼
         ┌───────────┐
         │  DaRaMon  │ ◄── (Phục vụ bưng lên bàn cho khách)
         └───────────┘
```
*(Nếu phát sinh sự cố: món ở trạng thái `ChoNau` hoặc `DangLam` có thể chuyển sang trạng thái `DaHuy` kèm theo lý do hủy món).*

#### Xử lý hàng loạt (Batch Processing):
Sử dụng Transaction trong CSDL:
$$\text{UPDATE ChiTietHoaDon SET TrangThai = ? WHERE MaCTHD IN (?, ?, ...)}$$
Đảm bảo tính trọn vẹn (Atomicity), hoặc tất cả các món được chọn cùng chuyển trạng thái thành công, hoặc không món nào bị thay đổi dở dang nếu có lỗi ngoại lệ.

---

### 2.5. Thuật toán Lọc & Tìm kiếm Thực đơn Đa tiêu chí không dấu Tiếng Việt

Khi nhân viên phục vụ tìm món trên màn hình cảm ứng, họ thường gõ tiếng Việt không dấu (ví dụ gõ `"bo"` để tìm `"Bò bít tết Wagyu"`, gõ `"nuoc"` để tìm `"Nước ép dưa hấu"`).
- **Thuật toán chuẩn hóa chuỗi**: Sử dụng `java.text.Normalizer` kết hợp biểu thức chính quy (Regex) loại bỏ dấu thanh:
  ```java
  public static String removeAccents(String text) {
      String nfd = Normalizer.normalize(text, Normalizer.Form.NFD);
      Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
      return pattern.matcher(nfd).replaceAll("").replace('đ', 'd').replace('Đ', 'D').toLowerCase();
  }
  ```
- **So khớp đa tiêu chí**:
  $$\text{Match} = (\text{MaDM}_{\text{mon}} == \text{SelectedDM} \lor \text{SelectedDM} == 0) \land (\text{removeAccents}(\text{TenMon}).\text{contains}(\text{removeAccents}(\text{SearchQuery})))$$
- Đảm bảo tốc độ lọc tức thì (< 5ms) trên danh mục hàng trăm món ăn.

---

# 3. CÁC CÔNG NGHỆ SỬ DỤNG

Hệ thống được xây dựng trên nền tảng công nghệ mạnh mẽ, chuẩn hóa và tối ưu cho môi trường doanh nghiệp:

| Lớp công nghệ | Công nghệ sử dụng | Phiên bản | Lý do lựa chọn & Vai trò trong hệ thống |
| :--- | :--- | :--- | :--- |
| **Ngôn ngữ cốt lõi** | **Java (JDK)** | **Java 21 LTS** | Phiên bản hỗ trợ dài hạn mới nhất, sở hữu khả năng tối ưu hóa bộ nhớ, cơ chế quản lý luồng hiện đại, tính năng Type Safety và tính đa nền tảng ("Write Once, Run Anywhere"). |
| **Giao diện người dùng** | **Java Swing + Java 2D** | Built-in Java 21 | Cung cấp toàn quyền kiểm soát việc vẽ đồ họa pixel (`Graphics2D`, `RenderingHints`, `GradientPaint`), không phụ thuộc vào trình duyệt web, thời gian phản hồi micro-giây cực nhanh. |
| **Giao diện hiện đại** | **FlatLaf (Flat Look and Feel)** | **3.5.4** | Thư viện giao diện Swing hiện đại nhất hiện nay. Hỗ trợ bo góc mịn màng (corner radius), tự động co giãn theo màn hình độ phân giải cao (High-DPI 4K), loại bỏ vẻ thô cứng cổ điển của Java Swing truyền thống. |
| **Cơ sở dữ liệu** | **MySQL Community Server** | **8.0.46** | Hệ quản trị CSDL quan hệ phổ biến nhất thế giới. Hỗ trợ ACID Transactions nghiêm ngặt bảo vệ tính đúng đắn của hóa đơn tài chính, khóa ngoại (Foreign Keys), Stored Views và Triggers. |
| **Giao tiếp CSDL** | **MySQL Connector/J (JDBC)** | **8.3.0** | Driver giao tiếp trực tiếp giữa Java và MySQL, hỗ trợ kết nối `PreparedStatement` ngăn chặn hoàn toàn lỗ hổng tấn công SQL Injection. |
| **Quản lý dự án** | **Apache Maven** | **3.9+** | Quản lý vòng đời dự án tự động (Clean, Compile, Test, Package), giải quyết xung đột thư viện dependencies thông qua file `pom.xml`. |
| **Khung kiểm thử** | **JUnit 5 (Jupiter)** | **5.10.2** | Khung kiểm thử đơn vị và tích hợp hiện đại, cung cấp các annotation `@Test`, `@BeforeEach`, `Assertions` hỗ trợ viết kịch bản test tự động. |

### Các Mẫu Thiết Kế (Design Patterns) Áp Dụng:
1. **MVC (Model - View - Controller / DAO)**: Tách bạch tuyệt đối giữa dữ liệu nghiệp vụ (Model), giao diện hiển thị (View) và thao tác truy xuất dữ liệu (DAO).
2. **Singleton Pattern**: Áp dụng trong `DatabaseConnection` (quản lý 1 kết nối JDBC duy nhất tránh quá tải connection pool) và `DummyDataFactory` (quản lý bộ đệm dữ liệu duy nhất trong bộ nhớ).
3. **Template Method Pattern**: Áp dụng trong `BaseCrudPanel` định nghĩa khung sườn quản lý CRUD (tạo bảng, thanh công cụ, ô tìm kiếm) để các lớp con tự hiện thực chi tiết.
4. **Observer / Event-Driven Pattern**: Các thành phần Swing lắng nghe sự kiện (`ActionListener`, `MouseListener`, `DocumentListener`) phản hồi tức thì với các thao tác của người dùng.

---

# 4. PHÂN TÍCH YÊU CẦU BÀI TOÁN

---

### 4.1. Ma trận Phân quyền Tác nhân (Actor / RBAC Matrix)

Hệ thống phân định rạch ròi 4 vai trò tác nhân với quyền hạn được kiểm soát từ khâu Đăng nhập:

```
                  ┌─────────────────────────────────┐
                  │    ĐĂNG NHẬP HỆ THỐNG (RBAC)    │
                  └────────────────┬────────────────┘
                                   │
         ┌─────────────────────────┼─────────────────────────┐
         ▼                         ▼                         ▼
   [ QUẢN LÝ ]               [ THU NGÂN ]              [ PHỤC VỤ ]
 (Role: QuanLy)            (Role: ThuNgan)           (Role: PhucVu)
         │                         │                         │
 ┌───────┴───────┐         ┌───────┴───────┐                 │
 │ • Toàn quyền  │         │ • Thu ngân    │                 ▼
 │ • Dashboard   │         │ • Sơ đồ bàn   │          ┌──────────────┐
 │ • Báo cáo DT  │         │ • Mở/Đóng ca  │          │ • Sơ đồ bàn  │
 │ • Quản trị DB │         │ • Xem HĐ cũ   │          │ • Gọi món    │
 └───────────────┘         └───────────────┘          │ • Chuyển bàn │
                                                      └──────────────┘
                                   │
                                   ▼
                             [ BẾP TRƯỞNG ]
                              (Role: Bep)
                                   │
                           ┌───────┴───────┐
                           │ • Điều hành   │
                           │ • Nhận nấu    │
                           │ • Báo ra món  │
                           │ • Báo hết món │
                           └───────────────┘
```

---

### 4.2. Đặc tả chi tiết 10 phân hệ nghiệp vụ cốt lõi

Dưới đây là bảng phân tích chi tiết từng chức năng theo đúng mẫu chuẩn: **Chức năng để làm gì? Ai sử dụng? Làm thế nào để thực hiện? Dữ liệu vào/ra? Ràng buộc nghiệp vụ?**

#### 1. Chức năng Đăng nhập & Xác thực Phân quyền (Authentication)
- **Mục đích**: Bảo vệ an toàn dữ liệu hệ thống, ngăn chặn truy cập trái phép, định danh nhân viên phục vụ/thu ngân chịu trách nhiệm trên từng đơn hàng.
- **Ai sử dụng**: Toàn bộ nhân sự nhà hàng (Quản lý, Thu ngân, Phục vụ, Bếp trưởng).
- **Cách thức thực hiện**:
  1. Người dùng mở ứng dụng, màn hình `LoginDialog` hiển thị yêu cầu thông tin.
  2. Nhập `Tên đăng nhập` (ví dụ: `admin`, `cashier01`, `waiter01`, `chef01`).
  3. Nhập `Mật khẩu`.
  4. Nhấn nút `ĐĂNG NHẬP` hoặc nhấn phím `Enter`.
  5. Hệ thống kiểm tra trong bảng `NhanVien`, nếu hợp lệ sẽ khởi tạo `MainFrame` với thanh Sidebar được cấu hình phân quyền tương ứng; nếu sai sẽ báo lỗi đỏ.
- **Dữ liệu vào / ra**:
  - *Đầu vào*: Username (chuỗi), Password (chuỗi ký tự ẩn).
  - *Đầu ra*: Đối tượng `NhanVien` kèm vai trò (`ChucVu`), trạng thái truy cập ứng dụng.
- **Ràng buộc nghiệp vụ**: Tài khoản phải ở trạng thái `HoatDong = 1`. Nếu sai mật khẩu quá số lần quy định sẽ cảnh báo.

#### 2. Chức năng Sơ đồ Bàn ăn Trực quan (Table Map)
- **Mục đích**: Giúp nhân viên và quản lý nắm bắt tức thì tình trạng bàn trống, bàn đang có khách trong toàn bộ nhà hàng theo từng tầng/khu vực.
- **Ai sử dụng**: Nhân viên Phục vụ, Thu ngân, Quản lý.
- **Cách thức thực hiện**:
  1. Người dùng chọn mục `Sơ đồ bàn ăn` trên Sidebar.
  2. Chọn bộ lọc khu vực: `Tất cả`, `Tầng 1`, `Tầng 2`, `Phòng VIP`.
  3. Quan sát màu sắc các ô bàn: **Màu Xanh lá** (Bàn trống), **Màu Đỏ** (Đang có khách).
  4. Thao tác:
     - Nhấp chuột vào bàn Xanh $\to$ Hệ thống hiện hộp thoại xác nhận mở bàn $\to$ Nhấn Đồng ý $\to$ Bàn chuyển sang Đỏ và chuyển thẳng sang màn hình Gọi món.
     - Nhấp chuột vào bàn Đỏ $\to$ Hệ thống hiển thị lựa chọn: "Mở Order để thêm món" hoặc "Chuyển nhanh sang Thu ngân".
- **Dữ liệu vào / ra**:
  - *Đầu vào*: Mã bàn, khu vực lọc.
  - *Đầu ra*: Lưới hiển thị 20 bàn ăn với trạng thái, sức chứa và màu sắc tương ứng.
- **Ràng buộc nghiệp vụ**: Bàn đã có khách không được phép mở bàn mới đè lên khi chưa thanh toán hóa đơn cũ.

#### 3. Chức năng Gọi món & Quản lý Order (Order Management)
- **Mục đích**: Ghi nhận các món ăn khách chọn, ghi chú khẩu vị cho đầu bếp, điều chỉnh số lượng hoặc chuyển bàn khi khách đổi chỗ.
- **Ai sử dụng**: Nhân viên Phục vụ.
- **Cách thức thực hiện**:
  1. Tại màn hình Order, hệ thống hiển thị bàn đang chọn ở tiêu đề bên trái.
  2. Bên phải là danh mục thực đơn: nhân viên bấm vào tab danh mục (Khai vị, Món chính, Đồ uống...) hoặc gõ tên món vào ô tìm kiếm.
  3. Bấm vào món ăn để thêm vào hóa đơn. Có thể dùng nút `+` / `-` để tăng giảm số lượng.
  4. Nhập ghi chú chế biến (ví dụ: *"Không ớt, ít đá, làm chín kỹ"*).
  5. Nếu khách muốn đổi bàn, bấm nút `Chuyển bàn` $\to$ Chọn bàn trống đích $\to$ Hệ thống tự động chuyển toàn bộ món ăn sang bàn mới và cập nhật trạng thái 2 bàn.
  6. Bấm nút `GỬI BẾP CHẾ BIẾN` để chuyển thông tin order xuống nhà bếp.
- **Dữ liệu vào / ra**:
  - *Đầu vào*: Mã bàn, mã món, số lượng, ghi chú.
  - *Đầu ra*: Bản ghi chi tiết hóa đơn trong bảng `ChiTietHoaDon` với trạng thái `ChoNau`.
- **Ràng buộc nghiệp vụ**: Không được phép thêm món đã bị bếp đánh dấu `[HẾT HÀNG]`. Số lượng món phải là số nguyên $> 0$.

#### 4. Chức năng Điều hành Bếp & Quầy Pha chế (Kitchen Operations)
- **Mục đích**: Nhận danh sách món cần nấu theo thứ tự thời gian, cảnh báo món chờ lâu, giúp bếp trưởng điều phối nấu nướng hiệu quả.
- **Ai sử dụng**: Bếp trưởng, Nhân viên quầy pha chế / quầy bar.
- **Cách thức thực hiện**:
  1. Mở màn hình `Quản lý Bếp & Bar`, xem danh sách món ăn đang chờ chế biến.
  2. Quan sát cột `Thời gian chờ`: Món chờ quá 15 phút sẽ tự động chuyển màu cảnh báo đỏ.
  3. Chọn một hoặc nhiều món (giữ phím `Ctrl` hoặc `Shift`):
     - Bấm nút `🍳 Bắt đầu nấu` $\to$ Các món được chọn chuyển sang trạng thái `DangLam`.
     - Bấm nút `✅ Hoàn thành (Ra món)` $\to$ Món chuyển sang `DaRaMon`, thông báo phục vụ bưng món.
     - Bấm nút `❌ Báo hủy món` $\to$ Nhập lý do (ví dụ: cháy món, hết nguyên liệu đột xuất).
  4. Có thể bật checkbox `Tự động làm mới (10s)` để hệ thống tự quét order mới bằng Swing Timer.
- **Dữ liệu vào / ra**:
  - *Đầu vào*: Danh sách mã CTHD được chọn, thao tác bấm nút.
  - *Đầu ra*: Cập nhật trạng thái `TrangThai` trong CSDL, cập nhật bảng hiển thị tức thì.
- **Ràng buộc nghiệp vụ**: Không thể bấm "Hoàn thành" nếu món chưa qua trạng thái "Bắt đầu nấu".

#### 5. Chức năng Quản lý Kho Thực đơn & Báo Hết Hàng
- **Mục đích**: Giúp bếp khóa các món ăn đã hết nguyên liệu để nhân viên phục vụ không nhận gọi món của khách nữa.
- **Ai sử dụng**: Bếp trưởng.
- **Cách thức thực hiện**:
  1. Tại màn hình Bếp, chọn Tab 2: `Kho Thực Đơn & Báo Hết Món`.
  2. Tìm kiếm món ăn cần thay đổi trạng thái.
  3. Chọn món và bấm nút `🔴 Báo Hết Hàng` $\to$ Món ăn chuyển trạng thái `HetHang = 1` trong MySQL.
  4. Ngay lập tức, màn hình Order của nhân viên phục vụ sẽ hiển thị gạch ngang `[HẾT HÀNG]` và vô hiệu hóa nút bấm thêm món.
  5. Khi có nguyên liệu mới, bếp chọn lại món và bấm nút `🟢 Mở Bán Lại` để phục hồi.
- **Dữ liệu vào / ra**:
  - *Đầu vào*: Mã món ăn, trạng thái còn món (boolean).
  - *Đầu ra*: Cập nhật cột `TrangThai` bảng `MonAn`.

#### 6. Chức năng Thu ngân, Voucher & Thanh toán Hóa đơn (Checkout)
- **Mục đích**: Tổng kết chi phí bàn ăn, áp dụng mã chiết khấu khuyến mãi, tính thuế VAT, in hóa đơn nhiệt K80 và thu tiền của khách.
- **Ai sử dụng**: Nhân viên Thu ngân.
- **Cách thức thực hiện**:
  1. Vào màn hình `Thu ngân (Checkout)`, chọn bàn đang có khách cần tính tiền.
  2. Bảng hiển thị toàn bộ danh sách món và tổng tiền gốc (Subtotal).
  3. Nhập mã khuyến mãi vào ô Voucher (ví dụ: `SUMMER20`, `VIP10`) rồi nhấn nút `Áp dụng`:
     - Hệ thống kiểm tra điều kiện và trừ tiền giảm giá tương ứng, hiển thị nhãn chiết khấu.
  4. Chọn tỷ lệ thuế VAT: `0%`, `8%`, `10%`.
  5. Chọn hình thức thanh toán: `Tiền mặt (Cash)`, `Chuyển khoản QR`, `Thẻ POS`.
  6. Nhấn nút `IN HÓA ĐƠN` để in phiếu thanh toán mẫu K80.
  7. Nhấn nút `XÁC NHẬN THANH TOÁN`:
     - Hóa đơn chuyển trạng thái `DaThanhToan`.
     - Bàn ăn tự động giải phóng về màu Xanh (Bàn trống).
     - Doanh thu tự động tích lũy vào Ca làm việc đang mở.
- **Dữ liệu vào / ra**:
  - *Đầu vào*: Mã bàn, mã voucher, tỷ lệ VAT, hình thức thanh toán.
  - *Đầu ra*: Hóa đơn thanh toán hoàn tất, phiếu in K80, cập nhật doanh thu ca.
- **Ràng buộc nghiệp vụ**: Bắt buộc phải có ca làm việc đang mở (`DangMo`) thì mới được phép thanh toán hóa đơn.

#### 7. Chức năng Tách Hóa Đơn Độc Lập (Split Bill)
- **Mục đích**: Phục vụ các đoàn khách đi chung bàn nhưng có nhu cầu thanh toán riêng từng nhóm hoặc từng cá nhân.
- **Ai sử dụng**: Thu ngân.
- **Cách thức thực hiện**:
  1. Trên màn hình Thu ngân, bấm nút `✂ Tách hóa đơn`.
  2. Hộp thoại `SplitBillDialog` hiển thị hai cột: Cột trái (Món bàn gốc), Cột phải (Món hóa đơn mới tách).
  3. Chọn món và số lượng cần tách, bấm nút `> Sang bill mới >`.
  4. Bấm `Xác nhận tách`: Hệ thống tạo một hóa đơn mới cho các món được tách và giảm số lượng tương ứng ở hóa đơn cũ.
- **Dữ liệu vào / ra**:
  - *Đầu vào*: Danh sách mã CTHD và số lượng cần tách.
  - *Đầu ra*: Bản ghi `HoaDon` mới trong CSDL.

#### 8. Chức năng Quản lý Ca Làm Việc & Đối Soát Két Tiền (Shift Management)
- **Mục đích**: Quản lý ca trực thu ngân, đối soát chính xác tiền mặt thực tế trong két, phát hiện và lập biên bản xử lý chênh lệch thừa/thiếu tiền.
- **Ai sử dụng**: Thu ngân, Quản lý nhà hàng.
- **Cách thức thực hiện**:
  - **Mở ca mới**:
    1. Bấm `🟢 Mở Ca Làm Việc Mới`.
    2. Hộp thoại `OpenShiftDialog` tự động hiển thị số tiền bàn giao từ ca trước và tự điền vào ô `Tiền mặt đầu ca`.
    3. Kiểm đếm tiền thực tế trong két khớp với số bàn giao rồi bấm `Xác nhận mở ca`.
  - **Kết thúc ca (Đóng ca & Bàn giao)**:
    1. Bấm `🔴 Bàn Giao Ca (Kết Ca)`.
    2. Hộp thoại `CloseShiftDialog` hiển thị: Tiền đầu ca, Doanh thu tiền mặt, Doanh thu chuyển khoản, và **Tiền mặt lý thuyết trong két**.
    3. Thu ngân đếm tiền mặt thực tế trong két và nhập vào ô `Tiền mặt thực tế`.
    4. Hệ thống tính chênh lệch: Nếu lệch, bắt buộc nhập lý do giải trình.
    5. Bấm `Xác nhận đóng ca` $\to$ Bấm `In Biên Bản Bàn Giao Ca` để ký xác nhận 2 bên.
- **Dữ liệu vào / ra**:
  - *Đầu vào*: Tiền mặt đầu ca, tiền mặt thực tế kiểm đếm, ghi chú giải trình.
  - *Đầu ra*: Bản ghi `CaLamViec` trạng thái `DaDong`, biên bản bàn giao in ra giấy.
- **Ràng buộc nghiệp vụ**: Không thể mở ca mới nếu ca cũ chưa được đóng. Không thể thanh toán đơn hàng nếu chưa mở ca.

#### 9. Chức năng Dashboard Phân tích Kinh doanh Thời gian thực
- **Mục đích**: Giúp chủ nhà hàng nắm bắt bức tranh toàn cảnh về sức khỏe tài chính và năng lực vận hành.
- **Ai sử dụng**: Quản lý / Admin.
- **Cách thức thực hiện**:
  1. Mở màn hình `Tổng quan Dashboard`.
  2. Chọn bộ lọc thời gian: `Hôm nay`, `7 ngày qua`, `Tháng này`, `Quý này`, `Năm nay`.
  3. Theo dõi 4 thẻ KPI: Doanh thu, Đơn hàng, Tỷ lệ lấp đầy bàn, Thời gian phục vụ trung bình.
  4. Quan sát Biểu đồ Phễu Vận hành Bàn & Gọi món để nhận biết các nút thắt cổ chai trong quy trình phục vụ.
  5. Rê chuột trên Biểu đồ Doanh thu Spline Bezier để xem chi tiết doanh thu thực tế so với mục tiêu đề ra.
  6. Xem danh sách 7 món ăn bán chạy nhất nạp trực tiếp từ View MySQL `v_TopMonBanChay`.
  7. Bấm `🔄 Làm mới` để cập nhật số liệu mới nhất tức thì.
- **Dữ liệu vào / ra**:
  - *Đầu vào*: Bộ lọc thời gian.
  - *Đầu ra*: Dữ liệu tổng hợp từ các bảng và view CSDL được trực quan hóa trên đồ họa Swing.

#### 10. Chức năng Quản trị Dữ liệu Danh mục, Món ăn, Bàn, Khuyến mãi & Nhân sự (Master Data CRUD)
- **Mục đích**: Cho phép quản trị viên thêm, sửa, xóa, tìm kiếm dữ liệu cơ sở của nhà hàng.
- **Ai sử dụng**: Quản lý / Admin.
- **Cách thức thực hiện**:
  1. Chọn màn hình quản trị tương ứng trên Sidebar (Nhân sự, Thực đơn, Danh mục, Bàn ăn, Khuyến mãi).
  2. Bấm `Thêm mới` $\to$ Điền thông tin vào Form $\to$ Bấm `Lưu`.
  3. Chọn 1 dòng trên bảng $\to$ Bấm `Chỉnh sửa` $\to$ Sửa thông tin $\to$ Bấm `Cập nhật`.
  4. Chọn dòng và bấm `Xóa` $\to$ Hệ thống hỏi xác nhận $\to$ Xóa bản ghi trong MySQL.
  5. Gõ từ khóa vào ô tìm kiếm để lọc dữ liệu tức thì.
- **Dữ liệu vào / ra**:
  - *Đầu vào*: Các trường thông tin của thực thể.
  - *Đầu ra*: Bảng dữ liệu cập nhật, thông báo kết quả.
- **Ràng buộc nghiệp vụ**: Không được xóa danh mục khi đang có món ăn trực thuộc (ràng buộc toàn vẹn khóa ngoại).

---

# 5. KIẾN TRÚC HỆ THỐNG

---

### 5.1. Mô hình tổng thể 3 tầng kết hợp MVC & DAO

Hệ thống được thiết kế theo kiến trúc phân tầng chuẩn mực trong công nghệ phần mềm:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER (GIAO DIỆN)                      │
│   MainFrame, LoginDialog, DashboardPanel, TableMapPanel, OrderPanel,    │
│   CheckoutPanel, KitchenManagementPanel, ShiftManagementPanel...        │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ Gọi qua Interfaces / Services
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    BUSINESS & SERVICE LAYER (NGHIỆP VỤ)                 │
│   IOrderService, IPaymentService, IShiftService, IKitchenService,       │
│   IPrintableReceipt, DummyDataFactory (Offline Caching)                 │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ Thao tác dữ liệu qua IBaseDAO<T>
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                   DATA ACCESS LAYER (TRUY XUẤT CSDL)                    │
│   BanDAO, MonAnDAO, DanhMucDAO, HoaDonDAO, CaLamViecDAO, ThongKeDAO...  │
│   └── DatabaseConnection (Singleton JDBC Pool: MySQL 8.0)               │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ SQL Queries / ResultSets
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                       DATABASE LAYER (MYSQL 8.0)                        │
│   Tables: Ban, MonAn, DanhMuc, HoaDon, ChiTietHoaDon, NhanVien, CaLamViec│
│   Views: v_DoanhThuTheoNgay, v_TopMonBanChay, v_LichSuCaLamViec         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### 5.2. Đặc tả chi tiết các Lớp (Class) trong hệ thống (Thuộc tính & Phương thức)

#### A. Nhóm Lớp Thực thể (Entity Model - Package `com.restaurant.model`)

##### 1. Lớp `Ban`
- **Mô tả**: Đại diện cho một bàn ăn trong nhà hàng.
- **Thuộc tính**:
  - `private int maBan`: Mã số định danh của bàn (Khóa chính).
  - `private String tenBan`: Tên hiển thị của bàn (ví dụ: "Bàn 01", "Bàn VIP 01").
  - `private String khuVuc`: Vị trí tầng/khu vực ("Tầng 1", "Tầng 2", "Phòng VIP").
  - `private int sucChua`: Số lượng khách tối đa (chỗ ngồi).
  - `private String trangThai`: Trạng thái bàn ("Trong" hoặc "CoKhach").
- **Phương thức**:
  - `public boolean isCoKhach()`: Kiểm tra bàn có đang phục vụ khách không (trả về `true` nếu `trangThai.equalsIgnoreCase("CoKhach")`).
  - `public String getMoTa()`: Trả về chuỗi mô tả ngắn gọn "Tên bàn • Số chỗ".
  - Các hàm Getters / Setters chuẩn cho toàn bộ thuộc tính.

##### 2. Lớp `MonAn`
- **Mô tả**: Đại diện cho một món ăn hoặc đồ uống trong thực đơn.
- **Thuộc tính**:
  - `private int maMon`: Mã định danh món ăn.
  - `private String tenMon`: Tên món ăn.
  - `private int maDM`: Mã danh mục trực thuộc.
  - `private String tenDanhMuc`: Tên danh mục phục vụ hiển thị nhanh.
  - `private double donGia`: Giá bán niêm yết (VND).
  - `private String hinhAnh`: Tên file hình ảnh đại diện.
  - `private boolean conMon`: Tình trạng kho (true: còn phục vụ, false: hết hàng).
- **Phương thức**:
  - `public String getGiaFormatted()`: Định dạng đơn giá tiền tệ có dấu phân cách nghìn (ví dụ: "125,000 VND").
  - Các hàm Getters / Setters.

##### 3. Lớp `HoaDon`
- **Mô tả**: Đại diện cho một đơn hàng / hóa đơn thanh toán của một bàn ăn.
- **Thuộc tính**:
  - `private int maHD`: Mã hóa đơn.
  - `private int maBan`: Mã bàn liên kết.
  - `private int maNV`: Mã nhân viên lập hóa đơn.
  - `private Integer maCa`: Mã ca làm việc thu tiền (khóa ngoại).
  - `private Timestamp gioVao`: Thời gian khách vào mở bàn.
  - `private Timestamp gioRa`: Thời gian thanh toán kết thúc.
  - `private double tongTien`: Tổng tiền sau thuế và chiết khấu.
  - `private double tienGiamGia`: Số tiền được giảm trừ từ voucher.
  - `private double vat`: Tiền thuế giá trị gia tăng.
  - `private String phuongThucThanhToan`: Hình thức ("Tiền mặt", "Chuyển khoản QR", "Thẻ POS").
  - `private String trangThai`: Trạng thái ("ChuaThanhToan", "DaThanhToan", "DaHuy").
  - `private List<ChiTietHoaDon> danhSachChiTiet`: Danh sách các món ăn trong hóa đơn.
- **Phương thức**:
  - `public double tinhTongTienGoc()`: Duyệt qua `danhSachChiTiet` tính tổng tiền trước giảm giá.
  - `public void themChiTiet(ChiTietHoaDon ct)`: Thêm món vào danh sách hóa đơn.
  - Các hàm Getters / Setters.

##### 4. Lớp `ChiTietHoaDon`
- **Mô tả**: Chi tiết từng món ăn trong một hóa đơn cụ thể.
- **Thuộc tính**:
  - `private int maCTHD`: Mã định danh bản ghi chi tiết.
  - `private int maHD`: Mã hóa đơn trực thuộc.
  - `private int maMon`: Mã món ăn được gọi.
  - `private String tenMon`: Tên món ăn.
  - `private int soLuong`: Số lượng phần gọi.
  - `private double donGia`: Đơn giá tại thời điểm gọi món.
  - `private String ghiChu`: Ghi chú chế biến của khách.
  - `private String trangThai`: Trạng thái bếp ("ChoNau", "DangLam", "DaRaMon", "DaHuy").
  - `private Timestamp thoiGianGoi`: Thời điểm bấm gọi món.
- **Phương thức**:
  - `public double getThanhTien()`: Tính thành tiền = `donGia * soLuong`.
  - `public long getSoPhutCho()`: Tính số phút chờ đợi từ lúc gọi đến hiện tại để cảnh báo bếp.
  - Các hàm Getters / Setters.

##### 5. Lớp `CaLamViec`
- **Mô tả**: Quản lý ca làm việc của thu ngân và đối soát két tiền mặt.
- **Thuộc tính**:
  - `private int maCa`: Mã ca làm việc.
  - `private int maNV`: Mã nhân viên trực ca.
  - `private String tenNV`: Họ tên nhân viên.
  - `private Timestamp thoiGianMo`: Thời điểm bắt đầu mở ca.
  - `private Timestamp thoiGianDong`: Thời điểm kết thúc bàn giao ca.
  - `private double tienDauCa`: Tiền mặt nhận bàn giao ban đầu trong két.
  - `private double doanhThuTienMat`: Tổng tiền mặt thu từ các hóa đơn trong ca.
  - `private double doanhThuChuyenKhoan`: Tổng tiền chuyển khoản/thẻ trong ca.
  - `private double tongDoanhThu`: DoanhThuTienMat + DoanhThuChuyenKhoan.
  - `private double tienLyThuyet`: Tiền mặt lý thuyết trong két = `tienDauCa + doanhThuTienMat`.
  - `private Double tienThucTe`: Số tiền mặt nhân viên thực tế đếm được khi đóng ca.
  - `private Double chenhLech`: Tiền thực tế - Tiền lý thuyết.
  - `private String trangThai`: "DangMo" hoặc "DaDong".
  - `private String ghiChu`: Giải trình nguyên nhân nếu két có chênh lệch.
  - `private int soHoaDon`: Tổng số hóa đơn đã thanh toán trong ca.
- **Phương thức**:
  - `public void tinhTienLyThuyet()`: Gán `tienLyThuyet = tienDauCa + doanhThuTienMat`.
  - `public boolean isDangMo()`: Kiểm tra ca có đang hoạt động hay không.
  - Các hàm Getters / Setters.

---

#### B. Nhóm Lớp Truy xuất Dữ liệu (DAO - Package `com.restaurant.dao`)

##### 1. Lớp `BanDAO` (Triển khai `IBaseDAO<Ban>`)
- **Thuộc tính**: Không có thuộc tính trạng thái; sử dụng biến cục bộ trong từng phương thức.
- **Phương thức**:
  - `public List<Ban> getAll()`: Truy vấn toàn bộ bàn trong bảng `Ban`.
  - `public boolean insert(Ban ban)`: Thêm bàn mới vào CSDL.
  - `public boolean update(Ban ban)`: Cập nhật thông tin bàn.
  - `public boolean delete(int maBan)`: Xóa bàn theo mã.
  - `public boolean updateStatus(int maBan, String status)`: Cập nhật nhanh trạng thái bàn thành "Trong" hoặc "CoKhach".

##### 2. Lớp `CaLamViecDAO`
- **Phương thức**:
  - `public CaLamViec getCaDangMo()`: Lấy ca đang mở gần nhất kèm cập nhật doanh thu tức thời.
  - `public CaLamViec getCaTruocGanNhat()`: Lấy thông tin ca đã đóng gần nhất để lấy số tiền bàn giao.
  - `public boolean moCa(CaLamViec ca)`: Mở ca làm việc mới, ghi nhận vào CSDL và trả về khóa tự tăng `MaCa`.
  - `public boolean dongCa(CaLamViec ca)`: Cập nhật giờ đóng, số tiền kiểm đếm, độ chênh lệch và giải trình.
  - `public List<HoaDon> getHoaDonByMaCa(int maCa)`: Lấy danh sách hóa đơn phát sinh trong một ca cụ thể.

##### 3. Lớp `ThongKeDAO`
- **Phương thức**:
  - `public Map<String, Object> getDashboardKPIs(String filter)`: Tổng hợp doanh thu, số đơn, tỷ lệ lấp đầy bàn, thời gian phục vụ TB theo bộ lọc.
  - `public int[] getFunnelPipelineData()`: Truy vấn mảng 5 chỉ số cho Biểu đồ Phễu Vận hành.
  - `public Map<String, Object> getRevenueTrendData(String filter)`: Truy vấn doanh thu 6 tháng từ View `v_DoanhThuTheoNgay` kèm mục tiêu kinh doanh.
  - `public List<Object[]> getTopMonBanChay(int topN)`: Đọc Top N món có doanh thu cao nhất từ View `v_TopMonBanChay`.

---

### 5.3. Hiện thực Tính Kế thừa (Inheritance) - Tối thiểu 3 lớp kế thừa

Đề tài thể hiện tính kế thừa sâu sắc và tái sử dụng mã nguồn tối ưu qua 3 kiến trúc kế thừa tiêu biểu:

#### Kế thừa 1: Khung sườn Quản trị Cơ sở `BaseCrudPanel` (Tái sử dụng cho 5 lớp con)
- **Lớp cha**: `public abstract class BaseCrudPanel extends JPanel`
  - Đóng gói toàn bộ các thành phần chung của một màn hình CRUD: Thanh tiêu đề, Thanh công cụ (Thêm, Sửa, Xóa, Làm mới), Thanh tìm kiếm với biểu tượng kính lúp, Bảng hiển thị `ModernTable`, Phân trang và Bố cục `BorderLayout`.
  - Khai báo các phương thức trừu tượng:
    - `protected abstract String[] getColumnNames();`
    - `protected abstract void loadData();`
    - `protected abstract void onAdd();`
    - `protected abstract void onEdit();`
    - `protected abstract void onDelete();`
    - `protected abstract void onSearch(String keyword);`
- **Các lớp con kế thừa**:
  1. `public class UserManagementPanel extends BaseCrudPanel`: Quản lý tài khoản nhân sự và phân quyền RBAC.
  2. `public class MenuManagementPanel extends BaseCrudPanel`: Quản lý danh sách thực đơn món ăn và giá bán.
  3. `public class TableManagementPanel extends BaseCrudPanel`: Quản lý danh mục bàn ăn và khu vực.
  4. `public class CategoryManagementPanel extends BaseCrudPanel`: Quản lý danh mục thực đơn.
  5. `public class VoucherManagementPanel extends BaseCrudPanel`: Quản lý mã khuyến mãi và chiết khấu.

#### Kế thừa 2: Thẻ Chỉ số `KpiCard` kế thừa `RoundedPanel` kế thừa `JPanel`
- **Lớp cha**: `public class RoundedPanel extends JPanel`
  - Kế thừa `JPanel`, ghi đè (Override) phương thức `paintComponent(Graphics g)` để vẽ panel có bo góc tròn mềm mại (Corner Radius), đổ màu nền và viền chống răng cưa (`RenderingHints.VALUE_ANTIALIAS_ON`).
- **Lớp con**: `public class KpiCard extends RoundedPanel`
  - Kế thừa trực tiếp từ `RoundedPanel`, bổ sung các thành phần chuyên biệt của thẻ chỉ số: Biểu tượng tròn trên nền Pastel (`iconBadge`), Tiêu đề KPI, Giá trị số to nổi bật (Font Segoe UI 22pt Bold), và Pill Badge bo góc tròn hiển thị tỷ lệ tăng trưởng (`+12.4% MoM`).

#### Kế thừa 3: Bảng dữ liệu hiện đại `ModernTable` kế thừa `JTable`
- **Lớp cha**: `javax.swing.JTable`
- **Lớp con**: `public class ModernTable extends JTable`
  - Ghi đè phương thức khởi tạo và cấu hình: Tự động đổi chiều cao dòng (`setRowHeight(36)`), bỏ đường lưới thô, cài đặt Renderer tùy biến với màu xen kẽ (`Zebra striping`), tùy biến `JTableHeader` với nền tối Navy sang trọng (`#0F172A`) và chữ trắng, hỗ trợ canh giữa/phải tự động cho các cột số tiền tệ.

---

### 5.4. Mô tả chi tiết phương thức hoạt động trên 6 màn hình giao diện chính

Theo yêu cầu đề bài, dưới đây là mô tả phương thức cụ thể trên 6 giao diện chính của hệ thống:

#### 1. Giao diện Sơ đồ bàn (`TableMapPanel`)
- `public void loadTables(String khuVuc)`: Truy vấn danh sách bàn từ `BanDAO`, duyệt qua từng bàn và khởi tạo component `TableCardPanel` tương ứng, gán màu Xanh (Trống) hoặc Đỏ (Có khách).
- `private void handleTableClick(Ban ban)`: Bắt sự kiện nhấp chuột vào bàn ăn. Nếu bàn trống sẽ hiển thị Dialog xác nhận mở bàn và gọi `mainFrame.navigateToOrder(maBan)`. Nếu bàn có khách sẽ hiện Menu tùy chọn xem Order hoặc chuyển sang Thu ngân.
- `private void filterByArea(String areaName)`: Xử lý sự kiện khi chọn ComboBox lọc khu vực (Tầng 1, Tầng 2, VIP).

#### 2. Giao diện Gọi món (`OrderPanel`)
- `public void loadOrderForTable(int maBan)`: Nhận mã bàn từ Sơ đồ bàn, nạp thông tin hóa đơn chưa thanh toán của bàn đó lên bảng hiển thị chi tiết bên trái.
- `private void addDishToOrder(MonAn mon)`: Xử lý khi nhấn vào thẻ món ăn: nếu món đã có trong bảng sẽ tăng số lượng lên 1, nếu chưa có sẽ tạo dòng chi tiết mới.
- `private void updateQuantity(int maCTHD, int delta)`: Tăng hoặc giảm số lượng món ăn; nếu số lượng giảm về 0 sẽ hỏi xác nhận xóa món.
- `private void handleTableTransfer()`: Mở hộp thoại chọn bàn đích còn trống, gọi phương thức chuyển bàn trong DAO và làm mới lại sơ đồ bàn.
- `private void sendToKitchen()`: Đổi trạng thái tất cả các món mới thêm thành `ChoNau` và cập nhật thông báo gửi bếp thành công.

#### 3. Giao diện Thu ngân (`CheckoutPanel`)
- `public void selectOccupiedTable(int maBan)`: Nạp toàn bộ thông tin hóa đơn của bàn được chọn lên màn hình thanh toán.
- `private void applyVoucher()`: Lấy mã nhập trong ô text, gọi `VoucherDAO.getByCode()`, kiểm tra hạn dùng và điều kiện đơn hàng, tính số tiền chiết khấu và cập nhật lại giao diện.
- `private void recalculateTotals()`: Tính toán lại Tiền món, Tiền giảm giá, Thuế VAT theo radio button được chọn (0%, 8%, 10%) và hiển thị Tổng thanh toán cuối cùng.
- `private void handlePrintReceipt()`: Kết xuất nội dung hóa đơn định dạng K80 và hiển thị hộp thoại xem trước/in ấn.
- `private void handleConfirmPayment()`: Kiểm tra ca làm việc hiện tại, gọi `HoaDonDAO.thanhToan()` cập nhật hóa đơn thành `DaThanhToan`, giải phóng bàn ăn về `Trong` và làm mới Dashboard.

#### 4. Giao diện Điều hành Bếp (`KitchenManagementPanel`)
- `public void loadKitchenOrders()`: Nạp danh sách tất cả các món ăn đang ở trạng thái `ChoNau`, `DangLam`, `DaRaMon` lên bảng `ModernTable`.
- `private void batchUpdateStatus(String newStatus)`: Lấy toàn bộ các dòng đang được bôi đen (multi-selected) trên bảng, gọi DAO cập nhật hàng loạt sang trạng thái mới (`DangLam` hoặc `DaRaMon`).
- `private void handleCancelDish()`: Bếp báo hủy món được chọn kèm lý do, tự động trừ tiền món tương ứng trong hóa đơn của khách.
- `private void toggleAutoRefresh(boolean enable)`: Bật hoặc tắt `javax.swing.Timer` chu kỳ 10 giây tự động nạp lại dữ liệu bếp.

#### 5. Giao diện Quản lý Ca làm việc (`ShiftManagementPanel`)
- `public void loadData()`: Nạp thông tin ca làm việc đang mở lên Banner phía trên (Mã ca, Thu ngân trực, Tiền đầu ca, Doanh thu tiền mặt, Tiền mặt lý thuyết trong két) và nạp bảng lịch sử các ca đã đóng.
- `private void openNewShift()`: Mở hộp thoại `OpenShiftDialog`, tự động lấy tiền thực tế của ca trước làm tiền đầu ca mới và lưu ca mới vào CSDL.
- `private void closeCurrentShift()`: Mở hộp thoại `CloseShiftDialog`, hiển thị chi tiết doanh thu và tiền lý thuyết, tiếp nhận số tiền thực tế kiểm đếm, tính chênh lệch và hoàn tất bàn giao ca.
- `private void printShiftReport(CaLamViec ca)`: Xuất văn bản biên bản bàn giao ca chi tiết phục vụ lưu trữ kế toán.

#### 6. Giao diện Dashboard Tổng quan (`DashboardPanel`)
- `public void loadRealData()`: Phương thức cốt lõi kết nối với `ThongKeDAO`:
  - Đọc và cập nhật số liệu cho 4 thẻ KPI (`cardRevenue`, `cardOrders`, `cardOccupancy`, `cardTableTurn`).
  - Đọc dữ liệu vận hành và gọi `funnelChartPanel.updateData(...)`.
  - Đọc dữ liệu xu hướng doanh thu và gọi `revenueChartPanel.updateChartData(...)`.
  - Đọc Top 7 món ăn bán chạy nhất nạp vào bảng `topDishesTable`.
- `public void refreshData()`: Gọi lại `loadRealData()` khi người dùng bấm nút `🔄 Làm mới` hoặc khi chuyển tab từ các phân hệ khác về Dashboard.

---

# 6. THIẾT KẾ GIAO DIỆN (INTERFACE DESIGN)

---

### 6.1. Thiết kế Giao diện Lập trình Hướng đối tượng (Tối thiểu 5 Java Interfaces)

Dưới đây là 6 Interface được thiết kế chuẩn mực trong mã nguồn (gói `com.restaurant.dao` và `com.restaurant.service`), đáp ứng đầy đủ tính trừu tượng (Abstraction) và nguyên lý SOLID trong thiết kế phần mềm:

```
                          ┌────────────────────────┐
                          │     «interface»        │
                          │     IBaseDAO<T>        │
                          └───────────┬────────────┘
                                      │
              ┌───────────────────────┴───────────────────────┐
              ▼                                               ▼
       ┌──────────────┐                                ┌──────────────┐
       │    BanDAO    │                                │  DanhMucDAO  │
       └──────────────┘                                └──────────────┘

┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐   ┌───────────────────┐
│   «interface»   │   │   «interface»   │   │   «interface»   │   │   «interface»   │   │    «interface»    │
│  IOrderService  │   │ IPaymentService │   │  IShiftService  │   │ IKitchenService │   │ IPrintableReceipt │
└─────────────────┘   └─────────────────┘   └─────────────────┘   └─────────────────┘   └───────────────────┘
```

#### Interface 1: `IBaseDAO<T>`
- **Package**: `com.restaurant.dao`
- **Mục đích**: Định nghĩa các thao tác CRUD cơ sở cho toàn bộ các lớp DAO truy xuất cơ sở dữ liệu.
- **Danh sách phương thức**:
  1. `List<T> getAll()`: Lấy toàn bộ danh sách bản ghi thực thể từ bảng tương ứng trong CSDL.
  2. `boolean insert(T entity)`: Thêm một đối tượng mới vào CSDL, tự động gán khóa chính tự tăng (nếu có).
  3. `boolean update(T entity)`: Cập nhật thông tin đối tượng đã tồn tại dựa theo khóa chính.
  4. `boolean delete(int id)`: Xóa một bản ghi trong CSDL dựa theo mã định danh ID.
- **Lớp hiện thực (Implementing Classes)**: `BanDAO implements IBaseDAO<Ban>`, `DanhMucDAO implements IBaseDAO<DanhMuc>`, `NhanVienDAO implements IBaseDAO<NhanVien>`, `MonAnDAO implements IBaseDAO<MonAn>`.

#### Interface 2: `IOrderService`
- **Package**: `com.restaurant.service`
- **Mục đích**: Định nghĩa các nghiệp vụ gọi món, quản lý chi tiết hóa đơn và chuyển bàn ăn.
- **Danh sách phương thức**:
  1. `HoaDon moBanVaTaoHoaDon(int maBan, int maNV)`: Khởi tạo hóa đơn mới khi mở bàn ăn.
  2. `boolean themMonVaoBan(int maBan, int maMon, int soLuong, String ghiChu)`: Thêm món ăn kèm số lượng và ghi chú chế biến vào bàn.
  3. `boolean capNhatSoLuongMon(int maCTHD, int soLuongMoi)`: Thay đổi số lượng món ăn trong order.
  4. `boolean huyMonKhoiBan(int maCTHD, String lyDo)`: Xóa/Hủy món ăn khỏi bàn kèm lý do giải trình.
  5. `boolean chuyenBan(int maBanCu, int maBanMoi)`: Chuyển toàn bộ món ăn và hóa đơn từ bàn cũ sang bàn mới.
  6. `List<ChiTietHoaDon> getOrderCuaBan(int maBan)`: Lấy danh sách các món ăn đang phục vụ tại bàn.

#### Interface 3: `IPaymentService`
- **Package**: `com.restaurant.service`
- **Mục đích**: Định nghĩa các nghiệp vụ tính toán chi phí, xác thực chiết khấu voucher, VAT và chốt thanh toán.
- **Danh sách phương thức**:
  1. `Voucher xacThucVaApDungVoucher(String code, double tongTienGoc)`: Kiểm tra tính hợp lệ của mã khuyến mãi dựa trên thời hạn và giá trị đơn hàng tối thiểu.
  2. `Map<String, Double> tinhToanChiPhiHoaDon(HoaDon hoaDon, Voucher voucher, double vatPercent)`: Tính toán đa tầng: Tiền món gốc, Tiền giảm giá, Tiền thuế VAT và Tổng thanh toán.
  3. `boolean thanhToanHoaDon(int maBan, String phuongThuc, double tienVoucher, double vatPercent)`: Hoàn tất thanh toán, cập nhật trạng thái hóa đơn, giải phóng bàn ăn và cộng dồn doanh thu vào ca làm việc.
  4. `int tachHoaDon(int maBan, List<Integer> dsMaCTHD)`: Tách một phần món ăn sang hóa đơn phụ độc lập.

#### Interface 4: `IShiftService`
- **Package**: `com.restaurant.service`
- **Mục đích**: Định nghĩa các quy trình quản lý ca thu ngân, bàn giao số dư két tiền và kiểm soát chênh lệch.
- **Danh sách phương thức**:
  1. `CaLamViec getCaDangMo()`: Truy vấn ca làm việc đang hoạt động gần nhất.
  2. `CaLamViec getCaTruocGanNhat()`: Lấy thông tin ca đã đóng gần nhất để lấy số tiền bàn giao cho ca sau.
  3. `boolean moCa(int maNV, double tienDauCa, String ghiChu)`: Khởi tạo ca trực mới với số tiền mặt đầu ca.
  4. `boolean dongCa(CaLamViec ca)`: Đóng ca trực, ghi nhận số tiền kiểm đếm thực tế và cập nhật doanh thu.
  5. `double tinhChenhLechKetTien(double tienLyThuyet, double tienThucTe)`: Tính toán số tiền thừa/thiếu giữa kiểm đếm và lý thuyết.
  6. `List<CaLamViec> getLichSuCaLamViec()`: Lấy lịch sử toàn bộ các ca làm việc phục vụ đối soát.

#### Interface 5: `IKitchenService`
- **Package**: `com.restaurant.service`
- **Mục đích**: Định nghĩa các thao tác điều phối món ăn tại nhà bếp, chuyển trạng thái chế biến và quản lý tình trạng tồn kho món.
- **Danh sách phương thức**:
  1. `List<ChiTietHoaDon> getDanhSachMonChoBep()`: Lấy danh sách toàn bộ các món cần chế biến theo thứ tự thời gian gọi.
  2. `int chuyenTrangThaiDangNau(List<Integer> dsMaCTHD)`: Cập nhật hàng loạt các món được chọn sang trạng thái "Đang nấu".
  3. `int chuyenTrangThaiDaRaMon(List<Integer> dsMaCTHD)`: Cập nhật hàng loạt các món hoàn thành sang trạng thái "Đã ra món".
  4. `boolean baoHuyMonBep(int maCTHD, String lyDo)`: Bếp báo hủy món ăn do sự cố hoặc hết nguyên liệu.
  5. `boolean capNhatTinhTrangMon(int maMon, boolean conMon)`: Báo hết hàng hoặc mở bán lại cho một món ăn trong thực đơn.
  6. `List<MonAn> getThucDonKemTrangThai()`: Lấy toàn bộ thực đơn kèm cờ trạng thái còn/hết hàng để bếp giám sát.

#### Interface 6: `IPrintableReceipt`
- **Package**: `com.restaurant.service`
- **Mục đích**: Định nghĩa việc định dạng và kết xuất văn bản in ấn hóa đơn nhiệt K80 và biên bản bàn giao ca.
- **Danh sách phương thức**:
  1. `String taoNoiDungHoaDonK80(HoaDon hoaDon, String tenNhaHang, String diaChi, String hotline)`: Tạo chuỗi hóa đơn nhiệt K80 có tiêu đề, ngày giờ, bảng món, tổng tiền, VAT, voucher và lời cảm ơn.
  2. `String taoBienBanBanGiaoCa(CaLamViec ca)`: Tạo văn bản biên bản bàn giao ca làm việc chi tiết với số tiền két, chênh lệch và vị trí ký nhận.
  3. `boolean inRaMayIn(String noiDungIn)`: Gửi trực tiếp lệnh in ra máy in hệ thống.

---

### 6.2. Thiết kế Giao diện Đồ họa Người dùng (GUI Components & Event Driven)

Toàn bộ hệ thống giao diện được thiết kế nhất quán theo bộ nhận diện thương hiệu hiện đại:

| Màn hình | Layout Manager | Các thành phần UI chính | Sự kiện (Event Listeners) |
| :--- | :--- | :--- | :--- |
| **`LoginDialog`** | `BorderLayout` + `GridBagLayout` | Logo nhà hàng, `JTextField` username, `JPasswordField` password, Đèn báo kết nối MySQL `JLabel`, nút `JButton` Đăng nhập. | `ActionListener` (bấm nút hoặc Enter), `WindowListener`. |
| **`MainFrame`** | `BorderLayout` | Header (Logo, Tên nhà hàng, Trạng thái CSDL, Tài khoản đăng nhập), Sidebar menu dọc (10 mục), Vùng trung tâm quản lý bởi `CardLayout`. | `MouseListener` (hiệu ứng hover & active menu sidebar), sự kiện chuyển Card. |
| **`DashboardPanel`** | `BorderLayout` trong `JScrollPane` | Hàng 4 thẻ `KpiCard`, Biểu đồ `FunnelChartPanel`, Biểu đồ `RevenueBarLineChartPanel`, Bảng `ModernTable` Top món bán chạy, `JComboBox` bộ lọc thời gian, nút `JButton` Làm mới. | `ActionListener` trên ComboBox và nút Làm mới, `MouseMotionListener` trên biểu đồ hiển thị Tooltip tương tác. |
| **`TableMapPanel`** | `BorderLayout` | Lưới `GridLayout(4, 5)` chứa 20 thẻ `RoundedPanel` bàn ăn, Chú thích màu sắc (Xanh: Trống, Đỏ: Có khách), ComboBox lọc khu vực. | `MouseListener` trên từng thẻ bàn để mở popup hành động. |
| **`OrderPanel`** | `JSplitPane` (Trái 45% / Phải 55%) | Trái: Bảng món bàn đang gọi `ModernTable`, tổng tiền tạm tính, nút Chuyển bàn, nút Gửi bếp.<br>Phải: Tabbed danh mục món ăn, ô tìm kiếm món, lưới các nút món ăn. | `DocumentListener` trên ô tìm kiếm (lọc tức thì), `ActionListener` tăng giảm số lượng, thêm món. |
| **`CheckoutPanel`** | `JSplitPane` (Trái 60% / Phải 40%) | Trái: Danh sách bàn có khách và chi tiết món ăn.<br>Phải: Khung tính tiền (Tiền gốc, Voucher, VAT radio buttons, Phương thức thanh toán), nút In Hóa Đơn, nút Thanh Toán, nút Tách Bill. | `ActionListener` áp dụng voucher, thay đổi VAT, chọn bàn, xác nhận thanh toán. |
| **`KitchenManagementPanel`** | `JTabbedPane` (2 Tabs) | Tab 1: 4 thẻ KPI hàng chờ bếp, Bảng món đang chờ `ModernTable` hỗ trợ chọn nhiều dòng (`MULTIPLE_INTERVAL_SELECTION`), nút Bắt đầu nấu, nút Báo ra món, nút Hủy món.<br>Tab 2: Danh sách thực đơn kho kèm nút Báo Hết Hàng / Mở Bán Lại. | `ActionListener` trên các nút tác vụ bếp, `Timer` tự động quét 10s. |
| **`ShiftManagementPanel`** | `BorderLayout` | Banner ca làm việc hiện tại (Số tiền lý thuyết két, giờ mở ca), Nút Mở ca / Kết ca, Bảng lịch sử ca `ModernTable`, Hộp thoại `OpenShiftDialog`, `CloseShiftDialog`. | `ActionListener` mở/đóng ca, tính toán chênh lệch tự động khi người dùng nhập số tiền thực tế vào ô text (`DocumentListener`). |

---

# 7. KẾT LUẬN & HƯỚNG PHÁT TRIỂN

### 7.1. Kết quả đạt được
1. **Hoàn thành 100% các mục tiêu đề tài**: Xây dựng thành công hệ thống Quản lý Vận hành Nhà hàng Đa phân hệ (**RESTAURANT OPS**) với kiến trúc mã nguồn sạch sẽ, tổ chức theo mô hình chuẩn MVC kết hợp DAO và Services.
2. **Đáp ứng toàn diện các tiêu chí học thuật và kỹ thuật**:
   - Tối thiểu 5 Interface Java: Đã thiết kế và cài đặt **6 Interface** (`IBaseDAO`, `IOrderService`, `IPaymentService`, `IShiftService`, `IKitchenService`, `IPrintableReceipt`).
   - Tối thiểu 3 lớp kế thừa: Đã hiện thực **7 lớp kế thừa** tiêu biểu (`BaseCrudPanel` với 5 màn hình CRUD con, `KpiCard` kế thừa `RoundedPanel`, `ModernTable` kế thừa `JTable`).
   - Tối thiểu 5 giao diện/màn hình: Đã xây dựng và đặc tả chi tiết **8 màn hình nghiệp vụ chính**.
3. **Độ tin cậy và chất lượng phần mềm**:
   - Toàn bộ **8/8 kịch bản kiểm thử tự động (JUnit 5)** đều chạy thành công (`BUILD SUCCESS`).
   - Kết nối MySQL 8.0 ổn định, có cơ chế tự động chuyển sang bộ đệm dữ liệu ngoại tuyến (Offline Caching) bảo đảm không gián đoạn kinh doanh.

### 7.2. Hướng phát triển trong tương lai
- **Tích hợp Cổng thanh toán trực tuyến**: Kết nối trực tiếp API VietQR / VNPay / MoMo để tự động nhận diện thanh toán thành công qua Webhook ngân hàng mà thu ngân không cần kiểm tra thủ công.
- **Ứng dụng Web / Mobile cho Khách hàng (Self-ordering)**: Mở rộng quét mã QR tại bàn để thực khách tự gọi món trên điện thoại thông minh, đồng bộ trực tiếp vào hệ thống Desktop của nhà hàng.
- **Trí tuệ nhân tạo (AI Forecasting)**: Áp dụng thuật toán máy học dự báo nhu cầu nguyên vật liệu và doanh thu theo mùa vụ dựa trên dữ liệu lịch sử hóa đơn.

---
*Báo cáo được hoàn thành tại dự án **RESTAURANT OPS** - Đại học / Đề tài Bài tập lớn Java.*
