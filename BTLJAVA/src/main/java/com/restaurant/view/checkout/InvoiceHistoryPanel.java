package com.restaurant.view.checkout;

import com.restaurant.dao.HoaDonDAO;
import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.ChiTietHoaDon;
import com.restaurant.model.HoaDon;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;
import com.restaurant.view.components.ModernTable;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Lịch sử Hóa đơn & Tra cứu chi tiết thanh toán
 */
public class InvoiceHistoryPanel extends JPanel {

    private ModernTable tableInvoices;
    private DefaultTableModel modelInvoices;
    private ModernTable tableItems;
    private DefaultTableModel modelItems;

    private JTextField txtSearch;
    private JComboBox<String> comboFilterStatus;
    private JComboBox<String> comboFilterMethod;
    private JLabel lblSelectedInvoiceInfo;

    private List<HoaDon> allInvoices = new ArrayList<>();
    private HoaDon selectedHoaDon = null;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public InvoiceHistoryPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UIConstants.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadData();
    }

    private void initUI() {
        // 1. Top Section: Title + Filter Toolbar
        JPanel topSection = new JPanel(new BorderLayout(0, 10));
        topSection.setOpaque(false);

        JLabel lblTitle = new JLabel("Lịch Sử Hóa Đơn & Tra Cứu Thanh Toán");
        lblTitle.setFont(UIConstants.FONT_PAGE_TITLE);
        lblTitle.setForeground(UIConstants.TEXT_MAIN);
        topSection.add(lblTitle, BorderLayout.NORTH);

        RoundedPanel filterCard = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));

        filterCard.add(new JLabel("Tìm kiếm (Mã/Bàn):"));
        txtSearch = new JTextField(12);
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập mã hoặc tên bàn...");
        txtSearch.addActionListener(e -> applyFilters());
        filterCard.add(txtSearch);

        filterCard.add(new JLabel("Trạng thái:"));
        comboFilterStatus = new JComboBox<>(new String[]{"Tất cả", "Đã thanh toán", "Chưa thanh toán"});
        comboFilterStatus.setBackground(Color.WHITE);
        comboFilterStatus.addActionListener(e -> applyFilters());
        filterCard.add(comboFilterStatus);

        filterCard.add(new JLabel("Phương thức:"));
        comboFilterMethod = new JComboBox<>(new String[]{"Tất cả", "TienMat", "ChuyenKhoan", "The"});
        comboFilterMethod.setBackground(Color.WHITE);
        comboFilterMethod.addActionListener(e -> applyFilters());
        filterCard.add(comboFilterMethod);

        JButton btnFilter = new JButton("Lọc");
        btnFilter.setFont(UIConstants.FONT_REGULAR);
        btnFilter.addActionListener(e -> applyFilters());
        filterCard.add(btnFilter);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(UIConstants.FONT_REGULAR);
        btnRefresh.addActionListener(e -> loadData());
        filterCard.add(btnRefresh);

        JButton btnReprint = new JButton("In lại Bill (K80)");
        btnReprint.setFont(UIConstants.FONT_BOLD);
        btnReprint.setForeground(UIConstants.PRIMARY_BLUE);
        btnReprint.addActionListener(e -> handleReprintBill());
        filterCard.add(btnReprint);

        topSection.add(filterCard, BorderLayout.CENTER);
        add(topSection, BorderLayout.NORTH);

        // 2. Center: SplitPane chứa danh sách Hóa đơn (trên) và Chi tiết món (dưới)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.55);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);

        // Panel trên: Danh sách Hóa đơn
        RoundedPanel pnlInvoices = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        pnlInvoices.setLayout(new BorderLayout(0, 6));
        pnlInvoices.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel lblListTitle = new JLabel("Danh Sách Hóa Đơn Nhà Hàng");
        lblListTitle.setFont(UIConstants.FONT_SECTION);
        pnlInvoices.add(lblListTitle, BorderLayout.NORTH);

        String[] colsInvoices = {"Mã HĐ", "Bàn", "Giờ Vào", "Giờ Ra", "Giảm (%)", "VAT (%)", "Tổng Tiền", "Phương Thức", "Thu Ngân/NV", "Trạng Thái"};
        modelInvoices = new DefaultTableModel(colsInvoices, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tableInvoices = new ModernTable(modelInvoices);
        tableInvoices.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onInvoiceSelected();
            }
        });

        JScrollPane scrollInvoices = new JScrollPane(tableInvoices);
        scrollInvoices.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        pnlInvoices.add(scrollInvoices, BorderLayout.CENTER);

        splitPane.setTopComponent(pnlInvoices);

        // Panel dưới: Chi tiết các món của Hóa đơn được chọn
        RoundedPanel pnlItems = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        pnlItems.setLayout(new BorderLayout(0, 6));
        pnlItems.setBorder(new EmptyBorder(10, 12, 10, 12));

        lblSelectedInvoiceInfo = new JLabel("Chi tiết món ăn của hóa đơn (Vui lòng chọn 1 hóa đơn ở trên)");
        lblSelectedInvoiceInfo.setFont(UIConstants.FONT_SECTION);
        lblSelectedInvoiceInfo.setForeground(UIConstants.PRIMARY_BLUE);
        pnlItems.add(lblSelectedInvoiceInfo, BorderLayout.NORTH);

        String[] colsItems = {"Mã CTHD", "Tên Món Ăn", "Số Lượng", "Đơn Giá", "Thành Tiền", "Ghi Chú Bếp", "Trạng Thái Món"};
        modelItems = new DefaultTableModel(colsItems, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tableItems = new ModernTable(modelItems);
        JScrollPane scrollItems = new JScrollPane(tableItems);
        scrollItems.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        pnlItems.add(scrollItems, BorderLayout.CENTER);

        splitPane.setBottomComponent(pnlItems);

        add(splitPane, BorderLayout.CENTER);
    }

    public void loadData() {
        allInvoices.clear();
        if (DatabaseConnection.isConnected()) {
            allInvoices = new HoaDonDAO().getAllInvoices();
        }

        // Fallback mô phỏng nếu chưa có hóa đơn trong CSDL
        if (allInvoices.isEmpty()) {
            HoaDon hd1 = new HoaDon(101, 1, "Bàn 1");
            hd1.setTongTien(477000);
            hd1.setHinhThucTT("TienMat");
            hd1.setTrangThai("DaThanhToan");
            hd1.setTenNV("Lê Thị Mai");

            HoaDon hd2 = new HoaDon(102, 4, "Bàn 4");
            hd2.setTongTien(1250000);
            hd2.setHinhThucTT("ChuyenKhoan");
            hd2.setTrangThai("DaThanhToan");
            hd2.setTenNV("Lê Thị Mai");

            allInvoices.add(hd1);
            allInvoices.add(hd2);
        }

        applyFilters();
    }

    private void applyFilters() {
        modelInvoices.setRowCount(0);
        String keyword = txtSearch.getText().trim().toLowerCase();
        String statusFilter = (String) comboFilterStatus.getSelectedItem();
        String methodFilter = (String) comboFilterMethod.getSelectedItem();

        for (HoaDon hd : allInvoices) {
            // Lọc từ khóa
            boolean matchKeyword = keyword.isEmpty()
                    || String.valueOf(hd.getMaHD()).contains(keyword)
                    || (hd.getTenBan() != null && hd.getTenBan().toLowerCase().contains(keyword));

            // Lọc trạng thái
            boolean matchStatus = true;
            if ("Đã thanh toán".equals(statusFilter)) {
                matchStatus = "DaThanhToan".equalsIgnoreCase(hd.getTrangThai());
            } else if ("Chưa thanh toán".equals(statusFilter)) {
                matchStatus = "ChuaThanhToan".equalsIgnoreCase(hd.getTrangThai());
            }

            // Lọc phương thức
            boolean matchMethod = true;
            if (!"Tất cả".equals(methodFilter)) {
                matchMethod = methodFilter.equalsIgnoreCase(hd.getHinhThucTT());
            }

            if (matchKeyword && matchStatus && matchMethod) {
                String gioVaoStr = hd.getGioVao() != null ? dateFormat.format(hd.getGioVao()) : "-";
                String gioRaStr = hd.getGioRa() != null ? dateFormat.format(hd.getGioRa()) : "-";
                String trangThaiVn = "DaThanhToan".equalsIgnoreCase(hd.getTrangThai()) ? "Đã thanh toán" : "Chưa thanh toán";

                modelInvoices.addRow(new Object[]{
                        "#" + hd.getMaHD(),
                        hd.getTenBan(),
                        gioVaoStr,
                        gioRaStr,
                        hd.getGiamGiaPhanTram() + "%",
                        hd.getVatPhanTram() + "%",
                        UIConstants.formatCurrency(hd.getTongTien()),
                        formatPaymentMethod(hd.getHinhThucTT()),
                        hd.getTenNV(),
                        trangThaiVn
                });
            }
        }

        modelItems.setRowCount(0);
        selectedHoaDon = null;
        lblSelectedInvoiceInfo.setText("Chi tiết món ăn của hóa đơn (Vui lòng chọn 1 hóa đơn ở trên)");
    }

    private String formatPaymentMethod(String method) {
        if ("TienMat".equalsIgnoreCase(method)) return "Tiền mặt (Cash)";
        if ("ChuyenKhoan".equalsIgnoreCase(method)) return "Chuyển khoản QR";
        if ("The".equalsIgnoreCase(method)) return "Thẻ tín dụng / POS";
        return method != null ? method : "Tiền mặt";
    }

    private void onInvoiceSelected() {
        int row = tableInvoices.getSelectedRow();
        if (row < 0) return;

        String idStr = (String) modelInvoices.getValueAt(row, 0);
        int maHD = Integer.parseInt(idStr.replace("#", ""));

        // Tìm hóa đơn
        for (HoaDon h : allInvoices) {
            if (h.getMaHD() == maHD) {
                selectedHoaDon = h;
                break;
            }
        }

        if (selectedHoaDon != null) {
            lblSelectedInvoiceInfo.setText("Chi tiết món ăn của Hóa đơn #" + selectedHoaDon.getMaHD() + " (" + selectedHoaDon.getTenBan() + ") - Tổng: " + UIConstants.formatCurrency(selectedHoaDon.getTongTien()));
            loadInvoiceItems(selectedHoaDon.getMaHD());
        }
    }

    private void loadInvoiceItems(int maHD) {
        modelItems.setRowCount(0);
        List<ChiTietHoaDon> items = null;

        if (DatabaseConnection.isConnected()) {
            items = new HoaDonDAO().getInvoiceItemsById(maHD);
        }

        if (items == null || items.isEmpty()) {
            // Mock items
            items = new ArrayList<>();
            items.add(new ChiTietHoaDon(1, maHD, 1, "Bò bít tết Wagyu", 2, 495000, "Sốt tiêu đen"));
            items.add(new ChiTietHoaDon(2, maHD, 4, "Nước ép cam tươi", 2, 45000, "Ít đá"));
        }

        for (ChiTietHoaDon ct : items) {
            modelItems.addRow(new Object[]{
                    ct.getMaCTHD(),
                    ct.getTenMon(),
                    ct.getSoLuong(),
                    UIConstants.formatCurrency(ct.getDonGia()),
                    UIConstants.formatCurrency(ct.getThanhTien()),
                    ct.getGhiChu() != null ? ct.getGhiChu() : "",
                    formatDishStatus(ct.getTrangThaiMon())
            });
        }
    }

    private String formatDishStatus(String status) {
        if ("DaRaMon".equalsIgnoreCase(status)) return "Đã ra món";
        if ("DangLam".equalsIgnoreCase(status)) return "Đang làm";
        if ("ChoLam".equalsIgnoreCase(status)) return "Chờ làm";
        if ("DaHuy".equalsIgnoreCase(status)) return "Đã hủy";
        return status != null ? status : "Đã ra món";
    }

    private void handleReprintBill() {
        if (selectedHoaDon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn từ bảng để in lại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("        RESTAURANT OPS - HÓA ĐƠN        \n");
        sb.append("         (PHIẾU IN LẠI / REPRINT)        \n");
        sb.append("=========================================\n");
        sb.append("Mã HĐ: #").append(selectedHoaDon.getMaHD()).append("\n");
        sb.append("Bàn: ").append(selectedHoaDon.getTenBan()).append("\n");
        sb.append("Thu ngân: ").append(selectedHoaDon.getTenNV()).append("\n");
        sb.append("Thời gian: ").append(selectedHoaDon.getGioRa() != null ? dateFormat.format(selectedHoaDon.getGioRa()) : dateFormat.format(new java.util.Date())).append("\n");
        sb.append("-----------------------------------------\n");
        sb.append(String.format("%-20s %4s %12s\n", "Tên món", "SL", "Thành tiền"));
        sb.append("-----------------------------------------\n");

        for (int i = 0; i < modelItems.getRowCount(); i++) {
            String name = (String) modelItems.getValueAt(i, 1);
            int qty = (int) modelItems.getValueAt(i, 2);
            String sub = (String) modelItems.getValueAt(i, 4);
            if (name.length() > 20) name = name.substring(0, 18) + "..";
            sb.append(String.format("%-20s %4d %12s\n", name, qty, sub));
        }

        sb.append("-----------------------------------------\n");
        sb.append("Chiết khấu: ").append(selectedHoaDon.getGiamGiaPhanTram()).append("%\n");
        sb.append("Thuế VAT: ").append(selectedHoaDon.getVatPhanTram()).append("%\n");
        sb.append("TỔNG THANH TOÁN: ").append(UIConstants.formatCurrency(selectedHoaDon.getTongTien())).append("\n");
        sb.append("Hình thức TT: ").append(formatPaymentMethod(selectedHoaDon.getHinhThucTT())).append("\n");
        sb.append("=========================================\n");
        sb.append("     CẢM ƠN QUÝ KHÁCH - HẸN GẶP LẠI!     \n");

        JTextArea txt = new JTextArea(sb.toString());
        txt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txt.setEditable(false);
        txt.setBackground(new Color(250, 250, 250));

        JScrollPane scroll = new JScrollPane(txt);
        scroll.setPreferredSize(new Dimension(380, 460));

        JOptionPane.showMessageDialog(this, scroll, "Xem trước Hóa đơn In lại (K80)", JOptionPane.PLAIN_MESSAGE);
    }
}
