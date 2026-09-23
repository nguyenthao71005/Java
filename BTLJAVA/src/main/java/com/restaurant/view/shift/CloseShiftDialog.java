package com.restaurant.view.shift;

import com.restaurant.dao.CaLamViecDAO;
import com.restaurant.model.CaLamViec;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hộp thoại (JDialog) Bàn giao ca & Kết ca
 * Nghiệp vụ:
 * - Tổng hợp doanh thu trong ca (Tiền mặt, chuyển khoản, tổng doanh thu, số hóa đơn)
 * - Tính toán số tiền mặt lý thuyết trong két (Tiền đầu ca + Doanh thu tiền mặt)
 * - Nhập tiền mặt thực tế kiểm đếm
 * - Xử lý so sánh và cảnh báo chênh lệch (Thừa / Thiếu / Khớp)
 * - Nhập ghi chú giải trình chênh lệch
 */
public class CloseShiftDialog extends JDialog {

    private final CaLamViec shift;
    private final CaLamViecDAO caLamViecDAO = new CaLamViecDAO();
    private boolean shiftClosed = false;

    private JTextField txtActualCash;
    private JLabel lblDifferenceVal;
    private JPanel diffCard;
    private JTextArea txtNote;
    private final DecimalFormat df = new DecimalFormat("#,### VND");

