package com.restaurant.view.shift;

import com.restaurant.dao.CaLamViecDAO;
import com.restaurant.model.CaLamViec;
import com.restaurant.model.NhanVien;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hộp thoại (JDialog) Bắt đầu ca làm việc (Mở ca)
 * Nghiệp vụ:
 * - Hiển thị chi tiết số dư và tiền thực tế bàn giao từ ca liền trước
 * - Tự động điền số tiền trong két từ ca trước để ca sau nắm rõ và khớp tiền
 * - Cho phép thu ngân kiểm đếm thực tế, điều chỉnh nếu có chênh lệch và nhập ghi chú
 */
public class OpenShiftDialog extends JDialog {

    private final NhanVien currentUser;
    private final CaLamViecDAO caLamViecDAO = new CaLamViecDAO();
    private boolean shiftOpened = false;

    private CaLamViec previousShift;
    private double previousHandoverCash = 1000000.0;
    private final DecimalFormat df = new DecimalFormat("#,### VND");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private JTextField txtInitialCash;
    private JTextArea txtNote;

    public OpenShiftDialog(Window parent, NhanVien user) {
        super(parent, "Bắt đầu ca làm việc (Mở ca mới)", ModalityType.APPLICATION_MODAL);
        this.currentUser = (user != null) ? user : new NhanVien(2, "Trần Thị Mai", "cashier01", "mai123", "ThuNgan", "DangLam");

        // Lấy thông tin ca đã đóng gần nhất
        loadPreviousShiftInfo();

        setSize(580, 580);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
    }

    private void loadPreviousShiftInfo() {
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            previousShift = caLamViecDAO.getCaTruocGanNhat();
        } else {
            previousShift = DummyDataFactory.getInstance().getCaTruocGanNhat();
        }

        if (previousShift != null) {
            if (previousShift.getTienThucTe() != null && previousShift.getTienThucTe() > 0) {
                previousHandoverCash = previousShift.getTienThucTe();
            } else if (previousShift.getTienLyThuyet() > 0) {
                previousHandoverCash = previousShift.getTienLyThuyet();
            }
        }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(UIConstants.BG_LIGHT);
        root.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("MỞ CA LÀM VIỆC MỚI");
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(new Color(16, 45, 78));

