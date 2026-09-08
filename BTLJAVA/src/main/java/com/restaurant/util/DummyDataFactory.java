package com.restaurant.util;

import com.restaurant.model.*;
import com.restaurant.dao.CaLamViecDAO;

import java.util.*;

/**
 * Lớp cung cấp Dữ liệu giả lập (Dummy Data) cho tầng View
 * =========================================================================
 * Điểm nối CSDL MySQL (Khi tích hợp Backend):
 * - Thay thế các phương thức tại đây bằng các lời gọi DAO / JDBC Service:
 *   + BanDAO: `getAll()`, `updateStatus(int maBan, String status)`
 *   + MonAnDAO: `getAll()`, `getByCategory(int maDM)`
 *   + HoaDonDAO: `getHoaDonChuaThanhToan(int maBan)`, `insert(HoaDon hd)`, `update(HoaDon hd)`
 *   + ChiTietHoaDonDAO: `getByHoaDon(int maHD)`, `insert(ChiTietHoaDon ct)`
 *   + NhanVienDAO: `getAll()`, `insert(NhanVien nv)`
 * =========================================================================
 */
public class DummyDataFactory {
    private static DummyDataFactory instance;

    private List<Ban> danhSachBan;
    private List<DanhMuc> danhSachDanhMuc;
    private List<MonAn> danhSachMonAn;
    private List<NhanVien> danhSachNhanVien;
    private Map<Integer, HoaDon> hoaDonTheoBan; // key: maBan, value: HoaDon hiện tại
    private KpiSummary kpiSummary;

    private DummyDataFactory() {
        initCategories();
        initDishes();
        initTables();
        initEmployees();
        initBills();
        kpiSummary = new KpiSummary();
    }

    public static synchronized DummyDataFactory getInstance() {
        if (instance == null) {
            instance = new DummyDataFactory();
        }
        return instance;
    }

    private void initCategories() {
        danhSachDanhMuc = new ArrayList<>();
        danhSachDanhMuc.add(new DanhMuc(1, "Khai vị", "Các món khởi đầu bữa tiệc"));
        danhSachDanhMuc.add(new DanhMuc(2, "Món chính", "Các món đặc sản hảo hạng"));
        danhSachDanhMuc.add(new DanhMuc(3, "Hải sản", "Hải sản tươi sống cao cấp"));
        danhSachDanhMuc.add(new DanhMuc(4, "Đồ uống", "Nước ép, sinh tố, bia & rượu vang"));
        danhSachDanhMuc.add(new DanhMuc(5, "Tráng miệng", "Bánh ngọt, kem và chè"));
    }

    private void initDishes() {
        danhSachMonAn = new ArrayList<>();
        danhSachMonAn.add(new MonAn(101, "Gỏi ngó sen tôm thịt", 1, "Khai vị", 125000, "salad.png", true));
        danhSachMonAn.add(new MonAn(102, "Súp vi cá bào ngư", 1, "Khai vị", 245000, "soup.png", true));
        danhSachMonAn.add(new MonAn(103, "Khoai tây chiên phô mai", 1, "Khai vị", 65000, "fries.png", true));

        danhSachMonAn.add(new MonAn(201, "Bò bít tết Wagyu sốt tiêu", 2, "Món chính", 495000, "steak.png", true));
        danhSachMonAn.add(new MonAn(202, "Sườn heo nướng BBQ", 2, "Món chính", 320000, "ribs.png", true));
        danhSachMonAn.add(new MonAn(203, "Cừu nướng thảo mộc", 2, "Món chính", 380000, "lamb.png", true));
        danhSachMonAn.add(new MonAn(204, "Gà nướng lu thảo mộc", 2, "Món chính", 290000, "chicken.png", true));

        danhSachMonAn.add(new MonAn(301, "Tôm hùm nướng bơ tỏi", 3, "Hải sản", 850000, "lobster.png", true));
        danhSachMonAn.add(new MonAn(302, "Cá hồi áp chảo sốt chanh dây", 3, "Hải sản", 360000, "salmon.png", true));
        danhSachMonAn.add(new MonAn(303, "Cua sốt ớt Singapore", 3, "Hải sản", 550000, "crab.png", true));

        danhSachMonAn.add(new MonAn(401, "Trà đào cam sả", 4, "Đồ uống", 55000, "tea.png", true));
        danhSachMonAn.add(new MonAn(402, "Nước ép cam tươi", 4, "Đồ uống", 60000, "juice.png", true));
        danhSachMonAn.add(new MonAn(403, "Rượu vang đỏ Cabernet Sauvignon", 4, "Đồ uống", 680000, "wine.png", true));
        danhSachMonAn.add(new MonAn(404, "Bia Heineken lon", 4, "Đồ uống", 45000, "beer.png", true));

        danhSachMonAn.add(new MonAn(501, "Bánh Tiramisu Ý", 5, "Tráng miệng", 75000, "tiramisu.png", true));
        danhSachMonAn.add(new MonAn(502, "Kem tươi dâu tây hạt dẻ", 5, "Tráng miệng", 65000, "icecream.png", true));
    }

