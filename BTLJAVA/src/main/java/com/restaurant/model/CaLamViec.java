package com.restaurant.model;

import java.sql.Timestamp;

/**
 * Entity Model ánh xạ với bảng CSDL: `CaLamViec`
 * Nghiệp vụ: Quản lý ca thu ngân, tiền đầu ca, doanh thu tiền mặt, chuyển khoản,
 * tiền lý thuyết, tiền thực tế kiểm đếm, chênh lệch thừa/thiếu và bàn giao ca.
 */
public class CaLamViec {

    private int maCa;
    private int maNV;
    private String tenNV;
    private Timestamp thoiGianMo;
    private Timestamp thoiGianDong;
    private double tienDauCa;
    private double doanhThuTienMat;
    private double doanhThuChuyenKhoan;
    private double tongDoanhThu;
    private double tienLyThuyet;
    private Double tienThucTe; // Có thể null khi ca đang mở
    private Double chenhLech;   // tienThucTe - tienLyThuyet
    private String trangThai;  // "DangMo", "DaDong"
    private String ghiChu;
    private int soHoaDon;

    public CaLamViec() {
        this.trangThai = "DangMo";
        this.thoiGianMo = new Timestamp(System.currentTimeMillis());
    }

    public CaLamViec(int maNV, String tenNV, double tienDauCa, String ghiChu) {
        this();
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.tienDauCa = tienDauCa;
        this.ghiChu = ghiChu;
        tinhTienLyThuyet();
    }

    public CaLamViec(int maCa, int maNV, String tenNV, Timestamp thoiGianMo, Timestamp thoiGianDong,
                     double tienDauCa, double doanhThuTienMat, double doanhThuChuyenKhoan, double tongDoanhThu,
                     double tienLyThuyet, Double tienThucTe, Double chenhLech, String trangThai, String ghiChu) {
        this.maCa = maCa;
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.thoiGianMo = thoiGianMo;
        this.thoiGianDong = thoiGianDong;
        this.tienDauCa = tienDauCa;
        this.doanhThuTienMat = doanhThuTienMat;
        this.doanhThuChuyenKhoan = doanhThuChuyenKhoan;
        this.tongDoanhThu = tongDoanhThu;
        this.tienLyThuyet = tienLyThuyet;
        this.tienThucTe = tienThucTe;
        this.chenhLech = chenhLech;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    /**
     * Tự động tính toán tiền lý thuyết trong két
     * Tiền lý thuyết = Tiền mặt đầu ca + Doanh thu tiền mặt
     */
    public void tinhTienLyThuyet() {
        this.tienLyThuyet = this.tienDauCa + this.doanhThuTienMat;
    }

    /**
     * Tự động tính chênh lệch khi có tiền thực tế kiểm đếm
     * Chênh lệch = Tiền thực tế - Tiền lý thuyết
     */
    public void tinhChenhLech() {
        tinhTienLyThuyet();
        if (this.tienThucTe != null) {
            this.chenhLech = this.tienThucTe - this.tienLyThuyet;
        } else {
            this.chenhLech = null;
        }
    }

    public boolean isDangMo() {
        return "DangMo".equalsIgnoreCase(this.trangThai);
    }

    // Getters and Setters

    public int getMaCa() {
        return maCa;
    }

    public void setMaCa(int maCa) {
        this.maCa = maCa;
    }

    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV != null ? tenNV : "Nhân viên #" + maNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public Timestamp getThoiGianMo() {
        return thoiGianMo;
    }

    public void setThoiGianMo(Timestamp thoiGianMo) {
        this.thoiGianMo = thoiGianMo;
    }

    public Timestamp getThoiGianDong() {
        return thoiGianDong;
    }

    public void setThoiGianDong(Timestamp thoiGianDong) {
        this.thoiGianDong = thoiGianDong;
    }

    public double getTienDauCa() {
        return tienDauCa;
    }

    public void setTienDauCa(double tienDauCa) {
        this.tienDauCa = tienDauCa;
        tinhTienLyThuyet();
    }

    public double getDoanhThuTienMat() {
        return doanhThuTienMat;
    }

    public void setDoanhThuTienMat(double doanhThuTienMat) {
        this.doanhThuTienMat = doanhThuTienMat;
        tinhTienLyThuyet();
    }

    public double getDoanhThuChuyenKhoan() {
        return doanhThuChuyenKhoan;
    }

    public void setDoanhThuChuyenKhoan(double doanhThuChuyenKhoan) {
        this.doanhThuChuyenKhoan = doanhThuChuyenKhoan;
    }

    public double getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(double tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }

    public double getTienLyThuyet() {
        return tienLyThuyet;
    }

    public void setTienLyThuyet(double tienLyThuyet) {
        this.tienLyThuyet = tienLyThuyet;
    }

    public Double getTienThucTe() {
        return tienThucTe;
    }

    public void setTienThucTe(Double tienThucTe) {
        this.tienThucTe = tienThucTe;
        tinhChenhLech();
    }

    public Double getChenhLech() {
        return chenhLech;
    }

    public void setChenhLech(Double chenhLech) {
        this.chenhLech = chenhLech;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public int getSoHoaDon() {
        return soHoaDon;
    }

    public void setSoHoaDon(int soHoaDon) {
        this.soHoaDon = soHoaDon;
    }
}
