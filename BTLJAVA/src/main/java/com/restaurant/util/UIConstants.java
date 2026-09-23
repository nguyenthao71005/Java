package com.restaurant.util;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class UIConstants {
    // ==========================================
    // BẢNG MÀU THIẾT KẾ (COLOR PALETTE)
    // ==========================================
    // Sidebar Dark Blue theo ảnh mẫu
    public static final Color SIDEBAR_BG = new Color(16, 41, 68);          // #102944 - Xanh đậm
    public static final Color SIDEBAR_HOVER = new Color(26, 60, 96);       // #1A3C60 - Hover button
    public static final Color SIDEBAR_ACTIVE = new Color(33, 76, 120);     // #214C78 - Nút đang chọn
    public static final Color SIDEBAR_ACCENT = new Color(41, 128, 185);    // #2980B9 - Chỉ báo viền
    public static final Color SIDEBAR_TEXT = new Color(210, 225, 240);     // Chữ menu
    public static final Color SIDEBAR_HEADER = new Color(125, 155, 185);   // Chữ nhóm: SYSTEM, SERVICE...

    // Màu nền chung và thẻ Card
    public static final Color BG_LIGHT = new Color(244, 246, 250);         // #F4F6FA - Nền màn hình chính
    public static final Color CARD_BG = Color.WHITE;                       // Nền trắng cho Panel/Card
    public static final Color BORDER_COLOR = new Color(226, 232, 240);     // #E2E8F0 - Viền xám nhạt

    // Màu trạng thái Sơ đồ bàn
    public static final Color TABLE_EMPTY = new Color(46, 160, 67);        // #2EA043 - Xanh lá (Trống)
    public static final Color TABLE_OCCUPIED = new Color(219, 55, 55);     // #DB3737 - Đỏ (Có khách)

    // Màu Accent & Chức năng
    public static final Color PRIMARY_BLUE = new Color(24, 119, 242);      // #1877F2
    public static final Color SUCCESS_GREEN = new Color(39, 174, 96);      // #27AE60
    public static final Color DANGER_RED = new Color(231, 76, 60);         // #E74C3C
    public static final Color WARNING_ORANGE = new Color(243, 156, 18);    // #F39C12
    public static final Color PILL_GREEN_BG = new Color(220, 252, 231);    // Nền pill tăng trưởng MoM
    public static final Color PILL_RED_BG = new Color(254, 226, 226);      // Nền pill giảm MoM

    // Màu Text
    public static final Color TEXT_MAIN = new Color(30, 41, 59);           // #1E293B - Chữ chính
    public static final Color TEXT_MUTED = new Color(100, 116, 139);       // #64748B - Chữ phụ

    // Màu Header Bảng
    public static final Color TABLE_HEADER_BG = new Color(16, 41, 68);     // Khớp với ảnh Top Key Dishes
    public static final Color TABLE_HEADER_TEXT = Color.WHITE;

    // ==========================================
    // FONT CHỮ
    // ==========================================
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER_SUB = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_PAGE_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_KPI_VALUE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_KPI_TITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SIDEBAR_HEADER = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_SIDEBAR_ITEM = new Font("Segoe UI", Font.PLAIN, 13);

    // ==========================================
    // ĐỊNH DẠNG SỐ VÀ TIỀN TỆ
    // ==========================================
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0 'VND'");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0");

    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }

    public static String formatNumber(long number) {
        return NUMBER_FORMAT.format(number);
    }
}