    public CloseShiftDialog(Window parent, CaLamViec shift) {
        super(parent, "Bàn giao ca làm việc (Kết ca)", ModalityType.APPLICATION_MODAL);
        this.shift = shift;

        setSize(580, 680);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Nạp số liệu mới nhất
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            caLamViecDAO.capNhatDoanhThuTucThoi(shift);
        } else {
            DummyDataFactory.getInstance().capNhatDoanhThuCa(shift);
        }

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(UIConstants.BG_LIGHT);
        root.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("BÀN GIAO & KẾT THÚC CA LÀM VIỆC");
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(new Color(16, 45, 78));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String timeMoStr = shift.getThoiGianMo() != null ? sdf.format(shift.getThoiGianMo()) : "--";
        JLabel lblSub = new JLabel("Ca #" + shift.getMaCa() + " | Trực ca: " + shift.getTenNV() + " (Mở lúc: " + timeMoStr + ")");
        lblSub.setFont(UIConstants.FONT_REGULAR);
        lblSub.setForeground(UIConstants.TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        root.add(headerPanel, BorderLayout.NORTH);

        // Center: Thống kê đối soát & Nhập tiền
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Card 1: Bảng tổng kết số liệu doanh thu trong ca
        RoundedPanel statsCard = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        statsCard.setLayout(new GridLayout(6, 2, 8, 8));
        statsCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        addStatRow(statsCard, "1. Số hóa đơn thanh toán:", shift.getSoHoaDon() + " đơn hàng", false);
        addStatRow(statsCard, "2. Tiền mặt đầu ca (Két):", df.format(shift.getTienDauCa()), false);
        addStatRow(statsCard, "3. Doanh thu tiền mặt trong ca:", df.format(shift.getDoanhThuTienMat()), false);
        addStatRow(statsCard, "4. Doanh thu Chuyển khoản / Thẻ:", df.format(shift.getDoanhThuChuyenKhoan()), false);
        addStatRow(statsCard, "5. Tổng doanh thu bán hàng:", df.format(shift.getTongDoanhThu()), true);
        addStatRow(statsCard, "★ TIỀN MẶT LÝ THUYẾT TRONG KÉT:", df.format(shift.getTienLyThuyet()), true);

        centerPanel.add(statsCard);
        centerPanel.add(Box.createVerticalStrut(12));

        // Card 2: Nhập số tiền thực tế & Chênh lệch
        RoundedPanel inputCard = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        inputCard.setLayout(new GridBagLayout());
        inputCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Nhập tiền mặt thực tế kiểm đếm
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.45;
        JLabel lblActual = new JLabel("Tiền mặt thực tế kiểm đếm:*");
        lblActual.setFont(UIConstants.FONT_BOLD);
        lblActual.setForeground(new Color(16, 45, 78));
        inputCard.add(lblActual, gbc);

        gbc.gridx = 1; gbc.weightx = 0.55;
        txtActualCash = new JTextField(String.valueOf((long) shift.getTienLyThuyet()));
        txtActualCash.setFont(new Font("Segoe UI", Font.BOLD, 15));
        txtActualCash.setForeground(UIConstants.PRIMARY_BLUE);
        inputCard.add(txtActualCash, gbc);

        // Khung hiển thị chênh lệch
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblDiffTitle = new JLabel("Tình trạng đối soát két:");
        lblDiffTitle.setFont(UIConstants.FONT_BOLD);
        inputCard.add(lblDiffTitle, gbc);

        gbc.gridx = 1;
        diffCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        diffCard.setBorder(BorderFactory.createLineBorder(new Color(46, 160, 67), 1));
        diffCard.setBackground(new Color(235, 250, 238));

        lblDifferenceVal = new JLabel("✅ Khớp tiền 100% (0 VND)");
        lblDifferenceVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDifferenceVal.setForeground(new Color(46, 160, 67));
        diffCard.add(lblDifferenceVal);
        inputCard.add(diffCard, gbc);

        // Ghi chú / Giải trình chênh lệch
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblNote = new JLabel("Ghi chú / Giải trình chênh lệch:");
        lblNote.setFont(UIConstants.FONT_REGULAR);
        inputCard.add(lblNote, gbc);

        gbc.gridx = 1;
        txtNote = new JTextArea(3, 20);
        txtNote.setFont(UIConstants.FONT_REGULAR);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setText("Bàn giao ca đúng quy trình.");
        JScrollPane spNote = new JScrollPane(txtNote);
        inputCard.add(spNote, gbc);

        centerPanel.add(inputCard);
        root.add(centerPanel, BorderLayout.CENTER);

        // Nút hành động phía dưới
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(UIConstants.FONT_REGULAR);
        btnCancel.addActionListener(e -> dispose());

        JButton btnConfirmClose = new JButton("🔴 Xác Nhận Đóng Ca & Bàn Giao");
        btnConfirmClose.setFont(UIConstants.FONT_BOLD);
        btnConfirmClose.setBackground(UIConstants.DANGER_RED);
        btnConfirmClose.setForeground(Color.WHITE);
        btnConfirmClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmClose.addActionListener(e -> handleConfirmClose());

        btnPanel.add(btnCancel);
        btnPanel.add(btnConfirmClose);
        root.add(btnPanel, BorderLayout.SOUTH);

        // Lắng nghe thay đổi tiền thực tế để tự động cập nhật dòng chênh lệch
        txtActualCash.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateDiff(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateDiff(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateDiff(); }
        });

