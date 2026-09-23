package com.restaurant.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model Lịch sử truy cập - Theo dõi đăng nhập/đăng xuất
 */
public class LichSuTruyCap {

    private int maTruyCap;
    private Integer maNV;
    private String tenDangNhap;
    private String hoTen;
    private HanhDong hanhDong;
    private LocalDateTime thoiGian;
    private String diaChiIP;
    private String trinhDuyet;
    private String mayTinh;
    private TrangThai trangThai;
    private String ghiChu;

    public enum HanhDong {
        DangNhap("Đăng nhập"),
        DangXuat("Đăng xuất"),
        DangNhapThatBai("Đăng nhập thất bại");

        private final String moTa;

        HanhDong(String moTa) {
            this.moTa = moTa;
        }

        public String getMoTa() {
            return moTa;
        }
    }

    public enum TrangThai {
        ThanhCong("Thành công"),
        ThatBai("Thất bại");

        private final String moTa;

        TrangThai(String moTa) {
            this.moTa = moTa;
        }

        public String getMoTa() {
            return moTa;
        }
    }

    // Constructors
    public LichSuTruyCap() {
    }

    public LichSuTruyCap(String tenDangNhap, HanhDong hanhDong, TrangThai trangThai) {
        this.tenDangNhap = tenDangNhap;
        this.hanhDong = hanhDong;
        this.trangThai = trangThai;
        this.thoiGian = LocalDateTime.now();
    }

    public LichSuTruyCap(Integer maNV, String tenDangNhap, String hoTen, HanhDong hanhDong,
                          TrangThai trangThai, String diaChiIP, String ghiChu) {
        this.maNV = maNV;
        this.tenDangNhap = tenDangNhap;
        this.hoTen = hoTen;
        this.hanhDong = hanhDong;
        this.trangThai = trangThai;
        this.diaChiIP = diaChiIP;
        this.ghiChu = ghiChu;
        this.thoiGian = LocalDateTime.now();
    }

    // Getters and Setters
    public int getMaTruyCap() {
        return maTruyCap;
    }

    public void setMaTruyCap(int maTruyCap) {
        this.maTruyCap = maTruyCap;
    }

    public Integer getMaNV() {
        return maNV;
    }

    public void setMaNV(Integer maNV) {
        this.maNV = maNV;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public HanhDong getHanhDong() {
        return hanhDong;
    }

    public void setHanhDong(HanhDong hanhDong) {
        this.hanhDong = hanhDong;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getDiaChiIP() {
        return diaChiIP;
    }

    public void setDiaChiIP(String diaChiIP) {
        this.diaChiIP = diaChiIP;
    }

    public String getTrinhDuyet() {
        return trinhDuyet;
    }

    public void setTrinhDuyet(String trinhDuyet) {
        this.trinhDuyet = trinhDuyet;
    }

    public String getMayTinh() {
        return mayTinh;
    }

    public void setMayTinh(String mayTinh) {
        this.mayTinh = mayTinh;
    }

    public TrangThai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThai trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getThoiGianFormatted() {
        if (thoiGian == null) return "";
        return thoiGian.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public String getThoiGianDate() {
        if (thoiGian == null) return "";
        return thoiGian.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getThoiGianTime() {
        if (thoiGian == null) return "";
        return thoiGian.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
