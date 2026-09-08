package com.restaurant.view.management;

import com.restaurant.model.DanhMuc;
import com.restaurant.model.MonAn;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Module 5B: Quản lý Thực đơn (Menu Items Management)
 * Ánh xạ với bảng CSDL: `MonAn`
 * =========================================================================
 * Điểm nối CSDL MySQL (Khi tích hợp Backend):
 * 1. Danh sách món ăn:
 *    - SELECT m.MaMon, m.TenMon, d.TenDM, m.GiaBan, m.HinhAnh, m.ConMon
 *      FROM MonAn m
 *      JOIN DanhMuc d ON m.MaDM = d.MaDM;
 * 2. Thêm món mới:
 *    - INSERT INTO MonAn (TenMon, MaDM, GiaBan, HinhAnh, ConMon) VALUES (?, ?, ?, ?, ?);
 * 3. Cập nhật món:
 *    - UPDATE MonAn SET TenMon = ?, MaDM = ?, GiaBan = ?, HinhAnh = ?, ConMon = ? WHERE MaMon = ?;
 * 4. Xóa món:
 *    - DELETE FROM MonAn WHERE MaMon = ?;
 * =========================================================================
 */
public class MenuManagementPanel extends BaseCrudPanel {

    private JTextField txtDishId;
    private JTextField txtDishName;
    private JTextField txtPrice;
    private JComboBox<DanhMuc> comboCategory;
    private JTextField txtImagePath;
    private JButton btnBrowseImage;
    private JCheckBox chkInStock;

    public MenuManagementPanel() {
        super("Quản lý Thực đơn Món ăn");
        loadData();
    }

    @Override
    protected void setupFormFields(JPanel container) {
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);

        // Hàng 1: Mã món & Tên món
        gbc.gridx = 0; gbc.gridy = 0;
        container.add(new JLabel("Mã Món:"), gbc);

        gbc.gridx = 1;
        txtDishId = new JTextField();
        txtDishId.setEditable(false);
        txtDishId.setPreferredSize(new Dimension(80, 30));
        txtDishId.setBackground(new Color(240, 240, 240));
        container.add(txtDishId, gbc);

