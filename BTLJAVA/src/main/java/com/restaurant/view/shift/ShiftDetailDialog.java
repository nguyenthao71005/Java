package com.restaurant.view.shift;

import com.restaurant.dao.CaLamViecDAO;
import com.restaurant.model.CaLamViec;
import com.restaurant.model.HoaDon;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;
import com.restaurant.view.components.ModernTable;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Hộp thoại (JDialog) Xem chi tiết Ca làm việc & Danh sách Hóa đơn trong ca
 */
public class ShiftDetailDialog extends JDialog {

    private final CaLamViec shift;
    private final CaLamViecDAO caLamViecDAO = new CaLamViecDAO();
    private final DecimalFormat df = new DecimalFormat("#,### VND");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private DefaultTableModel invoicesTableModel;
    private ModernTable invoicesTable;

    public ShiftDetailDialog(Window parent, CaLamViec shift) {
        super(parent, "Chi tiết Ca làm việc #" + shift.getMaCa(), ModalityType.APPLICATION_MODAL);
        this.shift = shift;

        setSize(900, 650);
        setLocationRelativeTo(parent);

        initUI();
        loadInvoices();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(UIConstants.BG_LIGHT);
        root.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("CHI TIẾT CA LÀM VIỆC #" + shift.getMaCa());
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(new Color(16, 45, 78));

        String statusStr = shift.isDangMo() ? "🟢 Đang mở" : "⚪ Đã đóng";
        JLabel lblStatus = new JLabel("Trạng thái: " + statusStr + " | Trực ca: " + shift.getTenNV());
        lblStatus.setFont(UIConstants.FONT_REGULAR);
        lblStatus.setForeground(UIConstants.TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblStatus, BorderLayout.SOUTH);
        root.add(headerPanel, BorderLayout.NORTH);

        // Center: Thẻ tóm tắt số liệu & Bảng hóa đơn
        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setOpaque(false);

        // Thẻ tóm tắt tài chính ca
        RoundedPanel summaryCard = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        summaryCard.setLayout(new GridLayout(3, 4, 10, 8));
        summaryCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        String openTime = shift.getThoiGianMo() != null ? sdf.format(shift.getThoiGianMo()) : "--";
        String closeTime = shift.getThoiGianDong() != null ? sdf.format(shift.getThoiGianDong()) : "--";

        addSummaryField(summaryCard, "Giờ mở ca:", openTime);
        addSummaryField(summaryCard, "Giờ đóng ca:", closeTime);
        addSummaryField(summaryCard, "Tiền đầu ca:", df.format(shift.getTienDauCa()));
        addSummaryField(summaryCard, "Doanh thu TM:", df.format(shift.getDoanhThuTienMat()));

        addSummaryField(summaryCard, "Doanh thu CK/Thẻ:", df.format(shift.getDoanhThuChuyenKhoan()));
        addSummaryField(summaryCard, "Tổng doanh thu:", df.format(shift.getTongDoanhThu()));
        addSummaryField(summaryCard, "Tiền lý thuyết:", df.format(shift.getTienLyThuyet()));
        String actualStr = shift.getTienThucTe() != null ? df.format(shift.getTienThucTe()) : "Chưa kiểm đếm";
        addSummaryField(summaryCard, "Tiền thực tế:", actualStr);

        String diffStr = "--";
        if (shift.getChenhLech() != null) {
            double d = shift.getChenhLech();
            diffStr = (d == 0) ? "0 VND (Khớp)" : ((d > 0 ? "+" : "") + df.format(d));
        }
        addSummaryField(summaryCard, "Chênh lệch két:", diffStr);
        addSummaryField(summaryCard, "Ghi chú:", shift.getGhiChu() != null ? shift.getGhiChu() : "--");

        centerPanel.add(summaryCard, BorderLayout.NORTH);

        // Bảng danh sách hóa đơn trong ca
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);

        JLabel lblListTitle = new JLabel("Danh sách Hóa đơn thanh toán trong ca này:");
        lblListTitle.setFont(UIConstants.FONT_BOLD);
        lblListTitle.setForeground(new Color(16, 45, 78));
        tableContainer.add(lblListTitle, BorderLayout.NORTH);

        String[] cols = {"Mã HĐ", "Bàn", "Giờ vào", "Giờ thanh toán", "Hình thức TT", "Tổng tiền (VND)", "Trạng thái"};
        invoicesTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        invoicesTable = new ModernTable(invoicesTableModel);
        JScrollPane sp = new JScrollPane(invoicesTable);
        sp.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        tableContainer.add(sp, BorderLayout.CENTER);

        centerPanel.add(tableContainer, BorderLayout.CENTER);
        root.add(centerPanel, BorderLayout.CENTER);

