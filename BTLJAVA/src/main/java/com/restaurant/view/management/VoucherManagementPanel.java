package com.restaurant.view.management;

import com.restaurant.dao.VoucherDAO;
import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.Voucher;
import com.restaurant.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Quản lý Khuyến mãi & Voucher (Kế thừa BaseCrudPanel)
 */
public class VoucherManagementPanel extends BaseCrudPanel {

    private JTextField txtCode;
    private JTextField txtName;
    private JSpinner spinPercent;
    private JTextField txtMaxDiscount;
    private JTextField txtMinOrder;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JComboBox<String> comboStatus;

    private VoucherDAO voucherDAO = new VoucherDAO();
    private List<Voucher> mockVouchers = new ArrayList<>();

    public VoucherManagementPanel() {
        super("QUẢN LÝ KHUYẾN MÃI & VOUCHER GIẢM GIÁ");
        initMockData();
        loadData();
    }

    private void initMockData() {
        mockVouchers.add(new Voucher("LEHOI10", "Khuyến mãi Lễ Hội giảm 10%", 10.0, 100000, 500000, "2026-01-01", "2026-12-31", "HoatDong"));
        mockVouchers.add(new Voucher("VIP20", "Tri ân khách VIP thân thiết 20%", 20.0, 300000, 1000000, "2026-01-01", "2026-12-31", "HoatDong"));
        mockVouchers.add(new Voucher("GIAM50K", "Chiết khấu trực tiếp 50k", 0.0, 50000, 300000, "2026-01-01", "2026-12-31", "HoatDong"));
    }

    @Override
    protected void setupFormFields(JPanel container) {
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);

