package com.restaurant.view.kitchen;

import com.restaurant.dao.KitchenDAO;
import com.restaurant.dao.MonAnDAO;
import com.restaurant.model.KitchenOrder;
import com.restaurant.model.MonAn;
import com.restaurant.model.NhanVien;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;
import com.restaurant.view.MainFrame;
import com.restaurant.view.components.ModernTable;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Module: Quản Lý Bếp & Bar (Kitchen Management / KDS)
 * Thiết kế chuẩn hóa theo yêu cầu nghiệp vụ:
 * 1. Nhận Order: Hiển thị danh sách order món theo thời gian thực (Bảng JTable)
 * 2. Xử lý trạng thái món: Chuyển trạng thái Chờ nấu -> Đang làm -> Hoàn thành (Hỗ trợ đa chọn Multi-select)
 * 3. Báo hết món: Quản lý tình trạng thực đơn, Bếp báo hết hàng / mở bán lại
 * 4. Lọc hiển thị: Lọc theo trạng thái, bàn, khu vực, tự động làm mới (Auto-refresh)
 */
public class KitchenManagementPanel extends JPanel {

    private final MainFrame mainFrame;
    private final NhanVien currentUser;
    private final KitchenDAO kitchenDAO = new KitchenDAO();
    private final MonAnDAO monAnDAO = new MonAnDAO();

    // Tabbed Pane: Tab 1 - Điều hành Bếp (KDS), Tab 2 - Báo hết món / Kho thực đơn
    private JTabbedPane tabbedPane;

    // Tab 1 Components
    private JLabel lblCountWaiting;
    private JLabel lblCountCooking;
    private JLabel lblCountCompleted;
    private JLabel lblCountTotal;

    private JComboBox<String> comboStatusFilter;
    private JComboBox<String> comboAreaFilter;
    private JTextField txtSearchDish;
    private JCheckBox chkAutoRefresh;
    private Timer autoRefreshTimer;

    private DefaultTableModel ordersTableModel;
    private ModernTable ordersTable;
    private List<KitchenOrder> currentKitchenOrders = new ArrayList<>();

    // Tab 2 Components (Menu availability)
    private DefaultTableModel menuStatusTableModel;
    private ModernTable menuStatusTable;
    private List<MonAn> dishesList = new ArrayList<>();

    private final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

