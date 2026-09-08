package com.restaurant;

import com.formdev.flatlaf.FlatLightLaf;
import com.restaurant.view.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Lớp khởi chạy ứng dụng (Main Entry Point)
 * Cài đặt giao diện hiện đại FlatLaf và cấu hình các thuộc tính UI tinh tế
 */
public class App {
    public static void main(String[] args) {
        // Cấu hình khử răng cưa cho font chữ trên Windows
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Khởi động Look and Feel FlatLaf Light
        try {
            FlatLightLaf.setup();

            // Tinh chỉnh thông số FlatLaf bo góc mềm mại
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.width", 8);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("TabbedPane.showTabSeparators", true);
        } catch (Exception ex) {
            System.err.println("Không thể nạp FlatLaf Look and Feel: " + ex.getMessage());
        }

        // Khởi tạo và hiển thị màn hình Đăng nhập (LoginDialog) trên Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            com.restaurant.view.auth.LoginDialog loginDialog = new com.restaurant.view.auth.LoginDialog(null);
            loginDialog.setVisible(true);

            com.restaurant.model.NhanVien user = loginDialog.getAuthenticatedUser();
            if (user != null) {
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}
