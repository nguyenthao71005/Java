package com.restaurant.view.management;

import com.restaurant.model.NhanVien;
import com.restaurant.service.AuditService;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Module 5A: Quản lý Nhân viên (User Management)
 * Ánh xạ với bảng CSDL: `NhanVien`
 * =========================================================================
 * Điểm nối CSDL MySQL (Khi tích hợp Backend):
 * 1. Danh sách nhân viên:
 *    - SELECT MaNV, HoTen, TenDangNhap, VaiTro, TrangThai FROM NhanVien;
 * 2. Thêm nhân viên:
 *    - INSERT INTO NhanVien (HoTen, TenDangNhap, MatKhau, VaiTro, TrangThai) VALUES (?, ?, ?, ?, ?);
 * 3. Cập nhật:
 *    - UPDATE NhanVien SET HoTen = ?, MatKhau = ?, VaiTro = ?, TrangThai = ? WHERE MaNV = ?;
 * 4. Xóa nhân viên:
 *    - DELETE FROM NhanVien WHERE MaNV = ?;
 * =========================================================================
 */
public class UserManagementPanel extends BaseCrudPanel {

    private JTextField txtId;
    private JTextField txtFullName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRole;
    private JComboBox<String> comboStatus;

    public UserManagementPanel() {
        super("Quản lý Tài khoản & Nhân viên");
        loadData();
    }

    @Override
    protected void setupFormFields(JPanel container) {
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);

        // Hàng 1: Mã NV (read only) & Họ tên
        gbc.gridx = 0; gbc.gridy = 0;
        container.add(new JLabel("Mã NV:"), gbc);

        gbc.gridx = 1;
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setPreferredSize(new Dimension(80, 30));
        txtId.setBackground(new Color(240, 240, 240));
        container.add(txtId, gbc);

        gbc.gridx = 2;
        container.add(new JLabel("Họ và Tên:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        txtFullName = new JTextField();
        txtFullName.setPreferredSize(new Dimension(220, 30));
        container.add(txtFullName, gbc);

        // Hàng 2: Tên đăng nhập & Mật khẩu
        gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = 1;
        container.add(new JLabel("Tên Đăng Nhập:"), gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(150, 30));
        container.add(txtUsername, gbc);

        gbc.gridx = 2;
        container.add(new JLabel("Mật Khẩu:"), gbc);

        gbc.gridx = 3;
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(220, 30));
        container.add(txtPassword, gbc);

        // Hàng 3: Vai trò & Trạng thái
        gbc.gridx = 0; gbc.gridy = 2;
        container.add(new JLabel("Vai Trò:"), gbc);

        gbc.gridx = 1;
        comboRole = new JComboBox<>(new String[]{"Quản trị viên (Admin)", "Quản lý", "Thu ngân", "Phục vụ", "Đầu bếp"});
        comboRole.setBackground(Color.WHITE);
        container.add(comboRole, gbc);

        gbc.gridx = 2;
        container.add(new JLabel("Trạng Thái:"), gbc);

        gbc.gridx = 3;
        comboStatus = new JComboBox<>(new String[]{"Đang làm việc", "Đã nghỉ việc"});
        comboStatus.setBackground(Color.WHITE);
        container.add(comboStatus, gbc);
    }

    private com.restaurant.dao.NhanVienDAO nhanVienDAO = new com.restaurant.dao.NhanVienDAO();
    private AuditService auditService = AuditService.getInstance();

    @Override
    protected DefaultTableModel createTableModel() {
        String[] cols = {"Mã NV", "Tên Đăng Nhập", "Họ và Tên", "Vai Trò", "Trạng Thái"};
        return new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<NhanVien> list = com.restaurant.database.DatabaseConnection.isConnected()
                ? nhanVienDAO.getAll()
                : DummyDataFactory.getInstance().getDanhSachNhanVien();
        for (NhanVien nv : list) {
            tableModel.addRow(new Object[]{
                    nv.getMaNV(),
                    nv.getTenDangNhap(),
                    nv.getHoTen(),
                    com.restaurant.dao.NhanVienDAO.toDisplayVaiTro(nv.getVaiTro()),
                    com.restaurant.dao.NhanVienDAO.toDisplayTrangThai(nv.getTrangThai())
            });
        }
    }

    @Override
    protected void onTableRowSelected(int selectedRow) {
        txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
        txtUsername.setText(tableModel.getValueAt(selectedRow, 1).toString());
        txtFullName.setText(tableModel.getValueAt(selectedRow, 2).toString());
        txtPassword.setText("******");

        String role = tableModel.getValueAt(selectedRow, 3).toString();
        comboRole.setSelectedItem(com.restaurant.dao.NhanVienDAO.toDisplayVaiTro(role));

        String status = tableModel.getValueAt(selectedRow, 4).toString();
        comboStatus.setSelectedItem(com.restaurant.dao.NhanVienDAO.toDisplayTrangThai(status));
    }

