package com.restaurant.view.management;

import com.restaurant.dao.LichSuThaoTacDAO;
import com.restaurant.dao.LichSuTruyCapDAO;
import com.restaurant.model.LichSuThaoTac;
import com.restaurant.model.LichSuTruyCap;
import com.restaurant.service.AuditService;
import com.restaurant.service.AuditSwingWorker;
import com.restaurant.util.UIConstants;
import com.restaurant.view.components.ModernTable;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel xem Lịch sử truy cập & Lịch sử thao tác Audit
 * Sử dụng SwingWorker để load dữ liệu bất đồng bộ, tránh block UI
 */
public class AuditLogPanel extends RoundedPanel {

    private final AuditService auditService;
    
    // Components
    private JTabbedPane tabbedPane;
    private JPanel accessLogPanel;
    private JPanel operationLogPanel;
    
    // Access Log Components
    private ModernTable accessTable;
    private DefaultTableModel accessTableModel;
    private JTextField txtSearchAccess;
    private JComboBox<String> cboFilterAccess;
    private JTextField txtDateFromAccess, txtDateToAccess;
    private JButton btnRefreshAccess, btnExportAccess;
    private JLabel lblAccessLoading;
    
    // Operation Log Components
    private ModernTable operationTable;
    private DefaultTableModel operationTableModel;
    private JTextField txtSearchOperation;
    private JComboBox<String> cboFilterTable, cboFilterAction;
    private JTextField txtDateFromOperation, txtDateToOperation;
    private JButton btnRefreshOperation, btnExportOperation;
    private JLabel lblOperationLoading;
    
    // Statistics
    private JLabel lblTotalAccess, lblTotalOperations;

    // SwingWorker references for cancellation
    private SwingWorker<List<LichSuTruyCap>, Void> accessWorker;
    private SwingWorker<List<LichSuThaoTac>, Void> operationWorker;

    public AuditLogPanel() {
        super(15);
        this.auditService = AuditService.getInstance();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIConstants.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        add(createHeader(), BorderLayout.NORTH);
        
        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.FONT_REGULAR);
        tabbedPane.setBackground(Color.WHITE);
        
        accessLogPanel = createAccessLogPanel();
        operationLogPanel = createOperationLogPanel();
        
        tabbedPane.addTab("Lịch sử truy cập", accessLogPanel);
        tabbedPane.addTab("Lịch sử thao tác", operationLogPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel title = new JLabel("Nhật ký hệ thống (Audit Log)");
        title.setFont(UIConstants.FONT_PAGE_TITLE);
        title.setForeground(UIConstants.TEXT_MAIN);
        
        JPanel statsPanel = createStatsPanel();
        
        header.add(title, BorderLayout.WEST);
        header.add(statsPanel, BorderLayout.EAST);
        
        return header;
    }

