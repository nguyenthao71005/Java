package com.restaurant.model;

/**
 * Entity model ánh xạ với bảng CSDL: `ChiTietHoaDon`
 * =========================================================================
 * MySQL Schema:
 *   CREATE TABLE ChiTietHoaDon (
 *       MaCTHD INT AUTO_INCREMENT PRIMARY KEY,
 *       MaHD INT NOT NULL,
 *       MaMon INT NOT NULL,
 *       SoLuong INT NOT NULL DEFAULT 1,
 *       DonGia DOUBLE NOT NULL,
 *       GhiChu VARCHAR(255),
 *       TrangThaiMon VARCHAR(30) DEFAULT 'ChoLam', -- 'ChoLam', 'DangLam', 'DaRaMon', 'DaHuy'
 *       FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD),
 *       FOREIGN KEY (MaMon) REFERENCES MonAn(MaMon)
 *   );
 * =========================================================================
 */
public class ChiTietHoaDon {
    private int maCTHD;
    private int maHD;
    private int maMon;
    private String tenMon;
    private int soLuong;
    private double donGia;
    private String ghiChu;
    private String trangThaiMon; // "ChoLam", "DangLam", "DaRaMon", "DaHuy"

    public ChiTietHoaDon() {
        this.soLuong = 1;
        this.trangThaiMon = "ChoLam";
    }

    public ChiTietHoaDon(int maCTHD, int maHD, int maMon, String tenMon, int soLuong, double donGia, String ghiChu, String trangThaiMon) {
        this.maCTHD = maCTHD;
        this.maHD = maHD;
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.ghiChu = ghiChu;
        this.trangThaiMon = trangThaiMon;
    }

    public ChiTietHoaDon(int maCTHD, int maHD, int maMon, String tenMon, int soLuong, double donGia, String ghiChu) {
        this(maCTHD, maHD, maMon, tenMon, soLuong, donGia, ghiChu, "DaRaMon");
    }

    public int getMaCTHD() {
        return maCTHD;
    }

    public void setMaCTHD(int maCTHD) {
        this.maCTHD = maCTHD;
    }

    public int getMaHD() {
        return maHD;
    }

    public void setMaHD(int maHD) {
        this.maHD = maHD;
    }

    public int getMaMon() {
        return maMon;
    }

    public void setMaMon(int maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public double getThanhTien() {
        return soLuong * donGia;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTrangThaiMon() {
        return trangThaiMon;
    }

    public void setTrangThaiMon(String trangThaiMon) {
        this.trangThaiMon = trangThaiMon;
    }
}