        // Hàng 0: Mã Voucher & Tên chương trình
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        container.add(new JLabel("Mã Voucher:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        txtCode = new JTextField();
        container.add(txtCode, gbc);

        gbc.gridx = 2; gbc.weightx = 0.15;
        container.add(new JLabel("Tên khuyến mãi:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.35;
        txtName = new JTextField();
        container.add(txtName, gbc);

        // Hàng 1: % Giảm & Giảm tối đa
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15;
        container.add(new JLabel("% Giảm giá:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        spinPercent = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 100.0, 1.0));
        container.add(spinPercent, gbc);

        gbc.gridx = 2; gbc.weightx = 0.15;
        container.add(new JLabel("Giảm tối đa (VND):"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.35;
        txtMaxDiscount = new JTextField("100000");
        container.add(txtMaxDiscount, gbc);

        // Hàng 2: Đơn tối thiểu & Trạng thái
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.15;
        container.add(new JLabel("Đơn tối thiểu (VND):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        txtMinOrder = new JTextField("500000");
        container.add(txtMinOrder, gbc);

        gbc.gridx = 2; gbc.weightx = 0.15;
        container.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.35;
        comboStatus = new JComboBox<>(new String[]{"HoatDong", "TamDung", "HetHan"});
        container.add(comboStatus, gbc);

        // Hàng 3: Ngày Bắt Đầu & Ngày Kết Thúc
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.15;
        container.add(new JLabel("Ngày bắt đầu:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        txtStartDate = new JTextField("2026-01-01");
        container.add(txtStartDate, gbc);

        gbc.gridx = 2; gbc.weightx = 0.15;
        container.add(new JLabel("Ngày kết thúc:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.35;
        txtEndDate = new JTextField("2026-12-31");
        container.add(txtEndDate, gbc);
    }

    @Override
    protected DefaultTableModel createTableModel() {
        String[] cols = {"Mã Voucher", "Tên Khuyến Mãi", "% Giảm", "Giảm Tối Đa", "Đơn Tối Thiểu", "Hạn Dùng", "Trạng Thái"};
        return new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Voucher> list = DatabaseConnection.isConnected() ? voucherDAO.getAll() : mockVouchers;
        for (Voucher v : list) {
            tableModel.addRow(new Object[]{
                    v.getMaVoucher(),
                    v.getTenVoucher(),
                    v.getPhanTramGiam() + "%",
                    UIConstants.formatCurrency(v.getGiamToiDa()),
                    UIConstants.formatCurrency(v.getDonHangToiThieu()),
                    v.getNgayKetThuc(),
                    v.getTrangThai()
            });
        }
    }

    @Override
    protected void onTableRowSelected(int selectedRow) {
        txtCode.setText(tableModel.getValueAt(selectedRow, 0).toString());
        txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());

        String pStr = tableModel.getValueAt(selectedRow, 2).toString().replace("%", "").trim();
        try {
            spinPercent.setValue(Double.parseDouble(pStr));
        } catch (Exception ignored) {}

        txtMaxDiscount.setText(tableModel.getValueAt(selectedRow, 3).toString().replaceAll("[^0-9]", ""));
        txtMinOrder.setText(tableModel.getValueAt(selectedRow, 4).toString().replaceAll("[^0-9]", ""));
        txtEndDate.setText(tableModel.getValueAt(selectedRow, 5).toString());
        comboStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 6).toString());
    }

    @Override
    protected void onAdd() {
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();

        if (code.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Voucher và Tên chương trình!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double percent = (double) spinPercent.getValue();
        double maxDisc = parseDoubleSafe(txtMaxDiscount.getText().trim());
        double minOrder = parseDoubleSafe(txtMinOrder.getText().trim());
        String start = txtStartDate.getText().trim();
        String end = txtEndDate.getText().trim();
        String status = (String) comboStatus.getSelectedItem();

        Voucher v = new Voucher(code, name, percent, maxDisc, minOrder, start, end, status);

        if (DatabaseConnection.isConnected()) {
            boolean ok = voucherDAO.insert(v);
            if (ok) {
                loadData();
                JOptionPane.showMessageDialog(this, "Đã thêm mã Voucher [" + code + "] vào CSDL MySQL thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                onReset();
                return;
            }
        }

        mockVouchers.add(v);
        loadData();
        JOptionPane.showMessageDialog(this, "Đã thêm mã Voucher thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        onReset();
    }

    @Override
    protected void onEdit() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Voucher để chỉnh sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();
        double percent = (double) spinPercent.getValue();
        double maxDisc = parseDoubleSafe(txtMaxDiscount.getText().trim());
        double minOrder = parseDoubleSafe(txtMinOrder.getText().trim());
        String start = txtStartDate.getText().trim();
        String end = txtEndDate.getText().trim();
        String status = (String) comboStatus.getSelectedItem();

        Voucher v = new Voucher(code, name, percent, maxDisc, minOrder, start, end, status);

        if (DatabaseConnection.isConnected()) {
            boolean ok = voucherDAO.update(v);
            if (ok) {
                loadData();
                JOptionPane.showMessageDialog(this, "Cập nhật Voucher trong CSDL MySQL thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        loadData();
        JOptionPane.showMessageDialog(this, "Cập nhật thông tin Voucher thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void onDelete() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Voucher để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = tableModel.getValueAt(selected, 0).toString();
        int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa Voucher [" + code + "]?", "Xác nhận Xóa", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (DatabaseConnection.isConnected()) {
                voucherDAO.delete(code);
            }
            mockVouchers.removeIf(v -> v.getMaVoucher().equalsIgnoreCase(code));
            loadData();
            JOptionPane.showMessageDialog(this, "Đã xóa Voucher thành công!", "Đã xóa", JOptionPane.INFORMATION_MESSAGE);
            onReset();
        }
    }

    @Override
    protected void onReset() {
        txtCode.setText("");
        txtName.setText("");
        spinPercent.setValue(10.0);
        txtMaxDiscount.setText("100000");
        txtMinOrder.setText("500000");
        txtStartDate.setText("2026-01-01");
        txtEndDate.setText("2026-12-31");
        comboStatus.setSelectedIndex(0);
        table.clearSelection();
    }

    @Override
    protected void onSearch(String keyword) {
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        tableModel.setRowCount(0);
        List<Voucher> list = DatabaseConnection.isConnected() ? voucherDAO.getAll() : mockVouchers;
        for (Voucher v : list) {
            if (v.getMaVoucher().toLowerCase().contains(keyword.toLowerCase()) ||
                    v.getTenVoucher().toLowerCase().contains(keyword.toLowerCase())) {
                tableModel.addRow(new Object[]{
                        v.getMaVoucher(),
                        v.getTenVoucher(),
                        v.getPhanTramGiam() + "%",
                        UIConstants.formatCurrency(v.getGiamToiDa()),
                        UIConstants.formatCurrency(v.getDonHangToiThieu()),
                        v.getNgayKetThuc(),
                        v.getTrangThai()
                });
            }
        }
    }

    private double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }
}
