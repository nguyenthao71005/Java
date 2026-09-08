package com.restaurant.view.management;

import com.restaurant.model.DanhMuc;
import com.restaurant.util.DummyDataFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Module 5C: Quản lý Danh mục món ăn (Category Management)
 * Ánh xạ với bảng CSDL: `DanhMuc`
 */
public class CategoryManagementPanel extends BaseCrudPanel {

    private JTextField txtCatId;
    private JTextField txtCatName;
    private JTextField txtCatDesc;

    public CategoryManagementPanel() {
        super("Quản lý Danh mục Món ăn");
        loadData();
    }

    @Override
    protected void setupFormFields(JPanel container) {
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);

        gbc.gridx = 0; gbc.gridy = 0;
        container.add(new JLabel("Mã Danh Mục:"), gbc);

        gbc.gridx = 1;
        txtCatId = new JTextField();
        txtCatId.setEditable(false);
        txtCatId.setPreferredSize(new Dimension(80, 30));
        txtCatId.setBackground(new Color(240, 240, 240));
        container.add(txtCatId, gbc);

        gbc.gridx = 2;
        container.add(new JLabel("Tên Danh Mục:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        txtCatName = new JTextField();
        txtCatName.setPreferredSize(new Dimension(200, 30));
        container.add(txtCatName, gbc);

        gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = 1;
        container.add(new JLabel("Mô Tả:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3;
        txtCatDesc = new JTextField();
        txtCatDesc.setPreferredSize(new Dimension(300, 30));
        container.add(txtCatDesc, gbc);
    }

    @Override
    protected DefaultTableModel createTableModel() {
        String[] cols = {"Mã Danh Mục", "Tên Danh Mục", "Mô Tả"};
        return new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private com.restaurant.dao.DanhMucDAO danhMucDAO = new com.restaurant.dao.DanhMucDAO();

    private void loadData() {
        tableModel.setRowCount(0);
        List<DanhMuc> list = com.restaurant.database.DatabaseConnection.isConnected()
                ? danhMucDAO.getAll()
                : DummyDataFactory.getInstance().getDanhSachDanhMuc();
        for (DanhMuc dm : list) {
            tableModel.addRow(new Object[]{dm.getMaDM(), dm.getTenDM(), dm.getMoTa()});
        }
    }

    @Override
    protected void onTableRowSelected(int selectedRow) {
        txtCatId.setText(tableModel.getValueAt(selectedRow, 0).toString());
        txtCatName.setText(tableModel.getValueAt(selectedRow, 1).toString());
        txtCatDesc.setText(tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "");
    }

    @Override
    protected void onAdd() {
        String name = txtCatName.getText().trim();
        String desc = txtCatDesc.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên Danh mục!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            DanhMuc dm = new DanhMuc(0, name, desc);
            boolean ok = danhMucDAO.insert(dm);
            if (ok) {
                loadData();
                JOptionPane.showMessageDialog(this, "Đã thêm danh mục mới vào CSDL MySQL thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                onReset();
                return;
            }
        }

        int newId = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{newId, name, desc});
        JOptionPane.showMessageDialog(this, "Đã thêm danh mục mới thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        onReset();
    }

    @Override
    protected void onEdit() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int maDM = Integer.parseInt(tableModel.getValueAt(selected, 0).toString());
        String name = txtCatName.getText().trim();
        String desc = txtCatDesc.getText().trim();

        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            DanhMuc dm = new DanhMuc(maDM, name, desc);
            boolean ok = danhMucDAO.update(dm);
            if (ok) {
                loadData();
                JOptionPane.showMessageDialog(this, "Cập nhật danh mục trong CSDL MySQL thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        tableModel.setValueAt(name, selected, 1);
        tableModel.setValueAt(desc, selected, 2);
        JOptionPane.showMessageDialog(this, "Cập nhật danh mục thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void onDelete() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int maDM = Integer.parseInt(tableModel.getValueAt(selected, 0).toString());
        String name = tableModel.getValueAt(selected, 1).toString();
        int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa danh mục [" + name + "]?", "Xác nhận Xóa", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (com.restaurant.database.DatabaseConnection.isConnected()) {
                boolean ok = danhMucDAO.delete(maDM);
                if (ok) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Đã xóa danh mục khỏi CSDL MySQL thành công!", "Đã xóa", JOptionPane.INFORMATION_MESSAGE);
                    onReset();
                    return;
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa danh mục này vì đang có món ăn trực thuộc! (Ràng buộc toàn vẹn CSDL)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            tableModel.removeRow(selected);
            JOptionPane.showMessageDialog(this, "Đã xóa danh mục thành công!", "Đã xóa", JOptionPane.INFORMATION_MESSAGE);
            onReset();
        }
    }

    @Override
    protected void onReset() {
        txtCatId.setText("");
        txtCatName.setText("");
        txtCatDesc.setText("");
        table.clearSelection();
    }

    @Override
    protected void onSearch(String keyword) {
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        tableModel.setRowCount(0);
        for (DanhMuc dm : DummyDataFactory.getInstance().getDanhSachDanhMuc()) {
            if (dm.getTenDM().toLowerCase().contains(keyword.toLowerCase())) {
                tableModel.addRow(new Object[]{dm.getMaDM(), dm.getTenDM(), dm.getMoTa()});
            }
        }
    }
}
