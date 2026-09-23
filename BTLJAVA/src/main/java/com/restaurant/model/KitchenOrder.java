package com.restaurant.model;

import java.sql.Timestamp;

/**
 * Model biểu diễn một món ăn cần chế biến trong Bếp & Bar
 * Phục vụ màn hình Điều hành Bếp (Kitchen Display System - KDS)
 */
public class KitchenOrder {

    private int maCTHD;
    private int maHD;
    private int maBan;
    private String tenBan;
    private String khuVuc;
    private int maMon;
    private String tenMon;
    private int soLuong;
    private double donGia;
    private String ghiChu;
    private String trangThaiMon; // "DangCho", "DangLam", "DaRaMon", "DaHuy"
    private Timestamp thoiGianGoi;

    public KitchenOrder() {
        this.trangThaiMon = "DangCho";
        this.thoiGianGoi = new Timestamp(System.currentTimeMillis());
    }

    public KitchenOrder(int maCTHD, int maHD, int maBan, String tenBan, String khuVuc,
                        int maMon, String tenMon, int soLuong, double donGia,
                        String ghiChu, String trangThaiMon, Timestamp thoiGianGoi) {
        this.maCTHD = maCTHD;
        this.maHD = maHD;
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.khuVuc = khuVuc;
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.ghiChu = ghiChu;
        this.trangThaiMon = trangThaiMon;
        this.thoiGianGoi = thoiGianGoi;
    }

    /**
     * Tính thời gian chờ chế biến (phút) kể từ lúc gọi
     */
    public long getSoPhutCho() {
        if (thoiGianGoi == null) return 0;
        long diff = System.currentTimeMillis() - thoiGianGoi.getTime();
        return Math.max(0, diff / (60 * 1000));
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

    public String getKhuVuc() {
        return khuVuc;
    }

    public void setKhuVuc(String khuVuc) {
        this.khuVuc = khuVuc;
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

    public String getGhiChu() {
        return ghiChu != null ? ghiChu : "";
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

    public Timestamp getThoiGianGoi() {
        return thoiGianGoi;
    }

    public void setThoiGianGoi(Timestamp thoiGianGoi) {
        this.thoiGianGoi = thoiGianGoi;
    }
}
