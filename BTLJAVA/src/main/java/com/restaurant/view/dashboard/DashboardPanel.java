package com.restaurant.view.dashboard;

import com.restaurant.dao.ThongKeDAO;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;
import com.restaurant.view.MainFrame;
import com.restaurant.view.components.FunnelChartPanel;
import com.restaurant.view.components.KpiCard;
import com.restaurant.view.components.ModernTable;
import com.restaurant.view.components.RevenueBarLineChartPanel;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Module 2: Màn hình Dashboard (Thống kê hoạt động kinh doanh)
 * Kết nối dữ liệu thực tế từ MySQL & nâng cấp đồ họa biểu đồ cao cấp:
 * - 4 Thẻ KPI động: Doanh thu, Đơn hàng, Tỷ lệ lấp đầy bàn, Thời gian phục vụ
 * - Biểu đồ Phễu Funnel: Quy trình Bàn & Order thời gian thực
 * - Biểu đồ Bar & Line: Xu hướng Doanh thu & Mục tiêu kinh doanh
 * - Bảng Top Món Ăn Bán Chạy Nhất từ CSDL
 * - Bộ lọc thời gian linh hoạt (Hôm nay, 7 ngày qua, Tháng này, Năm nay)
 */
public class DashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    private JComboBox<String> timeFilterCombo;
    private KpiCard cardRevenue;
    private KpiCard cardOrders;
    private KpiCard cardOccupancy;
    private KpiCard cardTableTurn;

    private FunnelChartPanel funnelChartPanel;
    private RevenueBarLineChartPanel revenueChartPanel;

    private ModernTable topDishesTable;
    private DefaultTableModel topDishesTableModel;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 16));
        setBackground(UIConstants.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadRealData();
    }

    private void initUI() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIConstants.BG_LIGHT);

        // 1. Tiêu đề + Bộ lọc thời gian + Nút làm mới
        container.add(createHeaderBar());
        container.add(Box.createVerticalStrut(12));

        // 2. Hàng 4 thẻ KPI Cards
        container.add(createKpiRow());
        container.add(Box.createVerticalStrut(16));

        // 3. Hàng 2 biểu đồ (Funnel Pipeline + Revenue vs Target)
        container.add(createChartsRow());
        container.add(Box.createVerticalStrut(16));

        // 4. Bảng danh sách Top Món Bán Chạy
        container.add(createTopDishesPanel());

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIConstants.BG_LIGHT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lblTitle = new JLabel("Thống kê Hoạt động Kinh doanh ");
        lblTitle.setFont(UIConstants.FONT_PAGE_TITLE);
        lblTitle.setForeground(UIConstants.TEXT_MAIN);
        lblTitle.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Vùng bên phải: Bộ lọc thời gian + Nút làm mới
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);

        JLabel lblFilter = new JLabel("Thời gian:");
        lblFilter.setFont(UIConstants.FONT_REGULAR);
        lblFilter.setForeground(UIConstants.TEXT_MUTED);
        rightActions.add(lblFilter);

        timeFilterCombo = new JComboBox<>(new String[]{"Tháng này", "7 ngày qua", "Hôm nay", "Quý này", "Năm nay", "Tất cả"});
        timeFilterCombo.setFont(UIConstants.FONT_REGULAR);
        timeFilterCombo.setPreferredSize(new Dimension(120, 32));
        timeFilterCombo.setBackground(Color.WHITE);
        timeFilterCombo.addActionListener(e -> loadRealData());
        rightActions.add(timeFilterCombo);

        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setPreferredSize(new Dimension(105, 32));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadRealData());
        rightActions.add(btnRefresh);

        bar.add(lblTitle, BorderLayout.WEST);
        bar.add(rightActions, BorderLayout.EAST);
        return bar;
    }

    private JPanel createKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        row.setPreferredSize(new Dimension(0, 120));

        cardRevenue = new KpiCard("Tổng Doanh Thu", "0 VND", "+12.4% MoM", true, "💰", new Color(254, 243, 199), new Color(217, 119, 6));
        cardOrders = new KpiCard("Tổng Đơn Hàng", "0 đơn", "+3.2% MoM", true, "🧾", new Color(243, 232, 255), new Color(147, 51, 234));
        cardOccupancy = new KpiCard("Tỷ Lệ Lấp Đầy Bàn", "0.0%", null, false, "🪑", new Color(255, 237, 213), new Color(234, 88, 12));
        cardTableTurn = new KpiCard("Thời Gian Phục Vụ", "0 phút", null, false, "⏱️", new Color(207, 250, 254), new Color(8, 145, 178));

        row.add(cardRevenue);
        row.add(cardOrders);
        row.add(cardOccupancy);
        row.add(cardTableTurn);

        return row;
    }

    private JPanel createChartsRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));
        row.setPreferredSize(new Dimension(0, 270));

        // Card trái: Quy trình Bàn & Gọi món (Funnel Chart)
        RoundedPanel leftCard = new RoundedPanel(14, Color.WHITE, UIConstants.BORDER_COLOR);
        leftCard.setLayout(new BorderLayout(0, 8));
        leftCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblPipelineTitle = new JLabel("Quy trình Bàn & Gọi món (Vận hành)");
        lblPipelineTitle.setFont(UIConstants.FONT_SECTION);
        lblPipelineTitle.setForeground(new Color(15, 23, 42));

        funnelChartPanel = new FunnelChartPanel();
        leftCard.add(lblPipelineTitle, BorderLayout.NORTH);
        leftCard.add(funnelChartPanel, BorderLayout.CENTER);

        // Card phải: Doanh thu & Mục tiêu (Bar + Line Chart)
        RoundedPanel rightCard = new RoundedPanel(14, Color.WHITE, UIConstants.BORDER_COLOR);
        rightCard.setLayout(new BorderLayout(0, 8));
        rightCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblRevTitle = new JLabel("Doanh thu & Mục tiêu kinh doanh");
        lblRevTitle.setFont(UIConstants.FONT_SECTION);
        lblRevTitle.setForeground(new Color(15, 23, 42));

        revenueChartPanel = new RevenueBarLineChartPanel();
        rightCard.add(lblRevTitle, BorderLayout.NORTH);
        rightCard.add(revenueChartPanel, BorderLayout.CENTER);

        row.add(leftCard);
        row.add(rightCard);

        return row;
    }

    private JPanel createTopDishesPanel() {
        RoundedPanel panel = new RoundedPanel(14, Color.WHITE, UIConstants.BORDER_COLOR);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblTableTitle = new JLabel("Top Món Ăn Bán Chạy Nhất (Thực tế từ CSDL)");
        lblTableTitle.setFont(UIConstants.FONT_SECTION);
        lblTableTitle.setForeground(new Color(15, 23, 42));

        panel.add(lblTableTitle, BorderLayout.NORTH);

        String[] columnNames = {"Tên món ăn", "Số lượng bán", "Doanh thu món"};
        topDishesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        topDishesTable = new ModernTable(topDishesTableModel);
        JScrollPane scrollTable = new JScrollPane(topDishesTable);
        scrollTable.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        scrollTable.setPreferredSize(new Dimension(0, 190));

        panel.add(scrollTable, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Nạp dữ liệu thực tế từ MySQL (hoặc bộ nhớ đệm DummyDataFactory khi offline)
     */
    public void loadRealData() {
        String filter = timeFilterCombo != null ? (String) timeFilterCombo.getSelectedItem() : "Tháng này";

        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            // 1. Nạp KPI thực tế từ MySQL
            Map<String, Object> kpi = thongKeDAO.getDashboardKPIs(filter);
            double tongDoanhThu = ((Number) kpi.getOrDefault("TongDoanhThu", 0.0)).doubleValue();
            int tongDon = ((Number) kpi.getOrDefault("TongDonHang", 0)).intValue();
            double rate = ((Number) kpi.getOrDefault("TyLeLapDay", 0.0)).doubleValue();
            int busy = ((Number) kpi.getOrDefault("BanCoKhach", 0)).intValue();
            int total = ((Number) kpi.getOrDefault("TongBan", 20)).intValue();
            int avgTime = ((Number) kpi.getOrDefault("ThoiGianTB", 55)).intValue();

            // Hiển thị định dạng tiền tệ ngắn gọn, dễ đọc
            if (tongDoanhThu >= 1_000_000_000) {
                cardRevenue.setValue(String.format("%,.2f Tỷ VND", tongDoanhThu / 1_000_000_000.0));
            } else if (tongDoanhThu >= 1_000_000) {
                cardRevenue.setValue(String.format("%,.1fM VND", tongDoanhThu / 1_000_000.0));
            } else {
                cardRevenue.setValue(UIConstants.formatCurrency(tongDoanhThu));
            }
            cardRevenue.setSubtitle("Kỳ: " + filter);

            cardOrders.setValue(tongDon + " đơn");
            cardOrders.setSubtitle("Đã thanh toán");

            cardOccupancy.setValue(String.format("%.1f%%", rate));
            cardOccupancy.setSubtitle(busy + "/" + total + " bàn có khách");

            cardTableTurn.setValue(avgTime + " phút");
            cardTableTurn.setSubtitle("Thời gian TB / bàn");

            // 2. Nạp dữ liệu Biểu đồ Phễu Funnel thực tế
            if (funnelChartPanel != null) {
                int[] funnel = thongKeDAO.getFunnelPipelineData();
                funnelChartPanel.updateData(funnel[0], funnel[1], funnel[2], funnel[3], funnel[4]);
            }

            // 3. Nạp dữ liệu Biểu đồ Doanh Thu & Mục Tiêu thực tế
            if (revenueChartPanel != null) {
                Map<String, Object> trend = thongKeDAO.getRevenueTrendData(filter);
                String[] labels = (String[]) trend.get("labels");
                double[] revs = (double[]) trend.get("revenues");
                double[] targs = (double[]) trend.get("targets");
                revenueChartPanel.updateChartData(labels, revs, targs);
            }

            // 4. Nạp dữ liệu bảng Top Món Ăn Bán Chạy thực tế
            if (topDishesTableModel != null) {
                topDishesTableModel.setRowCount(0);
                List<Object[]> dbTop = thongKeDAO.getTopMonBanChay(7);
                if (dbTop != null && !dbTop.isEmpty()) {
                    for (Object[] r : dbTop) {
                        topDishesTableModel.addRow(new Object[]{
                                r[0],
                                r[1] + " phần",
                                UIConstants.formatCurrency((Double) r[2])
                        });
                    }
                }
            }
        } else {
            // Chế độ Offline bộ nhớ đệm
            loadFallbackData();
        }
    }

    private void loadFallbackData() {
        DummyDataFactory factory = DummyDataFactory.getInstance();
        List<com.restaurant.model.Ban> banList = factory.getDanhSachBan();
        int totalTables = banList != null ? banList.size() : 20;
        int busyTables = 0;
        if (banList != null) {
            for (com.restaurant.model.Ban b : banList) {
                if (b.isCoKhach()) busyTables++;
            }
        }
        double rate = totalTables > 0 ? (busyTables * 100.0 / totalTables) : 0.0;

        cardRevenue.setValue("64.2M VND");
        cardRevenue.setSubtitle("Bộ đệm tạm thời (Offline)");

        cardOrders.setValue("28 đơn");
        cardOrders.setSubtitle("Bộ đệm tạm thời");

        cardOccupancy.setValue(String.format("%.1f%%", rate));
        cardOccupancy.setSubtitle(busyTables + " / " + totalTables + " bàn đang có khách");

        cardTableTurn.setValue("65 phút");
        cardTableTurn.setSubtitle("Thời gian trung bình");

        if (funnelChartPanel != null) {
            funnelChartPanel.updateData(totalTables, busyTables, Math.max(1, busyTables - 1), 6, 4);
        }

        if (topDishesTableModel != null && topDishesTableModel.getRowCount() == 0) {
            Object[][] defaultData = {
                    {"Bò bít tết Wagyu sốt tiêu", "125 phần", "61,875,000 VND"},
                    {"Tôm hùm nướng bơ tỏi", "72 phần", "61,200,000 VND"},
                    {"Cua sốt ớt Singapore", "84 phần", "46,200,000 VND"},
                    {"Cá hồi áp chảo sốt chanh dây", "95 phần", "34,200,000 VND"},
                    {"Súp vi cá bào ngư", "110 phần", "26,950,000 VND"},
                    {"Rượu vang đỏ Cabernet Sauvignon", "38 chai", "25,840,000 VND"}
            };
            for (Object[] row : defaultData) {
                topDishesTableModel.addRow(row);
            }
        }
    }

    public void refreshData() {
        loadRealData();
    }
}
