package vn.edu.eaut.lab3;

import javax.swing.*;
import java.awt.*;

public class Bai06LoginForm extends JFrame {
    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JComboBox<String> cbRole = new JComboBox<>(new String[]{"Admin", "User"});
    private final JCheckBox chkShowPassword = new JCheckBox("Hiển thị mật khẩu");

    public Bai06LoginForm() {
        setTitle("Bài 6 - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Tài khoản:"));
        formPanel.add(txtUsername);
        
        formPanel.add(new JLabel("Mật khẩu:"));
        formPanel.add(txtPassword);
        
        formPanel.add(new JLabel("Vai trò:"));
        formPanel.add(cbRole);
        
        formPanel.add(new JLabel("")); // Spacer
        formPanel.add(chkShowPassword);

        JButton btnLogin = new JButton("Đăng nhập");
        
        add(formPanel, BorderLayout.CENTER);
        add(btnLogin, BorderLayout.SOUTH);

        // Sự kiện hiển thị mật khẩu
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });

        // Sự kiện đăng nhập
        btnLogin.addActionListener(e -> handleLogin());

        setSize(350, 220);
        setLocationRelativeTo(null);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = (String) cbRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (username.equals("admin") && password.equals("123456") && role.equals("Admin")) {
            JOptionPane.showMessageDialog(this, "Chào mừng Admin đăng nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else if (username.equals("user") && password.equals("123456") && role.equals("User")) {
            JOptionPane.showMessageDialog(this, "Chào mừng User đăng nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Tài khoản, mật khẩu hoặc vai trò không hợp lệ!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Bai06LoginForm().setVisible(true));
    }
}