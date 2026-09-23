package com.restaurant.service;

import com.restaurant.dao.LichSuTruyCapDAO;
import com.restaurant.dao.LichSuThaoTacDAO;
import com.restaurant.model.LichSuTruyCap;
import com.restaurant.model.LichSuThaoTac;
import com.restaurant.model.NhanVien;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service ghi nhận mọi thao tác Audit trên hệ thống
 * Singleton pattern để đảm bảo chỉ có 1 instance
 */
public class AuditService {

    private static AuditService instance;
    private final LichSuTruyCapDAO lichSuTruyCapDAO;
    private final LichSuThaoTacDAO lichSuThaoTacDAO;

    private AuditService() {
        this.lichSuTruyCapDAO = new LichSuTruyCapDAO();
        this.lichSuThaoTacDAO = new LichSuThaoTacDAO();
    }

    public static synchronized AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    // ==================== LỊCH SỬ TRUY CẬP ====================

    /**
     * Ghi nhận đăng nhập thành công
     */
    public void ghiDangNhap(NhanVien nhanVien) {
        AuditSwingWorker.ghiDangNhapAsync(
            nhanVien != null ? nhanVien.getMaNV() : null,
            nhanVien != null ? nhanVien.getTenDangNhap() : "unknown",
            nhanVien != null ? nhanVien.getHoTen() : null,
            nhanVien != null ? nhanVien.getVaiTro() : null,
            LichSuTruyCap.HanhDong.DangNhap,
            LichSuTruyCap.TrangThai.ThanhCong,
            null
        );
    }

    /**
     * Ghi nhận đăng nhập thất bại
     */
    public void ghiDangNhapThatBai(String tenDangNhap, String lyDo) {
        AuditSwingWorker.ghiDangNhapAsync(
            null,
            tenDangNhap,
            null,
            null, // Không có vai trò khi đăng nhập thất bại
            LichSuTruyCap.HanhDong.DangNhapThatBai,
            LichSuTruyCap.TrangThai.ThatBai,
            lyDo
        );
    }

    /**
     * Ghi nhận đăng xuất
     */
    public void ghiDangXuat(NhanVien nhanVien) {
        if (nhanVien == null) return;

        AuditSwingWorker.ghiDangNhapAsync(
            nhanVien.getMaNV(),
            nhanVien.getTenDangNhap(),
            nhanVien.getHoTen(),
            nhanVien.getVaiTro(),
            LichSuTruyCap.HanhDong.DangXuat,
            LichSuTruyCap.TrangThai.ThanhCong,
            null
        );
    }

    // ==================== LỊCH SỬ THAO TÁC CRUD ====================

    /**
     * Ghi nhận thao tác INSERT (Tạo mới)
     */
    public void ghiTaoMoi(NhanVien nhanVien, String tenBang, String moTa, String banGhiMoi) {
        String hoTen = nhanVien != null ? nhanVien.getHoTen() : "Hệ thống";
        Integer maNV = nhanVien != null ? nhanVien.getMaNV() : null;
        AuditSwingWorker.ghiTaoMoiAsync(maNV, hoTen, tenBang, moTa, banGhiMoi);
    }

    /**
     * Ghi nhận thao tác UPDATE (Cập nhật)
     */
    public void ghiCapNhat(NhanVien nhanVien, String tenBang, String moTa, String banGhiCu, String banGhiMoi) {
        String hoTen = nhanVien != null ? nhanVien.getHoTen() : "Hệ thống";
        Integer maNV = nhanVien != null ? nhanVien.getMaNV() : null;
        AuditSwingWorker.ghiCapNhatAsync(maNV, hoTen, tenBang, moTa, banGhiCu, banGhiMoi);
    }

    /**
     * Ghi nhận thao tác DELETE (Xóa)
     */
    public void ghiXoa(NhanVien nhanVien, String tenBang, String moTa, String banGhiCu) {
        String hoTen = nhanVien != null ? nhanVien.getHoTen() : "Hệ thống";
        Integer maNV = nhanVien != null ? nhanVien.getMaNV() : null;
        AuditSwingWorker.ghiXoaAsync(maNV, hoTen, tenBang, moTa, banGhiCu);
    }

    /**
     * Ghi nhận thao tác với chi tiết thay đổi
     */
    public void ghiThaoTacChiTiet(NhanVien nhanVien, String tenBang, 
                                   LichSuThaoTac.HanhDong hanhDong,
                                   String moTa, String chiTiet) {
        String hoTen = nhanVien != null ? nhanVien.getHoTen() : "Hệ thống";
        Integer maNV = nhanVien != null ? nhanVien.getMaNV() : null;
        AuditSwingWorker.ghiThaoTacChiTietAsync(maNV, hoTen, tenBang, hanhDong, moTa, chiTiet);
    }

    // ==================== CÁC THAO TÁC NGHIỆP VỤ ĐẶC BIỆT ====================

    /**
     * Ghi thao tác thanh toán
     */
    public void ghiThanhToan(NhanVien nhanVien, int maHD, String tenBan, double tongTien) {
        String moTa = String.format("Thanh toán hóa đơn #%d - Bàn %s: %,.0f VNĐ", 
            maHD, tenBan, tongTien);
        String chiTiet = String.format("Mã HD: %d | Bàn: %s | Tổng tiền: %,.0f VNĐ", 
            maHD, tenBan, tongTien);
        
        ghiThaoTacChiTiet(nhanVien, "HoaDon", 
            LichSuThaoTac.HanhDong.UPDATE, moTa, chiTiet);
    }

