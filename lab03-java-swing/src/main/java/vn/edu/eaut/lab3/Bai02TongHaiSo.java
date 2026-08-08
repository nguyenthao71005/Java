package vn.edu.eaut.lab3;

import javax.swing.*;
import java.awt.*;

public class Bai02TongHaiSo extends JFrame {
    private final JTextField txtA = new JTextField();
    private final JTextField txtB = new JTextField();
    private final JLabel lblResult = new JLabel("Kết quả: ");

    public Bai02TongHaiSo() {
        setTitle("Bài 2 - Tính tổng hai số");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 8, 8));
        
        add(new JLabel("Số thứ nhất:"));
        add(txtA);
        add(new JLabel("Số thứ hai:"));
        add(txtB);
        
        JButton btnSum = new JButton("Tính tổng");
        JButton btnClear = new JButton("Làm mới");
        
        add(btnSum);
        add(btnClear);
        add(new JLabel(""));
        add(lblResult);
        
        btnSum.addActionListener(e -> tinhTong());
        btnClear.addActionListener(e -> lamMoi());
        
        setSize(380, 190);
        setLocationRelativeTo(null);
    }

    private void tinhTong() {
        try {
            double a = Double.parseDouble(txtA.getText().trim());
            double b = Double.parseDouble(txtB.getText().trim());
            lblResult.setText("Kết quả: " + (a + b));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu nhập phải là số hợp lệ!");
        }
    }

    private void lamMoi() {
        txtA.setText("");
        txtB.setText("");
        lblResult.setText("Kết quả: ");
        txtA.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Bai02TongHaiSo().setVisible(true));
    }
}