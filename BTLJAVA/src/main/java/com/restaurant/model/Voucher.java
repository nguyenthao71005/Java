package com.restaurant.model;

/**
 * Entity model ánh xạ với bảng CSDL: `Voucher`
 */
public class Voucher {
    private String maVoucher;
    private String tenVoucher;
    private double phanTramGiam;
    private double giamToiDa;
    private double donHangToiThieu;
    private String ngayBatDau;
    private String ngayKetThuc;
    private String trangThai; // "HoatDong", "TamDung", "HetHan"

    public Voucher() {
        this.phanTramGiam = 0.0;
        this.giamToiDa = 0.0;
        this.donHangToiThieu = 0.0;
        this.trangThai = "HoatDong";
    }

    public Voucher(String maVoucher, String tenVoucher, double phanTramGiam, double giamToiDa, double donHangToiThieu, String ngayBatDau, String ngayKetThuc, String trangThai) {
        this.maVoucher = maVoucher;
        this.tenVoucher = tenVoucher;
        this.phanTramGiam = phanTramGiam;
        this.giamToiDa = giamToiDa;
        this.donHangToiThieu = donHangToiThieu;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    public String getMaVoucher() {
        return maVoucher;
    }

    public void setMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher;
    }

    public String getTenVoucher() {
        return tenVoucher;
    }

    public void setTenVoucher(String tenVoucher) {
        this.tenVoucher = tenVoucher;
    }

    public double getPhanTramGiam() {
        return phanTramGiam;
    }

    public void setPhanTramGiam(double phanTramGiam) {
        this.phanTramGiam = phanTramGiam;
    }

    public double getGiamToiDa() {
        return giamToiDa;
    }

    public void setGiamToiDa(double giamToiDa) {
        this.giamToiDa = giamToiDa;
    }

    public double getDonHangToiThieu() {
        return donHangToiThieu;
    }

    public void setDonHangToiThieu(double donHangToiThieu) {
        this.donHangToiThieu = donHangToiThieu;
    }

    public String getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(String ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public String getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(String ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return maVoucher + " - " + tenVoucher + " (" + phanTramGiam + "%)";
    }
}