    /**
     * Ghi thao tác tạo order
     */
    public void ghiTaoOrder(NhanVien nhanVien, int maBan, String dsMon) {
        String moTa = String.format("Tạo order cho Bàn %d", maBan);
        String chiTiet = "Danh sách món: " + dsMon;
        
        ghiThaoTacChiTiet(nhanVien, "ChiTietHoaDon", 
            LichSuThaoTac.HanhDong.INSERT, moTa, chiTiet);
    }

    /**
     * Ghi thao tác chuyển bàn
     */
    public void ghiChuyenBan(NhanVien nhanVien, int tuBan, int denBan) {
        String moTa = String.format("Chuyển bàn từ Bàn %d sang Bàn %d", tuBan, denBan);
        String chiTiet = String.format("Từ Bàn %d → Bàn %d", tuBan, denBan);
        
        ghiThaoTacChiTiet(nhanVien, "HoaDon", 
            LichSuThaoTac.HanhDong.UPDATE, moTa, chiTiet);
    }

    /**
     * Ghi thao tác mở ca
     */
    public void ghiMoCa(NhanVien nhanVien, int maCa, double tienDauCa) {
        String moTa = String.format("Mở ca #%d với tiền đầu ca: %,.0f VNĐ", maCa, tienDauCa);
        String chiTiet = String.format("Mã ca: %d | Tiền đầu ca: %,.0f VNĐ", maCa, tienDauCa);
        
        ghiThaoTacChiTiet(nhanVien, "CaLamViec", 
            LichSuThaoTac.HanhDong.INSERT, moTa, chiTiet);
    }

    /**
     * Ghi thao tác đóng ca
     */
    public void ghiDongCa(NhanVien nhanVien, int maCa, double chenhLech) {
        String moTa = String.format("Đóng ca #%d - Chênh lệch: %,.0f VNĐ", maCa, chenhLech);
        String chiTiet = String.format("Mã ca: %d | Chênh lệch: %,.0f VNĐ (%s)", 
            maCa, chenhLech, chenhLech == 0 ? "Khớp tiền" : (chenhLech > 0 ? "Thừa" : "Thiếu"));
        
        ghiThaoTacChiTiet(nhanVien, "CaLamViec", 
            LichSuThaoTac.HanhDong.UPDATE, moTa, chiTiet);
    }

    /**
     * Ghi thao tác cập nhật trạng thái món trong bếp
     */
    public void ghiCapNhatTrangThaiMon(int maCTHD, String tenMon, String trangThaiCu, String trangThaiMoi, String nguoiThucHien) {
        String moTa = String.format("Cập nhật trạng thái món: %s", tenMon);
        String chiTiet = String.format("Món: %s | %s → %s | Bởi: %s", 
            tenMon, trangThaiCu, trangThaiMoi, nguoiThucHien);
        
        ghiThaoTacChiTiet(null, "ChiTietHoaDon", 
            LichSuThaoTac.HanhDong.UPDATE, moTa, chiTiet);
    }

    // ==================== LẤY DỮ LIỆU ====================

    /**
     * Lấy lịch sử truy cập gần đây
     */
    public List<LichSuTruyCap> getLichSuTruyCap(int limit) {
        return lichSuTruyCapDAO.getRecent(limit);
    }

    /**
     * Lấy lịch sử truy cập theo khoảng thời gian
     */
    public List<LichSuTruyCap> getLichSuTruyCap(LocalDateTime tuNgay, LocalDateTime denNgay) {
        return lichSuTruyCapDAO.getByKhoangThoiGian(tuNgay, denNgay);
    }

    /**
     * Lấy lịch sử truy cập theo nhân viên
     */
    public List<LichSuTruyCap> getLichSuTruyCap(int maNV, int limit) {
        List<LichSuTruyCap> all = lichSuTruyCapDAO.getByMaNV(maNV);
        return all.size() > limit ? all.subList(0, limit) : all;
    }

    /**
     * Lấy lịch sử thao tác gần đây
     */
    public List<LichSuThaoTac> getLichSuThaoTac(int limit) {
        return lichSuThaoTacDAO.getRecent(limit);
    }

    /**
     * Lấy lịch sử thao tác theo khoảng thời gian
     */
    public List<LichSuThaoTac> getLichSuThaoTac(LocalDateTime tuNgay, LocalDateTime denNgay) {
        return lichSuThaoTacDAO.getByKhoangThoiGian(tuNgay, denNgay);
    }

    /**
     * Lấy lịch sử thao tác theo bảng
     */
    public List<LichSuThaoTac> getLichSuThaoTacTheoBang(String tenBang, int limit) {
        List<LichSuThaoTac> all = lichSuThaoTacDAO.getByBang(tenBang);
        return all.size() > limit ? all.subList(0, limit) : all;
    }

    /**
     * Tìm kiếm lịch sử thao tác
     */
    public List<LichSuThaoTac> searchLichSuThaoTac(String tuKhoa) {
        return lichSuThaoTacDAO.search(tuKhoa);
    }

    // ==================== UTILITY ====================

    private String getDiaChiIP() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return ip.getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    private String getMayTinh() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return ip.getHostName();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    /**
     * Kiểm tra số lần đăng nhập thất bại trong ngày
     */
    public int demDangNhapThatBaiTrongNgay(String tenDangNhap) {
        return lichSuTruyCapDAO.demDangNhapThatBai(tenDangNhap, new java.sql.Date(System.currentTimeMillis()));
    }
}
