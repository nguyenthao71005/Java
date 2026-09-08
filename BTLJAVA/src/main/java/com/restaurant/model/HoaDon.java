package com.restaurant.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity model ánh xạ với bảng CSDL: `HoaDon`
 * =========================================================================
 * MySQL Schema:
 *   CREATE TABLE HoaDon (
 *       MaHD INT AUTO_INCREMENT PRIMARY KEY,
 *       MaBan INT NOT NULL,
 *       NgayLap DATETIME DEFAULT CURRENT_TIMESTAMP,
 *       GiamGiaPhanTram DOUBLE DEFAULT 0,
 *       VatPhanTram DOUBLE DEFAULT 8,
 *       TongTien DOUBLE DEFAULT 0,
 *       HinhThucTT VARCHAR(50) DEFAULT 'TienMat', -- 'TienMat', 'ChuyenKhoan', 'The'
 *       TrangThai VARCHAR(30) DEFAULT 'ChuaThanhToan', -- 'ChuaThanhToan', 'DaThanhToan', 'DaHuy'
 *       MaNV INT,
 *       FOREIGN KEY (MaBan) REFERENCES Ban(MaBan)
 *   );
 * =========================================================================
 */
public class HoaDon {
    private int maHD;
    private int maBan;
    private String tenBan;
    private Date ngayLap;
    private double giamGiaPhanTram; // Ví dụ: 5.0 = 5%
    private double vatPhanTram;     // Ví dụ: 8.0 = 8%
    private double tongTien;
    private String hinhThucTT;      // "TienMat", "ChuyenKhoan", "The"
    private String trangThai;       // "ChuaThanhToan", "DaThanhToan", "DaHuy"
    private int maNV;
    private Integer maCa;
    private List<ChiTietHoaDon> danhSachChiTiet;

    public HoaDon() {
        this.ngayLap = new Date();
        this.vatPhanTram = 8.0;
        this.giamGiaPhanTram = 0.0;
        this.hinhThucTT = "Tiền mặt";
        this.trangThai = "ChuaThanhToan";
        this.danhSachChiTiet = new ArrayList<>();
    }

    public HoaDon(int maHD, int maBan, String tenBan) {
        this();
        this.maHD = maHD;
        this.maBan = maBan;
        this.tenBan = tenBan;
    }

    public int getMaHD() {
        return maHD;
    }

    public void setMaHD(int maHD) {
        this.maHD = maHD;
    }

    public int getMaBan() {
        return maBan;
    }

    public void setMaBan(int maBan) {
        this.maBan = maBan;
    }

    public String getTenBan() {
        return tenBan;
    }

    public void setTenBan(String tenBan) {
        this.tenBan = tenBan;
    }

    public Date getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(Date ngayLap) {
        this.ngayLap = ngayLap;
    }

    public double getGiamGiaPhanTram() {
        return giamGiaPhanTram;
    }

    public void setGiamGiaPhanTram(double giamGiaPhanTram) {
        this.giamGiaPhanTram = giamGiaPhanTram;
    }

    public double getVatPhanTram() {
        return vatPhanTram;
    }

    public void setVatPhanTram(double vatPhanTram) {
        this.vatPhanTram = vatPhanTram;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public String getHinhThucTT() {
        return hinhThucTT;
    }

    public void setHinhThucTT(String hinhThucTT) {
        this.hinhThucTT = hinhThucTT;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public Integer getMaCa() {
        return maCa;
    }

    public void setMaCa(Integer maCa) {
        this.maCa = maCa;
    }

    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }

    public double getTamTinh() {
        double sum = 0;
        if (danhSachChiTiet != null) {
            for (ChiTietHoaDon ct : danhSachChiTiet) {
                if (!"DaHuy".equalsIgnoreCase(ct.getTrangThaiMon())) {
                    sum += ct.getThanhTien();
                }
            }
        }
        return sum;
    }

    public double getTienGiamGia() {
        return getTamTinh() * (giamGiaPhanTram / 100.0);
    }

    public double getTienVAT() {
        double afterDiscount = getTamTinh() - getTienGiamGia();
        return (afterDiscount > 0 ? afterDiscount : 0) * (vatPhanTram / 100.0);
    }

    private String tenNV;
    private Date gioVao;
    private Date gioRa;

    public String getTenNV() {
        return tenNV != null ? tenNV : "Nhân viên";
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public Date getGioVao() {
        return gioVao != null ? gioVao : ngayLap;
    }

    public void setGioVao(Date gioVao) {
        this.gioVao = gioVao;
    }

    public Date getGioRa() {
        return gioRa;
    }

    public void setGioRa(Date gioRa) {
        this.gioRa = gioRa;
    }

    public double tinhTongCong() {
        double sub = getTamTinh();
        double discount = getTienGiamGia();
        double vat = getTienVAT();
        this.tongTien = sub - discount + vat;
        return this.tongTien;
    }
}