    private JPanel createStatsPanel() {
        JPanel stats = new RoundedPanel(10);
        stats.setBackground(Color.WHITE);
        stats.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        stats.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        lblTotalAccess = createStatLabel("Tổng truy cập: 0", UIConstants.PRIMARY_BLUE);
        lblTotalOperations = createStatLabel("Tổng thao tác: 0", UIConstants.SUCCESS_GREEN);
        
        stats.add(lblTotalAccess);
        stats.add(lblTotalOperations);
        
        return stats;
    }

    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_BOLD);
        label.setForeground(color);
        return label;
    }

    private JPanel createAccessLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterBar.setOpaque(false);
        
        filterBar.add(new JLabel("Từ ngày:"));
        txtDateFromAccess = new JTextField(10);
        txtDateFromAccess.setPreferredSize(new Dimension(100, 30));
        txtDateFromAccess.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        filterBar.add(txtDateFromAccess);
        
        filterBar.add(new JLabel("Đến ngày:"));
        txtDateToAccess = new JTextField(10);
        txtDateToAccess.setPreferredSize(new Dimension(100, 30));
        txtDateToAccess.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        filterBar.add(txtDateToAccess);
        
        filterBar.add(new JLabel("Hành động:"));
        cboFilterAccess = new JComboBox<>(new String[]{"Tất cả", "Đăng nhập", "Đăng xuất", "Đăng nhập thất bại"});
        cboFilterAccess.setPreferredSize(new Dimension(150, 30));
        filterBar.add(cboFilterAccess);
        
        txtSearchAccess = new JTextField(15);
        txtSearchAccess.setPreferredSize(new Dimension(150, 30));
        txtSearchAccess.putClientProperty("JTextField.placeholderText", "Tìm kiếm...");
        filterBar.add(txtSearchAccess);
        
        btnRefreshAccess = createButton("Làm mới", UIConstants.PRIMARY_BLUE);
        btnExportAccess = createButton("Xuất Excel", UIConstants.SUCCESS_GREEN);
        lblAccessLoading = new JLabel("");
        lblAccessLoading.setForeground(UIConstants.PRIMARY_BLUE);
        
        filterBar.add(btnRefreshAccess);
        filterBar.add(btnExportAccess);
        filterBar.add(Box.createHorizontalStrut(10));
        filterBar.add(lblAccessLoading);
        
        // Table
        String[] columns = {"Mã", "Thời gian", "Tên đăng nhập", "Họ tên", "Hành động", "Trạng thái", "Địa chỉ IP", "Ghi chú"};
        accessTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        accessTable = new ModernTable(accessTableModel);
        
        JScrollPane scrollPane = new JScrollPane(accessTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        // Event listeners
        btnRefreshAccess.addActionListener(e -> loadAccessLog());
        btnExportAccess.addActionListener(e -> exportAccessLog());
        txtSearchAccess.addActionListener(e -> loadAccessLog());
        cboFilterAccess.addActionListener(e -> loadAccessLog());
        
        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createOperationLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterBar.setOpaque(false);
        
        filterBar.add(new JLabel("Từ ngày:"));
        txtDateFromOperation = new JTextField(10);
        txtDateFromOperation.setPreferredSize(new Dimension(100, 30));
        txtDateFromOperation.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        filterBar.add(txtDateFromOperation);
        
        filterBar.add(new JLabel("Đến ngày:"));
        txtDateToOperation = new JTextField(10);
        txtDateToOperation.setPreferredSize(new Dimension(100, 30));
        txtDateToOperation.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        filterBar.add(txtDateToOperation);
        
        filterBar.add(new JLabel("Bảng:"));
        cboFilterTable = new JComboBox<>(new String[]{"Tất cả", "NhanVien", "Ban", "MonAn", "DanhMuc", "Voucher", "HoaDon", "CaLamViec"});
        cboFilterTable.setPreferredSize(new Dimension(120, 30));
        filterBar.add(cboFilterTable);
        
        filterBar.add(new JLabel("Thao tác:"));
        cboFilterAction = new JComboBox<>(new String[]{"Tất cả", "Tạo mới", "Cập nhật", "Xóa"});
        cboFilterAction.setPreferredSize(new Dimension(120, 30));
        filterBar.add(cboFilterAction);
        
        txtSearchOperation = new JTextField(15);
        txtSearchOperation.setPreferredSize(new Dimension(150, 30));
        txtSearchOperation.putClientProperty("JTextField.placeholderText", "Tìm kiếm...");
        filterBar.add(txtSearchOperation);
        
        btnRefreshOperation = createButton("Làm mới", UIConstants.PRIMARY_BLUE);
        btnExportOperation = createButton("Xuất Excel", UIConstants.SUCCESS_GREEN);
        lblOperationLoading = new JLabel("");
        lblOperationLoading.setForeground(UIConstants.PRIMARY_BLUE);
        
        filterBar.add(btnRefreshOperation);
        filterBar.add(btnExportOperation);
        filterBar.add(Box.createHorizontalStrut(10));
        filterBar.add(lblOperationLoading);
        
        // Table
        String[] columns = {"Mã", "Thời gian", "Nhân viên", "Bảng", "Thao tác", "Mô tả", "Chi tiết", "Máy tính"};
        operationTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        operationTable = new ModernTable(operationTableModel);
        
        // Set column widths
        operationTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        operationTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        operationTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        operationTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        operationTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        operationTable.getColumnModel().getColumn(5).setPreferredWidth(200);
        operationTable.getColumnModel().getColumn(6).setPreferredWidth(300);
        operationTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(operationTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        // Event listeners
        btnRefreshOperation.addActionListener(e -> loadOperationLog());
        btnExportOperation.addActionListener(e -> exportOperationLog());
        txtSearchOperation.addActionListener(e -> loadOperationLog());
        cboFilterTable.addActionListener(e -> loadOperationLog());
        cboFilterAction.addActionListener(e -> loadOperationLog());
        
        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(UIConstants.FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 30));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }

    private void loadData() {
        loadAccessLog();
        loadOperationLog();
    }

    private void loadAccessLog() {
        // Cancel previous worker if running
        if (accessWorker != null && !accessWorker.isDone()) {
            accessWorker.cancel(true);
        }
        
        // Show loading indicator
        lblAccessLoading.setText("Đang tải...");
        btnRefreshAccess.setEnabled(false);
        
        accessWorker = new SwingWorker<List<LichSuTruyCap>, Void>() {
            @Override
            protected List<LichSuTruyCap> doInBackground() {
                try {
                    LichSuTruyCapDAO dao = new LichSuTruyCapDAO();
                    return dao.getRecent(500);
                } catch (Exception e) {
                    System.err.println("Lỗi lấy lịch sử truy cập: " + e.getMessage());
                    return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    if (!isCancelled()) {
                        List<LichSuTruyCap> list = get();
                        displayAccessLog(list);
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi nạp access log: " + e.getMessage());
                    displayAccessLog(List.of());
                } finally {
                    btnRefreshAccess.setEnabled(true);
                    lblAccessLoading.setText("");
                }
            }
        };
        accessWorker.execute();
    }
    
    private void displayAccessLog(List<LichSuTruyCap> list) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Apply filters
                String search = txtSearchAccess.getText().trim().toLowerCase();
                String filterAction = (String) cboFilterAccess.getSelectedItem();
                
                List<LichSuTruyCap> filteredList = list.stream()
                    .filter(item -> {
                        // Search filter
                        if (!search.isEmpty()) {
                            boolean match = (item.getTenDangNhap() != null && item.getTenDangNhap().toLowerCase().contains(search)) ||
                                          (item.getHoTen() != null && item.getHoTen().toLowerCase().contains(search));
                            if (!match) return false;
                        }
                        
                        // Action filter
                        if (filterAction != null && !filterAction.equals("Tất cả")) {
                            String actionName = item.getHanhDong() != null ? item.getHanhDong().name() : "";
                            switch (filterAction) {
                                case "Đăng nhập":
                                    return actionName.equals("DangNhap");
                                case "Đăng xuất":
                                    return actionName.equals("DangXuat");
                                case "Đăng nhập thất bại":
                                    return actionName.equals("DangNhapThatBai");
                            }
                        }
                        
                        // Date filter
                        LocalDate fromDate = parseDate(txtDateFromAccess.getText());
                        LocalDate toDate = parseDate(txtDateToAccess.getText());
                        
                        if (fromDate != null && item.getThoiGian() != null && item.getThoiGian().toLocalDate().isBefore(fromDate)) {
                            return false;
                        }
                        if (toDate != null && item.getThoiGian() != null && item.getThoiGian().toLocalDate().isAfter(toDate)) {
                            return false;
                        }
                        
                        return true;
                    })
                    .collect(Collectors.toList());
                
                // Clear and repopulate table
                accessTableModel.setRowCount(0);
                
                for (LichSuTruyCap item : filteredList) {
                    Object[] row = {
                        item.getMaTruyCap(),
                        item.getThoiGianFormatted(),
                        item.getTenDangNhap(),
                        item.getHoTen() != null ? item.getHoTen() : "-",
                        item.getHanhDong() != null ? item.getHanhDong().getMoTa() : "-",
                        item.getTrangThai() != null ? item.getTrangThai().getMoTa() : "-",
                        item.getDiaChiIP() != null ? item.getDiaChiIP() : "-",
                        item.getGhiChu() != null ? item.getGhiChu() : "-"
                    };
                    accessTableModel.addRow(row);
                }
                
                lblTotalAccess.setText("Tổng truy cập: " + filteredList.size());
                
            } catch (Exception e) {
                System.err.println("Lỗi hiển thị access log: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void loadOperationLog() {
        // Cancel previous worker if running
        if (operationWorker != null && !operationWorker.isDone()) {
            operationWorker.cancel(true);
        }
        
        // Show loading indicator
        lblOperationLoading.setText("Đang tải...");
        btnRefreshOperation.setEnabled(false);
        
        operationWorker = new SwingWorker<List<LichSuThaoTac>, Void>() {
            @Override
            protected List<LichSuThaoTac> doInBackground() {
                try {
                    LichSuThaoTacDAO dao = new LichSuThaoTacDAO();
                    return dao.getRecent(500);
                } catch (Exception e) {
                    System.err.println("Lỗi lấy lịch sử thao tác: " + e.getMessage());
                    return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    if (!isCancelled()) {
                        List<LichSuThaoTac> list = get();
                        displayOperationLog(list);
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi nạp operation log: " + e.getMessage());
                    displayOperationLog(List.of());
                } finally {
                    btnRefreshOperation.setEnabled(true);
                    lblOperationLoading.setText("");
                }
            }
        };
        operationWorker.execute();
    }
    
    private void displayOperationLog(List<LichSuThaoTac> list) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Apply filters
                String search = txtSearchOperation.getText().trim().toLowerCase();
                String filterTable = (String) cboFilterTable.getSelectedItem();
                String filterAction = (String) cboFilterAction.getSelectedItem();
                
                List<LichSuThaoTac> filteredList = list.stream()
                    .filter(item -> {
                        // Search filter
                        if (!search.isEmpty()) {
                            boolean match = (item.getHoTen() != null && item.getHoTen().toLowerCase().contains(search)) ||
                                          (item.getMoTa() != null && item.getMoTa().toLowerCase().contains(search)) ||
                                          (item.getChiTiet() != null && item.getChiTiet().toLowerCase().contains(search));
                            if (!match) return false;
                        }
                        
                        // Table filter
                        if (filterTable != null && !filterTable.equals("Tất cả")) {
                            if (!filterTable.equals(item.getTenBang())) {
                                return false;
                            }
                        }
                        
                        // Action filter
                        if (filterAction != null && !filterAction.equals("Tất cả")) {
                            String actionName = item.getHanhDong() != null ? item.getHanhDong().name() : "";
                            switch (filterAction) {
                                case "Tạo mới":
                                    return actionName.equals("INSERT");
                                case "Cập nhật":
                                    return actionName.equals("UPDATE");
                                case "Xóa":
                                    return actionName.equals("DELETE");
                            }
                        }
                        
                        // Date filter
                        LocalDate fromDate = parseDate(txtDateFromOperation.getText());
                        LocalDate toDate = parseDate(txtDateToOperation.getText());
                        
                        if (fromDate != null && item.getThoiGian() != null && item.getThoiGian().toLocalDate().isBefore(fromDate)) {
                            return false;
                        }
                        if (toDate != null && item.getThoiGian() != null && item.getThoiGian().toLocalDate().isAfter(toDate)) {
                            return false;
                        }
                        
                        return true;
                    })
                    .collect(Collectors.toList());
                
                // Clear and repopulate table
                operationTableModel.setRowCount(0);
                
                for (LichSuThaoTac item : filteredList) {
                    Object[] row = {
                        item.getMaThaoTac(),
                        item.getThoiGianFormatted(),
                        item.getHoTen() != null ? item.getHoTen() : "Hệ thống",
                        item.getTenBang() != null ? item.getTenBang() : "-",
                        item.getHanhDong() != null ? item.getHanhDong().getMoTa() : "-",
                        item.getMoTa() != null ? item.getMoTa() : "-",
                        item.getChiTiet() != null ? item.getChiTiet() : "-",
                        item.getMayTinh() != null ? item.getMayTinh() : "-"
                    };
                    operationTableModel.addRow(row);
                }
                
                lblTotalOperations.setText("Tổng thao tác: " + filteredList.size());
                
            } catch (Exception e) {
                System.err.println("Lỗi hiển thị operation log: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void exportAccessLog() {
        JOptionPane.showMessageDialog(this, 
            "Chức năng xuất Excel đang được phát triển!\nBạn có thể sao chép dữ liệu từ bảng.", 
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportOperationLog() {
        JOptionPane.showMessageDialog(this, 
            "Chức năng xuất Excel đang được phát triển!\nBạn có thể sao chép dữ liệu từ bảng.", 
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return null;
        }
    }

    public void refresh() {
        loadData();
    }
}
