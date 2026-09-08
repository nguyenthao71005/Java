package com.restaurant.view.checkout;

import com.restaurant.model.Ban;
import com.restaurant.model.ChiTietHoaDon;
import com.restaurant.model.HoaDon;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;
import com.restaurant.view.MainFrame;
import com.restaurant.view.components.ModernTable;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Module 4: Màn hình Thu ngân (Checkout & Payment)
 * Thiết kế chuẩn hóa theo popup billing trong ảnh thiết kế:
 * - Cột trái: Danh sách các Bàn đang có khách (Occupied Tables)
 * - Vùng phải: Bảng chi tiết món, ô nhập Giảm giá & VAT, Khung tính tổng tiền,
 *   Phương thức thanh toán và các nút: Tách hóa đơn, In hóa đơn, Thanh toán (In Bill).
 * =========================================================================
 * Điểm nối CSDL MySQL (Khi tích hợp Backend):
 * 1. Lấy danh sách bàn có khách:
 *    - SELECT MaBan, TenBan FROM Ban WHERE TrangThai = 'CoKhach';
 * 2. Lấy hóa đơn hiện tại:
 *    - SELECT * FROM HoaDon WHERE MaBan = ? AND TrangThai = 'ChuaThanhToan';
 * 3. Cập nhật thanh toán hóa đơn:
 *    - UPDATE HoaDon
 *      SET GiamGiaPhanTram = ?, VatPhanTram = ?, TongTien = ?, HinhThucTT = ?, TrangThai = 'DaThanhToan'
 *      WHERE MaHD = ?;
 * 4. Trả bàn về trống:
 *    - UPDATE Ban SET TrangThai = 'Trong' WHERE MaBan = ?;
 * =========================================================================
 */
public class CheckoutPanel extends JPanel {

    private final MainFrame mainFrame;

    // Danh sách bàn đang có khách (Cột trái)
    private DefaultListModel<Ban> occupiedListModel;
    private JList<Ban> occupiedJList;

    // Chi tiết món (Bảng giữa)
    private DefaultTableModel itemsTableModel;
    private ModernTable itemsTable;

    // Nhãn thông tin
    private JLabel lblSelectedTableTitle;
    private JLabel lblSubtotalVal;
    private JLabel lblVatVal;
    private JLabel lblDiscountVal;
    private JLabel lblTotalPayVal;

    // Input điều chỉnh
    private JSpinner spinVat;
    private JSpinner spinDiscount;
    private JComboBox<String> comboPaymentMethod;

    private Ban currentSelectedBan;
    private HoaDon currentHoaDon;

