package vn.edu.eaut.lab3;

import javax.swing.*;
import java.awt.*;

public class Bai05FibonacciSwing extends JFrame {
    private final JTextField txtN = new JTextField(10);
    private final JTextArea txtArea = new JTextArea(8, 35);

    public Bai05FibonacciSwing() {
        setTitle("Bài 5 - Hiển thị dãy Fibonacci");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Nhập n:"));
        topPanel.add(txtN);
        
        JButton btnShow = new JButton("Hiển thị");
        topPanel.add(btnShow);
        
        txtArea.setEditable(false);
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        
        btnShow.addActionListener(e -> hienThiFibonacci());
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(txtArea), BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
    }

    private void hienThiFibonacci() {
        try {
            int n = Integer.parseInt(txtN.getText().trim());
            if (n <= 0 || n > 92) {
                JOptionPane.showMessageDialog(this, "n phải từ 1 đến 92 để tránh tràn số long!");
                return;
            }
            txtArea.setText(taoDayFibonacci(n));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "n phải là số nguyên dương!");
        }
    }

    private String taoDayFibonacci(int n) {
        StringBuilder sb = new StringBuilder();
        long a = 0;
        long b = 1;
        for (int i = 1; i <= n; i++) {
            if (i > 1) sb.append(" ");
            sb.append(a);
            long next = a + b;
            a = b;
            b = next;
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Bai05FibonacciSwing().setVisible(true));
    }
}