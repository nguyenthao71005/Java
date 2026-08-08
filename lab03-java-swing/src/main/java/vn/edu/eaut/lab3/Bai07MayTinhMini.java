package vn.edu.eaut.lab3;

import javax.swing.*;
import java.awt.*;

public class Bai07MayTinhMini extends JFrame {
    private final JTextField txtA = new JTextField();
    private final JTextField txtB = new JTextField();
    private final JTextField txtResult = new JTextField();
    private final JTextArea txtHistory = new JTextArea(5, 20);

    public Bai07MayTinhMini() {
        setTitle("Bài 7 - Máy tính Mini");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topPanel.add(new JLabel("Số a:"));
        topPanel.add(txtA);
        topPanel.add(new JLabel("Số b:"));
        topPanel.add(txtB);
        topPanel.add(new JLabel("Kết quả:"));
        txtResult.setEditable(false);
        topPanel.add(txtResult);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Cộng");
        JButton btnSub = new JButton("Trừ");
        JButton btnMul = new JButton("Nhân");
        JButton btnDiv = new JButton("Chia");
        JButton btnClear = new JButton("Clear");

        btnPanel.add(btnAdd);
        btnPanel.add(btnSub);
        btnPanel.add(btnMul);
        btnPanel.add(btnDiv);
        btnPanel.add(btnClear);

        txtHistory.setEditable(false);
        JScrollPane scrollHistory = new JScrollPane(txtHistory);
        scrollHistory.setBorder(BorderFactory.createTitledBorder("Lịch sử phép tính"));

        add(topPanel, BorderLayout.NORTH);
        add(btnPanel, BorderLayout.CENTER);
        add(scrollHistory, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> calculate("+"));
        btnSub.addActionListener(e -> calculate("-"));
        btnMul.addActionListener(e -> calculate("*"));
        btnDiv.addActionListener(e -> calculate("/"));
        btnClear.addActionListener(e -> clearAll());

        setSize(400, 350);
        setLocationRelativeTo(null);
    }

    private void calculate(String operator) {
        try {
            double a = Double.parseDouble(txtA.getText().trim());
            double b = Double.parseDouble(txtB.getText().trim());
            double result = 0;

            switch (operator) {
                case "+": result = a + b; break;
                case "-": result = a - b; break;
                case "*": result = a * b; break;
                case "/":
                    if (b == 0) {
                        JOptionPane.showMessageDialog(this, "Lỗi: Không thể chia cho 0!");
                        return;
                    }
                    result = a / b;
                    break;
            }
            txtResult.setText(String.valueOf(result));
            txtHistory.append(a + " " + operator + " " + b + " = " + result + "\n");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
        }
    }

    private void clearAll() {
        txtA.setText("");
        txtB.setText("");
        txtResult.setText("");
        txtA.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Bai07MayTinhMini().setVisible(true));
    }
}