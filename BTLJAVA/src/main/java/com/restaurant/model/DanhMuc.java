package com.restaurant.model;

/**
 * Entity model ánh xạ với bảng CSDL: `DanhMuc`
 * =========================================================================
 * MySQL Schema:
 *   CREATE TABLE DanhMuc (
 *       MaDM INT AUTO_INCREMENT PRIMARY KEY,
 *       TenDM VARCHAR(100) NOT NULL,
 *       MoTa TEXT
 *   );
 * =========================================================================
 */
public class DanhMuc {
    private int maDM;
    private String tenDM;
    private String moTa;

    public DanhMuc() {}

    public DanhMuc(int maDM, String tenDM, String moTa) {
        this.maDM = maDM;
        this.tenDM = tenDM;
        this.moTa = moTa;
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

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return tenDM;
    }
}
