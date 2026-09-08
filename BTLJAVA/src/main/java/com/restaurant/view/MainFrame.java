package com.restaurant.view;

import com.restaurant.model.NhanVien;
import com.restaurant.util.UIConstants;
import com.restaurant.view.checkout.CheckoutPanel;
import com.restaurant.view.dashboard.DashboardPanel;
import com.restaurant.view.management.*;
import com.restaurant.view.order.OrderPanel;
import com.restaurant.view.tablemap.TableMapPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Màn hình chính của Ứng dụng Quản lý Vận hành Nhà hàng (RESTAURANT OPS)
 * =========================================================================
 * Module 1: Main Frame & Sidebar Navigation
 * - Layout chính: BorderLayout
 * - Top Bar: Logo, Tiêu đề hệ thống, Thông tin User đăng nhập
 * - Sidebar (West): Dark Blue (#102944), phân nhóm chức năng (SYSTEM, SERVICE, CASHIER, MANAGEMENT, REPORTS...)
 * - Content (Center): CardLayout chứa và chuyển đổi giữa các module
 * =========================================================================
 */
public class MainFrame extends JFrame {

    // CardLayout & Container
    private CardLayout cardLayout;
    private JPanel centerContentPanel;

    // Danh sách các Panel Module
    private DashboardPanel dashboardPanel;
    private TableMapPanel tableMapPanel;
    private OrderPanel orderPanel;
    private CheckoutPanel checkoutPanel;
    private UserManagementPanel userManagementPanel;
    private MenuManagementPanel menuManagementPanel;
    private CategoryManagementPanel categoryManagementPanel;
    private TableManagementPanel tableManagementPanel;

    // Lưu trữ các nút trên Sidebar để xử lý hiệu ứng Active/Selected
    private Map<String, JPanel> sidebarItemPanels = new HashMap<>();
    private String currentCardName = "DASHBOARD";

    // Tên các Card trong CardLayout
    public static final String CARD_DASHBOARD = "DASHBOARD";
    public static final String CARD_TABLE_MAP = "TABLE_MAP";
    public static final String CARD_ORDER_DETAILS = "ORDER_DETAILS";
    public static final String CARD_CHECKOUT = "CHECKOUT";
    public static final String CARD_INVOICE_HISTORY = "INVOICE_HISTORY";
    public static final String CARD_CATEGORIES = "CATEGORIES";
    public static final String CARD_MENU_ITEMS = "MENU_ITEMS";
    public static final String CARD_TABLES = "TABLES";
    public static final String CARD_VOUCHERS = "VOUCHERS";
    public static final String CARD_USERS = "USERS";
    public static final String CARD_REVENUE_REPORT = "REVENUE_REPORT";
    public static final String CARD_TOP_DISHES = "TOP_DISHES";
    public static final String CARD_KITCHEN = "KITCHEN";
    public static final String CARD_SHIFTS = "SHIFTS";

    private NhanVien currentUser;
    private VoucherManagementPanel voucherManagementPanel;
    private com.restaurant.view.checkout.InvoiceHistoryPanel invoiceHistoryPanel;
    private com.restaurant.view.dashboard.RevenueReportPanel revenueReportPanel;
    private com.restaurant.view.dashboard.TopDishesReportPanel topDishesReportPanel;
    private com.restaurant.view.kitchen.KitchenManagementPanel kitchenManagementPanel;
    private com.restaurant.view.shift.ShiftManagementPanel shiftManagementPanel;

    public MainFrame() {
        this(new NhanVien(1, "Quản trị viên", "admin", "admin123", "QuanLy", "DangLam"));
    }

    public MainFrame(NhanVien user) {
        this.currentUser = (user != null) ? user : new NhanVien(1, "Quản trị viên", "admin", "admin123", "QuanLy", "DangLam");

        setTitle("RESTAURANT OPS - Hệ Thống Quản Lý Vận Hành Nhà Hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1366, 768);
        setMinimumSize(new Dimension(1150, 680));
        setLocationRelativeTo(null);

        initLayout();
    }

    private void initLayout() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UIConstants.BG_LIGHT);

        // 1. Top Bar (Header)
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Sidebar Navigation (West)
        JPanel sidebarPanel = createSidebarPanel();
        rootPanel.add(sidebarPanel, BorderLayout.WEST);

        // 3. Center Content Panel (CardLayout)
        cardLayout = new CardLayout();
        centerContentPanel = new JPanel(cardLayout);
        centerContentPanel.setBackground(UIConstants.BG_LIGHT);

        // Khởi tạo các Panels Module
        initModules();

        rootPanel.add(centerContentPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);

        // Mặc định hiển thị theo phân quyền
        boolean isAdmin = "QuanLy".equalsIgnoreCase(currentUser.getVaiTro()) || "Admin".equalsIgnoreCase(currentUser.getVaiTro());
        showCard(isAdmin ? CARD_DASHBOARD : CARD_TABLE_MAP);
    }

    /**
     * Khởi tạo các module và thêm vào CardLayout
     */
    private void initModules() {
        // Module 2: Dashboard
        dashboardPanel = new DashboardPanel(this);
        centerContentPanel.add(dashboardPanel, CARD_DASHBOARD);

        // Module 3A: Sơ đồ bàn
        tableMapPanel = new TableMapPanel(this);
        centerContentPanel.add(tableMapPanel, CARD_TABLE_MAP);

        // Module 3B: Màn hình Order
        orderPanel = new OrderPanel(this);
        centerContentPanel.add(orderPanel, CARD_ORDER_DETAILS);

        // Module 4: Màn hình Thu ngân (Checkout)
        checkoutPanel = new CheckoutPanel(this);
        centerContentPanel.add(checkoutPanel, CARD_CHECKOUT);

        // Module 5: Quản trị
        userManagementPanel = new UserManagementPanel();
        centerContentPanel.add(userManagementPanel, CARD_USERS);

        menuManagementPanel = new MenuManagementPanel();
        centerContentPanel.add(menuManagementPanel, CARD_MENU_ITEMS);

        categoryManagementPanel = new CategoryManagementPanel();
        centerContentPanel.add(categoryManagementPanel, CARD_CATEGORIES);

        tableManagementPanel = new TableManagementPanel();
        centerContentPanel.add(tableManagementPanel, CARD_TABLES);

        voucherManagementPanel = new VoucherManagementPanel();
        centerContentPanel.add(voucherManagementPanel, CARD_VOUCHERS);

        invoiceHistoryPanel = new com.restaurant.view.checkout.InvoiceHistoryPanel();
        centerContentPanel.add(invoiceHistoryPanel, CARD_INVOICE_HISTORY);

        revenueReportPanel = new com.restaurant.view.dashboard.RevenueReportPanel();
        centerContentPanel.add(revenueReportPanel, CARD_REVENUE_REPORT);

        topDishesReportPanel = new com.restaurant.view.dashboard.TopDishesReportPanel();
        centerContentPanel.add(topDishesReportPanel, CARD_TOP_DISHES);

        // Module: Quản lý Bếp & Bar
        kitchenManagementPanel = new com.restaurant.view.kitchen.KitchenManagementPanel(this, currentUser);
        centerContentPanel.add(kitchenManagementPanel, CARD_KITCHEN);

        // Module: Quản lý Ca làm việc
        shiftManagementPanel = new com.restaurant.view.shift.ShiftManagementPanel(this, currentUser);
        centerContentPanel.add(shiftManagementPanel, CARD_SHIFTS);
    }

    /**
     * Tạo thanh Header phía trên cùng (khớp ảnh thiết kế)
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR),
                new EmptyBorder(0, 20, 0, 28)
        ));

        // Bên trái: Tiêu đề "RESTAURANT OPS | Quản lý Vận hành"
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        leftPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("RESTAURANT OPS");
        lblLogo.setFont(UIConstants.FONT_LOGO);
        lblLogo.setForeground(new Color(16, 45, 78)); // Dark Navy

        JLabel lblDivider = new JLabel("|");
        lblDivider.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblDivider.setForeground(new Color(203, 213, 225));

        JLabel lblSubtitle = new JLabel("Quản lý Vận hành Nhà hàng");
        lblSubtitle.setFont(UIConstants.FONT_HEADER_SUB);
        lblSubtitle.setForeground(UIConstants.TEXT_MUTED);

        leftPanel.add(lblLogo);
        leftPanel.add(lblDivider);
        leftPanel.add(lblSubtitle);

        // Bên phải: Trạng thái MySQL + Thông tin User "Đăng nhập: Quản trị viên" + Avatar
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 11));
        rightPanel.setOpaque(false);

        boolean dbOnline = com.restaurant.database.DatabaseConnection.isConnected();
        JLabel lblDbStatus = new JLabel(dbOnline ? "● MySQL Online " : "○ MySQL Offline ");
        lblDbStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDbStatus.setForeground(dbOnline ? new Color(46, 160, 67) : new Color(219, 55, 55));
        lblDbStatus.setBorder(new EmptyBorder(0, 0, 0, 10));
        lblDbStatus.setToolTipText(dbOnline ? "Đã kết nối thành công tới database restaurant_db" : "Chưa kết nối MySQL, đang dùng bộ nhớ đệm");

        JLabel lblUser = new JLabel("Đăng nhập: " + currentUser.getHoTen() + " (" + currentUser.getVaiTro() + ") ");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(UIConstants.TEXT_MAIN);
        lblUser.setBorder(new EmptyBorder(0, 0, 0, 6));

        rightPanel.add(lblDbStatus);
        rightPanel.add(lblUser);

        // Icon Avatar tròn
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Vòng tròn ngoài màu xanh
                g2.setColor(new Color(30, 90, 140));
                g2.fillOval(0, 0, 32, 32);
                // Đầu người
                g2.setColor(Color.WHITE);
                g2.fillOval(10, 7, 12, 12);
                // Thân người
                g2.fillArc(5, 17, 22, 20, 0, 180);
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(32, 32));
        avatarPanel.setOpaque(false);

        rightPanel.add(avatarPanel);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    /**
     * Tạo Sidebar Menu bên trái với các nhóm phân quyền (RBAC)
     */
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UIConstants.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(260, 0));

        // Panel chứa menu có thanh cuộn nếu màn hình nhỏ
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(UIConstants.SIDEBAR_BG);
        menuContainer.setBorder(new EmptyBorder(10, 0, 10, 0));

        boolean isAdmin = "QuanLy".equalsIgnoreCase(currentUser.getVaiTro()) || "Admin".equalsIgnoreCase(currentUser.getVaiTro());

        if (isAdmin) {
            // Nhóm HỆ THỐNG
            menuContainer.add(createSidebarHeader("HỆ THỐNG"));
            menuContainer.add(createSidebarItem("Tổng quan Dashboard", "\u25A3", CARD_DASHBOARD));
            menuContainer.add(Box.createVerticalStrut(10));
        }

        // Nhóm DỊCH VỤ & BÀN ĂN
        menuContainer.add(createSidebarHeader("DỊCH VỤ & BÀN ĂN"));
        menuContainer.add(createSidebarItem("Sơ đồ bàn ăn", "\u229E", CARD_TABLE_MAP));
        menuContainer.add(createSidebarItem("Gọi món (Order)", "\u2630", CARD_ORDER_DETAILS));
        menuContainer.add(createSidebarItem("Quản lý Bếp & Bar", "\uD83C\uDF73", CARD_KITCHEN));

        // Nhóm THU NGÂN
        menuContainer.add(Box.createVerticalStrut(10));
        menuContainer.add(createSidebarHeader("THU NGÂN & HÓA ĐƠN"));
        menuContainer.add(createSidebarItem("Thu ngân (Checkout)", "\uD83D\uDED2", CARD_CHECKOUT));
        menuContainer.add(createSidebarItem("Lịch sử hóa đơn", "\uD83D\uDCC4", CARD_INVOICE_HISTORY));
        menuContainer.add(createSidebarItem("Quản lý ca làm việc", "\u23F1", CARD_SHIFTS));

        if (isAdmin) {
            // Nhóm QUẢN TRỊ DỮ LIỆU
            menuContainer.add(Box.createVerticalStrut(10));
            menuContainer.add(createSidebarHeader("QUẢN TRỊ DỮ LIỆU"));
            menuContainer.add(createSidebarItem("Danh mục món ăn", "\u2637", CARD_CATEGORIES));
            menuContainer.add(createSidebarItem("Thực đơn món ăn", "\uD83C\uDF73", CARD_MENU_ITEMS));
            menuContainer.add(createSidebarItem("Danh sách bàn ăn", "\u25A5", CARD_TABLES));
            menuContainer.add(createSidebarItem("Khuyến mãi & Voucher", "\uD83C\uDFF7", CARD_VOUCHERS));
            menuContainer.add(createSidebarItem("Tài khoản nhân viên", "\uD83D\uDC65", CARD_USERS));

            // Nhóm BÁO CÁO
            menuContainer.add(Box.createVerticalStrut(10));
            menuContainer.add(createSidebarHeader("BÁO CÁO THỐNG KÊ"));
            menuContainer.add(createSidebarItem("Báo cáo doanh thu", "\uD83D\uDCC8", CARD_REVENUE_REPORT));
            menuContainer.add(createSidebarItem("Top món bán chạy", "\uD83C\uDF5C", CARD_TOP_DISHES));
        }

        // Cuộn menu
        JScrollPane scrollMenu = new JScrollPane(menuContainer);
        scrollMenu.setBorder(null);
        scrollMenu.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollMenu.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        scrollMenu.getViewport().setBackground(UIConstants.SIDEBAR_BG);

        sidebar.add(scrollMenu, BorderLayout.CENTER);

        // Nhóm dưới cùng (Bottom buttons: Account, Logout)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(UIConstants.SIDEBAR_BG);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(25, 55, 85)),
                new EmptyBorder(8, 0, 8, 0)
        ));

        JPanel itemAccount = createSidebarActionItem("Thông tin tài khoản", "\uD83D\uDC64", () -> {
            JOptionPane.showMessageDialog(this,
                    "Tài khoản: " + currentUser.getTenDangNhap() + "\n" +
                    "Họ và tên: " + currentUser.getHoTen() + "\n" +
                    "Vai trò: " + currentUser.getVaiTro() + "\n" +
                    "Trạng thái: " + currentUser.getTrangThai() + "\n" +
                    "Hệ thống: RESTAURANT OPS 1.0",
                    "Thông tin tài khoản", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel itemLogout = createSidebarActionItem("Đăng xuất hệ thống", "\u27A1", () -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
                    "Xác nhận Đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    com.restaurant.view.auth.LoginDialog login = new com.restaurant.view.auth.LoginDialog(null);
                    login.setVisible(true);
                    if (login.getAuthenticatedUser() != null) {
                        new MainFrame(login.getAuthenticatedUser()).setVisible(true);
                    }
                });
            }
        });

        bottomPanel.add(itemAccount);
        bottomPanel.add(itemLogout);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createSidebarHeader(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 4));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel lbl = new JLabel(title);
        lbl.setFont(UIConstants.FONT_SIDEBAR_HEADER);
        lbl.setForeground(UIConstants.SIDEBAR_HEADER);
        panel.add(lbl);
        return panel;
    }

    private JPanel createSidebarItem(String title, String iconGlyph, String cardName) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setOpaque(true);
        itemPanel.setBackground(UIConstants.SIDEBAR_BG);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        itemPanel.setPreferredSize(new Dimension(260, 38));
        itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thanh chỉ báo Active bên trái
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(4, 0));
        indicator.setOpaque(true);
        indicator.setBackground(UIConstants.SIDEBAR_BG);

        // Nội dung icon + text
        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        content.setOpaque(false);

        JLabel lblIcon = new JLabel(iconGlyph);
        lblIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 15));
        lblIcon.setForeground(UIConstants.SIDEBAR_TEXT);
        lblIcon.setPreferredSize(new Dimension(20, 20));

        JLabel lblText = new JLabel(title);
        lblText.setFont(UIConstants.FONT_SIDEBAR_ITEM);
        lblText.setForeground(UIConstants.SIDEBAR_TEXT);
        lblText.setBorder(new EmptyBorder(0, 0, 0, 10));

        content.add(lblIcon);
        content.add(lblText);

        itemPanel.add(indicator, BorderLayout.WEST);
        itemPanel.add(content, BorderLayout.CENTER);

        // Sự kiện chuột Click / Hover
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCard(cardName);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!cardName.equals(currentCardName)) {
                    itemPanel.setBackground(UIConstants.SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!cardName.equals(currentCardName)) {
                    itemPanel.setBackground(UIConstants.SIDEBAR_BG);
                }
            }
        });

        sidebarItemPanels.put(cardName, itemPanel);
        return itemPanel;
    }

    private JPanel createSidebarActionItem(String title, String iconGlyph, Runnable action) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setOpaque(true);
        itemPanel.setBackground(UIConstants.SIDEBAR_BG);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        itemPanel.setPreferredSize(new Dimension(260, 36));
        itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        content.setOpaque(false);

        JLabel lblIcon = new JLabel(iconGlyph);
        lblIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 15));
        lblIcon.setForeground(UIConstants.SIDEBAR_TEXT);

        JLabel lblText = new JLabel(title);
        lblText.setFont(UIConstants.FONT_SIDEBAR_ITEM);
        lblText.setForeground(UIConstants.SIDEBAR_TEXT);
        lblText.setBorder(new EmptyBorder(0, 0, 0, 10));

        content.add(lblIcon);
        content.add(lblText);
        itemPanel.add(content, BorderLayout.CENTER);

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) action.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                itemPanel.setBackground(UIConstants.SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                itemPanel.setBackground(UIConstants.SIDEBAR_BG);
            }
        });

        return itemPanel;
    }

    /**
     * Chuyển đổi màn hình bằng CardLayout và cập nhật trạng thái Sidebar Active
     */
    public void showCard(String cardName) {
        this.currentCardName = cardName;
        cardLayout.show(centerContentPanel, cardName);

        if (CARD_KITCHEN.equals(cardName) && kitchenManagementPanel != null) {
            kitchenManagementPanel.loadKitchenOrders();
            kitchenManagementPanel.loadDishesAvailability();
        } else if (CARD_SHIFTS.equals(cardName) && shiftManagementPanel != null) {
            shiftManagementPanel.loadData();
        } else if (CARD_DASHBOARD.equals(cardName) && dashboardPanel != null) {
            dashboardPanel.loadRealData();
        }

        // Reset màu tất cả menu item
        for (Map.Entry<String, JPanel> entry : sidebarItemPanels.entrySet()) {
            JPanel panel = entry.getValue();
            JPanel ind = (JPanel) panel.getComponent(0); // indicator
            if (entry.getKey().equals(cardName)) {
                panel.setBackground(UIConstants.SIDEBAR_ACTIVE);
                ind.setBackground(UIConstants.PRIMARY_BLUE);
            } else {
                panel.setBackground(UIConstants.SIDEBAR_BG);
                ind.setBackground(UIConstants.SIDEBAR_BG);
            }
        }
        repaint();
    }

    /**
     * Điều hướng nhanh tới Order của một bàn cụ thể
     */
    public void navigateToOrder(int maBan) {
        if (orderPanel != null) {
            orderPanel.loadOrderForTable(maBan);
        }
        showCard(CARD_ORDER_DETAILS);
    }

    /**
     * Điều hướng nhanh tới Checkout của một bàn cụ thể
     */
    public void navigateToCheckout(int maBan) {
        if (checkoutPanel != null) {
            checkoutPanel.selectOccupiedTable(maBan);
        }
        showCard(CARD_CHECKOUT);
    }

    public TableMapPanel getTableMapPanel() {
        return tableMapPanel;
    }

    public OrderPanel getOrderPanel() {
        return orderPanel;
    }

    public CheckoutPanel getCheckoutPanel() {
        return checkoutPanel;
    }

    public DashboardPanel getDashboardPanel() {
        return dashboardPanel;
    }

    public com.restaurant.view.checkout.InvoiceHistoryPanel getInvoiceHistoryPanel() {
        return invoiceHistoryPanel;
    }

    public com.restaurant.view.dashboard.RevenueReportPanel getRevenueReportPanel() {
        return revenueReportPanel;
    }

    public com.restaurant.view.dashboard.TopDishesReportPanel getTopDishesReportPanel() {
        return topDishesReportPanel;
    }

    public com.restaurant.view.kitchen.KitchenManagementPanel getKitchenManagementPanel() {
        return kitchenManagementPanel;
    }

    public com.restaurant.view.shift.ShiftManagementPanel getShiftManagementPanel() {
        return shiftManagementPanel;
    }

    private JPanel createPlaceholderPanel(String title, String desc) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIConstants.BG_LIGHT);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1, true),
                new EmptyBorder(30, 40, 30, 40)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIConstants.FONT_PAGE_TITLE);
        lblTitle.setForeground(UIConstants.TEXT_MAIN);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(UIConstants.FONT_REGULAR);
        lblDesc.setForeground(UIConstants.TEXT_MUTED);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblDesc);

        p.add(card);
        return p;
    }
}