        gbc.gridx = 2;
        container.add(new JLabel("Tên Món Ăn:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        txtDishName = new JTextField();
        txtDishName.setPreferredSize(new Dimension(220, 30));
        container.add(txtDishName, gbc);

        // Hàng 2: Giá bán & Danh mục
        gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = 1;
        container.add(new JLabel("Giá Bán (VND):"), gbc);

        gbc.gridx = 1;
        txtPrice = new JTextField();
        txtPrice.setPreferredSize(new Dimension(150, 30));
        container.add(txtPrice, gbc);

        gbc.gridx = 2;
        container.add(new JLabel("Danh Mục:"), gbc);

        gbc.gridx = 3;
        comboCategory = new JComboBox<>();
        comboCategory.setBackground(Color.WHITE);
        List<DanhMuc> catList = DummyDataFactory.getInstance().getDanhSachDanhMuc();
        for (DanhMuc d : catList) {
            comboCategory.addItem(d);
        }
        container.add(comboCategory, gbc);

        // Hàng 3: Hình ảnh & Trạng thái còn/hết
        gbc.gridx = 0; gbc.gridy = 2;
        container.add(new JLabel("Hình Ảnh:"), gbc);

        gbc.gridx = 1;
        JPanel imgPanel = new JPanel(new BorderLayout(4, 0));
        imgPanel.setOpaque(false);
        txtImagePath = new JTextField();
        txtImagePath.setPreferredSize(new Dimension(100, 30));
        btnBrowseImage = new JButton("Chọn ảnh...");
        btnBrowseImage.addActionListener(e -> chooseImageFile());
        imgPanel.add(txtImagePath, BorderLayout.CENTER);
        imgPanel.add(btnBrowseImage, BorderLayout.EAST);
        container.add(imgPanel, gbc);

        gbc.gridx = 2;
        container.add(new JLabel("Trạng Thái:"), gbc);

        gbc.gridx = 3;
        chkInStock = new JCheckBox("Còn món phục vụ", true);
        chkInStock.setOpaque(false);
        chkInStock.setFont(UIConstants.FONT_REGULAR);
        container.add(chkInStock, gbc);
    }

    private void chooseImageFile() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            txtImagePath.setText(f.getName());
        }
    }

    @Override
    protected DefaultTableModel createTableModel() {
        String[] cols = {"Mã Món", "Tên Món Ăn", "Danh Mục", "Giá Bán", "Hình Ảnh", "Trạng Thái"};
        return new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private com.restaurant.dao.MonAnDAO monAnDAO = new com.restaurant.dao.MonAnDAO();

    private void loadData() {
        tableModel.setRowCount(0);
        List<MonAn> list = com.restaurant.database.DatabaseConnection.isConnected()
                ? monAnDAO.getAll()
                : DummyDataFactory.getInstance().getDanhSachMonAn();
        for (MonAn m : list) {
            tableModel.addRow(new Object[]{
                    m.getMaMon(),
                    m.getTenMon(),
                    m.getTenDM(),
                    UIConstants.formatCurrency(m.getGiaBan()),
                    m.getHinhAnh() != null ? m.getHinhAnh() : "default.png",
                    m.isConMon() ? "Còn món" : "Tạm hết"
            });
        }
    }

    @Override
    protected void onTableRowSelected(int selectedRow) {
        txtDishId.setText(tableModel.getValueAt(selectedRow, 0).toString());
        txtDishName.setText(tableModel.getValueAt(selectedRow, 1).toString());

        String catName = tableModel.getValueAt(selectedRow, 2).toString();
        for (int i = 0; i < comboCategory.getItemCount(); i++) {
            if (comboCategory.getItemAt(i).getTenDM().equalsIgnoreCase(catName)) {
                comboCategory.setSelectedIndex(i);
                break;
            }
        }

        // Parse lại giá
        String rawPrice = tableModel.getValueAt(selectedRow, 3).toString().replaceAll("[^0-9]", "");
        txtPrice.setText(rawPrice);

        txtImagePath.setText(tableModel.getValueAt(selectedRow, 4).toString());
        String status = tableModel.getValueAt(selectedRow, 5).toString();
        chkInStock.setSelected("Còn món".equalsIgnoreCase(status));
    }

    @Override
    protected void onAdd() {
        String name = txtDishName.getText().trim();
        String priceStr = txtPrice.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên món và Giá bán!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá bán phải là số hợp lệ!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DanhMuc dm = (DanhMuc) comboCategory.getSelectedItem();
        int maDM = dm != null ? dm.getMaDM() : 1;
        String img = txtImagePath.getText().trim().isEmpty() ? "dish.png" : txtImagePath.getText().trim();
        boolean conMon = chkInStock.isSelected();

        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            MonAn m = new MonAn(0, name, maDM, price, img, conMon);
            boolean ok = monAnDAO.insert(m);
            if (ok) {
                loadData();
                JOptionPane.showMessageDialog(this, "Đã thêm món mới [" + name + "] vào CSDL MySQL thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                onReset();
                return;
            }
        }

        int newId = 100 + tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{
                newId,
                name,
                dm != null ? dm.getTenDM() : "",
                UIConstants.formatCurrency(price),
                img,
                conMon ? "Còn món" : "Tạm hết"
        });

        JOptionPane.showMessageDialog(this, "Đã thêm món mới [" + name + "] vào thực đơn!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        onReset();
    }

    @Override
    protected void onEdit() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món ăn để chỉnh sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int maMon = Integer.parseInt(tableModel.getValueAt(selected, 0).toString());
        String name = txtDishName.getText().trim();
        String priceStr = txtPrice.getText().trim();
        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Giá bán không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DanhMuc dm = (DanhMuc) comboCategory.getSelectedItem();
        int maDM = dm != null ? dm.getMaDM() : 1;
        String img = txtImagePath.getText().trim().isEmpty() ? "dish.png" : txtImagePath.getText().trim();
        boolean conMon = chkInStock.isSelected();

        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            MonAn m = new MonAn(maMon, name, maDM, price, img, conMon);
            boolean ok = monAnDAO.update(m);
            if (ok) {
                loadData();
                JOptionPane.showMessageDialog(this, "Đã cập nhật món ăn trong CSDL MySQL thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        tableModel.setValueAt(name, selected, 1);
        tableModel.setValueAt(dm != null ? dm.getTenDM() : "", selected, 2);
        tableModel.setValueAt(UIConstants.formatCurrency(price), selected, 3);
        tableModel.setValueAt(img, selected, 4);
        tableModel.setValueAt(conMon ? "Còn món" : "Tạm hết", selected, 5);

        JOptionPane.showMessageDialog(this, "Cập nhật thông tin món ăn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void onDelete() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món ăn để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int maMon = Integer.parseInt(tableModel.getValueAt(selected, 0).toString());
        String name = tableModel.getValueAt(selected, 1).toString();
        int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa món [" + name + "] khỏi thực đơn?", "Xác nhận Xóa", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (com.restaurant.database.DatabaseConnection.isConnected()) {
                monAnDAO.delete(maMon);
                loadData();
                JOptionPane.showMessageDialog(this, "Đã xóa món ăn khỏi CSDL MySQL thành công!", "Đã xóa", JOptionPane.INFORMATION_MESSAGE);
                onReset();
                return;
            }

            tableModel.removeRow(selected);
            JOptionPane.showMessageDialog(this, "Đã xóa món ăn thành công!", "Đã xóa", JOptionPane.INFORMATION_MESSAGE);
            onReset();
        }
    }

    @Override
    protected void onReset() {
        txtDishId.setText("");
        txtDishName.setText("");
        txtPrice.setText("");
        txtImagePath.setText("");
        comboCategory.setSelectedIndex(0);
        chkInStock.setSelected(true);
        table.clearSelection();
    }

    @Override
    protected void onSearch(String keyword) {
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        tableModel.setRowCount(0);
        List<MonAn> list = DummyDataFactory.getInstance().getDanhSachMonAn();
        for (MonAn m : list) {
            if (m.getTenMon().toLowerCase().contains(keyword.toLowerCase()) ||
                    m.getTenDM().toLowerCase().contains(keyword.toLowerCase())) {
                tableModel.addRow(new Object[]{
                        m.getMaMon(),
                        m.getTenMon(),
                        m.getTenDM(),
                        UIConstants.formatCurrency(m.getGiaBan()),
                        m.getHinhAnh() != null ? m.getHinhAnh() : "default.png",
                        m.isConMon() ? "Còn món" : "Tạm hết"
                });
            }
        }
    }
}
