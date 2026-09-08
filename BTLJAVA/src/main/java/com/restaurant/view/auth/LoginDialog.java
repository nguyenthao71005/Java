package com.restaurant.view.auth;

import com.restaurant.dao.NhanVienDAO;
import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.NhanVien;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Hộp thoại Đăng nhập hệ thống phân quyền (Login Dialog)
 */
public class LoginDialog extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblError;
    private NhanVien authenticatedUser = null;
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public LoginDialog(Frame parent) {
        super(parent, "Đăng Nhập - RESTAURANT OPS", true);
        initUI();
    }

    private void initUI() {
        setSize(440, 480);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(16, 45, 78), getWidth(), getHeight(), new Color(24, 70, 115));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 110));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(20, 24, 15, 24));

        JLabel lblLogo = new JLabel("RESTAURANT OPS");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Hệ Thống Quản Lý Vận Hành Nhà Hàng");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(203, 213, 225));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        boolean online = DatabaseConnection.isConnected();
        JLabel lblDb = new JLabel(online ? "● CSDL: MySQL Online" : "○ CSDL: Bộ nhớ đệm (Offline)");
        lblDb.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDb.setForeground(online ? new Color(46, 160, 67) : new Color(255, 179, 0));
        lblDb.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblLogo);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblSub);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(lblDb);

        add(headerPanel, BorderLayout.NORTH);

        // Body Form
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(20, 32, 20, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        JLabel lblU = new JLabel("Tên đăng nhập:");
        lblU.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bodyPanel.add(lblU, gbc);

        gbc.gridy = 1;
        txtUsername = new JTextField("admin");
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(0, 36));
        bodyPanel.add(txtUsername, gbc);

        // Password
        gbc.gridy = 2;
        JLabel lblP = new JLabel("Mật khẩu:");
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bodyPanel.add(lblP, gbc);

        gbc.gridy = 3;
        txtPassword = new JPasswordField("admin123");
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(0, 36));
        bodyPanel.add(txtPassword, gbc);

        // Error message label
        gbc.gridy = 4;
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblError.setForeground(new Color(219, 55, 55));
        bodyPanel.add(lblError, gbc);

        // Buttons Panel
        gbc.gridy = 5;
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        btnPanel.setOpaque(false);

        JButton btnExit = new JButton("Thoát");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExit.setPreferredSize(new Dimension(0, 38));
        btnExit.addActionListener(e -> {
            authenticatedUser = null;
            dispose();
        });

        JButton btnLogin = new JButton("Đăng Nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(new Color(16, 45, 78));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(0, 38));
        btnLogin.addActionListener(e -> handleLogin());

        btnPanel.add(btnExit);
        btnPanel.add(btnLogin);
        bodyPanel.add(btnPanel, gbc);

        // Gợi ý đăng nhập
        gbc.gridy = 6;
        JLabel lblHint = new JLabel("<html><center style='color:#64748B; font-size:11px;'>" +
                "Gợi ý tài khoản mẫu:<br>" +
                "<b>admin</b> / <b>admin123</b> (Quản lý - Toàn quyền)<br>" +
                "<b>waiter01</b> / <b>nam123</b> (Phục vụ) | <b>cashier01</b> / <b>mai123</b> (Thu ngân)" +
                "</center></html>", SwingConstants.CENTER);
        lblHint.setBorder(new EmptyBorder(8, 0, 0, 0));
        bodyPanel.add(lblHint, gbc);

        add(bodyPanel, BorderLayout.CENTER);

        // Enter key to login
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || pass.isEmpty()) {
            lblError.setText("Vui lòng điền đầy đủ Tên đăng nhập và Mật khẩu!");
            return;
        }

        NhanVien user = null;
        if (DatabaseConnection.isConnected()) {
            user = nhanVienDAO.authenticate(username, pass);
        }

        // Fallback kiểm tra từ DummyData nếu CSDL không kết nối
        if (user == null && !DatabaseConnection.isConnected()) {
            for (NhanVien nv : DummyDataFactory.getInstance().getDanhSachNhanVien()) {
                if (nv.getTenDangNhap().equalsIgnoreCase(username) && nv.getMatKhau().equals(pass)) {
                    user = nv;
                    break;
                }
            }
        }

        if (user != null) {
            this.authenticatedUser = user;
            dispose();
        } else {
            lblError.setText("Sai tên đăng nhập hoặc mật khẩu! Vui lòng thử lại.");
        }
    }

    public NhanVien getAuthenticatedUser() {
        return authenticatedUser;
    }
}
