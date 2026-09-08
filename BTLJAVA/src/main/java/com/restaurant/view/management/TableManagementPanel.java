package com.restaurant.view.management;

import com.restaurant.model.Ban;
import com.restaurant.util.DummyDataFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Module 5D: Quản lý Danh sách Bàn (Tables Management)
 * Ánh xạ với bảng CSDL: `Ban`
 */
public class TableManagementPanel extends BaseCrudPanel {

    private JTextField txtTableId;
    private JTextField txtTableName;
    private JComboBox<String> comboArea;
    private JSpinner spinCapacity;
    private JComboBox<String> comboStatus;

    public TableManagementPanel() {
        super("Quản lý Danh sách Bàn ăn");
        loadData();
    }

    @Override
    protected void setupFormFields(JPanel container) {
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);

        // Hàng 1: Mã bàn & Tên bàn
        gbc.gridx = 0; gbc.gridy = 0;
        container.add(new JLabel("Mã Bàn:"), gbc);

        gbc.gridx = 1;
        txtTableId = new JTextField();
        txtTableId.setEditable(false);
        txtTableId.setPreferredSize(new Dimension(80, 30));
        txtTableId.setBackground(new Color(240, 240, 240));
        container.add(txtTableId, gbc);

        gbc.gridx = 2;
        container.add(new JLabel("Tên Bàn:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        txtTableName = new JTextField();
        txtTableName.setPreferredSize(new Dimension(180, 30));
        container.add(txtTableName, gbc);

        // Hàng 2: Khu vực & Sức chứa & Trạng thái
        gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = 1;
        container.add(new JLabel("Khu Vực:"), gbc);

        gbc.gridx = 1;
        comboArea = new JComboBox<>(new String[]{"Tầng 1", "Tầng 2", "VIP", "Sân thượng"});
        comboArea.setBackground(Color.WHITE);
        container.add(comboArea, gbc);

        gbc.gridx = 2;
        container.add(new JLabel("Sức Chứa (Ghế):"), gbc);

        gbc.gridx = 3;
        spinCapacity = new JSpinner(new SpinnerNumberModel(4, 1, 30, 1));
        container.add(spinCapacity, gbc);
    }

    @Override
    protected DefaultTableModel createTableModel() {
        String[] cols = {"Mã Bàn", "Tên Bàn", "Khu Vực", "Sức Chứa", "Trạng Thái"};
        return new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private com.restaurant.dao.BanDAO banDAO = new com.restaurant.dao.BanDAO();

    private void loadData() {
        tableModel.setRowCount(0);
        List<Ban> list = com.restaurant.database.DatabaseConnection.isConnected()
                ? banDAO.getAll()
                : DummyDataFactory.getInstance().getDanhSachBan();
        for (Ban b : list) {
            tableModel.addRow(new Object[]{
                    b.getMaBan(),
                    b.getTenBan(),
                    b.getKhuVuc(),
                    b.getSucChua() + " người",
                    b.isCoKhach() ? "Có khách" : "Trống"
            });
        }
    }

    @Override
    protected void onTableRowSelected(int selectedRow) {
        txtTableId.setText(tableModel.getValueAt(selectedRow, 0).toString());
        txtTableName.setText(tableModel.getValueAt(selectedRow, 1).toString());
        comboArea.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());

        String capStr = tableModel.getValueAt(selectedRow, 3).toString().replaceAll("[^0-9]", "");
        try {
            spinCapacity.setValue(Integer.parseInt(capStr));
        } catch (Exception ignored) {}
    }

    @Override
    protected void onAdd() {
        String name = txtTableName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên bàn!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String area = (String) comboArea.getSelectedItem();
        int cap = (int) spinCapacity.getValue();

        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            Ban ban = new Ban(0, name, area, cap, "Trong");
            boolean ok = banDAO.insert(ban);
            if (ok) {
                loadData();
                JOptionPane.showMessageDialog(this, "Đã thêm bàn mới vào CSDL MySQL thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                onReset();
                return;
            }
        }

        int newId = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{newId, name, area, cap + " người", "Trống"});
        JOptionPane.showMessageDialog(this, "Đã thêm bàn mới thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        onReset();
    }

    @Override
    protected void onEdit() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int maBan = Integer.parseInt(tableModel.getValueAt(selected, 0).toString());
        String name = txtTableName.getText().trim();
        String area = (String) comboArea.getSelectedItem();
        int cap = (int) spinCapacity.getValue();
        String status = tableModel.getValueAt(selected, 4).toString().equalsIgnoreCase("Có khách") ? "CoKhach" : "Trong";

        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            Ban ban = new Ban(maBan, name, area, cap, status);
            boolean ok = banDAO.update(ban);
            if (ok) {
                loadData();
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin bàn trong CSDL MySQL thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        tableModel.setValueAt(name, selected, 1);
        tableModel.setValueAt(area, selected, 2);
        tableModel.setValueAt(cap + " người", selected, 3);
        JOptionPane.showMessageDialog(this, "Cập nhật thông tin bàn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void onDelete() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int maBan = Integer.parseInt(tableModel.getValueAt(selected, 0).toString());
        String name = tableModel.getValueAt(selected, 1).toString();
        int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa bàn [" + name + "]?", "Xác nhận Xóa", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (com.restaurant.database.DatabaseConnection.isConnected()) {
                boolean ok = banDAO.delete(maBan);
                if (ok) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Đã xóa bàn khỏi CSDL MySQL thành công!", "Đã xóa", JOptionPane.INFORMATION_MESSAGE);
                    onReset();
                    return;
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa bàn này vì đang có hóa đơn gắn liền!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            tableModel.removeRow(selected);
            JOptionPane.showMessageDialog(this, "Đã xóa bàn thành công!", "Đã xóa", JOptionPane.INFORMATION_MESSAGE);
            onReset();
        }
    }

    @Override
    protected void onReset() {
        txtTableId.setText("");
        txtTableName.setText("");
        comboArea.setSelectedIndex(0);
        spinCapacity.setValue(4);
        table.clearSelection();
    }

    @Override
    protected void onSearch(String keyword) {
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        tableModel.setRowCount(0);
        for (Ban b : DummyDataFactory.getInstance().getDanhSachBan()) {
            if (b.getTenBan().toLowerCase().contains(keyword.toLowerCase()) ||
                    b.getKhuVuc().toLowerCase().contains(keyword.toLowerCase())) {
                tableModel.addRow(new Object[]{
                        b.getMaBan(),
                        b.getTenBan(),
                        b.getKhuVuc(),
                        b.getSucChua() + " người",
                        b.isCoKhach() ? "Có khách" : "Trống"
                });
            }
        }
    }
}
