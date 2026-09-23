package com.restaurant.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model Lịch sử thao tác - Ghi nhận mọi thao tác CRUD trên hệ thống
 */
public class LichSuThaoTac {

    private int maThaoTac;
    private Integer maNV;
    private String hoTen;
    private String tenBang;
    private HanhDong hanhDong;
    private String banGhiCu;
    private String banGhiMoi;
    private String moTa;
    private String chiTiet;
    private LocalDateTime thoiGian;
    private String diaChiIP;
    private String mayTinh;

    public enum HanhDong {
        INSERT("Tạo mới"),
        UPDATE("Cập nhật"),
        DELETE("Xóa");

        private final String moTa;

        HanhDong(String moTa) {
            this.moTa = moTa;
        }

        public String getMoTa() {
            return moTa;
        }

        public static HanhDong fromString(String value) {
            if (value == null) return UPDATE;
            try {
                return HanhDong.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return UPDATE;
            }
        }
    }

    // Constructors
    public LichSuThaoTac() {
    }

    public LichSuThaoTac(Integer maNV, String hoTen, String tenBang, HanhDong hanhDong,
                          String moTa, String chiTiet) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.tenBang = tenBang;
        this.hanhDong = hanhDong;
        this.moTa = moTa;
        this.chiTiet = chiTiet;
        this.thoiGian = LocalDateTime.now();
    }

    // Builder pattern for convenience
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final LichSuThaoTac instance = new LichSuThaoTac();

        public Builder maNV(Integer maNV) {
            instance.maNV = maNV;
            return this;
        }

        public Builder hoTen(String hoTen) {
            instance.hoTen = hoTen;
            return this;
        }

        public Builder tenBang(String tenBang) {
            instance.tenBang = tenBang;
            return this;
        }

        public Builder hanhDong(HanhDong hanhDong) {
            instance.hanhDong = hanhDong;
            return this;
        }

        public Builder banGhiCu(String banGhiCu) {
            instance.banGhiCu = banGhiCu;
            return this;
        }

        public Builder banGhiMoi(String banGhiMoi) {
            instance.banGhiMoi = banGhiMoi;
            return this;
        }

        public Builder moTa(String moTa) {
            instance.moTa = moTa;
            return this;
        }

        public Builder chiTiet(String chiTiet) {
            instance.chiTiet = chiTiet;
            return this;
        }

        public Builder diaChiIP(String diaChiIP) {
            instance.diaChiIP = diaChiIP;
            return this;
        }

        public Builder mayTinh(String mayTinh) {
            instance.mayTinh = mayTinh;
            return this;
        }

        public LichSuThaoTac build() {
            instance.thoiGian = LocalDateTime.now();
            return instance;
        }
    }

    // Getters and Setters
    public int getMaThaoTac() {
        return maThaoTac;
    }

    public void setMaThaoTac(int maThaoTac) {
        this.maThaoTac = maThaoTac;
    }

    public Integer getMaNV() {
        return maNV;
    }

    public void setMaNV(Integer maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getTenBang() {
        return tenBang;
    }

    public void setTenBang(String tenBang) {
        this.tenBang = tenBang;
    }

    public HanhDong getHanhDong() {
        return hanhDong;
    }

    public void setHanhDong(HanhDong hanhDong) {
        this.hanhDong = hanhDong;
    }

    public String getBanGhiCu() {
        return banGhiCu;
    }

    public void setBanGhiCu(String banGhiCu) {
        this.banGhiCu = banGhiCu;
    }

    public String getBanGhiMoi() {
        return banGhiMoi;
    }

    public void setBanGhiMoi(String banGhiMoi) {
        this.banGhiMoi = banGhiMoi;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(String chiTiet) {
        this.chiTiet = chiTiet;
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

    public String getMayTinh() {
        return mayTinh;
    }

    public void setMayTinh(String mayTinh) {
        this.mayTinh = mayTinh;
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

    /**
     * Lấy icon cho loại hành động (để hiển thị trong UI)
     */
    public String getIconForHanhDong() {
        if (hanhDong == null) return "📝";
        switch (hanhDong) {
            case INSERT:
                return "➕";
            case UPDATE:
                return "✏️";
            case DELETE:
                return "🗑️";
            default:
                return "📝";
        }
    }
}