    private void initTables() {
        danhSachBan = new ArrayList<>();
        // Tạo 20 bàn: Giống ảnh mẫu (một số bàn Trống - Xanh, một số bàn Có khách - Đỏ)
        for (int i = 1; i <= 20; i++) {
            String khuVuc = (i <= 10) ? "Tầng 1" : (i <= 16 ? "Tầng 2" : "Phòng VIP");
            int sucChua = (i % 3 == 0) ? 6 : (i % 5 == 0 ? 8 : 4);
            // Một số bàn cố định có khách để UI hiển thị sinh động
            boolean coKhach = (i == 4 || i == 5 || i == 9 || i == 10 || i == 14 || i == 15);
            danhSachBan.add(new Ban(i, "Bàn " + i, khuVuc, sucChua, coKhach ? "CoKhach" : "Trong"));
        }
    }

    private void initEmployees() {
        danhSachNhanVien = new ArrayList<>();
        danhSachNhanVien.add(new NhanVien(1, "Nguyễn Văn An (Admin)", "admin", "123456", "Quản lý", "Đang làm việc"));
        danhSachNhanVien.add(new NhanVien(2, "Trần Thị Mai", "cashier01", "123456", "Thu ngân", "Đang làm việc"));
        danhSachNhanVien.add(new NhanVien(3, "Lê Hoàng Nam", "waiter01", "123456", "Phục vụ", "Đang làm việc"));
        danhSachNhanVien.add(new NhanVien(4, "Phạm Quốc Tuấn", "chef01", "123456", "Đầu bếp", "Đang làm việc"));
        danhSachNhanVien.add(new NhanVien(5, "Đỗ Kim Ngân", "cashier02", "123456", "Thu ngân", "Đang làm việc"));
        danhSachNhanVien.add(new NhanVien(6, "Vũ Đức Thịnh", "waiter02", "123456", "Phục vụ", "Đã nghỉ việc"));
    }

    private void initBills() {
        hoaDonTheoBan = new HashMap<>();

        // Khởi tạo sẵn đơn hàng cho các bàn đang Có khách
        taoHoaDonMauChoBan(4, Arrays.asList(
                new ChiTietHoaDon(1, 1001, 201, "Bò bít tết Wagyu sốt tiêu", 2, 495000, "Chín vừa (Medium)", "DaRaMon"),
                new ChiTietHoaDon(2, 1001, 403, "Rượu vang đỏ Cabernet Sauvignon", 1, 680000, "Ướp lạnh", "DaRaMon"),
                new ChiTietHoaDon(3, 1001, 101, "Gỏi ngó sen tôm thịt", 1, 125000, "Ít cay", "DaRaMon")
        ));

        taoHoaDonMauChoBan(5, Arrays.asList(
                new ChiTietHoaDon(4, 1002, 301, "Tôm hùm nướng bơ tỏi", 1, 850000, "Nhiều bơ tỏi", "DangLam"),
                new ChiTietHoaDon(5, 1002, 401, "Trà đào cam sả", 3, 55000, "Ít đường", "DaRaMon")
        ));

        taoHoaDonMauChoBan(9, Arrays.asList(
                new ChiTietHoaDon(6, 1003, 202, "Sườn heo nướng BBQ", 2, 320000, "", "DaRaMon"),
                new ChiTietHoaDon(7, 1003, 103, "Khoai tây chiên phô mai", 1, 65000, "Giao nhanh", "DaRaMon"),
                new ChiTietHoaDon(8, 1003, 404, "Bia Heineken lon", 6, 45000, "Kèm xô đá", "DaRaMon")
        ));

        taoHoaDonMauChoBan(10, Arrays.asList(
                new ChiTietHoaDon(9, 1004, 302, "Cá hồi áp chảo sốt chanh dây", 1, 360000, "", "ChoLam"),
                new ChiTietHoaDon(10, 1004, 501, "Bánh Tiramisu Ý", 2, 75000, "Dọn sau", "ChoLam")
        ));

        taoHoaDonMauChoBan(14, Arrays.asList(
                new ChiTietHoaDon(11, 1005, 203, "Cừu nướng thảo mộc", 1, 380000, "Không cay", "DangLam")
        ));

        taoHoaDonMauChoBan(15, Arrays.asList(
                new ChiTietHoaDon(12, 1006, 102, "Súp vi cá bào ngư", 2, 245000, "", "DaRaMon"),
                new ChiTietHoaDon(13, 1006, 303, "Cua sốt ớt Singapore", 1, 550000, "Ăn kèm bánh mì", "DangLam")
        ));
    }