    public KitchenManagementPanel(MainFrame mainFrame, NhanVien currentUser) {
        this.mainFrame = mainFrame;
        this.currentUser = (currentUser != null) ? currentUser : new NhanVien(4, "Phạm Quốc Tuấn", "chef01", "tuan123", "Bep", "DangLam");

        setLayout(new BorderLayout(0, 12));
        setBackground(UIConstants.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        initAutoRefreshTimer();
        loadKitchenOrders();
        loadDishesAvailability();
    }

    private void initUI() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Trung Tâm Điều Hành Bếp & Bar (Kitchen Ops) ");
        lblTitle.setFont(UIConstants.FONT_PAGE_TITLE);
        lblTitle.setForeground(UIConstants.TEXT_MAIN);

        JLabel lblSubtitle = new JLabel("Tiếp nhận order từ các bàn, cập nhật tiến độ chế biến và quản lý tình trạng thực đơn");
        lblSubtitle.setFont(UIConstants.FONT_HEADER_SUB);
        lblSubtitle.setForeground(UIConstants.TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane chứa 2 màn hình
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tab 1: Màn hình nhận và chế biến order (KDS)
        JPanel tabOrders = createOrdersTab();
        tabbedPane.addTab("🍳 Điều Hành Bếp & Chế Biến", tabOrders);

        // Tab 2: Quản lý tình trạng món ăn (Báo hết hàng / Còn hàng)
        JPanel tabMenu = createMenuAvailabilityTab();
        tabbedPane.addTab("📦 Kho Thực Đơn & Báo Hết Món", tabMenu);

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Tab 1: Màn hình nhận Order và xử lý trạng thái chế biến
     */
    private JPanel createOrdersTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UIConstants.BG_LIGHT);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // 1. KPI Cards Bar: Đếm số món theo trạng thái
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiRow.setOpaque(false);
        kpiRow.setPreferredSize(new Dimension(0, 75));

        lblCountWaiting = new JLabel("0");
        lblCountCooking = new JLabel("0");
        lblCountCompleted = new JLabel("0");
        lblCountTotal = new JLabel("0");

        kpiRow.add(createKpiCard("CHỜ CHẾ BIẾN", lblCountWaiting, new Color(226, 135, 67), "⏳"));
        kpiRow.add(createKpiCard("ĐANG NẤU", lblCountCooking, UIConstants.PRIMARY_BLUE, "🔥"));
        kpiRow.add(createKpiCard("ĐÃ XONG / RA MÓN", lblCountCompleted, UIConstants.SUCCESS_GREEN, "✅"));
        kpiRow.add(createKpiCard("TỔNG ORDER", lblCountTotal, new Color(100, 116, 139), "📋"));

        panel.add(kpiRow, BorderLayout.NORTH);

        // 2. Vùng trung tâm: Thanh lọc + Bảng Order + Nút hành động
        JPanel centerContainer = new JPanel(new BorderLayout(0, 10));
        centerContainer.setOpaque(false);

        // Thanh Filter & Search
        RoundedPanel filterBar = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        filterBar.setLayout(new BorderLayout(10, 0));
        filterBar.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel leftFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilter.setOpaque(false);

        leftFilter.add(new JLabel("Trạng thái món:"));
        comboStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Chờ nấu", "Đang nấu", "Đã nấu xong"});
        comboStatusFilter.setBackground(Color.WHITE);
        comboStatusFilter.setPreferredSize(new Dimension(130, 32));
        comboStatusFilter.addActionListener(e -> filterKitchenOrders());
        leftFilter.add(comboStatusFilter);

        leftFilter.add(new JLabel("Khu vực / Bàn:"));
        comboAreaFilter = new JComboBox<>(new String[]{"Tất cả", "Tầng 1", "Tầng 2", "Phòng VIP"});
        comboAreaFilter.setBackground(Color.WHITE);
        comboAreaFilter.setPreferredSize(new Dimension(120, 32));
        comboAreaFilter.addActionListener(e -> filterKitchenOrders());
        leftFilter.add(comboAreaFilter);

        leftFilter.add(new JLabel("Tìm món:"));
        txtSearchDish = new JTextField();
        txtSearchDish.setPreferredSize(new Dimension(160, 32));
        txtSearchDish.putClientProperty("JTextField.placeholderText", "Tên món ăn...");
        txtSearchDish.addActionListener(e -> filterKitchenOrders());
        leftFilter.add(txtSearchDish);

        // Bên phải: Tự động làm mới & nút Làm mới
        JPanel rightFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightFilter.setOpaque(false);

        chkAutoRefresh = new JCheckBox("Tự động làm mới (10s)", true);
        chkAutoRefresh.setFont(UIConstants.FONT_REGULAR);
        chkAutoRefresh.setOpaque(false);
        chkAutoRefresh.addActionListener(e -> {
            if (chkAutoRefresh.isSelected()) {
                autoRefreshTimer.start();
            } else {
                autoRefreshTimer.stop();
            }
        });
        rightFilter.add(chkAutoRefresh);

        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.setFont(UIConstants.FONT_REGULAR);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadKitchenOrders());
        rightFilter.add(btnRefresh);

        filterBar.add(leftFilter, BorderLayout.WEST);
        filterBar.add(rightFilter, BorderLayout.EAST);
        centerContainer.add(filterBar, BorderLayout.NORTH);

