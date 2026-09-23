package com.restaurant.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class cho mã hóa và xác thực mật khẩu
 * Sử dụng thuật toán BCrypt (Blowfish-based password hashing)
 * 
 * BCrypt features:
 * - Tự động tạo salt ngẫu nhiên
 * - Có sẵn cost factor để chống brute force
 * - Salt được nhúng trong hash output
 */
public class PasswordUtils {

    // Cost factor: số vòng lặp = 2^cost
    // Default là 10, có thể tăng lên 12-14 cho bảo mật cao hơn
    private static final int BCRYPT_COST = 10;

    /**
     * Mã hóa mật khẩu thành BCrypt hash
     * 
     * @param plainPassword Mật khẩu plain text
     * @return BCrypt hash (bao gồm salt)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_COST));
    }

    /**
     * Xác thực mật khẩu với BCrypt hash
     * 
     * @param plainPassword Mật khẩu plain text cần kiểm tra
     * @param hashedPassword BCrypt hash đã lưu trong CSDL
     * @return true nếu mật khẩu khớp, false nếu không
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("Lỗi xác thực mật khẩu: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra mật khẩu có đủ mạnh không
     * 
     * @param password Mật khẩu cần kiểm tra
     * @return true nếu mật khẩu đủ mạnh
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        boolean hasLetter = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        
        return hasLetter && hasDigit;
    }

    /**
     * Kiểm tra mật khẩu có phải là BCrypt hash không
     * 
     * @param password Chuỗi cần kiểm tra
     * @return true nếu là BCrypt hash ($2a$ hoặc $2b$)
     */
    public static boolean isBCryptHash(String password) {
        if (password == null) return false;
        return password.matches("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");
    }

    /**
     * Kiểm tra và mã hóa mật khẩu nếu chưa được mã hóa
     * Dùng cho migration từ plain text sang BCrypt
     * 
     * @param plainPassword Mật khẩu plain text
     * @param existingHash Hash hiện có (có thể là plain text cũ)
     * @return BCrypt hash
     */
    public static String hashIfNeeded(String plainPassword, String existingHash) {
        if (isBCryptHash(existingHash)) {
            // Đã là BCrypt hash, kiểm tra xem plainPassword có khớp không
            if (verifyPassword(plainPassword, existingHash)) {
                return existingHash; // Giữ nguyên hash cũ
            }
        }
        // Chưa mã hóa hoặc không khớp -> mã hóa mới
        return hashPassword(plainPassword);
    }
}
