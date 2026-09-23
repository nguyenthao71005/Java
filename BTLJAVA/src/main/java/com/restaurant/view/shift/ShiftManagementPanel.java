package com.restaurant.view.shift;

import com.restaurant.dao.CaLamViecDAO;
import com.restaurant.model.CaLamViec;
import com.restaurant.model.NhanVien;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;
import com.restaurant.view.MainFrame;
import com.restaurant.view.components.ModernTable;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Quản lý Ca Làm Việc (Shift Management)
 * Thiết kế chuẩn hóa theo tài liệu nghiệp vụ:
 * 1. Mở ca (Bắt đầu ca)
 * 2. Bàn giao ca (Đóng ca / Kết ca)
 * 3. Xử lý chênh lệch tiền trong két
 * 4. Lịch sử các ca làm việc
 */
public class ShiftManagementPanel extends JPanel {

    private final MainFrame mainFrame;
    private final NhanVien currentUser;
    private final CaLamViecDAO caLamViecDAO = new CaLamViecDAO();

    private CaLamViec activeShift;
    private List<CaLamViec> allShiftsList = new ArrayList<>();

    // Banner hiển thị ca đang mở
    private RoundedPanel activeShiftBanner;
    private JLabel lblActiveStatus;
    private JLabel lblActiveDetails;
    private JButton btnActiveAction;

    // Filter controls
    private JComboBox<String> comboStatusFilter;
    private JTextField txtSearch;
    private DefaultTableModel shiftsTableModel;
    private ModernTable shiftsTable;

    private final DecimalFormat df = new DecimalFormat("#,### VND");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ShiftManagementPanel(MainFrame mainFrame, NhanVien currentUser) {
        this.mainFrame = mainFrame;
        this.currentUser = (currentUser != null) ? currentUser : new NhanVien(1, "Quản trị viên", "admin", "admin123", "QuanLy", "DangLam");

        setLayout(new BorderLayout(0, 14));
        setBackground(UIConstants.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadData();
    }

    private void initUI() {
        // 1. Header trên cùng
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản Lý Ca Làm Việc & Đối Soát Thu Ngân ");
        lblTitle.setFont(UIConstants.FONT_PAGE_TITLE);
        lblTitle.setForeground(UIConstants.TEXT_MAIN);

        JLabel lblSubtitle = new JLabel("Kiểm soát số dư két tiền mặt, mở ca, bàn giao kết ca và theo dõi chênh lệch");
        lblSubtitle.setFont(UIConstants.FONT_HEADER_SUB);
        lblSubtitle.setForeground(UIConstants.TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        topContainer.add(headerPanel);
        topContainer.add(Box.createVerticalStrut(12));

        // Banner ca làm việc hiện tại
        activeShiftBanner = createActiveShiftBanner();
        topContainer.add(activeShiftBanner);
        topContainer.add(Box.createVerticalStrut(12));

        add(topContainer, BorderLayout.NORTH);

        // 2. Trung tâm: Toolbar lọc + Bảng lịch sử ca
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);

        RoundedPanel filterCard = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        filterCard.setLayout(new BorderLayout(10, 0));
        filterCard.setBorder(new EmptyBorder(10, 14, 10, 14));

        // Bên trái thanh filter: Lọc trạng thái & Tìm kiếm
        JPanel leftFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilter.setOpaque(false);

        leftFilter.add(new JLabel("Trạng thái:"));
        comboStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Đang mở", "Đã đóng"});
        comboStatusFilter.setBackground(Color.WHITE);
        comboStatusFilter.setPreferredSize(new Dimension(120, 32));
        comboStatusFilter.addActionListener(e -> filterTableData());
        leftFilter.add(comboStatusFilter);

        leftFilter.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(180, 32));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tên nhân viên, mã ca...");
        txtSearch.addActionListener(e -> filterTableData());
        leftFilter.add(txtSearch);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> filterTableData());
        leftFilter.add(btnSearch);

        // Bên phải thanh filter: Các nút hành động
        JPanel rightFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightFilter.setOpaque(false);

        JButton btnNewShift = new JButton("+ Mở Ca Mới");
        btnNewShift.setFont(UIConstants.FONT_BOLD);
        btnNewShift.setBackground(UIConstants.SUCCESS_GREEN);
        btnNewShift.setForeground(Color.WHITE);
        btnNewShift.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNewShift.addActionListener(e -> handleOpenShift());
        rightFilter.add(btnNewShift);

        JButton btnViewDetail = new JButton("📄 Xem Chi Tiết");
        btnViewDetail.setFont(UIConstants.FONT_REGULAR);
        btnViewDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewDetail.addActionListener(e -> handleViewSelectedShift());
        rightFilter.add(btnViewDetail);