        // Bảng danh sách Order
        String[] cols = {"Mã CTHD", "Bàn Ăn", "Khu Vực", "Tên Món Ăn", "SL", "Thời Gian Gọi", "Chờ (Phút)", "Ghi Chú Bếp", "Trạng Thái"};
        ordersTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        ordersTable = new ModernTable(ordersTableModel);
        ordersTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setupOrdersTableRenderers();

        JScrollPane scrollOrders = new JScrollPane(ordersTable);
        scrollOrders.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        scrollOrders.getViewport().setBackground(Color.WHITE);
        centerContainer.add(scrollOrders, BorderLayout.CENTER);

        // Hàng nút thao tác nghiệp vụ bếp
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        actionBar.setOpaque(false);

        JLabel lblHint = new JLabel("💡 Mẹo: Giữ phím Ctrl hoặc Shift để chọn nhiều món và thao tác hàng loạt");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHint.setForeground(UIConstants.TEXT_MUTED);

        JButton btnStartCooking = new JButton("🍳 Bắt Đầu Nấu (Đang Làm)");
        btnStartCooking.setFont(UIConstants.FONT_BOLD);
        btnStartCooking.setBackground(UIConstants.PRIMARY_BLUE);
        btnStartCooking.setForeground(Color.WHITE);
        btnStartCooking.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStartCooking.addActionListener(e -> handleUpdateSelectedStatus("DangLam", "Bắt đầu nấu"));

        JButton btnFinishDish = new JButton("✅ Hoàn Thành (Ra Món)");
        btnFinishDish.setFont(UIConstants.FONT_BOLD);
        btnFinishDish.setBackground(UIConstants.SUCCESS_GREEN);
        btnFinishDish.setForeground(Color.WHITE);
        btnFinishDish.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFinishDish.addActionListener(e -> handleUpdateSelectedStatus("DaRaMon", "Đã ra món"));

        JButton btnCancelDish = new JButton("❌ Hủy Món");
        btnCancelDish.setFont(UIConstants.FONT_REGULAR);
        btnCancelDish.setBackground(new Color(254, 242, 242));
        btnCancelDish.setForeground(UIConstants.DANGER_RED);
        btnCancelDish.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelDish.addActionListener(e -> handleCancelSelectedDish());

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setOpaque(false);
        bottomContainer.add(lblHint, BorderLayout.WEST);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(btnStartCooking);
        btnGroup.add(btnFinishDish);
        btnGroup.add(btnCancelDish);
        bottomContainer.add(btnGroup, BorderLayout.EAST);

        centerContainer.add(bottomContainer, BorderLayout.SOUTH);
        panel.add(centerContainer, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tab 2: Quản lý tình trạng món ăn (Báo hết hàng / Mở bán lại)
     */
    private JPanel createMenuAvailabilityTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UIConstants.BG_LIGHT);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Thanh công cụ
        RoundedPanel toolCard = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        toolCard.setLayout(new BorderLayout(10, 0));
        toolCard.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel lblNotice = new JLabel("<html><b>Quản lý tình trạng nguyên liệu & món ăn:</b> Khi Bếp đánh dấu <i>Hết hàng</i>, món ăn sẽ lập tức bị khóa trên màn hình Order.</html>");
        lblNotice.setFont(UIConstants.FONT_REGULAR);
        lblNotice.setForeground(UIConstants.TEXT_MAIN);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setOpaque(false);

        JButton btnMarkOutOfStock = new JButton("🔴 Báo Hết Hàng");
        btnMarkOutOfStock.setFont(UIConstants.FONT_BOLD);
        btnMarkOutOfStock.setBackground(UIConstants.DANGER_RED);
        btnMarkOutOfStock.setForeground(Color.WHITE);
        btnMarkOutOfStock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMarkOutOfStock.addActionListener(e -> handleToggleAvailability(false));

