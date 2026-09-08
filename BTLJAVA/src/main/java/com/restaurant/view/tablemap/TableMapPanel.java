package com.restaurant.view.tablemap;

import com.restaurant.model.Ban;
import com.restaurant.model.HoaDon;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;
import com.restaurant.view.MainFrame;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Module 3A: Sơ đồ bàn (Table Map)
 * =========================================================================
 * Điểm nối CSDL MySQL (Khi tích hợp Backend):
 * 1. Tải danh sách bàn:
 *    - SELECT MaBan, TenBan, KhuVuc, SucChua, TrangThai FROM Ban ORDER BY MaBan ASC;
 * 2. Mở bàn mới:
 *    - UPDATE Ban SET TrangThai = 'CoKhach' WHERE MaBan = ?;
 *    - INSERT INTO HoaDon (MaBan, NgayLap, TrangThai) VALUES (?, NOW(), 'ChuaThanhToan');
 * 3. Chuyển bàn / Đổi trạng thái:
 *    - UPDATE Ban SET TrangThai = ? WHERE MaBan = ?;
 * =========================================================================
 */
public class TableMapPanel extends JPanel {

    private final MainFrame mainFrame;
    private JPanel tablesContainer;
    private JLabel lblTotalTables;
    private JLabel lblEmptyTables;
    private JLabel lblOccupiedTables;
    private JComboBox<String> floorFilterCombo;

