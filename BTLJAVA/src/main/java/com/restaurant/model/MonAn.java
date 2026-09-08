package com.restaurant.model;

/**
 * Entity model ánh xạ với bảng CSDL: `MonAn`
 * =========================================================================
 * MySQL Schema:
 *   CREATE TABLE MonAn (
 *       MaMon INT AUTO_INCREMENT PRIMARY KEY,
 *       TenMon VARCHAR(150) NOT NULL,
 *       MaDM INT,
 *       GiaBan DOUBLE NOT NULL,
 *       HinhAnh VARCHAR(255),
 *       ConMon BOOLEAN DEFAULT TRUE,
 *       FOREIGN KEY (MaDM) REFERENCES DanhMuc(MaDM)
 *   );
 * =========================================================================
 */
public class MonAn {
    private int maMon;
    private String tenMon;
    private int maDM;
    private String tenDM; // Tên danh mục (lấy từ JOIN)
    private double giaBan;
    private String hinhAnh;
    private boolean conMon;

    public MonAn() {
        this.conMon = true;
    }

    public MonAn(int maMon, String tenMon, int maDM, String tenDM, double giaBan, String hinhAnh, boolean conMon) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.maDM = maDM;
        this.tenDM = tenDM;
        this.giaBan = giaBan;
        this.hinhAnh = hinhAnh;
        this.conMon = conMon;
    }

    public MonAn(int maMon, String tenMon, int maDM, double giaBan, String hinhAnh, boolean conMon) {
        this(maMon, tenMon, maDM, "", giaBan, hinhAnh, conMon);
    }

    public double getDonGia() {
        return giaBan;
    }

    public void setDonGia(double donGia) {
        this.giaBan = donGia;
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

    public int getMaDM() {
        return maDM;
    }

    public void setMaDM(int maDM) {
        this.maDM = maDM;
    }

    public String getTenDM() {
        return tenDM;
    }

    public void setTenDM(String tenDM) {
        this.tenDM = tenDM;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public boolean isConMon() {
        return conMon;
    }

    public void setConMon(boolean conMon) {
        this.conMon = conMon;
    }

    @Override
    public String toString() {
        return tenMon;
    }
}