        // Nút dưới cùng
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnPrint = new JButton("🖨️ In Biên Bản Bàn Giao Ca");
        btnPrint.setFont(UIConstants.FONT_BOLD);
        btnPrint.setBackground(UIConstants.PRIMARY_BLUE);
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrint.addActionListener(e -> handlePrintHandoverSlip());

        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(UIConstants.FONT_REGULAR);
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void addSummaryField(JPanel container, String title, String val) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblT.setForeground(UIConstants.TEXT_MUTED);

        JLabel lblV = new JLabel(val);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblV.setForeground(UIConstants.TEXT_MAIN);

        p.add(lblT, BorderLayout.NORTH);
        p.add(lblV, BorderLayout.SOUTH);
        container.add(p);
    }

    private void loadInvoices() {
        invoicesTableModel.setRowCount(0);
        List<HoaDon> list;
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            list = caLamViecDAO.getHoaDonByMaCa(shift.getMaCa());
        } else {
            list = DummyDataFactory.getInstance().getHoaDonTheoCa(shift.getMaCa());
        }

        for (HoaDon h : list) {
            String inTime = h.getNgayLap() != null ? sdf.format(h.getNgayLap()) : "--";
            String outTime = h.getGioRa() != null ? sdf.format(h.getGioRa()) : "--";
            invoicesTableModel.addRow(new Object[]{
                    "#" + h.getMaHD(),
                    h.getTenBan(),
                    inTime,
                    outTime,
                    h.getHinhThucTT(),
                    df.format(h.getTongTien()),
                    "DaThanhToan".equalsIgnoreCase(h.getTrangThai()) ? "Đã thanh toán" : h.getTrangThai()
            });
        }
    }

    private void handlePrintHandoverSlip() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("         BIÊN BẢN BÀN GIAO CA LÀM VIỆC             \n");
        sb.append("             RESTAURANT OPS 1.0                    \n");
        sb.append("====================================================\n");
        sb.append("Mã ca làm việc:        #").append(shift.getMaCa()).append("\n");
        sb.append("Nhân viên trực ca:     ").append(shift.getTenNV()).append("\n");
        sb.append("Thời gian mở ca:       ").append(shift.getThoiGianMo() != null ? sdf.format(shift.getThoiGianMo()) : "--").append("\n");
        sb.append("Thời gian đóng ca:     ").append(shift.getThoiGianDong() != null ? sdf.format(shift.getThoiGianDong()) : "Chưa đóng").append("\n");
        sb.append("Trạng thái ca:         ").append(shift.isDangMo() ? "Đang mở" : "Đã hoàn thành bàn giao").append("\n");
        sb.append("----------------------------------------------------\n");
        sb.append("THỐNG KÊ DOANH THU & ĐỐI SOÁT KÉT:\n");
        sb.append("1. Tiền mặt ban đầu trong két:    ").append(df.format(shift.getTienDauCa())).append("\n");
        sb.append("2. Doanh thu tiền mặt trong ca:    ").append(df.format(shift.getDoanhThuTienMat())).append("\n");
        sb.append("3. Doanh thu Chuyển khoản / Thẻ:   ").append(df.format(shift.getDoanhThuChuyenKhoan())).append("\n");
        sb.append("4. Tổng doanh thu bán hàng:        ").append(df.format(shift.getTongDoanhThu())).append("\n");
        sb.append("5. Số hóa đơn thanh toán:          ").append(shift.getSoHoaDon()).append(" đơn hàng\n");
        sb.append("----------------------------------------------------\n");
        sb.append("TIỀN MẶT LÝ THUYẾT TRONG KÉT:      ").append(df.format(shift.getTienLyThuyet())).append("\n");
        sb.append("TIỀN MẶT THỰC TẾ KIỂM ĐẾM:         ").append(shift.getTienThucTe() != null ? df.format(shift.getTienThucTe()) : "--").append("\n");
        if (shift.getChenhLech() != null) {
            double diff = shift.getChenhLech();
            String diffStatus = (diff == 0) ? "0 VND (Khớp 100%)" : ((diff > 0 ? "+" : "") + df.format(diff));
            sb.append("CHÊNH LỆCH KÉT (Thừa/Thiếu):       ").append(diffStatus).append("\n");
        }
        sb.append("Ghi chú bàn giao: ").append(shift.getGhiChu() != null ? shift.getGhiChu() : "Không có").append("\n");
        sb.append("====================================================\n");
        sb.append("   Người bàn giao                     Người nhận ca  \n");
        sb.append("      (Ký tên)                           (Ký tên)    \n\n\n\n");
        sb.append("====================================================\n");

        JTextArea txt = new JTextArea(sb.toString());
        txt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txt.setEditable(false);
        JScrollPane sp = new JScrollPane(txt);
        sp.setPreferredSize(new Dimension(500, 520));

        JOptionPane.showMessageDialog(this, sp, "Biên bản bàn giao ca", JOptionPane.PLAIN_MESSAGE);
    }
}
