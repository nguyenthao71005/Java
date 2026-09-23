package com.restaurant.service;

import com.restaurant.dao.LichSuTruyCapDAO;
import com.restaurant.dao.LichSuThaoTacDAO;
import com.restaurant.model.LichSuThaoTac;
import com.restaurant.model.LichSuTruyCap;

import javax.swing.*;
import java.util.List;

/**
 * SwingWorker để ghi nhận Audit Log trong background thread
 * Tránh làm chậm UI khi ghi log vào database
 */
public class AuditSwingWorker {

    // ==================== GHI NHẬN TRUY CẬP ====================

    /**
     * Ghi nhận đăng nhập bất đồng bộ (chạy nền, không block UI)
     */
    public static void ghiDangNhapAsync(Integer maNV, String tenDangNhap, String hoTen, String vaiTro,
                                         LichSuTruyCap.HanhDong hanhDong,
                                         LichSuTruyCap.TrangThai trangThai,
                                         String ghiChu) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    // Format hiển thị: "Họ Tên (Vai trò)" 
                    String hoTenHienThi = formatHoTenVoiVaiTro(hoTen, vaiTro);
                    
                    LichSuTruyCapDAO dao = new LichSuTruyCapDAO();
                    LichSuTruyCap lichSu = new LichSuTruyCap(maNV, tenDangNhap, hoTenHienThi, hanhDong, trangThai, getDiaChiIP(), ghiChu);
                    lichSu.setTrinhDuyet("Restaurant POS System v1.0");
                    lichSu.setMayTinh(getMayTinh());
                    return dao.ghiNhanTruyCap(lichSu);
                } catch (Exception e) {
                    System.err.println("Lỗi ghi log đăng nhập: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void done() {
                // Không làm gì sau khi hoàn thành - silent logging
            }
        }.execute();
    }

    // ==================== GHI NHẬN THAO TÁC CRUD ====================

    /**
     * Ghi nhận thao tác TẠO MỚI bất đồng bộ
     */
    public static void ghiTaoMoiAsync(Integer maNV, String hoTen, String tenBang, String moTa, String banGhiMoi) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    LichSuThaoTacDAO dao = new LichSuThaoTacDAO();
                    LichSuThaoTac lichSu = LichSuThaoTac.builder()
                        .maNV(maNV)
                        .hoTen(hoTen != null ? hoTen : "Hệ thống")
                        .tenBang(tenBang)
                        .hanhDong(LichSuThaoTac.HanhDong.INSERT)
                        .banGhiMoi(banGhiMoi)
                        .moTa(moTa)
                        .diaChiIP(getDiaChiIP())
                        .mayTinh(getMayTinh())
                        .build();
                    return dao.ghiNhanThaoTac(lichSu);
                } catch (Exception e) {
                    System.err.println("Lỗi ghi log tạo mới: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void done() {
                // Silent logging
            }
        }.execute();
    }

    /**
     * Ghi nhận thao tác CẬP NHẬT bất đồng bộ
     */
    public static void ghiCapNhatAsync(Integer maNV, String hoTen, String tenBang, String moTa,
                                       String banGhiCu, String banGhiMoi) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    LichSuThaoTacDAO dao = new LichSuThaoTacDAO();
                    LichSuThaoTac lichSu = LichSuThaoTac.builder()
                        .maNV(maNV)
                        .hoTen(hoTen != null ? hoTen : "Hệ thống")
                        .tenBang(tenBang)
                        .hanhDong(LichSuThaoTac.HanhDong.UPDATE)
                        .banGhiCu(banGhiCu)
                        .banGhiMoi(banGhiMoi)
                        .moTa(moTa)
                        .diaChiIP(getDiaChiIP())
                        .mayTinh(getMayTinh())
                        .build();
                    return dao.ghiNhanThaoTac(lichSu);
                } catch (Exception e) {
                    System.err.println("Lỗi ghi log cập nhật: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void done() {
                // Silent logging
            }
        }.execute();
    }

    /**
     * Ghi nhận thao tác XÓA bất đồng bộ
     */
    public static void ghiXoaAsync(Integer maNV, String hoTen, String tenBang, String moTa, String banGhiCu) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    LichSuThaoTacDAO dao = new LichSuThaoTacDAO();
                    LichSuThaoTac lichSu = LichSuThaoTac.builder()
                        .maNV(maNV)
                        .hoTen(hoTen != null ? hoTen : "Hệ thống")
                        .tenBang(tenBang)
                        .hanhDong(LichSuThaoTac.HanhDong.DELETE)
                        .banGhiCu(banGhiCu)
                        .moTa(moTa)
                        .diaChiIP(getDiaChiIP())
                        .mayTinh(getMayTinh())
                        .build();
                    return dao.ghiNhanThaoTac(lichSu);
                } catch (Exception e) {
                    System.err.println("Lỗi ghi log xóa: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void done() {
                // Silent logging
            }
        }.execute();
    }

    /**
     * Ghi nhận thao tác với chi tiết bất đồng bộ
     */
    public static void ghiThaoTacChiTietAsync(Integer maNV, String hoTen, String tenBang,
                                              LichSuThaoTac.HanhDong hanhDong,
                                              String moTa, String chiTiet) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    LichSuThaoTacDAO dao = new LichSuThaoTacDAO();
                    LichSuThaoTac lichSu = LichSuThaoTac.builder()
                        .maNV(maNV)
                        .hoTen(hoTen != null ? hoTen : "Hệ thống")
                        .tenBang(tenBang)
                        .hanhDong(hanhDong)
                        .moTa(moTa)
                        .chiTiet(chiTiet)
                        .diaChiIP(getDiaChiIP())
                        .mayTinh(getMayTinh())
                        .build();
                    return dao.ghiNhanThaoTac(lichSu);
                } catch (Exception e) {
                    System.err.println("Lỗi ghi log thao tác: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void done() {
                // Silent logging
            }
        }.execute();
    }

    // ==================== LẤY DỮ LIỆU (CÓ LOADING INDICATOR) ====================

    /**
     * Lấy lịch sử truy cập với SwingWorker (cho JTable)
     */
    public static SwingWorker<List<LichSuTruyCap>, Void> layLichSuTruyCap(int limit, 
                                                                           Runnable onSuccess, 
                                                                           Runnable onError) {
        return new SwingWorker<List<LichSuTruyCap>, Void>() {
            @Override
            protected List<LichSuTruyCap> doInBackground() {
                try {
                    LichSuTruyCapDAO dao = new LichSuTruyCapDAO();
                    return dao.getRecent(limit);
                } catch (Exception e) {
                    System.err.println("Lỗi lấy lịch sử truy cập: " + e.getMessage());
                    return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                } catch (Exception e) {
                    if (onError != null) {
                        onError.run();
                    }
                }
            }
        };
    }

    /**
     * Lấy lịch sử thao tác với SwingWorker (cho JTable)
     */
    public static SwingWorker<List<LichSuThaoTac>, Void> layLichSuThaoTac(int limit,
                                                                          Runnable onSuccess,
                                                                          Runnable onError) {
        return new SwingWorker<List<LichSuThaoTac>, Void>() {
            @Override
            protected List<LichSuThaoTac> doInBackground() {
                try {
                    LichSuThaoTacDAO dao = new LichSuThaoTacDAO();
                    return dao.getRecent(limit);
                } catch (Exception e) {
                    System.err.println("Lỗi lấy lịch sử thao tác: " + e.getMessage());
                    return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                } catch (Exception e) {
                    if (onError != null) {
                        onError.run();
                    }
                }
            }
        };
    }

    // ==================== UTILITY ====================

    /**
     * Format hiển thị: "Họ Tên (Vai trò)" hoặc "Username (Vai trò)"
     * Trả về null nếu không có thông tin (cho đăng nhập thất bại)
     */
    private static String formatHoTenVoiVaiTro(String hoTen, String vaiTro) {
        // Nếu là đăng nhập thất bại (không có vai trò)
        if (vaiTro == null || vaiTro.trim().isEmpty()) {
            if (hoTen != null && !hoTen.trim().isEmpty()) {
                return hoTen.trim();
            }
            return null;
        }
        
        // Chuyển mã vai trò sang tiếng Việt
        String vaiTroDisplay = convertVaiTroToDisplay(vaiTro);
        
        // Nếu có họ tên đầy đủ
        if (hoTen != null && !hoTen.trim().isEmpty() && !hoTen.equalsIgnoreCase("unknown")) {
            return String.format("%s (%s)", hoTen.trim(), vaiTroDisplay);
        }
        
        // Không có họ tên, chỉ hiển thị vai trò
        return vaiTroDisplay;
    }
    
    /**
     * Chuyển mã vai trò thành tên hiển thị tiếng Việt
     */
    private static String convertVaiTroToDisplay(String vaiTro) {
        if (vaiTro == null) return "Nhân viên";
        
        switch (vaiTro.trim().toLowerCase()) {
            case "quanly":
                return "Quản lý";
            case "thungan":
                return "Thu ngân";
            case "phucvu":
                return "Phục vụ";
            case "bep":
                return "Đầu bếp";
            default:
                return vaiTro;
        }
    }

    private static String getDiaChiIP() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private static String getMayTinh() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