        JButton btnRefresh = new JButton("🔄 Làm Mới");
        btnRefresh.setFont(UIConstants.FONT_REGULAR);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadData());
        rightFilter.add(btnRefresh);

        filterCard.add(leftFilter, BorderLayout.WEST);
        filterCard.add(rightFilter, BorderLayout.EAST);
        centerPanel.add(filterCard, BorderLayout.NORTH);

        // Bảng lịch sử ca
        String[] columns = {
                "Mã Ca", "Nhân Viên", "Thời Gian Mở", "Thời Gian Đóng", "Tiền Đầu Ca",
                "Doanh Thu TM", "Doanh Thu CK", "Tổng Doanh Thu", "Tiền Lý Thuyết",
                "Tiền Thực Tế", "Chênh Lệch Két", "Trạng Thái", "Ghi Chú"
        };

        shiftsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        shiftsTable = new ModernTable(shiftsTableModel);
        setupTableRenderers();

        JScrollPane scrollPane = new JScrollPane(shiftsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private RoundedPanel createActiveShiftBanner() {
        RoundedPanel banner = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        banner.setLayout(new BorderLayout(16, 0));
        banner.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        lblActiveStatus = new JLabel("Đang kiểm tra ca làm việc...");
        lblActiveStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblActiveDetails = new JLabel("Vui lòng chờ...");
        lblActiveDetails.setFont(UIConstants.FONT_REGULAR);
        lblActiveDetails.setForeground(UIConstants.TEXT_MUTED);

        leftPanel.add(lblActiveStatus);
        leftPanel.add(Box.createVerticalStrut(4));
        leftPanel.add(lblActiveDetails);

        btnActiveAction = new JButton("Bàn giao ca");
        btnActiveAction.setFont(UIConstants.FONT_BOLD);
        btnActiveAction.setPreferredSize(new Dimension(180, 38));
        btnActiveAction.setCursor(new Cursor(Cursor.HAND_CURSOR));

        banner.add(leftPanel, BorderLayout.CENTER);
        banner.add(btnActiveAction, BorderLayout.EAST);

        return banner;
    }

    private void setupTableRenderers() {
        // Cột 10: Chênh lệch két -> Tô màu trực quan
        shiftsTable.getColumnModel().getColumn(10).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String valStr = value != null ? value.toString() : "";
                if (!isSelected) {
                    if (valStr.contains("Khớp")) {
                        c.setForeground(new Color(46, 160, 67));
                        c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else if (valStr.startsWith("+")) {
                        c.setForeground(new Color(30, 90, 180));
                        c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else if (valStr.startsWith("-")) {
                        c.setForeground(new Color(219, 55, 55));
                        c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        c.setForeground(UIConstants.TEXT_MAIN);
                    }
                }
                return c;
            }
        });

        // Cột 11: Trạng thái -> Pill badge
        shiftsTable.getColumnModel().getColumn(11).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String valStr = value != null ? value.toString() : "";
                if (!isSelected) {
                    if ("Đang mở".equalsIgnoreCase(valStr)) {
                        lbl.setForeground(new Color(46, 160, 67));
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        lbl.setForeground(new Color(100, 116, 139));
                    }
                }
                return lbl;
            }
        });
    }

    public void loadData() {
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            activeShift = caLamViecDAO.getCaDangMo();
            allShiftsList = caLamViecDAO.getAll();
        } else {
            activeShift = DummyDataFactory.getInstance().getCaHienTai();
            allShiftsList = DummyDataFactory.getInstance().getDanhSachCaLamViec();
        }

        updateBannerUI();
        filterTableData();
    }

    private void updateBannerUI() {
        if (activeShift != null && activeShift.isDangMo()) {
            lblActiveStatus.setText("🟢 CA LÀM VIỆC HIỆN TẠI ĐANG MỞ (#" + activeShift.getMaCa() + ")");
            lblActiveStatus.setForeground(new Color(46, 160, 67));

            String openTime = activeShift.getThoiGianMo() != null ? sdf.format(activeShift.getThoiGianMo()) : "--";
            lblActiveDetails.setText(String.format(
                    "Thu ngân: %s | Mở lúc: %s | Tiền đầu ca: %s | Tiền mặt thu trong ca: %s | Tiền lý thuyết két: %s",
                    activeShift.getTenNV(),
                    openTime,
                    df.format(activeShift.getTienDauCa()),
                    df.format(activeShift.getDoanhThuTienMat()),
                    df.format(activeShift.getTienLyThuyet())
            ));

            btnActiveAction.setText("🔴 Bàn Giao Ca (Kết Ca)");
            btnActiveAction.setBackground(UIConstants.DANGER_RED);
            btnActiveAction.setForeground(Color.WHITE);
            for (java.awt.event.ActionListener al : btnActiveAction.getActionListeners()) {
                btnActiveAction.removeActionListener(al);
            }
            btnActiveAction.addActionListener(e -> handleCloseShift());
            btnActiveAction.setVisible(true);
        } else {
            lblActiveStatus.setText("⚪ HIỆN CHƯA CÓ CA LÀM VIỆC NÀO ĐANG MỞ");
            lblActiveStatus.setForeground(UIConstants.TEXT_MUTED);
            lblActiveDetails.setText("Vui lòng mở ca làm việc đầu phiên để nhân viên thu ngân bắt đầu kiểm đếm và ghi nhận doanh thu.");

            btnActiveAction.setText("🟢 Mở Ca Làm Việc Mới");
            btnActiveAction.setBackground(UIConstants.SUCCESS_GREEN);
            btnActiveAction.setForeground(Color.WHITE);
            for (java.awt.event.ActionListener al : btnActiveAction.getActionListeners()) {
                btnActiveAction.removeActionListener(al);
            }
            btnActiveAction.addActionListener(e -> handleOpenShift());
            btnActiveAction.setVisible(true);
        }

        activeShiftBanner.revalidate();
        activeShiftBanner.repaint();
    }

    private void filterTableData() {
        shiftsTableModel.setRowCount(0);
        String selectedStatus = (String) comboStatusFilter.getSelectedItem();
        String keyword = txtSearch.getText().trim().toLowerCase();

        for (CaLamViec ca : allShiftsList) {
            // Lọc theo trạng thái
            if ("Đang mở".equalsIgnoreCase(selectedStatus) && !ca.isDangMo()) continue;
            if ("Đã đóng".equalsIgnoreCase(selectedStatus) && ca.isDangMo()) continue;

            // Lọc theo từ khóa
            if (!keyword.isEmpty()) {
                boolean match = String.valueOf(ca.getMaCa()).contains(keyword) ||
                        ca.getTenNV().toLowerCase().contains(keyword) ||
                        (ca.getGhiChu() != null && ca.getGhiChu().toLowerCase().contains(keyword));
                if (!match) continue;
            }

            String openTime = ca.getThoiGianMo() != null ? sdf.format(ca.getThoiGianMo()) : "--";
            String closeTime = ca.getThoiGianDong() != null ? sdf.format(ca.getThoiGianDong()) : "--";
            String actualCashStr = ca.getTienThucTe() != null ? df.format(ca.getTienThucTe()) : "--";

            String diffStr = "--";
            if (ca.getChenhLech() != null) {
                double diff = ca.getChenhLech();
                if (Math.abs(diff) < 1.0) diffStr = "0 VND (Khớp)";
                else if (diff > 0) diffStr = "+" + df.format(diff) + " (Thừa)";
                else diffStr = df.format(diff) + " (Thiếu)";
            }

            shiftsTableModel.addRow(new Object[]{
                    "#" + ca.getMaCa(),
                    ca.getTenNV(),
                    openTime,
                    closeTime,
                    df.format(ca.getTienDauCa()),
                    df.format(ca.getDoanhThuTienMat()),
                    df.format(ca.getDoanhThuChuyenKhoan()),
                    df.format(ca.getTongDoanhThu()),
                    df.format(ca.getTienLyThuyet()),
                    actualCashStr,
                    diffStr,
                    ca.isDangMo() ? "Đang mở" : "Đã đóng",
                    ca.getGhiChu() != null ? ca.getGhiChu() : ""
            });
        }
    }

    private void handleOpenShift() {
        if (activeShift != null && activeShift.isDangMo()) {
            int opt = JOptionPane.showConfirmDialog(
                    this,
                    "Hiện tại Ca #" + activeShift.getMaCa() + " của [" + activeShift.getTenNV() + "] vẫn đang mở.\n" +
                    "Bạn có muốn đóng ca hiện tại trước khi mở ca mới không?",
                    "Đang có ca mở",
                    JOptionPane.YES_NO_OPTION
            );
            if (opt == JOptionPane.YES_OPTION) {
                handleCloseShift();
            }
            return;
        }

        OpenShiftDialog dialog = new OpenShiftDialog(SwingUtilities.getWindowAncestor(this), currentUser);
        dialog.setVisible(true);
        if (dialog.isShiftOpened()) {
            loadData();
        }
    }

    private void handleCloseShift() {
        if (activeShift == null || !activeShift.isDangMo()) {
            JOptionPane.showMessageDialog(this, "Hiện không có ca làm việc nào đang mở để bàn giao!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CloseShiftDialog dialog = new CloseShiftDialog(SwingUtilities.getWindowAncestor(this), activeShift);
        dialog.setVisible(true);
        if (dialog.isShiftClosed()) {
            loadData();
        }
    }

    private void handleViewSelectedShift() {
        int selectedRow = shiftsTable.getSelectedRow();
        if (selectedRow < 0) {
            if (activeShift != null) {
                ShiftDetailDialog dialog = new ShiftDetailDialog(SwingUtilities.getWindowAncestor(this), activeShift);
                dialog.setVisible(true);
                return;
            }
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một ca làm việc trên bảng để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = shiftsTable.convertRowIndexToModel(selectedRow);
        String maCaStr = shiftsTableModel.getValueAt(modelRow, 0).toString().replace("#", "").trim();
        int maCa = Integer.parseInt(maCaStr);

        CaLamViec target = null;
        for (CaLamViec ca : allShiftsList) {
            if (ca.getMaCa() == maCa) {
                target = ca;
                break;
            }
        }

        if (target != null) {
            ShiftDetailDialog dialog = new ShiftDetailDialog(SwingUtilities.getWindowAncestor(this), target);
            dialog.setVisible(true);
        }
    }
}