        JLabel lblSub = new JLabel("Tiếp nhận số dư bàn giao từ ca trước và khởi tạo phiên giao dịch thu ngân");
        lblSub.setFont(UIConstants.FONT_REGULAR);
        lblSub.setForeground(UIConstants.TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        root.add(headerPanel, BorderLayout.NORTH);

        // Vùng trung tâm: Thẻ ca trước + Form mở ca
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // 1. Thẻ thông tin bàn giao từ ca trước
        centerPanel.add(createPreviousShiftCard());
        centerPanel.add(Box.createVerticalStrut(12));

        // 2. Thẻ form nhập liệu ca mới
        RoundedPanel formCard = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Hàng 1: Thu ngân trực ca
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        JLabel lblStaff = new JLabel("Nhân viên trực ca:");
        lblStaff.setFont(UIConstants.FONT_BOLD);
        formCard.add(lblStaff, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        JTextField txtStaff = new JTextField(currentUser.getHoTen() + " (" + currentUser.getVaiTro() + ")");
        txtStaff.setEditable(false);
        txtStaff.setFont(UIConstants.FONT_REGULAR);
        txtStaff.setBackground(new Color(245, 247, 250));
        formCard.add(txtStaff, gbc);

        // Hàng 2: Thời gian bắt đầu mở ca
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblTime = new JLabel("Thời gian mở ca:");
        lblTime.setFont(UIConstants.FONT_BOLD);
        formCard.add(lblTime, gbc);

        gbc.gridx = 1;
        SimpleDateFormat sdfNow = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        JTextField txtTime = new JTextField(sdfNow.format(new Date()));
        txtTime.setEditable(false);
        txtTime.setFont(UIConstants.FONT_REGULAR);
        txtTime.setBackground(new Color(245, 247, 250));
        formCard.add(txtTime, gbc);

        // Hàng 3: Tiền đầu ca (bắt buộc nhập) + Nút lấy theo ca trước
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblCash = new JLabel("Tiền mặt đầu ca (VND):*");
        lblCash.setFont(UIConstants.FONT_BOLD);
        lblCash.setForeground(UIConstants.PRIMARY_BLUE);
        formCard.add(lblCash, gbc);

        gbc.gridx = 1;
        JPanel cashInputBox = new JPanel(new BorderLayout(8, 0));
        cashInputBox.setOpaque(false);

        txtInitialCash = new JTextField(String.valueOf((long) previousHandoverCash));
        txtInitialCash.setFont(new Font("Segoe UI", Font.BOLD, 15));
        txtInitialCash.setForeground(UIConstants.PRIMARY_BLUE);
        cashInputBox.add(txtInitialCash, BorderLayout.CENTER);

        JButton btnSyncPrev = new JButton("↺ Ca trước");
        btnSyncPrev.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnSyncPrev.setToolTipText("Khôi phục lại số tiền thực tế bàn giao từ ca trước");
        btnSyncPrev.addActionListener(e -> txtInitialCash.setText(String.valueOf((long) previousHandoverCash)));
        cashInputBox.add(btnSyncPrev, BorderLayout.EAST);

        formCard.add(cashInputBox, gbc);

        // Hàng 4: Ghi chú mở ca
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblNote = new JLabel("Ghi chú mở ca:");
        lblNote.setFont(UIConstants.FONT_REGULAR);
        formCard.add(lblNote, gbc);

        gbc.gridx = 1;
        txtNote = new JTextArea(3, 20);
        txtNote.setFont(UIConstants.FONT_REGULAR);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        if (previousShift != null) {
            txtNote.setText("Nhận bàn giao ca #" + previousShift.getMaCa() + " từ " + previousShift.getTenNV() + ". Số tiền trong két khớp thực tế.");
        } else {
            txtNote.setText("Mở ca làm việc đầu tiên.");
        }
        JScrollPane spNote = new JScrollPane(txtNote);
        formCard.add(spNote, gbc);

        centerPanel.add(formCard);
        root.add(centerPanel, BorderLayout.CENTER);

        // Nút hành động phía dưới
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(UIConstants.FONT_REGULAR);
        btnCancel.addActionListener(e -> dispose());

        JButton btnOpen = new JButton("🟢 Mở Ca Làm Việc");
        btnOpen.setFont(UIConstants.FONT_BOLD);
        btnOpen.setBackground(UIConstants.SUCCESS_GREEN);
        btnOpen.setForeground(Color.WHITE);
        btnOpen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOpen.addActionListener(e -> handleOpenShift());

        btnPanel.add(btnCancel);
        btnPanel.add(btnOpen);
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    /**
     * Tạo thẻ hiển thị chi tiết số dư và thông tin bàn giao từ ca liền trước
     */
    private JPanel createPreviousShiftCard() {
        if (previousShift != null) {
            RoundedPanel card = new RoundedPanel(10, new Color(240, 248, 255), new Color(180, 215, 250));
            card.setLayout(new BorderLayout(8, 8));
            card.setBorder(new EmptyBorder(10, 16, 12, 16));

            // Header thẻ: Mã ca và thời gian đóng
            JPanel topRow = new JPanel(new BorderLayout());
            topRow.setOpaque(false);

            String timeCloseStr = previousShift.getThoiGianDong() != null ? sdf.format(previousShift.getThoiGianDong()) : "--";
            JLabel lblHeader = new JLabel("📋 BÀN GIAO TỪ CA TRƯỚC: CA #" + previousShift.getMaCa());
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblHeader.setForeground(new Color(16, 60, 120));

            JLabel lblCloseTime = new JLabel("Đóng lúc: " + timeCloseStr);
            lblCloseTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblCloseTime.setForeground(UIConstants.TEXT_MUTED);

            topRow.add(lblHeader, BorderLayout.WEST);
            topRow.add(lblCloseTime, BorderLayout.EAST);
            card.add(topRow, BorderLayout.NORTH);

            // Dòng tiền bàn giao nổi bật ở giữa
            JPanel centerContent = new JPanel();
            centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
            centerContent.setOpaque(false);

            double actualCash = previousShift.getTienThucTe() != null ? previousShift.getTienThucTe() : previousShift.getTienLyThuyet();
            JPanel cashRow = new JPanel(new BorderLayout());
            cashRow.setOpaque(false);

            JLabel lblCashTitle = new JLabel("Tiền mặt thực tế bàn giao trong két:");
            lblCashTitle.setFont(UIConstants.FONT_BOLD);
            lblCashTitle.setForeground(new Color(16, 45, 78));

            JLabel lblCashVal = new JLabel(df.format(actualCash));
            lblCashVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblCashVal.setForeground(new Color(22, 101, 52)); // Xanh lá đậm

            cashRow.add(lblCashTitle, BorderLayout.WEST);
            cashRow.add(lblCashVal, BorderLayout.EAST);
            centerContent.add(cashRow);
            centerContent.add(Box.createVerticalStrut(6));

            // Chi tiết nhân viên và đối soát
            JPanel detailGrid = new JPanel(new GridLayout(2, 2, 10, 4));
            detailGrid.setOpaque(false);

            JLabel lblStaff = new JLabel("• Nhân viên bàn giao: " + previousShift.getTenNV());
            lblStaff.setFont(UIConstants.FONT_REGULAR);

            String diffStatus;
            if (previousShift.getChenhLech() == null || Math.abs(previousShift.getChenhLech()) < 1.0) {
                diffStatus = "• Đối soát két: ✅ Khớp 100%";
            } else if (previousShift.getChenhLech() > 0) {
                diffStatus = "• Đối soát két: ⚠️ Thừa +" + df.format(previousShift.getChenhLech());
            } else {
                diffStatus = "• Đối soát két: ❌ Thiếu " + df.format(previousShift.getChenhLech());
            }
            JLabel lblDiff = new JLabel(diffStatus);
            lblDiff.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblDiff.setForeground(new Color(16, 60, 120));

            String noteStr = previousShift.getGhiChu() != null && !previousShift.getGhiChu().isEmpty() ? previousShift.getGhiChu() : "Khớp tiền 100%, không chênh lệch";
            JLabel lblNote = new JLabel("• Ghi chú: " + (noteStr.length() > 38 ? noteStr.substring(0, 38) + "..." : noteStr));
            lblNote.setFont(UIConstants.FONT_REGULAR);
            lblNote.setForeground(UIConstants.TEXT_MUTED);

            JLabel lblPreFillHint = new JLabel("• Gợi ý: Số tiền này đã được tự động điền vào ô bên dưới");
            lblPreFillHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblPreFillHint.setForeground(new Color(71, 85, 105));

            detailGrid.add(lblStaff);
            detailGrid.add(lblDiff);
            detailGrid.add(lblNote);
            detailGrid.add(lblPreFillHint);

            centerContent.add(detailGrid);
            card.add(centerContent, BorderLayout.CENTER);
            return card;
        } else {
            // Không có ca liền trước
            RoundedPanel card = new RoundedPanel(10, new Color(248, 250, 252), UIConstants.BORDER_COLOR);
            card.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
            card.setBorder(new EmptyBorder(8, 12, 8, 12));

            JLabel lblEmpty = new JLabel("ℹ️ Chưa có dữ liệu ca liền trước (Ca mở đầu tiên trong ngày). Vui lòng kiểm đếm và nhập số tiền mặt thực tế.");
            lblEmpty.setFont(UIConstants.FONT_REGULAR);
            lblEmpty.setForeground(UIConstants.TEXT_MUTED);
            card.add(lblEmpty);
            return card;
        }
    }

    private void handleOpenShift() {
        String cashStr = txtInitialCash.getText().trim().replaceAll("[^0-9.]", "");
        if (cashStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền mặt đầu ca trong két!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            txtInitialCash.requestFocus();
            return;
        }

        double initialCash;
        try {
            initialCash = Double.parseDouble(cashStr);
            if (initialCash < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền đầu ca không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CaLamViec ca = new CaLamViec();
        ca.setMaNV(currentUser.getMaNV());
        ca.setTenNV(currentUser.getHoTen());
        ca.setThoiGianMo(new Timestamp(System.currentTimeMillis()));
        ca.setTienDauCa(initialCash);
        ca.setGhiChu(txtNote.getText().trim());

        boolean ok = false;
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            ok = caLamViecDAO.moCa(ca);
        } else {
            // Chế độ Offline bộ nhớ đệm
            ca.setMaCa((int) (System.currentTimeMillis() % 100000));
            DummyDataFactory.getInstance().setCaHienTai(ca);
            ok = true;
        }

        if (ok) {
            this.shiftOpened = true;
            String prevInfo = (previousShift != null) ? "\nSố tiền bàn giao ca #" + previousShift.getMaCa() + ": " + df.format(previousHandoverCash) : "";
            JOptionPane.showMessageDialog(this,
                    "Mở ca làm việc thành công!\n" +
                    "Mã ca: #" + ca.getMaCa() + "\n" +
                    "Nhân viên: " + currentUser.getHoTen() + "\n" +
                    "Tiền mặt ban đầu trong két: " + df.format(initialCash) + prevInfo,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể mở ca làm việc. Vui lòng kiểm tra kết nối CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isShiftOpened() {
        return shiftOpened;
    }
}