    public CheckoutPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 14));
        setBackground(UIConstants.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
    }

    private void initUI() {
        // Tiêu đề trang
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JLabel lblPageTitle = new JLabel("Thu ngân & Thanh toán Hóa đơn ");
        lblPageTitle.setFont(UIConstants.FONT_PAGE_TITLE);
        lblPageTitle.setForeground(UIConstants.TEXT_MAIN);
        lblPageTitle.setBorder(new EmptyBorder(0, 0, 0, 16));
        topBar.add(lblPageTitle, BorderLayout.WEST);

        add(topBar, BorderLayout.NORTH);

        // Vùng trung tâm chia 2 phần (Cột trái: Bàn có khách - Cột phải: Hóa đơn & Tính tiền)
        JPanel mainContent = new JPanel(new BorderLayout(16, 0));
        mainContent.setOpaque(false);

        // Cột trái: Occupied Tables List
        mainContent.add(createOccupiedTablesPanel(), BorderLayout.WEST);

        // Vùng phải: Chi tiết hóa đơn và thanh toán
        mainContent.add(createBillingDetailsPanel(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // Nạp danh sách bàn đang có khách
        refreshOccupiedTables();
    }

    /**
     * Cột bên trái: Danh sách các Bàn đang có khách
     */
    private JPanel createOccupiedTablesPanel() {
        RoundedPanel panel = new RoundedPanel(12, Color.WHITE, UIConstants.BORDER_COLOR);
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header cột trái: "Bàn Có Khách"
        JLabel lblHeader = new JLabel("Bàn Đang Có Khách", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHeader.setOpaque(true);
        lblHeader.setBackground(UIConstants.TABLE_HEADER_BG);
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setPreferredSize(new Dimension(0, 36));

        panel.add(lblHeader, BorderLayout.NORTH);

        occupiedListModel = new DefaultListModel<>();
        occupiedJList = new JList<>(occupiedListModel);
        occupiedJList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        occupiedJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        occupiedJList.setFixedCellHeight(38);
        occupiedJList.setSelectionBackground(new Color(225, 239, 254));
        occupiedJList.setSelectionForeground(UIConstants.TEXT_MAIN);

        // Custom Cell Renderer hiển thị tên bàn và trạng thái
        occupiedJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Ban) {
                    Ban b = (Ban) value;
                    lbl.setText("  \uD83E\uDE91  " + b.getTenBan() + " (" + b.getKhuVuc() + ")");
                }
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 243, 246)));
                return lbl;
            }
        });

        occupiedJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Ban selected = occupiedJList.getSelectedValue();
                if (selected != null) {
                    loadBillingForTable(selected);
                }
            }
        });

        JScrollPane scrollList = new JScrollPane(occupiedJList);
        scrollList.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        panel.add(scrollList, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Vùng bên phải: Bảng món ăn, ô nhập liệu, tóm tắt chi phí và các nút hành động
     */
    private JPanel createBillingDetailsPanel() {
        RoundedPanel panel = new RoundedPanel(12, Color.WHITE, UIConstants.BORDER_COLOR);
        panel.setLayout(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Header hóa đơn
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        lblSelectedTableTitle = new JLabel("Chi tiết Hóa đơn: Vui lòng chọn bàn");
        lblSelectedTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSelectedTableTitle.setForeground(new Color(16, 45, 78));
        topRow.add(lblSelectedTableTitle, BorderLayout.WEST);

        panel.add(topRow, BorderLayout.NORTH);

        // Bảng danh sách món ăn (Món ăn, Số lượng, Đơn giá, Thành tiền)
        String[] columns = {"Tên món ăn", "Số lượng", "Đơn giá", "Thành tiền"};
        itemsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        itemsTable = new ModernTable(itemsTableModel);
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(220);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(110);

        JScrollPane scrollTable = new JScrollPane(itemsTable);
        scrollTable.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        scrollTable.setPreferredSize(new Dimension(0, 180));

        panel.add(scrollTable, BorderLayout.CENTER);

        // Vùng dưới: Khung nhập VAT, Chiết khấu, Tóm tắt tổng tiền và Nút thanh toán
        JPanel bottomSection = new JPanel(new BorderLayout(16, 10));
        bottomSection.setOpaque(false);

        // 1. Bên trái vùng dưới: Bộ điều khiển nhập VAT, Chiết khấu, Phương thức thanh toán
        JPanel inputsPanel = new JPanel(new GridBagLayout());
        inputsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        // Hàng 1: Tách hóa đơn
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblSplit = new JLabel("Tách hóa đơn:");
        lblSplit.setFont(UIConstants.FONT_REGULAR);
        inputsPanel.add(lblSplit, gbc);

        gbc.gridx = 1;
        JButton btnSplitBill = new JButton("Tách hóa đơn");
        btnSplitBill.setFont(UIConstants.FONT_REGULAR);
        btnSplitBill.addActionListener(e -> handleSplitBill());
        inputsPanel.add(btnSplitBill, gbc);

        // Hàng 2: VAT (%)
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblVat = new JLabel("Thuế VAT (%):");
        lblVat.setFont(UIConstants.FONT_REGULAR);
        inputsPanel.add(lblVat, gbc);

        gbc.gridx = 1;
        spinVat = new JSpinner(new SpinnerNumberModel(8.0, 0.0, 30.0, 1.0));
        spinVat.setFont(UIConstants.FONT_REGULAR);
        spinVat.addChangeListener(e -> recalculateTotal());
        inputsPanel.add(spinVat, gbc);

        // Hàng 3: Giảm giá (%)
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblDisc = new JLabel("Chiết khấu (%):");
        lblDisc.setFont(UIConstants.FONT_REGULAR);
        inputsPanel.add(lblDisc, gbc);

        gbc.gridx = 1;
        spinDiscount = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 5.0));
        spinDiscount.setFont(UIConstants.FONT_REGULAR);
        spinDiscount.addChangeListener(e -> recalculateTotal());
        inputsPanel.add(spinDiscount, gbc);

        // Hàng 4: Mã Voucher khuyến mãi
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblVoucher = new JLabel("Mã Voucher:");
        lblVoucher.setFont(UIConstants.FONT_REGULAR);
        inputsPanel.add(lblVoucher, gbc);

        gbc.gridx = 1;
        JPanel voucherBox = new JPanel(new BorderLayout(4, 0));
        voucherBox.setOpaque(false);
        JTextField txtVoucher = new JTextField();
        txtVoucher.setPreferredSize(new Dimension(90, 26));
        JButton btnApplyVoucher = new JButton("Áp dụng");
        btnApplyVoucher.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnApplyVoucher.addActionListener(e -> applyVoucher(txtVoucher.getText().trim()));
        voucherBox.add(txtVoucher, BorderLayout.CENTER);
        voucherBox.add(btnApplyVoucher, BorderLayout.EAST);
        inputsPanel.add(voucherBox, gbc);

        // Hàng 5: Phương thức thanh toán
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblMethod = new JLabel("Phương thức thanh toán:");
        lblMethod.setFont(UIConstants.FONT_REGULAR);
        inputsPanel.add(lblMethod, gbc);

        gbc.gridx = 1;
        comboPaymentMethod = new JComboBox<>(new String[]{"Tiền mặt (Cash)", "Chuyển khoản QR", "Thẻ tín dụng / POS"});
        comboPaymentMethod.setFont(UIConstants.FONT_REGULAR);
        comboPaymentMethod.setBackground(Color.WHITE);
        inputsPanel.add(comboPaymentMethod, gbc);

        bottomSection.add(inputsPanel, BorderLayout.WEST);

        // 2. Bên phải vùng dưới: Tóm tắt tiền (Tạm tính, VAT, Giảm giá, Tổng tiền) + Nút Thanh toán
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new EmptyBorder(0, 10, 0, 24));

        lblSubtotalVal = new JLabel("Tạm tính: 0 VND ");
        lblSubtotalVal.setFont(UIConstants.FONT_REGULAR);
        lblSubtotalVal.setBorder(new EmptyBorder(0, 0, 0, 6));
        lblSubtotalVal.setAlignmentX(Component.RIGHT_ALIGNMENT);

        lblDiscountVal = new JLabel("Tiền giảm giá: 0 VND ");
        lblDiscountVal.setFont(UIConstants.FONT_REGULAR);
        lblDiscountVal.setForeground(UIConstants.DANGER_RED);
        lblDiscountVal.setBorder(new EmptyBorder(0, 0, 0, 6));
        lblDiscountVal.setAlignmentX(Component.RIGHT_ALIGNMENT);

        lblVatVal = new JLabel("Tiền VAT (8%): 0 VND ");
        lblVatVal.setFont(UIConstants.FONT_REGULAR);
        lblVatVal.setBorder(new EmptyBorder(0, 0, 0, 6));
        lblVatVal.setAlignmentX(Component.RIGHT_ALIGNMENT);

        lblTotalPayVal = new JLabel("TỔNG TIỀN: 0 VND ");
        lblTotalPayVal.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTotalPayVal.setForeground(UIConstants.PRIMARY_BLUE);
        lblTotalPayVal.setBorder(new EmptyBorder(0, 0, 0, 6));
        lblTotalPayVal.setAlignmentX(Component.RIGHT_ALIGNMENT);

        summaryPanel.add(lblSubtotalVal);
        summaryPanel.add(Box.createVerticalStrut(4));
        summaryPanel.add(lblDiscountVal);
        summaryPanel.add(Box.createVerticalStrut(4));
        summaryPanel.add(lblVatVal);
        summaryPanel.add(Box.createVerticalStrut(8));
        summaryPanel.add(lblTotalPayVal);
        summaryPanel.add(Box.createVerticalStrut(12));

        // Hàng nút chức năng (In hóa đơn, Thanh toán)
        JPanel actionButtonsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionButtonsRow.setOpaque(false);
        actionButtonsRow.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton btnPrintBill = new JButton("In hóa đơn");
        btnPrintBill.setFont(UIConstants.FONT_REGULAR);
        btnPrintBill.addActionListener(e -> handlePrintBillPreview());

        JButton btnPay = new JButton("Thanh toán (In Bill)");
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPay.setBackground(UIConstants.TABLE_EMPTY); // Xanh lá nổi bật
        btnPay.setForeground(Color.WHITE);
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPay.setPreferredSize(new Dimension(170, 36));
        btnPay.addActionListener(e -> handleCheckoutPayment());

        actionButtonsRow.add(btnPrintBill);
        actionButtonsRow.add(btnPay);
        summaryPanel.add(actionButtonsRow);

        bottomSection.add(summaryPanel, BorderLayout.EAST);

        panel.add(bottomSection, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Tải danh sách bàn đang có khách vào JList
     */
    public void refreshOccupiedTables() {
        occupiedListModel.clear();
        List<Ban> occupied = DummyDataFactory.getInstance().getDanhSachBanCoKhach();
        for (Ban b : occupied) {
            occupiedListModel.addElement(b);
        }

        if (!occupied.isEmpty()) {
            occupiedJList.setSelectedIndex(0);
        } else {
            lblSelectedTableTitle.setText("Không có bàn nào đang hoạt động");
            itemsTableModel.setRowCount(0);
            resetSummaryLabels();
        }
    }

    public void selectOccupiedTable(int maBan) {
        refreshOccupiedTables();
        for (int i = 0; i < occupiedListModel.size(); i++) {
            if (occupiedListModel.get(i).getMaBan() == maBan) {
                occupiedJList.setSelectedIndex(i);
                break;
            }
        }
    }

    private void loadBillingForTable(Ban ban) {
        this.currentSelectedBan = ban;
        lblSelectedTableTitle.setText("Chi tiết Hóa đơn: " + ban.getTenBan() + " (" + ban.getKhuVuc() + ")");

        currentHoaDon = DummyDataFactory.getInstance().getHoaDonChoBan(ban.getMaBan());
        itemsTableModel.setRowCount(0);

        if (currentHoaDon != null && currentHoaDon.getDanhSachChiTiet() != null) {
            for (ChiTietHoaDon ct : currentHoaDon.getDanhSachChiTiet()) {
                if (!"DaHuy".equalsIgnoreCase(ct.getTrangThaiMon())) {
                    itemsTableModel.addRow(new Object[]{
                            ct.getTenMon(),
                            ct.getSoLuong(),
                            UIConstants.formatCurrency(ct.getDonGia()),
                            UIConstants.formatCurrency(ct.getThanhTien())
                    });
                }
            }
        }

        recalculateTotal();
    }

    private void recalculateTotal() {
        if (currentHoaDon == null) {
            resetSummaryLabels();
            return;
        }

        double vatRate = ((Number) spinVat.getValue()).doubleValue();
        double discountRate = ((Number) spinDiscount.getValue()).doubleValue();

        currentHoaDon.setVatPhanTram(vatRate);
        currentHoaDon.setGiamGiaPhanTram(discountRate);

        double subtotal = currentHoaDon.getTamTinh();
        double discountAmt = currentHoaDon.getTienGiamGia();
        double vatAmt = currentHoaDon.getTienVAT();
        double total = currentHoaDon.tinhTongCong();

        lblSubtotalVal.setText("Tạm tính: " + UIConstants.formatCurrency(subtotal) + " ");
        lblDiscountVal.setText("Tiền giảm giá (" + discountRate + "%): -" + UIConstants.formatCurrency(discountAmt) + " ");
        lblVatVal.setText("Tiền VAT (" + vatRate + "%): +" + UIConstants.formatCurrency(vatAmt) + " ");
        lblTotalPayVal.setText("TỔNG TIỀN: " + UIConstants.formatCurrency(total) + " ");
    }

    private void resetSummaryLabels() {
        lblSubtotalVal.setText("Tạm tính: 0 VND ");
        lblDiscountVal.setText("Tiền giảm giá: 0 VND ");
        lblVatVal.setText("Tiền VAT: 0 VND ");
        lblTotalPayVal.setText("TỔNG TIỀN: 0 VND ");
    }

    private void handleSplitBill() {
        if (currentHoaDon == null || currentHoaDon.getDanhSachChiTiet() == null || currentHoaDon.getDanhSachChiTiet().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có món nào để tách hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SplitBillDialog dialog = new SplitBillDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                currentSelectedBan != null ? currentSelectedBan.getTenBan() : "Hóa đơn",
                currentHoaDon.getDanhSachChiTiet()
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            List<ChiTietHoaDon> splitItems = dialog.getSplitItems();
            if (splitItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chưa chọn món nào để tách!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Đã tạo Hóa đơn mới gồm " + splitItems.size() + " món được tách!\n(Sẽ thực thi INSERT INTO HoaDon và UPDATE ChiTietHoaDon khi kết nối MySQL)",
                        "Tách hóa đơn thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void handlePrintBillPreview() {
        if (currentHoaDon == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("         HÓA ĐƠN THANH TOÁN              \n");
        sb.append("           RESTAURANT OPS                \n");
        sb.append("=========================================\n");
        sb.append("Bàn: ").append(currentSelectedBan.getTenBan()).append("\n");
        sb.append("Thời gian: ").append(currentHoaDon.getNgayLap().toString()).append("\n");
        sb.append("-----------------------------------------\n");
        sb.append(String.format("%-20s %4s %12s\n", "Món ăn", "SL", "Thành tiền"));
        sb.append("-----------------------------------------\n");

        for (ChiTietHoaDon ct : currentHoaDon.getDanhSachChiTiet()) {
            if (!"DaHuy".equalsIgnoreCase(ct.getTrangThaiMon())) {
                sb.append(String.format("%-20s %4d %12s\n",
                        ct.getTenMon().length() > 18 ? ct.getTenMon().substring(0, 18) : ct.getTenMon(),
                        ct.getSoLuong(),
                        UIConstants.formatCurrency(ct.getThanhTien())));
            }
        }
        sb.append("-----------------------------------------\n");
        sb.append("Tạm tính:         ").append(UIConstants.formatCurrency(currentHoaDon.getTamTinh())).append("\n");
        sb.append("Giảm giá (").append(spinDiscount.getValue()).append("%):   -").append(UIConstants.formatCurrency(currentHoaDon.getTienGiamGia())).append("\n");
        sb.append("Thuế VAT (").append(spinVat.getValue()).append("%):      +").append(UIConstants.formatCurrency(currentHoaDon.getTienVAT())).append("\n");
        sb.append("TỔNG CỘNG:        ").append(UIConstants.formatCurrency(currentHoaDon.getTongTien())).append("\n");
        sb.append("Hình thức:        ").append(comboPaymentMethod.getSelectedItem()).append("\n");
        sb.append("=========================================\n");
        sb.append("      CẢM ƠN QUÝ KHÁCH & HẸN GẶP LẠI!    \n");
        sb.append("=========================================\n");

        JTextArea txt = new JTextArea(sb.toString());
        txt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txt.setEditable(false);
        JScrollPane sp = new JScrollPane(txt);
        sp.setPreferredSize(new Dimension(380, 420));

        JOptionPane.showMessageDialog(this, sp, "Xem trước Hóa đơn in", JOptionPane.PLAIN_MESSAGE);
    }

    private void handleCheckoutPayment() {
        if (currentSelectedBan == null || currentHoaDon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bàn đang có khách để thanh toán!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xác nhận thanh toán hóa đơn cho [" + currentSelectedBan.getTenBan() + "]?\n" +
                        "Tổng tiền thanh toán: " + UIConstants.formatCurrency(currentHoaDon.getTongTien()) + "\n" +
                        "Phương thức: " + comboPaymentMethod.getSelectedItem(),
                "Xác nhận Thanh toán",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            double vatRate = ((Number) spinVat.getValue()).doubleValue();
            double discountRate = ((Number) spinDiscount.getValue()).doubleValue();
            String method = (String) comboPaymentMethod.getSelectedItem();

            // Cập nhật Dummy Data: chuyển bàn về Xanh (Trống), giải phóng hóa đơn
            DummyDataFactory.getInstance().thanhToanBan(currentSelectedBan.getMaBan(), method, discountRate, vatRate);

            JOptionPane.showMessageDialog(this,
                    "Thanh toán thành công!\n" +
                            "Bàn [" + currentSelectedBan.getTenBan() + "] đã được chuyển về trạng thái 'Trống' (Xanh lá).\n" +
                            "(Hệ thống sẽ chạy UPDATE HoaDon SET TrangThai = 'DaThanhToan' và UPDATE Ban SET TrangThai = 'Trong')",
                    "Thanh toán hoàn tất",
                    JOptionPane.INFORMATION_MESSAGE);

            // Nạp lại dữ liệu
            refreshOccupiedTables();

            // Cập nhật Sơ đồ bàn ở Module 3A
            if (mainFrame != null) {
                if (mainFrame.getTableMapPanel() != null) {
                    mainFrame.getTableMapPanel().refreshTableGrid("Tất cả");
                }
                if (mainFrame.getShiftManagementPanel() != null) {
                    mainFrame.getShiftManagementPanel().loadData();
                }
                if (mainFrame.getRevenueReportPanel() != null) {
                    mainFrame.getRevenueReportPanel().loadData();
                }
                if (mainFrame.getInvoiceHistoryPanel() != null) {
                    mainFrame.getInvoiceHistoryPanel().loadData();
                }
                if (mainFrame.getDashboardPanel() != null) {
                    mainFrame.getDashboardPanel().refreshData();
                }
            }
        }
    }

    private void applyVoucher(String code) {
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã Voucher!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        com.restaurant.dao.VoucherDAO vDao = new com.restaurant.dao.VoucherDAO();
        com.restaurant.model.Voucher v = null;
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            v = vDao.getByCode(code);
        }

        if (v == null) {
            if ("LEHOI10".equalsIgnoreCase(code)) v = new com.restaurant.model.Voucher("LEHOI10", "Khuyến mãi Lễ Hội giảm 10%", 10.0, 100000, 500000, "2026-01-01", "2026-12-31", "HoatDong");
            else if ("VIP20".equalsIgnoreCase(code)) v = new com.restaurant.model.Voucher("VIP20", "Tri ân khách VIP thân thiết 20%", 20.0, 300000, 1000000, "2026-01-01", "2026-12-31", "HoatDong");
            else if ("GIAM50K".equalsIgnoreCase(code)) v = new com.restaurant.model.Voucher("GIAM50K", "Chiết khấu trực tiếp 50k", 0.0, 50000, 300000, "2026-01-01", "2026-12-31", "HoatDong");
        }

        if (v != null && "HoatDong".equalsIgnoreCase(v.getTrangThai())) {
            spinDiscount.setValue(v.getPhanTramGiam());
            recalculateTotal();
            JOptionPane.showMessageDialog(this,
                    "Áp dụng thành công Voucher [" + v.getTenVoucher() + "]!\n" +
                    "Mức chiết khấu được áp dụng: " + v.getPhanTramGiam() + "%",
                    "Áp dụng Voucher", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Mã Voucher [" + code + "] không tồn tại hoặc đã hết hạn áp dụng!",
                    "Voucher không hợp lệ", JOptionPane.ERROR_MESSAGE);
        }
    }
}
