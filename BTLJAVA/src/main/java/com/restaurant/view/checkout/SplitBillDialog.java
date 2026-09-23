package com.restaurant.view.checkout;

import com.restaurant.model.ChiTietHoaDon;
import com.restaurant.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Hộp thoại Popup: Tách Hóa Đơn (Split Billing)
 * Cho phép thu ngân tick chọn các món ăn cần tách sang một hóa đơn riêng
 */
public class SplitBillDialog extends JDialog {

    private final List<ChiTietHoaDon> originalItems;
    private DefaultTableModel model;
    private JTable table;
    private boolean confirmed = false;
    private List<ChiTietHoaDon> splitItems = new ArrayList<>();

    public SplitBillDialog(Frame parent, String tableName, List<ChiTietHoaDon> items) {
        super(parent, "Tách hóa đơn - " + tableName, true);
        this.originalItems = items;

        setSize(550, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(0, 12));

        initUI();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblTitle = new JLabel("Chọn các món muốn tách sang hóa đơn mới:");
        lblTitle.setFont(UIConstants.FONT_BOLD);
        lblTitle.setForeground(UIConstants.TEXT_MAIN);
        header.add(lblTitle, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Bảng chọn món có checkbox
        String[] columns = {"Chọn", "Tên món", "Số lượng", "Đơn giá", "Thành tiền"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0) ? Boolean.class : super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Chỉ cho tick cột Checkbox
            }
        };

        if (originalItems != null) {
            for (ChiTietHoaDon ct : originalItems) {
                if (!"DaHuy".equalsIgnoreCase(ct.getTrangThaiMon())) {
                    model.addRow(new Object[]{
                            false,
                            ct.getTenMon(),
                            ct.getSoLuong(),
                            UIConstants.formatCurrency(ct.getDonGia()),
                            UIConstants.formatCurrency(ct.getThanhTien())
                    });
                }
            }
        }

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(UIConstants.FONT_REGULAR);
        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(0, 16, 0, 16));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottomPanel.setBackground(Color.WHITE);

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(UIConstants.FONT_REGULAR);
        btnCancel.addActionListener(e -> dispose());

        JButton btnConfirm = new JButton("Xác nhận Tách Hóa Đơn");
        btnConfirm.setFont(UIConstants.FONT_BOLD);
        btnConfirm.setBackground(UIConstants.PRIMARY_BLUE);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.addActionListener(e -> {
            collectSplitItems();
            confirmed = true;
            dispose();
        });

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnConfirm);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void collectSplitItems() {
        splitItems.clear();
        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean checked = (Boolean) model.getValueAt(i, 0);
            if (Boolean.TRUE.equals(checked) && i < originalItems.size()) {
                splitItems.add(originalItems.get(i));
            }
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<ChiTietHoaDon> getSplitItems() {
        return splitItems;
    }
}