        JButton btnMarkAvailable = new JButton("🟢 Mở Bán Lại (Còn Món)");
        btnMarkAvailable.setFont(UIConstants.FONT_BOLD);
        btnMarkAvailable.setBackground(UIConstants.SUCCESS_GREEN);
        btnMarkAvailable.setForeground(Color.WHITE);
        btnMarkAvailable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMarkAvailable.addActionListener(e -> handleToggleAvailability(true));

        JButton btnReloadMenu = new JButton("🔄 Nạp lại");
        btnReloadMenu.setFont(UIConstants.FONT_REGULAR);
        btnReloadMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReloadMenu.addActionListener(e -> loadDishesAvailability());

        btnGroup.add(btnMarkOutOfStock);
        btnGroup.add(btnMarkAvailable);
        btnGroup.add(btnReloadMenu);

        toolCard.add(lblNotice, BorderLayout.WEST);
        toolCard.add(btnGroup, BorderLayout.EAST);
        panel.add(toolCard, BorderLayout.NORTH);

        // Bảng danh sách món ăn & trạng thái
        String[] cols = {"Mã Món", "Tên Món Ăn", "Danh Mục", "Đơn Giá (VND)", "Tình Trạng Bếp"};
        menuStatusTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        menuStatusTable = new ModernTable(menuStatusTableModel);
        menuStatusTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String valStr = value != null ? value.toString() : "";
                if (!isSelected) {
                    if (valStr.contains("Còn món")) {
                        lbl.setForeground(new Color(46, 160, 67));
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        lbl.setForeground(new Color(219, 55, 55));
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    }
                }
                return lbl;
            }
        });

        JScrollPane spMenu = new JScrollPane(menuStatusTable);
        spMenu.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        spMenu.getViewport().setBackground(Color.WHITE);
        panel.add(spMenu, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createKpiCard(String title, JLabel lblValue, Color accentColor, String icon) {
        RoundedPanel card = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        card.setLayout(new BorderLayout(8, 4));
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblT.setForeground(UIConstants.TEXT_MUTED);

        JLabel lblI = new JLabel(icon);
        lblI.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        top.add(lblT, BorderLayout.WEST);
        top.add(lblI, BorderLayout.EAST);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(accentColor);

        card.add(top, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private void setupOrdersTableRenderers() {
        // Cột 6: Thời gian chờ (phút) -> Nếu chờ quá 15 phút thì cảnh báo đỏ
        ordersTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    try {
                        long mins = Long.parseLong(value.toString());
                        if (mins >= 15) {
                            c.setForeground(new Color(219, 55, 55));
                            c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        } else {
                            c.setForeground(UIConstants.TEXT_MAIN);
                        }
                    } catch (Exception ignored) {}
                }
                return c;
            }
        });

        // Cột 8: Trạng thái -> Hiển thị huy hiệu màu
        ordersTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String s = value != null ? value.toString() : "";
                if (!isSelected) {
                    if (s.contains("Chờ")) {
                        lbl.setForeground(new Color(217, 119, 6)); // Amber
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else if (s.contains("Đang nấu")) {
                        lbl.setForeground(new Color(30, 90, 180)); // Blue
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else if (s.contains("Đã")) {
                        lbl.setForeground(new Color(46, 160, 67)); // Green
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        lbl.setForeground(UIConstants.TEXT_MUTED);
                    }
                }
                return lbl;
            }
        });
    }

    private void initAutoRefreshTimer() {
        // Tự động làm mới mỗi 10 giây
        autoRefreshTimer = new Timer(10000, e -> {
            if (isShowing()) {
                loadKitchenOrders();
            }
        });
        autoRefreshTimer.start();
    }

    public void loadKitchenOrders() {
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            currentKitchenOrders = kitchenDAO.getKitchenOrders(null, null, null);
        } else {
            currentKitchenOrders = DummyDataFactory.getInstance().getKitchenOrders();
        }

        updateKpiCounts();
        filterKitchenOrders();
    }

    private void updateKpiCounts() {
        int waitCount = 0;
        int cookCount = 0;
        int doneCount = 0;

        for (KitchenOrder o : currentKitchenOrders) {
            String st = o.getTrangThaiMon();
            if ("DangCho".equalsIgnoreCase(st) || "ChoLam".equalsIgnoreCase(st)) {
                waitCount += o.getSoLuong();
            } else if ("DangLam".equalsIgnoreCase(st)) {
                cookCount += o.getSoLuong();
            } else if ("DaRaMon".equalsIgnoreCase(st)) {
                doneCount += o.getSoLuong();
            }
        }

        lblCountWaiting.setText(String.valueOf(waitCount));
        lblCountCooking.setText(String.valueOf(cookCount));
        lblCountCompleted.setText(String.valueOf(doneCount));
        lblCountTotal.setText(String.valueOf(waitCount + cookCount + doneCount));
    }

    private void filterKitchenOrders() {
        ordersTableModel.setRowCount(0);

        String statusFilter = (String) comboStatusFilter.getSelectedItem();
        String areaFilter = (String) comboAreaFilter.getSelectedItem();
        String keyword = txtSearchDish.getText().trim().toLowerCase();

        for (KitchenOrder o : currentKitchenOrders) {
            // Lọc theo trạng thái
            if ("Chờ nấu".equalsIgnoreCase(statusFilter) && !"DangCho".equalsIgnoreCase(o.getTrangThaiMon()) && !"ChoLam".equalsIgnoreCase(o.getTrangThaiMon())) continue;
            if ("Đang nấu".equalsIgnoreCase(statusFilter) && !"DangLam".equalsIgnoreCase(o.getTrangThaiMon())) continue;
            if ("Đã nấu xong".equalsIgnoreCase(statusFilter) && !"DaRaMon".equalsIgnoreCase(o.getTrangThaiMon())) continue;

            // Lọc theo khu vực
            if (!"Tất cả".equalsIgnoreCase(areaFilter) && !areaFilter.equalsIgnoreCase(o.getKhuVuc())) continue;

            // Lọc theo tên món
            if (!keyword.isEmpty() && !o.getTenMon().toLowerCase().contains(keyword)) continue;

            String timeStr = o.getThoiGianGoi() != null ? sdfTime.format(o.getThoiGianGoi()) : "--";
            String statusText = formatStatusText(o.getTrangThaiMon());

            ordersTableModel.addRow(new Object[]{
                    "#" + o.getMaCTHD(),
                    o.getTenBan(),
                    o.getKhuVuc(),
                    o.getTenMon(),
                    o.getSoLuong(),
                    timeStr,
                    o.getSoPhutCho(),
                    o.getGhiChu(),
                    statusText
            });
        }
    }

    private String formatStatusText(String status) {
        if ("DangCho".equalsIgnoreCase(status) || "ChoLam".equalsIgnoreCase(status)) return "⏳ Chờ nấu";
        if ("DangLam".equalsIgnoreCase(status)) return "🔥 Đang nấu";
        if ("DaRaMon".equalsIgnoreCase(status)) return "✅ Đã nấu xong";
        if ("DaHuy".equalsIgnoreCase(status)) return "❌ Đã hủy";
        return status;
    }

    /**
     * Cập nhật trạng thái cho một hoặc nhiều món được chọn
     */
    private void handleUpdateSelectedStatus(String newStatus, String actionName) {
        int[] selectedRows = ordersTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn ít nhất một món ăn trên bảng để " + actionName + "!\n(Có thể giữ phím Ctrl hoặc Shift để chọn nhiều món)",
                    "Chưa chọn món", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Integer> selectedIds = new ArrayList<>();
        StringBuilder dishNames = new StringBuilder();

        for (int row : selectedRows) {
            int modelRow = ordersTable.convertRowIndexToModel(row);
            String idStr = ordersTableModel.getValueAt(modelRow, 0).toString().replace("#", "").trim();
            int maCTHD = Integer.parseInt(idStr);
            selectedIds.add(maCTHD);

            String name = ordersTableModel.getValueAt(modelRow, 3).toString();
            if (dishNames.length() > 0) dishNames.append(", ");
            dishNames.append(name);
        }

        boolean ok;
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            ok = kitchenDAO.updateBatchItemStatus(selectedIds, newStatus);
        } else {
            DummyDataFactory.getInstance().capNhatTrangThaiMonHangLoat(selectedIds, newStatus);
            ok = true;
        }

        if (ok) {
            loadKitchenOrders();
            JOptionPane.showMessageDialog(this,
                    "Đã chuyển trạng thái " + selectedIds.size() + " món sang '" + actionName + "' thành công!\nCác món: " + dishNames.toString(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái món. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancelSelectedDish() {
        int[] selectedRows = ordersTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần báo hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reason = JOptionPane.showInputDialog(this,
                "Nhập lý do báo hủy món ăn (Hết nguyên liệu, lỗi kỹ thuật...):",
                "Xác nhận Hủy món", JOptionPane.WARNING_MESSAGE);

        if (reason == null || reason.trim().isEmpty()) return;

        for (int row : selectedRows) {
            int modelRow = ordersTable.convertRowIndexToModel(row);
            String idStr = ordersTableModel.getValueAt(modelRow, 0).toString().replace("#", "").trim();
            int maCTHD = Integer.parseInt(idStr);

            if (com.restaurant.database.DatabaseConnection.isConnected()) {
                kitchenDAO.cancelItem(maCTHD, reason);
            } else {
                DummyDataFactory.getInstance().capNhatTrangThaiMonHangLoat(List.of(maCTHD), "DaHuy");
            }
        }

        loadKitchenOrders();
        JOptionPane.showMessageDialog(this, "Đã báo hủy các món được chọn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public void loadDishesAvailability() {
        menuStatusTableModel.setRowCount(0);
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            dishesList = monAnDAO.getAll();
        } else {
            dishesList = DummyDataFactory.getInstance().getDanhSachMonAn();
        }

        for (MonAn m : dishesList) {
            menuStatusTableModel.addRow(new Object[]{
                    "#" + m.getMaMon(),
                    m.getTenMon(),
                    m.getTenDM(),
                    UIConstants.formatCurrency(m.getGiaBan()),
                    m.isConMon() ? "🟢 Còn món phục vụ" : "🔴 Hết hàng"
            });
        }
    }

    private void handleToggleAvailability(boolean setAvailable) {
        int selectedRow = menuStatusTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn cần thay đổi tình trạng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = menuStatusTable.convertRowIndexToModel(selectedRow);
        String idStr = menuStatusTableModel.getValueAt(modelRow, 0).toString().replace("#", "").trim();
        int maMon = Integer.parseInt(idStr);
        String tenMon = menuStatusTableModel.getValueAt(modelRow, 1).toString();

        boolean ok;
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            ok = kitchenDAO.updateDishAvailability(maMon, setAvailable);
        } else {
            DummyDataFactory.getInstance().capNhatTinhTrangMon(maMon, setAvailable);
            ok = true;
        }

        if (ok) {
            loadDishesAvailability();
            String statusName = setAvailable ? "Còn món (Mở bán lại)" : "Hết hàng (Tạm ngưng nhận order)";
            JOptionPane.showMessageDialog(this,
                    "Đã chuyển món [" + tenMon + "] sang trạng thái: " + statusName + "!",
                    "Cập nhật thành công", JOptionPane.INFORMATION_MESSAGE);

            // Đồng bộ sang OrderPanel nếu đang mở
            if (mainFrame != null && mainFrame.getOrderPanel() != null) {
                mainFrame.getOrderPanel().refreshOrderPanel();
            }
        }
    }
}