    @Override
    protected void onAdd() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (fullName.isEmpty() || username.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ Họ tên, Username và Mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String role = (String) comboRole.getSelectedItem();
        String status = (String) comboStatus.getSelectedItem();

        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            // Chuẩn hóa sang mã ENUM CSDL: QuanLy, ThuNgan, PhucVu, Bep / DangLam, NghiViec
            String dbRole = com.restaurant.dao.NhanVienDAO.toDbVaiTro(role);
            String dbStatus = com.restaurant.dao.NhanVienDAO.toDbTrangThai(status);
            NhanVien nv = new NhanVien(0, fullName, username, pass, dbRole, dbStatus);
            boolean ok = nhanVienDAO.insert(nv);
            if (ok) {
                // Ghi log audit
                auditService.ghiTaoMoi(null, "NhanVien", 
                    String.format("Tạo nhân viên: %s (Username: %s, Vai trò: %s)", fullName, username, role), null);
                loadData();
                JOptionPane.showMessageDialog(this, "Đã thêm nhân viên [" + fullName + "] vào CSDL MySQL thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                onReset();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thêm nhân viên vào CSDL MySQL!\n(Gợi ý: Tên đăng nhập '" + username + "' có thể đã tồn tại trong hệ thống).", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        int newId = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{newId, username, fullName, role, status});
        JOptionPane.showMessageDialog(this, "Thêm nhân viên [" + fullName + "] thành công (Chế độ Offline)!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        onReset();
    }

    @Override
    protected void onEdit() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên từ bảng để chỉnh sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int maNV = Integer.parseInt(tableModel.getValueAt(selected, 0).toString());
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        String role = (String) comboRole.getSelectedItem();
        String status = (String) comboStatus.getSelectedItem();

        if (fullName.isEmpty() || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên và Tên đăng nhập không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            String dbRole = com.restaurant.dao.NhanVienDAO.toDbVaiTro(role);
            String dbStatus = com.restaurant.dao.NhanVienDAO.toDbTrangThai(status);
            NhanVien nv = new NhanVien(maNV, fullName, username, pass, dbRole, dbStatus);
            boolean ok = nhanVienDAO.update(nv);
            if (ok) {
                // Ghi log audit
                auditService.ghiCapNhat(null, "NhanVien",
                    String.format("Cập nhật nhân viên: %s (Username: %s, Vai trò: %s)", fullName, username, role),
                    String.format("Username: %s, Họ tên: %s, Vai trò: %s", 
                        tableModel.getValueAt(selected, 1).toString(),
                        tableModel.getValueAt(selected, 2).toString(),
                        tableModel.getValueAt(selected, 3).toString()),
                    String.format("Username: %s, Họ tên: %s, Vai trò: %s, Trạng thái: %s",
                        username, fullName, role, status));
                loadData();
                JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin nhân viên trong CSDL MySQL thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                onReset();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể cập nhật thông tin nhân viên trong CSDL MySQL!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        tableModel.setValueAt(username, selected, 1);
        tableModel.setValueAt(fullName, selected, 2);
        tableModel.setValueAt(role, selected, 3);
        tableModel.setValueAt(status, selected, 4);

        JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên thành công (Chế độ Offline)!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        onReset();
    }

    @Override
    protected void onDelete() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int maNV = Integer.parseInt(tableModel.getValueAt(selected, 0).toString());
        String name = tableModel.getValueAt(selected, 2).toString();
        int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhân viên [" + name + "]?", "Xác nhận Xóa", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (com.restaurant.database.DatabaseConnection.isConnected()) {
                boolean ok = nhanVienDAO.delete(maNV);
                if (ok) {
                    // Ghi log audit
                    auditService.ghiXoa(null, "NhanVien",
                        String.format("Xóa nhân viên: %s (Mã NV: %d)", name, maNV),
                        String.format("Mã NV: %d, Tên: %s", maNV, name));
                    loadData();
                    JOptionPane.showMessageDialog(this, "Đã xóa nhân viên khỏi CSDL MySQL thành công!", "Đã xóa", JOptionPane.INFORMATION_MESSAGE);
                    onReset();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa nhân viên do đã có ràng buộc dữ liệu (hóa đơn, ca làm việc)!\nBạn có thể chuyển trạng thái sang 'Đã nghỉ việc'.", "Lỗi ràng buộc CSDL", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }

            tableModel.removeRow(selected);
            JOptionPane.showMessageDialog(this, "Đã xóa nhân viên thành công!", "Đã xóa", JOptionPane.INFORMATION_MESSAGE);
            onReset();
        }
    }

    @Override
    protected void onReset() {
        txtId.setText("");
        txtFullName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        comboRole.setSelectedIndex(0);
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
        List<NhanVien> list = com.restaurant.database.DatabaseConnection.isConnected()
                ? nhanVienDAO.getAll()
                : DummyDataFactory.getInstance().getDanhSachNhanVien();
        for (NhanVien nv : list) {
            String displayRole = com.restaurant.dao.NhanVienDAO.toDisplayVaiTro(nv.getVaiTro());
            String displayStatus = com.restaurant.dao.NhanVienDAO.toDisplayTrangThai(nv.getTrangThai());
            if (nv.getHoTen().toLowerCase().contains(keyword.toLowerCase()) ||
                    nv.getTenDangNhap().toLowerCase().contains(keyword.toLowerCase()) ||
                    displayRole.toLowerCase().contains(keyword.toLowerCase()) ||
                    nv.getVaiTro().toLowerCase().contains(keyword.toLowerCase())) {
                tableModel.addRow(new Object[]{
                        nv.getMaNV(),
                        nv.getTenDangNhap(),
                        nv.getHoTen(),
                        displayRole,
                        displayStatus
                });
            }
        }
    }
}