    private void taoHoaDonMauChoBan(int maBan, List<ChiTietHoaDon> chiTiets) {
        HoaDon hd = new HoaDon(1000 + maBan, maBan, "Bàn " + maBan);
        hd.setDanhSachChiTiet(new ArrayList<>(chiTiets));
        hd.tinhTongCong();
        hoaDonTheoBan.put(maBan, hd);
    }

    // DAOs kết nối MySQL
    private com.restaurant.dao.BanDAO banDAO = new com.restaurant.dao.BanDAO();
    private com.restaurant.dao.DanhMucDAO danhMucDAO = new com.restaurant.dao.DanhMucDAO();
    private com.restaurant.dao.MonAnDAO monAnDAO = new com.restaurant.dao.MonAnDAO();
    private com.restaurant.dao.NhanVienDAO nhanVienDAO = new com.restaurant.dao.NhanVienDAO();
    private com.restaurant.dao.HoaDonDAO hoaDonDAO = new com.restaurant.dao.HoaDonDAO();
    private CaLamViecDAO caLamViecDAO = new CaLamViecDAO();

    // =========================================================================
    // CÁC HÀM GETTER / SETTER TỰ ĐỘNG LẤY TỪ MYSQL HOẶC BỘ NHỚ ĐỆM
    // =========================================================================
    public List<Ban> getDanhSachBan() {
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            List<Ban> dbList = banDAO.getAll();
            if (dbList != null && !dbList.isEmpty()) {
                if (this.danhSachBan == null || this.danhSachBan.isEmpty()) {
                    this.danhSachBan = dbList;
                } else {
                    for (Ban dbBan : dbList) {
                        boolean found = false;
                        for (Ban curBan : this.danhSachBan) {
                            if (curBan.getMaBan() == dbBan.getMaBan()) {
                                curBan.setTenBan(dbBan.getTenBan());
                                curBan.setKhuVuc(dbBan.getKhuVuc());
                                curBan.setSucChua(dbBan.getSucChua());
                                curBan.setTrangThai(dbBan.getTrangThai());
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            this.danhSachBan.add(dbBan);
                        }
                    }
                }
            }
        }
        return danhSachBan;
    }