    public TableMapPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 14));
        setBackground(UIConstants.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
    }

    private void initUI() {
        // 1. Thanh tiêu đề và bộ lọc khu vực
        add(createTopBar(), BorderLayout.NORTH);

        // 2. Lưới hiển thị các bàn
        tablesContainer = new JPanel();
        tablesContainer.setBackground(Color.WHITE);
        tablesContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(tablesContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Tải danh sách bàn
        refreshTableGrid("Tất cả");
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 8));
        bar.setOpaque(false);

        // Góc trái: Tiêu đề + Chú thích màu sắc (Legend)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Sơ đồ Bàn Nhà Hàng ");
        lblTitle.setFont(UIConstants.FONT_PAGE_TITLE);
        lblTitle.setForeground(UIConstants.TEXT_MAIN);
        lblTitle.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Legend: Trống (Xanh)
        JPanel legendEmpty = createLegendBadge("Bàn trống", UIConstants.TABLE_EMPTY);
        // Legend: Có khách (Đỏ)
        JPanel legendOccupied = createLegendBadge("Bàn có khách ", UIConstants.TABLE_OCCUPIED);

        leftPanel.add(lblTitle);
        leftPanel.add(Box.createHorizontalStrut(15));
        leftPanel.add(legendEmpty);
        leftPanel.add(legendOccupied);

        // Góc phải: Thống kê nhanh + Bộ lọc tầng
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        lblTotalTables = new JLabel("Tổng: 20 bàn ");
        lblTotalTables.setFont(UIConstants.FONT_BOLD);
        lblTotalTables.setForeground(UIConstants.TEXT_MAIN);
        lblTotalTables.setBorder(new EmptyBorder(0, 0, 0, 4));

        lblEmptyTables = new JLabel("Trống: 14 bàn ");
        lblEmptyTables.setFont(UIConstants.FONT_BOLD);
        lblEmptyTables.setForeground(UIConstants.TABLE_EMPTY);
        lblEmptyTables.setBorder(new EmptyBorder(0, 0, 0, 4));

        lblOccupiedTables = new JLabel("Có khách: 6 bàn ");
        lblOccupiedTables.setFont(UIConstants.FONT_BOLD);
        lblOccupiedTables.setForeground(UIConstants.TABLE_OCCUPIED);
        lblOccupiedTables.setBorder(new EmptyBorder(0, 0, 0, 4));

        floorFilterCombo = new JComboBox<>(new String[]{"Tất cả", "Tầng 1", "Tầng 2", "Phòng VIP"});
        floorFilterCombo.setFont(UIConstants.FONT_REGULAR);
        floorFilterCombo.setBackground(Color.WHITE);
        floorFilterCombo.addActionListener(e -> {
            String selected = (String) floorFilterCombo.getSelectedItem();
            refreshTableGrid(selected);
        });

        JButton btnRefresh = new JButton("Làm mới sơ đồ");
        btnRefresh.setFont(UIConstants.FONT_REGULAR);
        btnRefresh.addActionListener(e -> refreshTableGrid((String) floorFilterCombo.getSelectedItem()));

        rightPanel.add(lblTotalTables);
        rightPanel.add(new JLabel("|"));
        rightPanel.add(lblEmptyTables);
        rightPanel.add(new JLabel("|"));
        rightPanel.add(lblOccupiedTables);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(new JLabel("Khu vực:"));
        rightPanel.add(floorFilterCombo);
        rightPanel.add(btnRefresh);

        bar.add(leftPanel, BorderLayout.WEST);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

    private JPanel createLegendBadge(String labelText, Color dotColor) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        badge.setOpaque(false);

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillRoundRect(0, 0, 14, 14, 4, 4);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(14, 14));
        dot.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(UIConstants.TEXT_MUTED);

        badge.add(dot);
        badge.add(lbl);
        return badge;
    }

    /**
     * Nạp lại giao diện lưới bàn theo bộ lọc khu vực
     */
    public void refreshTableGrid(String floorFilter) {
        tablesContainer.removeAll();
        List<Ban> allTables = DummyDataFactory.getInstance().getDanhSachBan();

        // Đếm số lượng
        int total = 0;
        int emptyCount = 0;
        int occupiedCount = 0;

        for (Ban b : allTables) {
            total++;
            if (b.isCoKhach()) occupiedCount++;
            else emptyCount++;
        }

        lblTotalTables.setText("Tổng: " + total);
        lblEmptyTables.setText("Trống: " + emptyCount);
        lblOccupiedTables.setText("Có khách: " + occupiedCount);

        // Lưới bàn (GridLayout 5 cột tương tự góc trên phải ảnh mẫu)
        tablesContainer.setLayout(new GridLayout(0, 5, 16, 16));

        for (Ban ban : allTables) {
            if (!"Tất cả".equals(floorFilter) && !ban.getKhuVuc().equalsIgnoreCase(floorFilter)) {
                continue;
            }

            JButton btnTable = createTableButton(ban);
            tablesContainer.add(btnTable);
        }

        tablesContainer.revalidate();
        tablesContainer.repaint();
    }

    /**
     * Tạo Button đại diện cho một Bàn với màu sắc trực quan (Xanh lá = Trống, Đỏ = Có khách)
     */
    private JButton createTableButton(Ban ban) {
        boolean coKhach = ban.isCoKhach();
        Color statusColor = coKhach ? UIConstants.TABLE_OCCUPIED : UIConstants.TABLE_EMPTY;

        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(0, 4));
        btn.setPreferredSize(new Dimension(130, 85));
        btn.setBackground(statusColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tên bàn to, đậm
        JLabel lblName = new JLabel(ban.getTenBan(), SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblName.setForeground(Color.WHITE);

        // Trạng thái (Trống / Có khách) + Sức chứa
        String subInfo = (coKhach ? "Đang có khách" : "Bàn trống") + " • " + ban.getSucChua() + " chỗ";
        JLabel lblStatus = new JLabel(subInfo, SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(255, 255, 255, 220));

        // Khu vực bàn
        JLabel lblKhuVuc = new JLabel(ban.getKhuVuc(), SwingConstants.CENTER);
        lblKhuVuc.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblKhuVuc.setForeground(new Color(255, 255, 255, 180));

        btn.add(lblName, BorderLayout.NORTH);
        btn.add(lblStatus, BorderLayout.CENTER);
        btn.add(lblKhuVuc, BorderLayout.SOUTH);

        // Sự kiện Click bàn theo yêu cầu nghiệp vụ
        btn.addActionListener(e -> handleTableClick(ban));

        return btn;
    }

    /**
     * Xử lý tương tác khi click vào bàn:
     * - Bàn Xanh (Trống): Hiện JOptionPane xác nhận "Mở bàn mới?" -> Đổi sang Đỏ & mở Order
     * - Bàn Đỏ (Có khách): Mở tùy chọn Order hoặc Checkout
     */
    private void handleTableClick(Ban ban) {
        if (!ban.isCoKhach()) {
            // Bàn Trống -> Xác nhận mở bàn
            int opt = JOptionPane.showConfirmDialog(
                    this,
                    "[" + ban.getTenBan() + "] hiện đang còn trống.\nBạn có muốn MỞ BÀN MỚI để gọi món cho khách không?",
                    "Xác nhận Mở bàn mới",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (opt == JOptionPane.YES_OPTION) {
                // Chuyển trạng thái bàn sang "CoKhach" và tạo hóa đơn
                DummyDataFactory.getInstance().moBanMoi(ban.getMaBan());
                refreshTableGrid((String) floorFilterCombo.getSelectedItem());

                // Chuyển ngay sang màn hình Order cho bàn này
                mainFrame.navigateToOrder(ban.getMaBan());
            }
        } else {
            // Bàn Có khách -> Mở màn hình Order hoặc Checkout
            String[] options = {"Gọi món (Order)", "Thanh toán (Thu ngân)", "Đóng"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "[" + ban.getTenBan() + "] đang có khách phục vụ.\nBạn muốn thực hiện thao tác nào?",
                    "Tùy chọn: " + ban.getTenBan(),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                mainFrame.navigateToOrder(ban.getMaBan());
            } else if (choice == 1) {
                mainFrame.navigateToCheckout(ban.getMaBan());
            }
        }
    }
}
