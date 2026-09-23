package com.restaurant.model;

/**
 * Entity model ánh xạ với bảng CSDL: `Ban`
 * =========================================================================
 * MySQL Schema:
 *   CREATE TABLE Ban (
 *       MaBan INT AUTO_INCREMENT PRIMARY KEY,
 *       TenBan VARCHAR(50) NOT NULL,
 *       KhuVuc VARCHAR(50) DEFAULT 'Tầng 1',
 *       SucChua INT DEFAULT 4,
 *       TrangThai VARCHAR(20) DEFAULT 'Trong' -- 'Trong', 'CoKhach', 'DatTruoc'
 *   );
 * =========================================================================
 */
public class Ban {
    private int maBan;
    private String tenBan;
    private String khuVuc;
    private int sucChua;
    private String trangThai; // "Trong" (Xanh lá), "CoKhach" (Đỏ), "DatTruoc" (Vàng)

    public Ban() {}

    public Ban(int maBan, String tenBan, String khuVuc, int sucChua, String trangThai) {
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.khuVuc = khuVuc;
        this.sucChua = sucChua;
        this.trangThai = trangThai;
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

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        this.sucChua = sucChua;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isCoKhach() {
        return "CoKhach".equalsIgnoreCase(this.trangThai);
    }

    @Override
    public String toString() {
        return tenBan + " (" + khuVuc + ")";
    }
}