    public List<DanhMuc> getDanhSachDanhMuc() {
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            List<DanhMuc> dbList = danhMucDAO.getAll();
            if (dbList != null && !dbList.isEmpty()) {
                this.danhSachDanhMuc = dbList;
            }
        }
        return danhSachDanhMuc;
    }

    public List<MonAn> getDanhSachMonAn() {
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            List<MonAn> dbList = monAnDAO.getAll();
            if (dbList != null && !dbList.isEmpty()) {
                this.danhSachMonAn = dbList;
            }
        }
        return danhSachMonAn;
    }

    public List<NhanVien> getDanhSachNhanVien() {
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            List<NhanVien> dbList = nhanVienDAO.getAll();
            if (dbList != null && !dbList.isEmpty()) {
                this.danhSachNhanVien = dbList;
            }
        }
        return danhSachNhanVien;
    }

    public KpiSummary getKpiSummary() {
        return kpiSummary;
    }

    public HoaDon getHoaDonChoBan(int maBan) {
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            HoaDon dbHd = hoaDonDAO.getActiveInvoiceByTable(maBan);
            if (dbHd != null) {
                hoaDonTheoBan.put(maBan, dbHd);
                return dbHd;
            }
        }
        return hoaDonTheoBan.get(maBan);
    }

    public List<Ban> getDanhSachBanCoKhach() {
        List<Ban> all = getDanhSachBan();
        List<Ban> result = new ArrayList<>();
        for (Ban b : all) {
            if (b.isCoKhach()) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Mở bàn mới: Cập nhật trạng thái bàn sang 'CoKhach' và tạo hóa đơn trong CSDL MySQL
     */
    public HoaDon moBanMoi(int maBan) {
        Ban b = timBanTheoMa(maBan);
        String tenBan = (b != null) ? b.getTenBan() : ("Bàn " + maBan);

        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            HoaDon dbHd = hoaDonDAO.createInvoiceForTable(maBan, tenBan);
            if (dbHd != null) {
                hoaDonTheoBan.put(maBan, dbHd);
                if (b != null) b.setTrangThai("CoKhach");
                return dbHd;
            }
        }

        // Fallback bộ nhớ nếu CSDL offline
        if (b != null) {
            b.setTrangThai("CoKhach");
        }
        HoaDon hd = new HoaDon(2000 + maBan, maBan, tenBan);
        hoaDonTheoBan.put(maBan, hd);
        return hd;
    }

    /**
     * Thanh toán bàn: Cập nhật CSDL MySQL và giải phóng bàn
     */
    public void thanhToanBan(int maBan, String hinhThucTT, double giamGia, double vat) {
        HoaDon hd = hoaDonTheoBan.get(maBan);
        if (hd == null && com.restaurant.database.DatabaseConnection.isConnected()) {
            hd = hoaDonDAO.getActiveInvoiceByTable(maBan);
        }

        if (com.restaurant.database.DatabaseConnection.isConnected() && hd != null) {
            hoaDonDAO.checkout(maBan, hd.getMaHD(), hinhThucTT, giamGia, vat);
        }

        Ban b = timBanTheoMa(maBan);
        if (b != null) {
            b.setTrangThai("Trong");
        }
        if (hd != null) {
            hd.setGiamGiaPhanTram(giamGia);
            hd.setVatPhanTram(vat);
            hd.setHinhThucTT(hinhThucTT);
            hd.setTrangThai("DaThanhToan");
            double billTotal = hd.tinhTongCong();

            initShifts();
            if (caHienTai != null && caHienTai.isDangMo()) {
                hd.setMaCa(caHienTai.getMaCa());
                boolean isCash = "TienMat".equalsIgnoreCase(hinhThucTT) || "Tiền mặt".equalsIgnoreCase(hinhThucTT) || (hinhThucTT != null && hinhThucTT.toLowerCase().contains("tiền mặt"));
                if (isCash) {
                    caHienTai.setDoanhThuTienMat(caHienTai.getDoanhThuTienMat() + billTotal);
                } else {
                    caHienTai.setDoanhThuChuyenKhoan(caHienTai.getDoanhThuChuyenKhoan() + billTotal);
                }
                caHienTai.setTongDoanhThu(caHienTai.getDoanhThuTienMat() + caHienTai.getDoanhThuChuyenKhoan());
                caHienTai.setSoHoaDon(caHienTai.getSoHoaDon() + 1);
                caHienTai.tinhTienLyThuyet();
            }

            hoaDonTheoBan.remove(maBan);
        }

        // Tự động đồng bộ doanh thu tức thời cho ca làm việc trong CSDL MySQL
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            CaLamViecDAO cDao = new CaLamViecDAO();
            CaLamViec openCa = cDao.getCaDangMo();
            if (openCa != null) {
                cDao.capNhatDoanhThuTucThoi(openCa);
            }
        }
    }

    public Ban timBanTheoMa(int maBan) {
        if (danhSachBan != null) {
            for (Ban b : danhSachBan) {
                if (b.getMaBan() == maBan) return b;
            }
        }
        return null;
    }

    // =========================================================================
    // HỖ TRỢ PHÂN HỆ QUẢN LÝ CA LÀM VIỆC & BẾP (OFFLINE FALLBACK)
    // =========================================================================
    private List<CaLamViec> danhSachCaLamViec = new ArrayList<>();
    private CaLamViec caHienTai;

    public void initShifts() {
        if (!danhSachCaLamViec.isEmpty()) return;

        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        java.sql.Timestamp twoDaysAgoStart = new java.sql.Timestamp(now.getTime() - 2L * 24 * 3600 * 1000 + 7L * 3600 * 1000);
        java.sql.Timestamp twoDaysAgoEnd = new java.sql.Timestamp(now.getTime() - 2L * 24 * 3600 * 1000 + 15L * 3600 * 1000);
        java.sql.Timestamp fourHoursAgo = new java.sql.Timestamp(now.getTime() - 4L * 3600 * 1000);

        CaLamViec c1 = new CaLamViec(1, 2, "Trần Thị Mai", twoDaysAgoStart, twoDaysAgoEnd,
                1000000.0, 2450000.0, 0.0, 2450000.0, 3450000.0, 3450000.0, 0.0, "DaDong",
                "Ca làm việc khớp tiền 100%, bàn giao ca chiều suôn sẻ");
        c1.setSoHoaDon(1);

        CaLamViec c2 = new CaLamViec(2, 2, "Trần Thị Mai", fourHoursAgo, null,
                1500000.0, 3925800.0, 2494800.0, 6420600.0, 5425800.0, null, null, "DangMo",
                "Ca trực hiện tại của Thu ngân Trần Thị Mai");
        c2.setSoHoaDon(6);

        danhSachCaLamViec.add(c1);
        danhSachCaLamViec.add(c2);
        this.caHienTai = c2;
    }

    public CaLamViec getCaHienTai() {
        initShifts();
        return caHienTai;
    }

    public void setCaHienTai(CaLamViec ca) {
        initShifts();
        this.caHienTai = ca;
        if (ca != null && !danhSachCaLamViec.contains(ca)) {
            danhSachCaLamViec.add(0, ca);
        }
    }

    public void dongCa(CaLamViec ca) {
        initShifts();
        ca.setTrangThai("DaDong");
        ca.setThoiGianDong(new java.sql.Timestamp(System.currentTimeMillis()));
        if (this.caHienTai == ca) {
            this.caHienTai = null;
        }
    }

    public void capNhatDoanhThuCa(CaLamViec ca) {
        if (ca == null) return;
        ca.tinhTienLyThuyet();
        ca.tinhChenhLech();
    }

    public CaLamViec getCaTruocGanNhat() {
        initShifts();
        for (CaLamViec c : danhSachCaLamViec) {
            if ("DaDong".equalsIgnoreCase(c.getTrangThai())) {
                return c;
            }
        }
        return null;
    }

    public List<CaLamViec> getDanhSachCaLamViec() {
        initShifts();
        return new ArrayList<>(danhSachCaLamViec);
    }

    public List<HoaDon> getHoaDonTheoCa(int maCa) {
        List<HoaDon> list = new ArrayList<>();
        for (HoaDon hd : hoaDonTheoBan.values()) {
            list.add(hd);
        }
        return list;
    }

    public List<KitchenOrder> getKitchenOrders() {
        List<KitchenOrder> list = new ArrayList<>();
        int idCounter = 1;
        for (HoaDon hd : hoaDonTheoBan.values()) {
            Ban b = timBanTheoMa(hd.getMaBan());
            String kv = (b != null) ? b.getKhuVuc() : "Tầng 1";
            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                if (!"DaHuy".equalsIgnoreCase(ct.getTrangThaiMon())) {
                    KitchenOrder ko = new KitchenOrder(
                            ct.getMaCTHD() > 0 ? ct.getMaCTHD() : idCounter++,
                            hd.getMaHD(),
                            hd.getMaBan(),
                            hd.getTenBan(),
                            kv,
                            ct.getMaMon(),
                            ct.getTenMon(),
                            ct.getSoLuong(),
                            ct.getDonGia(),
                            ct.getGhiChu(),
                            ct.getTrangThaiMon(),
                            new java.sql.Timestamp(System.currentTimeMillis() - (idCounter * 5L * 60 * 1000))
                    );
                    list.add(ko);
                }
            }
        }
        return list;
    }

    public void capNhatTrangThaiMonHangLoat(List<Integer> maCTHDList, String newStatus) {
        for (HoaDon hd : hoaDonTheoBan.values()) {
            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                if (maCTHDList.contains(ct.getMaCTHD())) {
                    ct.setTrangThaiMon(newStatus);
                }
            }
        }
    }

    public void capNhatTinhTrangMon(int maMon, boolean conMon) {
        for (MonAn m : danhSachMonAn) {
            if (m.getMaMon() == maMon) {
                m.setConMon(conMon);
                break;
            }
        }
    }
}