        updateDiff();
        setContentPane(root);
    }

    private void addStatRow(JPanel container, String title, String value, boolean highlight) {
        JLabel lblT = new JLabel(title);
        lblT.setFont(highlight ? UIConstants.FONT_BOLD : UIConstants.FONT_REGULAR);
        lblT.setForeground(highlight ? new Color(16, 45, 78) : UIConstants.TEXT_MAIN);

        JLabel lblV = new JLabel(value, SwingConstants.RIGHT);
        lblV.setFont(highlight ? new Font("Segoe UI", Font.BOLD, 13) : UIConstants.FONT_REGULAR);
        lblV.setForeground(highlight ? UIConstants.PRIMARY_BLUE : UIConstants.TEXT_MAIN);

        container.add(lblT);
        container.add(lblV);
    }

    private void updateDiff() {
        String s = txtActualCash.getText().trim().replaceAll("[^0-9.]", "");
        if (s.isEmpty()) {
            lblDifferenceVal.setText("Chưa nhập tiền thực tế");
            lblDifferenceVal.setForeground(Color.GRAY);
            diffCard.setBackground(new Color(245, 245, 245));
            diffCard.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            return;
        }

        try {
            double actual = Double.parseDouble(s);
            double diff = actual - shift.getTienLyThuyet();

            if (Math.abs(diff) < 1.0) {
                lblDifferenceVal.setText("✅ Khớp tiền 100% (Chênh lệch: 0 VND)");
                lblDifferenceVal.setForeground(new Color(46, 160, 67));
                diffCard.setBackground(new Color(235, 250, 238));
                diffCard.setBorder(BorderFactory.createLineBorder(new Color(46, 160, 67)));
            } else if (diff > 0) {
                lblDifferenceVal.setText("⚠️ Thừa tiền trong két: +" + df.format(diff));
                lblDifferenceVal.setForeground(new Color(30, 90, 180));
                diffCard.setBackground(new Color(235, 243, 255));
                diffCard.setBorder(BorderFactory.createLineBorder(new Color(30, 90, 180)));
            } else {
                lblDifferenceVal.setText("❌ Thiếu tiền trong két: " + df.format(diff));
                lblDifferenceVal.setForeground(new Color(219, 55, 55));
                diffCard.setBackground(new Color(255, 235, 235));
                diffCard.setBorder(BorderFactory.createLineBorder(new Color(219, 55, 55)));
            }
        } catch (NumberFormatException ex) {
            lblDifferenceVal.setText("Số tiền không hợp lệ");
            lblDifferenceVal.setForeground(Color.RED);
        }
    }

    private void handleConfirmClose() {
        String cashStr = txtActualCash.getText().trim().replaceAll("[^0-9.]", "");
        if (cashStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền mặt thực tế kiểm đếm trong két!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            txtActualCash.requestFocus();
            return;
        }

        double actualCash;
        try {
            actualCash = Double.parseDouble(cashStr);
            if (actualCash < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền thực tế không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double diff = actualCash - shift.getTienLyThuyet();
        String note = txtNote.getText().trim();

        // Nếu thừa hoặc thiếu tiền mà chưa có giải trình
        if (Math.abs(diff) >= 1.0 && note.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Két tiền có chênh lệch (" + df.format(diff) + ").\nVui lòng nhập lý do giải trình vào ô Ghi chú!", "Yêu cầu giải trình", JOptionPane.WARNING_MESSAGE);
            txtNote.requestFocus();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn ĐÓNG CA LÀM VIỆC #" + shift.getMaCa() + "?\n" +
                "Tiền lý thuyết: " + df.format(shift.getTienLyThuyet()) + "\n" +
                "Tiền thực tế: " + df.format(actualCash) + "\n" +
                "Chênh lệch: " + df.format(diff) + "\n\n" +
                "Sau khi đóng, ca làm việc này sẽ được lưu vào lịch sử và không thể chỉnh sửa.",
                "Xác nhận Bàn giao & Đóng ca",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        shift.setTienThucTe(actualCash);
        shift.setChenhLech(diff);
        shift.setGhiChu(note);
        shift.setTrangThai("DaDong");

        boolean ok = false;
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            ok = caLamViecDAO.dongCa(shift);
        } else {
            DummyDataFactory.getInstance().dongCa(shift);
            ok = true;
        }

        if (ok) {
            this.shiftClosed = true;
            JOptionPane.showMessageDialog(this,
                    "Đã bàn giao và đóng ca làm việc #" + shift.getMaCa() + " thành công!\n" +
                    "Tổng doanh thu ghi nhận: " + df.format(shift.getTongDoanhThu()) + "\n" +
                    "Trạng thái đối soát: " + (diff == 0 ? "Khớp 100%" : (diff > 0 ? "Thừa " + df.format(diff) : "Thiếu " + df.format(diff))),
                    "Kết ca thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể đóng ca. Vui lòng kiểm tra lại kết nối!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isShiftClosed() {
        return shiftClosed;
    }
}
