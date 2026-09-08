package com.restaurant.model;

/**
 * Entity model ánh xạ với bảng CSDL: `NhanVien`
 * =========================================================================
 * MySQL Schema:
 *   CREATE TABLE NhanVien (
 *       MaNV INT AUTO_INCREMENT PRIMARY KEY,
 *       HoTen VARCHAR(100) NOT NULL,
 *       TenDangNhap VARCHAR(50) UNIQUE NOT NULL,
 *       MatKhau VARCHAR(255) NOT NULL,
 *       VaiTro VARCHAR(30) DEFAULT 'PhucVu', -- 'QuanLy', 'ThuNgan', 'PhucVu', 'Bep'
 *       TrangThai VARCHAR(20) DEFAULT 'DangLam' -- 'DangLam', 'NghiViec'
 *   );
 * =========================================================================
 */
public class NhanVien {
    private int maNV;
    private String hoTen;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;   // "QuanLy", "ThuNgan", "PhucVu", "Bep"
    private String trangThai; // "DangLam", "NghiViec"

    public NhanVien() {
        this.vaiTro = "PhucVu";
        this.trangThai = "DangLam";
    }

    public NhanVien(int maNV, String hoTen, String tenDangNhap, String matKhau, String vaiTro, String trangThai) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
    }

    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
