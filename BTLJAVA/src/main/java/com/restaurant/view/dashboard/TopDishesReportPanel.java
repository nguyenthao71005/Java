package com.restaurant.view.dashboard;

import com.restaurant.dao.ThongKeDAO;
import com.restaurant.database.DatabaseConnection;
import com.restaurant.util.UIConstants;
import com.restaurant.view.components.KpiCard;
import com.restaurant.view.components.ModernTable;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Màn hình Báo Cáo Top Món Ăn Bán Chạy Nhất & Phân Tích Thực Đơn
 */
public class TopDishesReportPanel extends JPanel {

    private KpiCard kpiTop1;
    private KpiCard kpiTop2;
    private KpiCard kpiTop3;

    private ModernTable tableDishes;
    private DefaultTableModel modelDishes;

    public TopDishesReportPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UIConstants.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadData();
    }

    private void initUI() {
        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Báo Cáo Xếp Hạng Top Món Ăn Bán Chạy");
        lblTitle.setFont(UIConstants.FONT_PAGE_TITLE);
        lblTitle.setForeground(UIConstants.TEXT_MAIN);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Làm mới từ CSDL");
        btnRefresh.setFont(UIConstants.FONT_REGULAR);
        btnRefresh.addActionListener(e -> loadData());
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Center: Top 3 Cards + Bảng danh sách chi tiết
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);

        // Hàng 3 thẻ vinh danh Top 3
        JPanel top3Row = new JPanel(new GridLayout(1, 3, 14, 0));
        top3Row.setOpaque(false);
        top3Row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        top3Row.setPreferredSize(new Dimension(0, 110));

        kpiTop1 = new KpiCard("Quán Quân (Top 1 Doanh Số)", "Bò Wagyu sốt tiêu", "125 phần", true);
        kpiTop2 = new KpiCard("Á Quân (Top 2 Doanh Số)", "Tôm hùm nướng bơ tỏi", "72 phần", true);
        kpiTop3 = new KpiCard("Hạng 3 (Top 3 Doanh Số)", "Cua sốt ớt Singapore", "84 phần", true);

        top3Row.add(kpiTop1);
        top3Row.add(kpiTop2);
        top3Row.add(kpiTop3);

        centerContainer.add(top3Row);
        centerContainer.add(Box.createVerticalStrut(14));

        // Bảng chi tiết món bán chạy
        RoundedPanel tableCard = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblTableTitle = new JLabel("Bảng Chi Tiết Xếp Hạng Món Ăn Theo Doanh Thu (View v_TopMonBanChay)");
        lblTableTitle.setFont(UIConstants.FONT_SECTION);
        lblTableTitle.setForeground(UIConstants.TEXT_MAIN);
        tableCard.add(lblTableTitle, BorderLayout.NORTH);

        String[] cols = {"Hạng", "Tên Món Ăn", "Tổng Số Lượng Bán", "Tổng Doanh Thu (VND)", "Tỷ Trọng Đóng Góp"};
        modelDishes = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tableDishes = new ModernTable(modelDishes);
        JScrollPane scrollPane = new JScrollPane(tableDishes);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerContainer.add(tableCard);

        add(centerContainer, BorderLayout.CENTER);
    }

    public void loadData() {
        modelDishes.setRowCount(0);
        boolean loaded = false;

        if (DatabaseConnection.isConnected()) {
            List<Object[]> list = new ThongKeDAO().getTopMonBanChay(20);
            if (list != null && !list.isEmpty()) {
                double totalTopRev = 0;
                for (Object[] r : list) {
                    totalTopRev += (Double) r[2];
                }

                int rank = 1;
                for (Object[] r : list) {
                    String name = (String) r[0];
                    int qty = (int) r[1];
                    double rev = (double) r[2];
                    double pct = totalTopRev > 0 ? (rev * 100.0 / totalTopRev) : 0;

                    if (rank == 1) kpiTop1.setValue(name);
                    else if (rank == 2) kpiTop2.setValue(name);
                    else if (rank == 3) kpiTop3.setValue(name);

                    modelDishes.addRow(new Object[]{
                            "#" + rank,
                            name,
                            qty + " phần",
                            UIConstants.formatCurrency(rev),
                            String.format("%.1f%%", pct)
                    });
                    rank++;
                }
                loaded = true;
            }
        }

        if (!loaded) {
            Object[][] defaultData = {
                    {"#1", "Bò bít tết Wagyu sốt tiêu", "125 phần", "61.875.000 VND", "24.5%"},
                    {"#2", "Tôm hùm nướng bơ tỏi", "72 phần", "61.200.000 VND", "24.2%"},
                    {"#3", "Cua sốt ớt Singapore", "84 phần", "46.200.000 VND", "18.3%"},
                    {"#4", "Cá hồi áp chảo sốt chanh dây", "95 phần", "34.200.000 VND", "13.5%"},
                    {"#5", "Súp vi cá bào ngư", "110 phần", "26.950.000 VND", "10.7%"},
                    {"#6", "Rượu vang đỏ Cabernet Sauvignon", "38 chai", "25.840.000 VND", "10.2%"},
                    {"#7", "Sườn heo nướng BBQ", "76 phần", "24.320.000 VND", "9.6%"}
            };
            for (Object[] row : defaultData) {
                modelDishes.addRow(row);
            }
        }
    }
}
