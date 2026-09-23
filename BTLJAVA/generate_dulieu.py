# -*- coding: utf-8 -*-
"""
Script sinh file dulieu.sql chuẩn hóa cho CSDL restaurant_db.
Đảm bảo:
- Bảng NhanVien: 15 bản ghi (10-20 dữ liệu)
- Bảng Ban: 20 bản ghi (10-20 dữ liệu)
- Các bảng còn lại: mỗi bảng ít nhất 100 bản ghi:
    + DanhMuc: 100 bản ghi
    + MonAn: 120 bản ghi
    + Voucher: 105 bản ghi
    + CaLamViec: 105 bản ghi
    + HoaDon: 130 bản ghi
    + ChiTietHoaDon: 350+ bản ghi
    + LichSuTruyCap: 120 bản ghi
    + LichSuThaoTac: 125 bản ghi
"""
import random
from datetime import datetime, timedelta

def main():
    out_file = "d:/BTLJAVA/dulieu.sql"
    
    lines = []
    
    lines.append("-- =============================================================================")
    lines.append("-- FILE DỮ LIỆU MẪU TOÀN DIỆN CHO CSDL RESTAURANT_DB")
    lines.append("-- Sinh tự động, chuẩn hóa dữ liệu thực tế nhà hàng ẩm thực")
    lines.append("-- Bảng NhanVien: 15 bản ghi (10 - 20 dữ liệu)")
    lines.append("-- Bảng Ban: 20 bản ghi (10 - 20 dữ liệu)")
    lines.append("-- Các bảng còn lại: Đều có ít nhất 100 bản ghi")
    lines.append("-- =============================================================================")
    lines.append("")
    lines.append("USE `restaurant_db`;")
    lines.append("")
    lines.append("SET FOREIGN_KEY_CHECKS = 0;")
    lines.append("")
    lines.append("-- Xóa dữ liệu cũ theo đúng thứ tự")
    lines.append("TRUNCATE TABLE `ChiTietHoaDon`;")
    lines.append("TRUNCATE TABLE `HoaDon`;")
    lines.append("TRUNCATE TABLE `CaLamViec`;")
    lines.append("TRUNCATE TABLE `Voucher`;")
    lines.append("TRUNCATE TABLE `MonAn`;")
    lines.append("TRUNCATE TABLE `DanhMuc`;")
    lines.append("TRUNCATE TABLE `Ban`;")
    lines.append("TRUNCATE TABLE `NhanVien`;")
    lines.append("TRUNCATE TABLE `LichSuTruyCap`;")
    lines.append("TRUNCATE TABLE `LichSuThaoTac`;")
    lines.append("")
    lines.append("SET FOREIGN_KEY_CHECKS = 1;")
    lines.append("")

    # -------------------------------------------------------------------------
    # 1. BẢNG NHÂN VIÊN (15 bản ghi)
    # -------------------------------------------------------------------------
    lines.append("-- =============================================================================")
    lines.append("-- 1. BẢNG NHÂN VIÊN (NhanVien) - 15 bản ghi (Phân quyền Admin, QuanLy, ThuNgan, PhucVu, Bep)")
    lines.append("-- =============================================================================")
    lines.append("INSERT INTO `NhanVien` (`MaNV`, `HoTen`, `TenDangNhap`, `MatKhau`, `SoDienThoai`, `VaiTro`, `TrangThai`, `NgayTao`) VALUES")
    
    nhan_vien_list = [
        (1, "Quản trị viên Hệ thống", "admin", "admin123", "0901234567", "Admin", "DangLam", "2026-01-01 08:00:00"),
        (2, "Trần Thị Mai", "cashier01", "mai123", "0912345678", "ThuNgan", "DangLam", "2026-01-02 08:00:00"),
        (3, "Lê Hoàng Nam", "waiter01", "nam123", "0923456789", "PhucVu", "DangLam", "2026-01-02 08:30:00"),
        (4, "Phạm Quốc Tuấn", "chef01", "tuan123", "0934567890", "Bep", "DangLam", "2026-01-03 09:00:00"),
        (5, "Đỗ Kim Ngân", "cashier02", "ngan123", "0945678901", "ThuNgan", "DangLam", "2026-01-05 08:00:00"),
        (6, "Vũ Đức Thịnh", "waiter02", "thinh123", "0956789012", "PhucVu", "NghiViec", "2026-01-05 08:30:00"),
        (7, "Nguyễn Văn Quản Lý", "quanly", "quanly123", "0901234568", "QuanLy", "DangLam", "2026-01-06 08:00:00"),
        (8, "Nguyễn Phương Thảo (Admin)", "admin02", "admin123", "0987654321", "Admin", "DangLam", "2026-01-06 09:00:00"),
        (9, "Hoàng Minh Trí", "cashier03", "tri123", "0967890123", "ThuNgan", "DangLam", "2026-01-10 08:00:00"),
        (10, "Bùi Thị Ánh", "waiter03", "anh123", "0978901234", "PhucVu", "DangLam", "2026-01-10 08:30:00"),
        (11, "Đặng Văn Lâm", "chef02", "lam123", "0989012345", "Bep", "DangLam", "2026-01-12 09:00:00"),
        (12, "Ngô Bảo Châu", "waiter04", "chau123", "0990123456", "PhucVu", "DangLam", "2026-01-15 08:30:00"),
        (13, "Lý Gia Hân", "waiter05", "han123", "0909876543", "PhucVu", "DangLam", "2026-01-18 08:30:00"),
        (14, "Trịnh Đình Quang", "chef03", "quang123", "0918765432", "Bep", "DangLam", "2026-01-20 09:00:00"),
        (15, "Phan Hải Yến", "quanly02", "yen123", "0927654321", "QuanLy", "DangLam", "2026-01-22 08:00:00")
    ]
    
    nv_sql = []
    for nv in nhan_vien_list:
        nv_sql.append(f"({nv[0]}, '{nv[1]}', '{nv[2]}', '{nv[3]}', '{nv[4]}', '{nv[5]}', '{nv[6]}', '{nv[7]}')")
    lines.append(",\n".join(nv_sql) + ";\n")

    # -------------------------------------------------------------------------
    # 2. BẢNG BÀN ĂN (20 bản ghi)
    # -------------------------------------------------------------------------
    lines.append("-- =============================================================================")
    lines.append("-- 2. BẢNG BÀN ĂN (Ban) - 20 bản ghi (Tầng 1: 10 bàn, Tầng 2: 6 bàn, Phòng VIP: 4 bàn)")
    lines.append("-- =============================================================================")
    lines.append("INSERT INTO `Ban` (`MaBan`, `TenBan`, `KhuVuc`, `SucChua`, `TrangThai`, `MaNVPhuTrach`) VALUES")
    
    ban_list = [
        (1, "Bàn 1", "Tầng 1", 4, "Trong", "NULL"),
        (2, "Bàn 2", "Tầng 1", 4, "Trong", "NULL"),
        (3, "Bàn 3", "Tầng 1", 6, "Trong", "NULL"),
        (4, "Bàn 4", "Tầng 1", 4, "CoKhach", "3"),
        (5, "Bàn 5", "Tầng 1", 8, "CoKhach", "3"),
        (6, "Bàn 6", "Tầng 1", 6, "Trong", "NULL"),
        (7, "Bàn 7", "Tầng 1", 4, "Trong", "NULL"),
        (8, "Bàn 8", "Tầng 1", 4, "Trong", "NULL"),
        (9, "Bàn 9", "Tầng 1", 6, "CoKhach", "3"),
        (10, "Bàn 10", "Tầng 1", 8, "CoKhach", "3"),
        (11, "Bàn 11", "Tầng 2", 4, "Trong", "NULL"),
        (12, "Bàn 12", "Tầng 2", 6, "Trong", "NULL"),
        (13, "Bàn 13", "Tầng 2", 4, "Trong", "NULL"),
        (14, "Bàn 14", "Tầng 2", 4, "CoKhach", "10"),
        (15, "Bàn 15", "Tầng 2", 6, "CoKhach", "10"),
        (16, "Bàn 16", "Tầng 2", 4, "Trong", "NULL"),
        (17, "Bàn 17", "Phòng VIP", 4, "Trong", "NULL"),
        (18, "Bàn 18", "Phòng VIP", 6, "Trong", "NULL"),
        (19, "Bàn 19", "Phòng VIP", 4, "Trong", "NULL"),
        (20, "Bàn 20", "Phòng VIP", 8, "Trong", "NULL")
    ]
    
    ban_sql = []
    for b in ban_list:
        ban_sql.append(f"({b[0]}, '{b[1]}', '{b[2]}', {b[3]}, '{b[4]}', {b[5]})")
    lines.append(",\n".join(ban_sql) + ";\n")

    # -------------------------------------------------------------------------
    # 3. BẢNG DANH MỤC (100 bản ghi)
    # -------------------------------------------------------------------------
    lines.append("-- =============================================================================")
    lines.append("-- 3. BẢNG DANH MỤC MÓN ĂN (DanhMuc) - 100 bản ghi")
    lines.append("-- =============================================================================")
    lines.append("INSERT INTO `DanhMuc` (`MaDM`, `TenDM`, `MoTa`) VALUES")
    
    # 5 danh mục gốc + 95 danh mục chi tiết đa dạng
    danh_muc_base = [
        (1, "Khai vị", "Các món nhẹ mở đầu bữa tiệc: salad, súp, khai vị"),
        (2, "Món chính", "Bò bít tết Wagyu, sườn nướng BBQ, cừu nướng"),
        (3, "Hải sản", "Tôm hùm, cua sốt ớt, cá hồi cao cấp tươi sống"),
        (4, "Đồ uống", "Rượu vang đỏ cao cấp, cocktail, trà, nước hoa quả"),
        (5, "Tráng miệng", "Bánh ngọt phong cách Pháp, kem tươi, chè tráng miệng")
    ]
    
    danh_muc_names = [
        "Súp & Cháo thượng hạng", "Salad & Gỏi tươi", "Món nhắm bia", "Khai vị phương Tây", "Khai vị Á Đông",
        "Bò Wagyu & Kobe", "Bò Black Angus Mỹ", "Bò Úc thượng hạng", "Thịt cừu New Zealand", "Sườn cừu đút lò",
        "Heo Iberico Tây Ban Nha", "Sườn heo BBQ", "Thịt heo nướng than hoa", "Gà ta thả vườn", "Vịt quay Bắc Kinh",
        "Bồ câu quay da giòn", "Tôm hùm Alaska", "Tôm hùm bông Nha Trang", "Tôm sú sốt bơ tỏi", "Tôm càng xanh",
        "Cua hoàng đế King Crab", "Cua Cà Mau sốt me", "Cua sốt tiêu đen", "Ghẹ hấp bia sả", "Cá hồi Na Uy",
        "Cá tuyết áp chảo", "Cá mú hấp xì dầu", "Cá lăng nướng muối ớt", "Mực ống nhảy nướng", "Bạch tuộc nướng sa tế",
        "Bào ngư Úc hầm nấm", "Hải sâm hầm thuốc bắc", "Ốc hương xào bơ bắp", "Sò điệp Nhật nướng phô mai", "Hàu sữa Pháp ăn sống",
        "Hàu nướng mỡ hành", "Ngao hoa hấp thái", "Lẩu nấm thiên nhiên", "Lẩu Thái Tomyum", "Lẩu hải sản chua cay",
        "Lẩu riêu cua bắp bò", "Lẩu gà lá é Phú Yên", "Lẩu cá tầm Sapa", "Lẩu cừu Mông Cổ", "Lẩu Tứ Xuyên cay nồng",
        "Lẩu Miso Nhật Bản", "Đồ nướng than hoa", "Xiên que tổng hợp BBQ", "Cơm rang ngọc bích", "Cơm chiên hải sản hoàng kim",
        "Cơm niêu truyền thống", "Mì Ý Carbonara", "Mì Ý sốt bò băm Bolognese", "Mì xào giòn hải sản", "Miến xào cua bể",
        "Phở cuốn Hà Nội", "Bún chả nướng than hoa", "Món chay thực dưỡng", "Món chay thanh tịnh", "Rau xào tỏi & dầu hào",
        "Rau luộc kho quẹt", "Rượu vang đỏ Bordeaux", "Rượu vang đỏ Ý", "Rượu vang trắng Chile", "Rượu Champagne Pháp",
        "Rượu Whisky Scotland", "Bia thủ công Craft Beer", "Bia tươi nhập khẩu", "Cocktail nhiệt đới", "Mocktail trái cây",
        "Nước ép cam nguyên chất", "Nước ép dưa hấu", "Nước ép táo & cần tây", "Sinh tố bơ sáp", "Sinh tố xoài cát",
        "Trà hoa cúc mật ong", "Trà đào cam sả", "Trà sen vàng", "Trà ô long sữa", "Cà phê Espresso",
        "Cà phê Cappuccino", "Cà phê pha phin truyền thống", "Cà phê cốt dừa", "Nước khoáng có ga Perrier", "Nước ngọt đóng lon",
        "Bánh Tiramisu truyền thống", "Bánh Mousse chanh leo", "Bánh phô mai nướng Cheesecake", "Bánh kem socola dung nham", "Panna Cotta dâu tây",
        "Kem Gelato Ý", "Kem dừa non Côn Đảo", "Chè khúc bạch thanh mát", "Chè sen long nhãn", "Hoa quả tươi theo mùa"
    ]
    
    danh_muc_all = list(danh_muc_base)
    for idx, name in enumerate(danh_muc_names, start=6):
        danh_muc_all.append((idx, name, f"Danh mục thực đơn {name} chuẩn chất lượng cao"))
    
    dm_sql = []
    for dm in danh_muc_all:
        dm_sql.append(f"({dm[0]}, '{dm[1]}', '{dm[2]}')")
    lines.append(",\n".join(dm_sql) + ";\n")

    # -------------------------------------------------------------------------
    # 4. BẢNG MÓN ĂN (120 bản ghi)
    # -------------------------------------------------------------------------
    lines.append("-- =============================================================================")
    lines.append("-- 4. BẢNG MÓN ĂN (MonAn) - 120 bản ghi (Gồm các món cố định để bảo đảm Test Suite)")
    lines.append("-- =============================================================================")
    lines.append("INSERT INTO `MonAn` (`MaMon`, `TenMon`, `MaDM`, `DonGia`, `HinhAnh`, `TrangThai`) VALUES")
    
    # 14 món ăn cốt lõi chuẩn (được test suite và demo sử dụng)
    core_mon_an = [
        (101, "Gỏi ngó sen tôm thịt", 1, 125000.00, "salad.png", "ConMon"),
        (102, "Súp vi cá bào ngư", 1, 245000.00, "soup.png", "ConMon"),
        (103, "Khoai tây chiên phô mai", 1, 65000.00, "fries.png", "ConMon"),
        (201, "Bò bít tết Wagyu sốt tiêu", 2, 495000.00, "steak.png", "ConMon"),
        (202, "Sườn heo nướng BBQ", 2, 320000.00, "ribs.png", "ConMon"),
        (203, "Cừu nướng thảo mộc", 2, 380000.00, "lamb.png", "ConMon"),
        (204, "Gà nướng lu thảo mộc", 2, 290000.00, "chicken.png", "ConMon"),
        (301, "Tôm hùm nướng bơ tỏi", 3, 850000.00, "lobster.png", "ConMon"),
        (302, "Cá hồi áp chảo sốt chanh dây", 3, 360000.00, "salmon.png", "ConMon"),
        (303, "Cua sốt ớt Singapore", 3, 550000.00, "crab.png", "ConMon"),
        (401, "Rượu vang đỏ Cabernet Sauvignon", 4, 680000.00, "wine.png", "ConMon"),
        (402, "Trà đào cam sả", 4, 55000.00, "tea.png", "ConMon"),
        (403, "Nước ép cam tươi", 4, 60000.00, "juice.png", "ConMon"),
        (501, "Bánh Tiramisu Ý", 5, 85000.00, "cake.png", "ConMon")
    ]
    
    # Bổ sung 106 món ăn phong phú từ MaMon 104 trở đi (tránh trùng các mã 101, 102, 103, 201..204, 301..303, 401..403, 501)
    extra_dishes = [
        # Khai vị (DM 1, 6, 7, 8, 9, 10)
        ("Salad ức gà áp chảo sốt mè rang", 1, 115000.00, "salad.png"),
        ("Salad cá ngừ đại dương", 1, 135000.00, "salad.png"),
        ("Súp kem nấm Truffle đen", 1, 185000.00, "soup.png"),
        ("Súp hải sản chua cay Tứ Xuyên", 1, 145000.00, "soup.png"),
        ("Nem rán hải sản hoàng gia", 1, 120000.00, "food_placeholder.png"),
        ("Chả giò tôm thịt chiên giòn", 1, 95000.00, "food_placeholder.png"),
        ("Phô mai que chiên giòn sốt tartare", 1, 75000.00, "fries.png"),
        ("Mực vòng chiên giòn Calamari", 1, 155000.00, "food_placeholder.png"),
        ("Gỏi bưởi tôm sú làng quê", 1, 140000.00, "salad.png"),
        ("Gỏi cuốn tôm thịt chấm tương đen", 1, 85000.00, "salad.png"),
        
        # Món chính Bò, Cừu, Heo, Gia cầm (DM 2, 11, 12, 13, 14, 15, 16, 17, 18)
        ("Bò Fuji áp chảo sốt tiêu đen", 2, 420000.00, "steak.png"),
        ("Bò Mỹ nướng tảng đá sốt phô mai", 2, 460000.00, "steak.png"),
        ("Thăn ngoại bò Úc nướng sốt vang đỏ", 2, 380000.00, "steak.png"),
        ("Lườn ngỗng hun khói sốt cam mật ong", 2, 280000.00, "food_placeholder.png"),
        ("Sườn cừu áp chảo sốt lá hương thảo", 2, 450000.00, "lamb.png"),
        ("Heo muối Iberico cắt lát bánh mì bơ", 2, 520000.00, "food_placeholder.png"),
        ("Thịt ba chỉ heo đen nướng kim chi", 2, 230000.00, "ribs.png"),
        ("Sườn heo nướng mật ong rừng", 2, 310000.00, "ribs.png"),
        ("Gà ác tiềm thuốc bắc nhân sâm", 2, 260000.00, "chicken.png"),
        ("Gà hấp lá chanh truyền thống", 2, 250000.00, "chicken.png"),
        ("Vịt quay Quảng Đông nửa con", 2, 340000.00, "food_placeholder.png"),
        ("Bồ câu nướng mật ong", 2, 210000.00, "food_placeholder.png"),
        ("Dê hấp tía tô chấm chao", 2, 290000.00, "food_placeholder.png"),
        ("Thịt bê non chao dầu lá lốt", 2, 270000.00, "food_placeholder.png"),
        
        # Món Hải sản (DM 3, 19..37)
        ("Tôm hùm bỏ lò phô mai Mozzarella", 3, 890000.00, "lobster.png"),
        ("Tôm sú rang muối tuyết HongKong", 3, 290000.00, "food_placeholder.png"),
        ("Tôm càng xanh nướng mọi", 3, 380000.00, "food_placeholder.png"),
        ("Cua biển hấp bia xả ớt", 3, 520000.00, "crab.png"),
        ("Cua lột chiên hoàng kim sốt trứng muối", 3, 380000.00, "crab.png"),
        ("Cá hồi nướng sốt Teriyaki Nhật Bản", 3, 350000.00, "salmon.png"),
        ("Cá tuyết hấp tàu xì Thượng Hải", 3, 620000.00, "food_placeholder.png"),
        ("Cá mú đỏ hấp xì dầu kiểu Hồng Kông", 3, 680000.00, "food_placeholder.png"),
        ("Cá lăng nướng than hoa ăn kèm bún", 3, 390000.00, "food_placeholder.png"),
        ("Bạch tuộc nướng sa tế cay giòn", 3, 210000.00, "food_placeholder.png"),
        ("Mực trứng chiên nước mắm nhĩ", 3, 240000.00, "food_placeholder.png"),
        ("Mực một nắng nướng sa tế", 3, 270000.00, "food_placeholder.png"),
        ("Ốc hương nướng muối ớt", 3, 260000.00, "food_placeholder.png"),
        ("Ốc hương sốt trứng muối béo ngậy", 3, 280000.00, "food_placeholder.png"),
        ("Sò điệp Hokkaido áp chảo bơ tỏi", 3, 320000.00, "food_placeholder.png"),
        ("Hàu Pháp ăn sống mù tạt chanh (6 con)", 3, 240000.00, "food_placeholder.png"),
        ("Hàu nướng phô mai đút lò (6 con)", 3, 260000.00, "food_placeholder.png"),
        ("Bào ngư sốt dầu hào súp lơ xanh", 3, 750000.00, "food_placeholder.png"),
        ("Ngao hoa hấp sả ớt nước dừa", 3, 160000.00, "food_placeholder.png"),
        ("Nghêu hai cồi xào húng quế", 3, 180000.00, "food_placeholder.png"),
        
        # Món Lẩu & Nướng (DM 38..48)
        ("Nồi lẩu nấm thiên nhiên thanh ngọt", 38, 450000.00, "food_placeholder.png"),
        ("Nồi lẩu Thái chua cay tôm càng", 39, 480000.00, "food_placeholder.png"),
        ("Nồi lẩu riêu cua bắp bò sườn sụn", 41, 460000.00, "food_placeholder.png"),
        ("Nồi lẩu gà lá é đặc sản Đà Lạt", 42, 390000.00, "chicken.png"),
        ("Nồi lẩu hải sản thập cẩm thượng hạng", 40, 590000.00, "food_placeholder.png"),
        ("Set nướng thịt bò nướng than hoa", 47, 520000.00, "steak.png"),
        ("Set hải sản nướng tổng hợp", 47, 560000.00, "food_placeholder.png"),
        
        # Cơm, Mì, Tinh bột (DM 49..57)
        ("Cơm chiên hải sản hạt lựu hoàng kim", 50, 160000.00, "food_placeholder.png"),
        ("Cơm chiên dưa bò Hà Nội", 49, 140000.00, "food_placeholder.png"),
        ("Cơm niêu cá kho tộ gia truyền", 51, 170000.00, "food_placeholder.png"),
        ("Mì Ý hải sản sốt cà chua cay", 52, 195000.00, "food_placeholder.png"),
        ("Mì Ý bò băm đút lò phô mai", 53, 175000.00, "food_placeholder.png"),
        ("Mì xào giòn hải sản rau củ", 54, 165000.00, "food_placeholder.png"),
        ("Miến xào thịt cua bắp cải tím", 55, 210000.00, "food_placeholder.png"),
        ("Bún chả que tre Hà Nội", 57, 110000.00, "food_placeholder.png"),
        ("Phở cuốn bò xào đặc biệt", 56, 115000.00, "food_placeholder.png"),
        
        # Món Rau & Chay (DM 58..61)
        ("Nấm đùi gà xào hạt điều chua ngọt", 58, 125000.00, "food_placeholder.png"),
        ("Đậu hũ non sốt nấm đông cô", 59, 105000.00, "food_placeholder.png"),
        ("Cải ngồng xào tỏi thơm lừng", 60, 75000.00, "food_placeholder.png"),
        ("Rau rừng luộc chấm kho quẹt Nam Bộ", 61, 95000.00, "food_placeholder.png"),
        ("Đọt su su xào bò tơ", 60, 135000.00, "food_placeholder.png"),
        ("Măng tây xào tỏi giòn ngọt", 60, 115000.00, "food_placeholder.png"),
        
        # Đồ uống & Rượu (DM 4, 62..85)
        ("Rượu vang Chile Montes Alpha Syrah", 64, 850000.00, "wine.png"),
        ("Rượu vang đỏ Ý Chianti Classico", 63, 920000.00, "wine.png"),
        ("Rượu Champagne Moet & Chandon", 65, 2100000.00, "wine.png"),
        ("Bia Heineken Silver chai 330ml", 68, 38000.00, "food_placeholder.png"),
        ("Bia Corona Extra lát chanh", 68, 55000.00, "food_placeholder.png"),
        ("Bia thủ công IPA Đông Sơn", 67, 85000.00, "food_placeholder.png"),
        ("Cocktail Mojito Bạc hà truyền thống", 69, 95000.00, "tea.png"),
        ("Cocktail Margarita chanh muối", 69, 105000.00, "tea.png"),
        ("Mocktail Cinderella nhiệt đới", 70, 75000.00, "juice.png"),
        ("Nước ép dưa hấu tươi mát", 72, 50000.00, "juice.png"),
        ("Nước ép bưởi hồng giảm cân", 71, 65000.00, "juice.png"),
        ("Nước ép táo & cần tây thanh lọc", 73, 65000.00, "juice.png"),
        ("Sinh tố bơ Đắk Lắk cốt dừa", 74, 65000.00, "juice.png"),
        ("Sinh tố xoài cát Hòa Lộc", 75, 55000.00, "juice.png"),
        ("Trà ô long vải hoa hồng", 79, 55000.00, "tea.png"),
        ("Trà sen vàng hạt sen nhãn nhục", 78, 60000.00, "tea.png"),
        ("Trà hoa cúc táo đỏ mật ong ấm", 76, 50000.00, "tea.png"),
        ("Cà phê đen đá pha phin", 82, 35000.00, "food_placeholder.png"),
        ("Cà phê sữa đá Sài Gòn", 82, 40000.00, "food_placeholder.png"),
        ("Cà phê cốt dừa Hải Phòng", 83, 55000.00, "food_placeholder.png"),
        ("Cà phê Latte nghệ thuật", 81, 60000.00, "food_placeholder.png"),
        ("Nước khoáng có ga Perrier 330ml", 84, 55000.00, "food_placeholder.png"),
        ("Coca Cola lon 330ml", 85, 25000.00, "food_placeholder.png"),
        ("Nước suối đóng chai Lavie 500ml", 85, 20000.00, "food_placeholder.png"),
        
        # Tráng miệng (DM 5, 86..95)
        ("Bánh Panna Cotta dâu tây Pháp", 90, 65000.00, "cake.png"),
        ("Bánh Mousse chanh leo tươi mát", 87, 70000.00, "cake.png"),
        ("Bánh Cheesecake nướng New York", 88, 85000.00, "cake.png"),
        ("Bánh Fondant socola chảy kem tươi", 89, 95000.00, "cake.png"),
        ("Kem Gelato Ý vị Vani Madagascar", 91, 55000.00, "cake.png"),
        ("Kem Gelato Ý vị Socola Bỉ", 91, 55000.00, "cake.png"),
        ("Kem trái dừa non Côn Đảo", 92, 75000.00, "cake.png"),
        ("Chè khúc bạch hạnh nhân thanh mát", 93, 50000.00, "cake.png"),
        ("Chè long nhãn hạt sen cung đình", 94, 55000.00, "cake.png"),
        ("Đĩa hoa quả tươi tổng hợp theo mùa", 95, 120000.00, "salad.png"),
        ("Bánh Flan caramel truyền thống", 5, 45000.00, "cake.png"),
        ("Kem dâu tây tươi sữa chua", 91, 50000.00, "cake.png")
    ]
    
    # Kết hợp các món thành danh sách 120 món ăn
    all_mon_an = list(core_mon_an)
    assigned_ids = {m[0] for m in core_mon_an}
    
    current_new_id = 104
    for item in extra_dishes:
        while current_new_id in assigned_ids:
            current_new_id += 1
        all_mon_an.append((current_new_id, item[0], item[1], item[2], item[3], "ConMon"))
        assigned_ids.add(current_new_id)
        current_new_id += 1
        if len(all_mon_an) >= 120:
            break
            
    # Đảm bảo đủ ít nhất 120 món ăn
    while len(all_mon_an) < 120:
        while current_new_id in assigned_ids:
            current_new_id += 1
        all_mon_an.append((current_new_id, f"Món ngon đặc sản số {current_new_id}", 2, 150000.00, "food_placeholder.png", "ConMon"))
        assigned_ids.add(current_new_id)
        current_new_id += 1

    mon_sql = []
    for m in all_mon_an:
        mon_sql.append(f"({m[0]}, '{m[1]}', {m[2]}, {m[3]:.2f}, '{m[4]}', '{m[5]}')")
    lines.append(",\n".join(mon_sql) + ";\n")

    # -------------------------------------------------------------------------
    # 5. BẢNG VOUCHER (105 bản ghi)
    # -------------------------------------------------------------------------
    lines.append("-- =============================================================================")
    lines.append("-- 5. BẢNG VOUCHER KHUYẾN MÃI (Voucher) - 105 bản ghi")
    lines.append("-- =============================================================================")
    lines.append("INSERT INTO `Voucher` (`MaVoucher`, `TenVoucher`, `PhanTramGiam`, `GiamToiDa`, `DonHangToiThieu`, `NgayBatDau`, `NgayKetThuc`, `TrangThai`) VALUES")
    
    voucher_base = [
        ("LEHOI10", "Khuyến mãi Lễ Hội giảm 10%", 10.00, 100000.00, 500000.00, "2026-01-01", "2026-12-31", "HoatDong"),
        ("VIP20", "Tri ân khách VIP thân thiết 20%", 20.00, 300000.00, 1000000.00, "2026-01-01", "2026-12-31", "HoatDong"),
        ("GIAM50K", "Chiết khấu trực tiếp 50k", 0.00, 50000.00, 300000.00, "2026-01-01", "2026-12-31", "HoatDong"),
        ("TET2026", "Đón xuân Ất Tỵ giảm 15%", 15.00, 200000.00, 800000.00, "2026-01-15", "2026-02-28", "HoatDong"),
        ("SUMMER2026", "Chào hè rực rỡ giảm 12%", 12.00, 150000.00, 600000.00, "2026-05-01", "2026-08-31", "HoatDong")
    ]
    
    all_vouchers = list(voucher_base)
    for i in range(1, 101):
        code = f"DISC{i:03d}"
        pct = random.choice([5.00, 8.00, 10.00, 12.00, 15.00, 20.00, 25.00])
        max_discount = random.choice([50000.00, 100000.00, 150000.00, 200000.00, 300000.00, 500000.00])
        min_order = random.choice([200000.00, 300000.00, 500000.00, 800000.00, 1000000.00])
        name = f"Voucher ưu đãi chiến dịch {code} giảm {int(pct)}%"
        status = "HoatDong" if i <= 90 else random.choice(["TamDung", "HetHan"])
        all_vouchers.append((code, name, pct, max_discount, min_order, "2026-01-01", "2026-12-31", status))
        
    v_sql = []
    for v in all_vouchers:
        v_sql.append(f"('{v[0]}', '{v[1]}', {v[2]:.2f}, {v[3]:.2f}, {v[4]:.2f}, '{v[5]}', '{v[6]}', '{v[7]}')")
    lines.append(",\n".join(v_sql) + ";\n")

    # -------------------------------------------------------------------------
    # 6. BẢNG CA LÀM VIỆC (105 bản ghi)
    # Ca 1 đến 104: DaDong
    # Ca 105: DangMo (bắt buộc cho CaLamViecDAO.getCaDangMo())
    # -------------------------------------------------------------------------
    lines.append("-- =============================================================================")
    lines.append("-- 6. BẢNG CA LÀM VIỆC (CaLamViec) - 105 bản ghi (104 ca đã đóng + Ca #105 đang mở)")
    lines.append("-- =============================================================================")
    lines.append("INSERT INTO `CaLamViec` (`MaCa`, `MaNV`, `ThoiGianMo`, `ThoiGianDong`, `TienDauCa`, `DoanhThuTienMat`, `DoanhThuChuyenKhoan`, `TongDoanhThu`, `TienLyThuyet`, `TienThucTe`, `ChenhLech`, `TrangThai`, `GhiChu`) VALUES")
    
    now = datetime.now()
    ca_list = []
    
    # Sinh 104 ca trong quá khứ (khoảng 35 ngày, mỗi ngày 3 ca)
    cashier_nv_ids = [2, 5, 9]
    for i in range(1, 105):
        days_ago = (105 - i) // 3 + 1
        shift_of_day = (105 - i) % 3
        
        open_hour = 7 + shift_of_day * 5
        close_hour = open_hour + 5
        
        t_open = now - timedelta(days=days_ago)
        t_open = t_open.replace(hour=open_hour, minute=0, second=0)
        t_close = t_open + timedelta(hours=5)
        
        nv_id = cashier_nv_ids[i % len(cashier_nv_ids)]
        tien_dau_ca = 1000000.00 + (i % 5) * 200000.00
        tien_mat = 1500000.00 + (i * 37000) % 3500000
        tien_ck = 2000000.00 + (i * 53000) % 4500000
        tong_dt = tien_mat + tien_ck
        tien_ly_thuyet = tien_dau_ca + tien_mat
        tien_thuc_te = tien_ly_thuyet # khớp chuẩn
        chenh_lech = 0.00
        
        str_open = t_open.strftime("%Y-%m-%d %H:%M:%S")
        str_close = t_close.strftime("%Y-%m-%d %H:%M:%S")
        note = f"Ca làm việc #{i} hoàn thành tốt, bàn giao ca đầy đủ, khớp tiền 100%"
        
        ca_list.append((i, nv_id, f"'{str_open}'", f"'{str_close}'", tien_dau_ca, tien_mat, tien_ck, tong_dt, tien_ly_thuyet, tien_thuc_te, chenh_lech, "DaDong", f"'{note}'"))
        
    # Ca 105: Ca đang mở hiện tại
    t_open_current = now - timedelta(hours=4)
    str_open_curr = t_open_current.strftime("%Y-%m-%d %H:%M:%S")
    ca_list.append((105, 2, f"'{str_open_curr}'", "NULL", 1500000.00, 3925800.00, 2494800.00, 6420600.00, 5425800.00, "NULL", "NULL", "DangMo", "'Ca trực hiện tại của Thu ngân Trần Thị Mai'"))
    
    ca_sql = []
    for c in ca_list:
        tt_val = f"{c[9]:.2f}" if c[9] != "NULL" else "NULL"
        cl_val = f"{c[10]:.2f}" if c[10] != "NULL" else "NULL"
        ca_sql.append(f"({c[0]}, {c[1]}, {c[2]}, {c[3]}, {c[4]:.2f}, {c[5]:.2f}, {c[6]:.2f}, {c[7]:.2f}, {c[8]:.2f}, {tt_val}, {cl_val}, '{c[11]}', {c[12]})")
    lines.append(",\n".join(ca_sql) + ";\n")

    # -------------------------------------------------------------------------
    # 7. BẢNG HÓA ĐƠN (130 bản ghi)
    # Hóa đơn 1004, 1005, 1009, 1010, 1014, 1015: Chưa thanh toán (Bàn 4, 5, 9, 10, 14, 15) thuộc Ca 105
    # Hóa đơn 9001..9124: Đã thanh toán (lịch sử báo cáo thống kê qua các ca)
    # -------------------------------------------------------------------------
    lines.append("-- =============================================================================")
    lines.append("-- 7. BẢNG HÓA ĐƠN (HoaDon) - 130 bản ghi (6 đơn đang phục vụ + 124 đơn đã thanh toán)")
    lines.append("-- =============================================================================")
    lines.append("INSERT INTO `HoaDon` (`MaHD`, `MaBan`, `MaNV`, `MaCa`, `MaVoucher`, `MaHD_Goc`, `GioVao`, `GioRa`, `TamTinh`, `GiamGiaPhanTram`, `TienGiamGia`, `VatPhanTram`, `TienVAT`, `TongTien`, `HinhThucTT`, `TrangThai`) VALUES")
    
    hoa_don_list = []
    
    # 6 Hóa đơn đang phục vụ (Trùng khớp Test Suite và Screenshots UI)
    active_invoices = [
        (1004, 4, 2, 105, "NULL", "NULL", "NOW() - INTERVAL 45 MINUTE", "NULL", 1795000.00, 0.00, 0.00, 8.00, 143600.00, 1938600.00, "TienMat", "ChuaThanhToan"),
        (1005, 5, 2, 105, "'LEHOI10'", "NULL", "NOW() - INTERVAL 60 MINUTE", "NULL", 1370000.00, 10.00, 100000.00, 8.00, 101600.00, 1371600.00, "ChuyenKhoan", "ChuaThanhToan"),
        (1009, 9, 2, 105, "NULL", "NULL", "NOW() - INTERVAL 30 MINUTE", "NULL", 850000.00, 0.00, 0.00, 8.00, 68000.00, 918000.00, "TienMat", "ChuaThanhToan"),
        (1010, 10, 2, 105, "NULL", "NULL", "NOW() - INTERVAL 75 MINUTE", "NULL", 1270000.00, 0.00, 0.00, 8.00, 101600.00, 1371600.00, "The", "ChuaThanhToan"),
        (1014, 14, 2, 105, "NULL", "NULL", "NOW() - INTERVAL 20 MINUTE", "NULL", 990000.00, 0.00, 0.00, 8.00, 79200.00, 1069200.00, "TienMat", "ChuaThanhToan"),
        (1015, 15, 2, 105, "NULL", "NULL", "NOW() - INTERVAL 90 MINUTE", "NULL", 1040000.00, 0.00, 0.00, 8.00, 83200.00, 1123200.00, "ChuyenKhoan", "ChuaThanhToan")
    ]
    hoa_don_list.extend(active_invoices)
    
    # 124 Hóa đơn lịch sử đã thanh toán (Từ mã 9001 đến 9124)
    # Phân bổ qua các ca làm việc 1..104
    payment_methods = ["TienMat", "ChuyenKhoan", "The"]
    for hd_id in range(9001, 9125):
        seq = hd_id - 9001
        maca = (seq % 104) + 1
        maban = (seq % 20) + 1
        manv = cashier_nv_ids[seq % len(cashier_nv_ids)]
        
        # Voucher thỉnh thoảng có
        v_code = "'LEHOI10'" if seq % 7 == 0 else ("'VIP20'" if seq % 11 == 0 else "NULL")
        pct_giam = 10.00 if v_code == "'LEHOI10'" else (20.00 if v_code == "'VIP20'" else 0.00)
        
        days_back = (125 - seq) // 4 + 1
        hours_back = (seq % 12) + 1
        t_vao = now - timedelta(days=days_back, hours=hours_back)
        t_ra = t_vao + timedelta(minutes=45 + (seq % 60))
        
        str_vao = f"'{t_vao.strftime('%Y-%m-%d %H:%M:%S')}'"
        str_ra = f"'{t_ra.strftime('%Y-%m-%d %H:%M:%S')}'"
        
        tam_tinh = 450000.00 + (seq * 33000) % 2500000
        tien_giam = tam_tinh * (pct_giam / 100.0)
        vat_pct = 8.00
        tien_vat = (tam_tinh - tien_giam) * (vat_pct / 100.0)
        tong_tien = (tam_tinh - tien_giam) + tien_vat
        hinh_thuc = payment_methods[seq % len(payment_methods)]
        
        hoa_don_list.append((hd_id, maban, manv, maca, v_code, "NULL", str_vao, str_ra, tam_tinh, pct_giam, tien_giam, vat_pct, tien_vat, tong_tien, hinh_thuc, "DaThanhToan"))
        
    hd_sql = []
    for h in hoa_don_list:
        hd_sql.append(f"({h[0]}, {h[1]}, {h[2]}, {h[3]}, {h[4]}, {h[5]}, {h[6]}, {h[7]}, {h[8]:.2f}, {h[9]:.2f}, {h[10]:.2f}, {h[11]:.2f}, {h[12]:.2f}, {h[13]:.2f}, '{h[14]}', '{h[15]}')")
    lines.append(",\n".join(hd_sql) + ";\n")

    # -------------------------------------------------------------------------
    # 8. BẢNG CHI TIẾT HÓA ĐƠN (380+ bản ghi)
    # Bao gồm các chi tiết cho đơn đang mở (Khớp Kitchen test và UI)
    # và các chi tiết cho toàn bộ 124 hóa đơn đã thanh toán
    # -------------------------------------------------------------------------
    lines.append("-- =============================================================================")
    lines.append("-- 8. BẢNG CHI TIẾT HÓA ĐƠN (ChiTietHoaDon) - 380+ bản ghi")
    lines.append("-- =============================================================================")
    lines.append("INSERT INTO `ChiTietHoaDon` (`MaCTHD`, `MaHD`, `MaMon`, `SoLuong`, `DonGia`, `GhiChu`, `TrangThaiMon`, `ThoiGianGoi`) VALUES")
    
    cthd_list = []
    
    # 12 chi tiết cố định cho 6 hóa đơn đang phục vụ (Test Kitchen yêu cầu có DangCho, DangLam, DaRaMon)
    cthd_active = [
        # Bàn 4 (HD 1004)
        (1, 1004, 201, 2, 495000.00, "Chín vừa (Medium Rare)", "DaRaMon", "NOW() - INTERVAL 40 MINUTE"),
        (2, 1004, 401, 1, 680000.00, "Ướp lạnh trước khi rót", "DaRaMon", "NOW() - INTERVAL 35 MINUTE"),
        (3, 1004, 101, 1, 125000.00, "Ít cay", "DaRaMon", "NOW() - INTERVAL 30 MINUTE"),
        # Bàn 5 (HD 1005)
        (4, 1005, 301, 1, 850000.00, "Nhiều bơ tỏi", "DangLam", "NOW() - INTERVAL 55 MINUTE"),
        (5, 1005, 202, 1, 320000.00, "Sốt BBQ cay đậm", "DangCho", "NOW() - INTERVAL 50 MINUTE"),
        (6, 1005, 103, 2, 65000.00, "Phục vụ trước", "DaRaMon", "NOW() - INTERVAL 45 MINUTE"),
        # Bàn 9 (HD 1009)
        (7, 1009, 301, 1, 850000.00, "", "DangLam", "NOW() - INTERVAL 25 MINUTE"),
        # Bàn 10 (HD 1010)
        (8, 1010, 203, 2, 380000.00, "Không lấy hành tây", "DaRaMon", "NOW() - INTERVAL 70 MINUTE"),
        (9, 1010, 303, 1, 550000.00, "Ăn kèm bánh mì nóng", "DangLam", "NOW() - INTERVAL 65 MINUTE"),
        # Bàn 14 (HD 1014)
        (10, 1014, 201, 2, 495000.00, "Làm chín kỹ (Well done)", "DangCho", "NOW() - INTERVAL 15 MINUTE"),
        # Bàn 15 (HD 1015)
        (11, 1015, 102, 2, 245000.00, "", "DaRaMon", "NOW() - INTERVAL 85 MINUTE"),
        (12, 1015, 303, 1, 550000.00, "Ăn kèm bánh mì", "DangLam", "NOW() - INTERVAL 80 MINUTE")
    ]
    cthd_list.extend(cthd_active)
    
    # Sinh chi tiết cho 124 hóa đơn lịch sử (mỗi hóa đơn 2-4 món)
    cthd_id = 13
    mon_pool = [
        (101, 125000.00), (102, 245000.00), (103, 65000.00),
        (201, 495000.00), (202, 320000.00), (203, 380000.00), (204, 290000.00),
        (301, 850000.00), (302, 360000.00), (303, 550000.00),
        (401, 680000.00), (402, 55000.00), (403, 60000.00),
        (501, 85000.00), (104, 115000.00), (105, 135000.00),
        (114, 420000.00), (117, 280000.00), (128, 890000.00), (133, 350000.00)
    ]
    
    notes_pool = ["", "", "", "Ít cay", "Không hành", "Mang ra sớm", "Làm chín kỹ", "Ít ngọt", "Thêm đá"]
    
    for hd_id in range(9001, 9125):
        num_items = 2 + (hd_id % 3) # 2 đến 4 món mỗi đơn
        for item_idx in range(num_items):
            m_tuple = mon_pool[(hd_id + item_idx * 3) % len(mon_pool)]
            qty = 1 if (hd_id + item_idx) % 4 != 0 else 2
            note = notes_pool[(hd_id + item_idx) % len(notes_pool)]
            
            # Giờ gọi tính theo giờ vào của hóa đơn
            seq = hd_id - 9001
            days_back = (125 - seq) // 4 + 1
            hours_back = (seq % 12) + 1
            t_goi = now - timedelta(days=days_back, hours=hours_back, minutes=-(item_idx * 5))
            str_goi = f"'{t_goi.strftime('%Y-%m-%d %H:%M:%S')}'"
            
            cthd_list.append((cthd_id, hd_id, m_tuple[0], qty, m_tuple[1], note, "DaRaMon", str_goi))
            cthd_id += 1
            
    cthd_sql = []
    for ct in cthd_list:
        cthd_sql.append(f"({ct[0]}, {ct[1]}, {ct[2]}, {ct[3]}, {ct[4]:.2f}, '{ct[5]}', '{ct[6]}', {ct[7]})")
    lines.append(",\n".join(cthd_sql) + ";\n")

    # -------------------------------------------------------------------------
    # 9. BẢNG LỊCH SỬ TRUY CẬP (120 bản ghi)
    # -------------------------------------------------------------------------
    lines.append("-- =============================================================================")
    lines.append("-- 9. BẢNG LỊCH SỬ TRUY CẬP (LichSuTruyCap) - 120 bản ghi (Audit Access Log)")
    lines.append("-- =============================================================================")
    lines.append("INSERT INTO `LichSuTruyCap` (`MaTruyCap`, `MaNV`, `TenDangNhap`, `HoTen`, `HanhDong`, `ThoiGian`, `DiaChiIP`, `TrinhDuyet`, `TrangThai`, `GhiChu`) VALUES")
    
    access_logs = []
    ip_pool = ["192.168.1.10", "192.168.1.15", "192.168.1.20", "192.168.1.25", "192.168.1.50", "127.0.0.1"]
    browser_pool = [
        "Restaurant POS Desktop v2.0 (Windows 11)",
        "Restaurant Waiter App (Android 14)",
        "Restaurant Kitchen Display System (Windows 10)",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0"
    ]
    
    user_pool = [
        (1, "admin", "Quản trị viên Hệ thống"),
        (2, "cashier01", "Trần Thị Mai"),
        (3, "waiter01", "Lê Hoàng Nam"),
        (4, "chef01", "Phạm Quốc Tuấn"),
        (5, "cashier02", "Đỗ Kim Ngân"),
        (7, "quanly", "Nguyễn Văn Quản Lý"),
        (8, "admin02", "Nguyễn Phương Thảo (Admin)")
    ]
    
    for i in range(1, 121):
        u = user_pool[i % len(user_pool)]
        t_log = now - timedelta(days=(120 - i) // 4, hours=(i % 24), minutes=(i * 13 % 60))
        str_t = t_log.strftime("%Y-%m-%d %H:%M:%S")
        ip = ip_pool[i % len(ip_pool)]
        client = browser_pool[i % len(browser_pool)]
        
        if i % 15 == 0:
            action = "DangNhapThatBai"
            status = "ThatBai"
            note = "Sai mật khẩu xác thực người dùng"
        elif i % 2 == 0:
            action = "DangNhap"
            status = "ThanhCong"
            note = "Đăng nhập hệ thống thành công"
        else:
            action = "DangXuat"
            status = "ThanhCong"
            note = "Đăng xuất kết thúc phiên làm việc"
            
        access_logs.append((i, u[0], u[1], u[2], action, str_t, ip, client, status, note))
        
    acc_sql = []
    for a in access_logs:
        acc_sql.append(f"({a[0]}, {a[1]}, '{a[2]}', '{a[3]}', '{a[4]}', '{a[5]}', '{a[6]}', '{a[7]}', '{a[8]}', '{a[9]}')")
    lines.append(",\n".join(acc_sql) + ";\n")

    # -------------------------------------------------------------------------
    # 10. BẢNG LỊCH SỬ THAO TÁC (125 bản ghi)
    # -------------------------------------------------------------------------
    lines.append("-- =============================================================================")
    lines.append("-- 10. BẢNG LỊCH SỬ THAO TÁC (LichSuThaoTac) - 125 bản ghi (Audit Operation Log)")
    lines.append("-- =============================================================================")
    lines.append("INSERT INTO `LichSuThaoTac` (`MaThaoTac`, `MaNV`, `HoTen`, `TenBang`, `HanhDong`, `BanGhiCu`, `BanGhiMoi`, `MoTa`, `ChiTiet`, `ThoiGian`, `DiaChiIP`, `MayTinh`) VALUES")
    
    audit_ops = []
    tables = ["MonAn", "HoaDon", "CaLamViec", "Ban", "Voucher", "NhanVien"]
    actions = ["INSERT", "UPDATE", "DELETE"]
    hostnames = ["POS-DESKTOP-01", "MANAGER-LAPTOP", "CASHIER-COUNTER", "ADMIN-PC", "KITCHEN-TAB"]
    
    op_templates = [
        ("MonAn", "UPDATE", "Cập nhật đơn giá món ăn", "Thay đổi giá bán niêm yết theo điều chỉnh nguyên liệu", '{"DonGia": 450000}', '{"DonGia": 495000}'),
        ("MonAn", "UPDATE", "Chuyển trạng thái món hết hàng / còn món", "Bếp cập nhật trạng thái phục vụ của thực đơn", '{"TrangThai": "ConMon"}', '{"TrangThai": "HetHang"}'),
        ("Ban", "UPDATE", "Chuyển bàn ăn khách yêu cầu", "Chuyển khách từ Bàn 3 sang Bàn 5 tầng 1", '{"MaBan": 3}', '{"MaBan": 5}'),
        ("HoaDon", "INSERT", "Mở bàn tạo hóa đơn mới", "Khách vào bàn phát sinh order mới", 'NULL', '{"MaHD": 1020, "MaBan": 6}'),
        ("HoaDon", "UPDATE", "Thanh toán hóa đơn bàn giao khách", "Thực hiện thu tiền và in hóa đơn", '{"TrangThai": "ChuaThanhToan"}', '{"TrangThai": "DaThanhToan"}'),
        ("CaLamViec", "INSERT", "Mở ca làm việc thu ngân", "Đầu ca làm việc mới với tiền két ban đầu", 'NULL', '{"MaCa": 105, "TienDauCa": 1500000}'),
        ("CaLamViec", "UPDATE", "Đóng ca kiểm đếm và kết chuyển", "Bàn giao tiền ca trực và giải trình doanh thu", '{"TrangThai": "DangMo"}', '{"TrangThai": "DaDong"}'),
        ("Voucher", "INSERT", "Thêm mới mã khuyến mãi chiến dịch", "Thiết lập voucher giảm giá sự kiện tri ân", 'NULL', '{"MaVoucher": "VIP25", "Giam": 25.0}'),
        ("NhanVien", "UPDATE", "Cập nhật thông tin nhân viên", "Thay đổi số điện thoại và vai trò nội bộ", '{"SoDienThoai": "0912345678"}', '{"SoDienThoai": "0988776655"}')
    ]
    
    for i in range(1, 126):
        op = op_templates[i % len(op_templates)]
        u = user_pool[i % len(user_pool)]
        t_op = now - timedelta(days=(125 - i) // 4, hours=(i % 24), minutes=((i * 17) % 60))
        str_t = t_op.strftime("%Y-%m-%d %H:%M:%S")
        ip = ip_pool[i % len(ip_pool)]
        host = hostnames[i % len(hostnames)]
        
        bg_cu = op[4] if op[4] == "NULL" else f"'{op[4]}'"
        bg_moi = op[5] if op[5] == "NULL" else f"'{op[5]}'"
        
        mota = f"{op[2]} #{i}"
        chitiet = f"{op[3]} (Mã thao tác hệ thống: {i})"
        
        audit_ops.append((i, u[0], u[2], op[0], op[1], bg_cu, bg_moi, mota, chitiet, str_t, ip, host))
        
    aop_sql = []
    for o in audit_ops:
        aop_sql.append(f"({o[0]}, {o[1]}, '{o[2]}', '{o[3]}', '{o[4]}', {o[5]}, {o[6]}, '{o[7]}', '{o[8]}', '{o[9]}', '{o[10]}', '{o[11]}')")
    lines.append(",\n".join(aop_sql) + ";\n")

    lines.append("-- =============================================================================")
    lines.append("-- HOÀN TẤT NẠP DỮ LIỆU SEED DATA CHO TOÀN BỘ 10 BẢNG")
    lines.append("-- =============================================================================")
    lines.append("COMMIT;")
    lines.append("")
    
    content = "\n".join(lines)
    with open(out_file, "w", encoding="utf-8") as f:
        f.write(content)
        
    print(f"Created file {out_file} successfully ({len(content)} bytes)!")
    print(f"- NhanVien: {len(nhan_vien_list)} records (10-20)")
    print(f"- Ban: {len(ban_list)} records (10-20)")
    print(f"- DanhMuc: {len(danh_muc_all)} records (>= 100)")
    print(f"- MonAn: {len(all_mon_an)} records (>= 100)")
    print(f"- Voucher: {len(all_vouchers)} records (>= 100)")
    print(f"- CaLamViec: {len(ca_list)} records (>= 100)")
    print(f"- HoaDon: {len(hoa_don_list)} records (>= 100)")
    print(f"- ChiTietHoaDon: {len(cthd_list)} records (>= 100)")
    print(f"- LichSuTruyCap: {len(access_logs)} records (>= 100)")
    print(f"- LichSuThaoTac: {len(audit_ops)} records (>= 100)")

if __name__ == "__main__":
    main()
