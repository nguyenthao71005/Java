package vn.edu.eaut.lab3;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Bai08QuanLySinhVien extends JFrame {
    private final JTextField txtId = new JTextField();
    private final JTextField txtName = new JTextField();
    private final JTextField txtScore = new JTextField();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public Bai08QuanLySinhVien() {
        setTitle("Bài 8 - Quản lý sinh viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Form nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        inputPanel.add(new JLabel("Mã SV:"));
        inputPanel.add(txtId);
        inputPanel.add(new JLabel("Họ tên:"));
        inputPanel.add(txtName);
        inputPanel.add(new JLabel("Điểm TB:"));
        inputPanel.add(txtScore);

        // Bảng dữ liệu
        String[] columns = {"Mã SV", "Họ tên", "Điểm TB", "Xếp loại"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        
        // Sự kiện click vào bảng để đưa dữ liệu lên form
        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtScore.setText(tableModel.getValueAt(selectedRow, 2).toString());
            }
        });

        // Khu vực nút bấm
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Làm mới");

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // Đăng ký sự kiện
        btnAdd.addActionListener(e -> addStudent());
        btnEdit.addActionListener(e -> editStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearForm());

        setSize(500, 400);
        setLocationRelativeTo(null);
    }

    private void addStudent() {
        try {
            Student st = validateInput();
            if (st == null) return;
            tableModel.addRow(new Object[]{st.getId(), st.getName(), st.getAvgScore(), st.getRank()});
            clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Điểm TB phải là số hợp lệ!");
        }
    }

    private void editStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần sửa!");
            return;
        }
        try {
            Student st = validateInput();
            if (st == null) return;
            tableModel.setValueAt(st.getId(), selectedRow, 0);
            tableModel.setValueAt(st.getName(), selectedRow, 1);
            tableModel.setValueAt(st.getAvgScore(), selectedRow, 2);
            tableModel.setValueAt(st.getRank(), selectedRow, 3);
            clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Điểm TB phải là số hợp lệ!");
        }
    }

    private void deleteStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần xóa!");
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtScore.setText("");
        table.clearSelection();
        txtId.requestFocus();
    }

    private Student validateInput() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ Mã SV và Họ tên!");
            return null;
        }
        double score = Double.parseDouble(txtScore.getText().trim());
        if (score < 0 || score > 10) {
            JOptionPane.showMessageDialog(this, "Điểm phải từ 0 đến 10!");
            return null;
        }
        return new Student(id, name, score);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Bai08QuanLySinhVien().setVisible(true));
    }
}