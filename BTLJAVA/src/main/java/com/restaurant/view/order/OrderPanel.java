package com.restaurant.view.order;

import com.restaurant.model.Ban;
import com.restaurant.model.ChiTietHoaDon;
import com.restaurant.model.DanhMuc;
import com.restaurant.model.HoaDon;
import com.restaurant.model.MonAn;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.util.UIConstants;
import com.restaurant.view.MainFrame;
import com.restaurant.view.components.ModernTable;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Module 3B: Màn hình Order (Split Pane)
 * =========================================================================
 * Điểm nối CSDL MySQL (Khi tích hợp Backend):
 * 1. Tải chi tiết đơn:
 *    - SELECT ct.MaCTHD, m.TenMon, ct.SoLuong, ct.DonGia, (ct.SoLuong * ct.DonGia) AS ThanhTien, ct.GhiChu, ct.TrangThaiMon
 *      FROM ChiTietHoaDon ct
 *      JOIN MonAn m ON ct.MaMon = m.MaMon
 *      WHERE ct.MaHD = ?;
 * 2. Lưu / Gửi Order vào Bếp:
 *    - INSERT INTO ChiTietHoaDon (MaHD, MaMon, SoLuong, DonGia, GhiChu, TrangThaiMon)
 *      VALUES (?, ?, ?, ?, ?, 'ChoLam');
 * 3. Hủy món / Cập nhật số lượng:
 *    - UPDATE ChiTietHoaDon SET SoLuong = ? WHERE MaCTHD = ?;
 *    - DELETE FROM ChiTietHoaDon WHERE MaCTHD = ?;
 * 4. Chuyển bàn:
 *    - UPDATE HoaDon SET MaBan = ? WHERE MaHD = ?;
 *    - UPDATE Ban SET TrangThai = 'Trong' WHERE MaBan = ?; (bàn cũ)
 *    - UPDATE Ban SET TrangThai = 'CoKhach' WHERE MaBan = ?; (bàn mới)
 * =========================================================================
 */
public class OrderPanel extends JPanel {

    private final MainFrame mainFrame;
    private int currentMaBan = 4; // Mặc định hiển thị Bàn 4 có sẵn dữ liệu
    private HoaDon currentHoaDon;

    // Components bên trái (Order Details)
    private JLabel lblCurrentTable;
    private JLabel lblSubtotal;
    private DefaultTableModel orderTableModel;
    private ModernTable orderTable;
    private List<ChiTietHoaDon> currentItemsList = new ArrayList<>();

    // Components bên phải (Menu Items)
    private JPanel dishesContainer;
    private JTextField txtSearchDish;
    private int selectedCategoryFilter = 0; // 0: Tất cả

    public OrderPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_LIGHT);

        initUI();
    }

    private void initUI() {
        // SplitPane chia làm 2 phần: Bên trái (Order JTable) & Bên phải (Lưới món ăn)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.48);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);

        // Vùng bên trái: Chi tiết Order
        JPanel leftPanel = createOrderDetailsPanel();
        splitPane.setLeftComponent(leftPanel);

        // Vùng bên phải: Menu món ăn theo danh mục
        JPanel rightPanel = createMenuSelectionPanel();
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        // Tải dữ liệu ban đầu
        loadOrderForTable(currentMaBan);
    }

    /**
     * Vùng bên trái: Danh sách món đã gọi + Toolbar tính năng
     */
    private JPanel createOrderDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UIConstants.BG_LIGHT);
        panel.setBorder(new EmptyBorder(16, 16, 16, 8));

        // Header trên cùng: Tên bàn & Tổng tiền
        RoundedPanel headerCard = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        headerCard.setLayout(new BorderLayout());
        headerCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        lblCurrentTable = new JLabel("Đơn hàng hiện tại: Bàn 4 ");
        lblCurrentTable.setFont(UIConstants.FONT_PAGE_TITLE);
        lblCurrentTable.setForeground(new Color(16, 45, 78));
        lblCurrentTable.setBorder(new EmptyBorder(0, 0, 0, 16));

        lblSubtotal = new JLabel("Tạm tính: 0 VND ");
        lblSubtotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSubtotal.setForeground(UIConstants.PRIMARY_BLUE);
        lblSubtotal.setBorder(new EmptyBorder(0, 0, 0, 4));

        headerCard.add(lblCurrentTable, BorderLayout.WEST);
        headerCard.add(lblSubtotal, BorderLayout.EAST);
        panel.add(headerCard, BorderLayout.NORTH);

        // Bảng danh sách món
        String[] columns = {"Tên món ăn", "SL", "Đơn giá", "Thành tiền", "Ghi chú", "Trạng thái"};
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        orderTable = new ModernTable(orderTableModel);
        orderTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // Căn chỉnh độ rộng cột
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(115);
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(36);
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(78);
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(85);
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(95);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        RoundedPanel tableCard = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        tableCard.add(scrollPane, BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        // Thanh công cụ thao tác dưới cùng (Buttons: + / -, Hủy món, Ghi chú, Chuyển bàn, Gửi order)
        panel.add(createOrderActionsToolbar(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOrderActionsToolbar() {
        RoundedPanel toolbarCard = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        toolbarCard.setLayout(new BorderLayout(8, 8));
        toolbarCard.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Hàng trên: Các nút điều chỉnh món (+, -, Hủy món, Ghi chú)
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row1.setOpaque(false);

        JButton btnPlus = new JButton("  +  ");
        btnPlus.setFont(UIConstants.FONT_BOLD);
        btnPlus.setToolTipText("Tăng số lượng");
        btnPlus.addActionListener(e -> modifySelectedItemQty(1));

        JButton btnMinus = new JButton("  -  ");
        btnMinus.setFont(UIConstants.FONT_BOLD);
        btnMinus.setToolTipText("Giảm số lượng");
        btnMinus.addActionListener(e -> modifySelectedItemQty(-1));

        JButton btnNote = new JButton("Ghi chú");
        btnNote.setFont(UIConstants.FONT_REGULAR);
        btnNote.addActionListener(e -> addNoteToSelectedItem());

        JButton btnServed = new JButton("Đã ra món");
        btnServed.setFont(UIConstants.FONT_BOLD);
        btnServed.setForeground(new Color(46, 160, 67));
        btnServed.setToolTipText("Xác nhận món đã chế biến xong và đã bưng ra bàn phục vụ khách");
        btnServed.addActionListener(e -> markSelectedItemAsServed());

        JButton btnDelete = new JButton("Hủy món");
        btnDelete.setFont(UIConstants.FONT_REGULAR);
        btnDelete.setForeground(UIConstants.DANGER_RED);
        btnDelete.setToolTipText("Hủy món khách đã gọi");
        btnDelete.addActionListener(e -> cancelSelectedItem());

        row1.add(btnPlus);
        row1.add(btnMinus);
        row1.add(btnNote);
        row1.add(btnServed);
        row1.add(btnDelete);

        // Hàng dưới: Chuyển bàn & Gửi order
        JPanel row2 = new JPanel(new BorderLayout(8, 0));
        row2.setOpaque(false);

        JButton btnTransfer = new JButton("Chuyển bàn / Ghép bàn");
        btnTransfer.setFont(UIConstants.FONT_REGULAR);
        btnTransfer.addActionListener(e -> handleTransferTable());

        JButton btnSendOrder = new JButton("Lưu & Gửi Bếp");
        btnSendOrder.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSendOrder.setBackground(UIConstants.PRIMARY_BLUE);
        btnSendOrder.setForeground(Color.WHITE);
        btnSendOrder.addActionListener(e -> handleSendOrderToKitchen());

        row2.add(btnTransfer, BorderLayout.WEST);
        row2.add(btnSendOrder, BorderLayout.EAST);

        toolbarCard.add(row1, BorderLayout.NORTH);
        toolbarCard.add(row2, BorderLayout.SOUTH);

        return toolbarCard;
    }

    /**
     * Vùng bên phải: Bộ lọc danh mục + Lưới chọn món ăn
     */
    private JPanel createMenuSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UIConstants.BG_LIGHT);
        panel.setBorder(new EmptyBorder(16, 8, 16, 16));

        // Header: Filter Buttons theo Danh mục & Tìm kiếm
        RoundedPanel filterCard = new RoundedPanel(10, Color.WHITE, UIConstants.BORDER_COLOR);
        filterCard.setLayout(new BorderLayout(8, 8));
        filterCard.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Ô tìm kiếm món
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        JLabel lblSearch = new JLabel("Tìm kiếm món:");
        lblSearch.setFont(UIConstants.FONT_REGULAR);
        txtSearchDish = new JTextField();
        txtSearchDish.setFont(UIConstants.FONT_REGULAR);
        txtSearchDish.putClientProperty("JTextField.placeholderText", "Nhập tên món ăn cần tìm...");
        txtSearchDish.addActionListener(e -> filterDishes());

        searchRow.add(lblSearch, BorderLayout.WEST);
        searchRow.add(txtSearchDish, BorderLayout.CENTER);

        // Các nút Filter danh mục
        JPanel catButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        catButtonsPanel.setOpaque(false);

        JButton btnAll = createCategoryButton("Tất cả", 0);
        catButtonsPanel.add(btnAll);

        List<DanhMuc> categories = DummyDataFactory.getInstance().getDanhSachDanhMuc();
        for (DanhMuc dm : categories) {
            JButton btnCat = createCategoryButton(dm.getTenDM(), dm.getMaDM());
            catButtonsPanel.add(btnCat);
        }

        filterCard.add(searchRow, BorderLayout.NORTH);
        filterCard.add(catButtonsPanel, BorderLayout.SOUTH);

        panel.add(filterCard, BorderLayout.NORTH);

        // Lưới thẻ món ăn
        dishesContainer = new JPanel(new GridLayout(0, 3, 10, 10));
        dishesContainer.setBackground(Color.WHITE);
        dishesContainer.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scrollDishes = new JScrollPane(dishesContainer);
        scrollDishes.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        scrollDishes.getViewport().setBackground(Color.WHITE);
        scrollDishes.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollDishes, BorderLayout.CENTER);

        // Tải danh sách món ăn ban đầu
        filterDishes();

        return panel;
    }

    private JButton createCategoryButton(String text, int categoryId) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_REGULAR);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            this.selectedCategoryFilter = categoryId;
            filterDishes();
        });
        return btn;
    }

    public void refreshOrderPanel() {
        filterDishes();
        if (currentHoaDon != null) {
            loadOrderForTable(currentMaBan);
        }
    }

    private void filterDishes() {
        dishesContainer.removeAll();
        List<MonAn> dishes;
        if (com.restaurant.database.DatabaseConnection.isConnected()) {
            dishes = new com.restaurant.dao.MonAnDAO().getAll();
        } else {
            dishes = DummyDataFactory.getInstance().getDanhSachMonAn();
        }
        String keyword = txtSearchDish != null ? txtSearchDish.getText().trim().toLowerCase() : "";

        for (MonAn mon : dishes) {
            // Lọc theo danh mục
            if (selectedCategoryFilter > 0 && mon.getMaDM() != selectedCategoryFilter) {
                continue;
            }
            // Lọc theo từ khóa tìm kiếm
            if (!keyword.isEmpty() && !mon.getTenMon().toLowerCase().contains(keyword)) {
                continue;
            }

            dishesContainer.add(createDishCard(mon));
        }

        dishesContainer.revalidate();
        dishesContainer.repaint();
    }

    /**
     * Thẻ hiển thị món ăn có ảnh/icon, tên món, giá và nút thêm nhanh
     */
    private JPanel createDishCard(MonAn mon) {
        boolean isAvailable = mon.isConMon();
        RoundedPanel card = new RoundedPanel(8, isAvailable ? new Color(248, 250, 252) : new Color(241, 245, 249), UIConstants.BORDER_COLOR);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Icon món ăn minh họa
        JLabel lblIcon = new JLabel(isAvailable ? "\uD83C\uDF7D\uFE0F" : "🚫", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcon.setPreferredSize(new Dimension(0, 45));

        // Tên món kèm nhãn HẾT MÓN nếu bếp báo hết
        String titleHtml = isAvailable
                ? "<html><center><b>" + mon.getTenMon() + "</b></center></html>"
                : "<html><center><strike style='color:#94a3b8;'>" + mon.getTenMon() + "</strike><br><font color='#e11d48'><b>[HẾT HÀNG]</b></font></center></html>";

        JLabel lblName = new JLabel(titleHtml, SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblName.setForeground(isAvailable ? UIConstants.TEXT_MAIN : UIConstants.TEXT_MUTED);

        // Giá bán
        JLabel lblPrice = new JLabel(UIConstants.formatCurrency(mon.getGiaBan()), SwingConstants.CENTER);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPrice.setForeground(isAvailable ? UIConstants.PRIMARY_BLUE : UIConstants.TEXT_MUTED);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);
        infoPanel.add(lblName);
        infoPanel.add(lblPrice);

        // Nút thêm vào hóa đơn
        JButton btnAdd = new JButton(isAvailable ? "+ Thêm" : "Hết món");
        btnAdd.setFont(UIConstants.FONT_SMALL);
        btnAdd.setFocusPainted(false);
        if (isAvailable) {
            btnAdd.setBackground(new Color(230, 240, 255));
            btnAdd.setForeground(UIConstants.PRIMARY_BLUE);
            btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnAdd.addActionListener(e -> addDishToOrder(mon));
        } else {
            btnAdd.setEnabled(false);
            btnAdd.setBackground(new Color(226, 232, 240));
            btnAdd.setForeground(new Color(148, 163, 184));
        }

        card.add(lblIcon, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Tải dữ liệu hóa đơn của bàn được chỉ định
     */
    public void loadOrderForTable(int maBan) {
        this.currentMaBan = maBan;
        Ban b = DummyDataFactory.getInstance().timBanTheoMa(maBan);
        String tenBan = (b != null) ? b.getTenBan() : ("Bàn " + maBan);

        lblCurrentTable.setText("Đơn hàng hiện tại: " + tenBan + " ");

        currentHoaDon = DummyDataFactory.getInstance().getHoaDonChoBan(maBan);
        if (currentHoaDon == null) {
            currentHoaDon = DummyDataFactory.getInstance().moBanMoi(maBan);
        }

        currentItemsList = currentHoaDon.getDanhSachChiTiet();
        refreshOrderTable();
    }

    private void refreshOrderTable() {
        orderTableModel.setRowCount(0);
        double subtotal = 0;

        if (currentItemsList != null) {
            for (ChiTietHoaDon ct : currentItemsList) {
                if ("DaHuy".equalsIgnoreCase(ct.getTrangThaiMon())) continue;

                subtotal += ct.getThanhTien();
                orderTableModel.addRow(new Object[]{
                        ct.getTenMon(),
                        ct.getSoLuong(),
                        UIConstants.formatCurrency(ct.getDonGia()),
                        UIConstants.formatCurrency(ct.getThanhTien()),
                        ct.getGhiChu() != null ? ct.getGhiChu() : "",
                        formatStatusLabel(ct.getTrangThaiMon())
                });
            }
        }

        lblSubtotal.setText("Tạm tính: " + UIConstants.formatCurrency(subtotal));
    }

    private String formatStatusLabel(String status) {
        if ("ChoLam".equalsIgnoreCase(status)) return "Chờ làm";
        if ("DangLam".equalsIgnoreCase(status)) return "Đang làm";
        if ("DaRaMon".equalsIgnoreCase(status)) return "Đã ra món";
        return status;
    }

    private void addDishToOrder(MonAn mon) {
        // Kiểm tra món đã có trong danh sách hay chưa
        for (ChiTietHoaDon ct : currentItemsList) {
            if (ct.getMaMon() == mon.getMaMon() && !"DaHuy".equalsIgnoreCase(ct.getTrangThaiMon())) {
                ct.setSoLuong(ct.getSoLuong() + 1);
                refreshOrderTable();
                return;
            }
        }

        // Chưa có -> Tạo mới
        ChiTietHoaDon newItem = new ChiTietHoaDon(
                currentItemsList.size() + 1,
                currentHoaDon.getMaHD(),
                mon.getMaMon(),
                mon.getTenMon(),
                1,
                mon.getGiaBan(),
                "",
                "ChoLam"
        );
        currentItemsList.add(newItem);
        refreshOrderTable();
    }

    private void modifySelectedItemQty(int delta) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món trong bảng để chỉnh số lượng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ChiTietHoaDon item = currentItemsList.get(selectedRow);
        int newQty = item.getSoLuong() + delta;
        if (newQty <= 0) {
            cancelSelectedItem();
        } else {
            item.setSoLuong(newQty);
            refreshOrderTable();
            orderTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private void cancelSelectedItem() {
        int[] selectedRows = orderTable.getSelectedRows();
        if (selectedRows == null || selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một món cần hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn hủy " + selectedRows.length + " món đã chọn không?",
                "Xác nhận Hủy món",
                JOptionPane.YES_NO_OPTION
        );
        if (opt == JOptionPane.YES_OPTION) {
            // Xóa theo thứ tự giảm dần để không bị lệch chỉ số index
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int r = selectedRows[i];
                if (r >= 0 && r < currentItemsList.size()) {
                    currentItemsList.remove(r);
                }
            }
            refreshOrderTable();

            if (com.restaurant.database.DatabaseConnection.isConnected() && currentHoaDon != null) {
                new com.restaurant.dao.HoaDonDAO().saveOrderItems(currentHoaDon.getMaHD(), currentItemsList);
            }
        }
    }

    private void addNoteToSelectedItem() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món để nhập ghi chú bếp!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ChiTietHoaDon item = currentItemsList.get(selectedRow);
        String note = JOptionPane.showInputDialog(
                this,
                "Nhập ghi chú cho món [" + item.getTenMon() + "]:\n(Ví dụ: Ít đường, Không cay, Mang ra sau...)",
                item.getGhiChu()
        );

        if (note != null) {
            item.setGhiChu(note);
            refreshOrderTable();
            orderTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    /**
     * Xác nhận ra món cho một hoặc NHIỀU món được chọn cùng lúc
     */
    private void markSelectedItemAsServed() {
        int[] selectedRows = orderTable.getSelectedRows();

        // Trường hợp 1: Chưa chọn dòng nào -> Hỏi có muốn ra TẤT CẢ các món đang làm không
        if (selectedRows == null || selectedRows.length == 0) {
            int opt = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn chưa chọn dòng nào trên bảng.\nBạn có muốn chuyển TẤT CẢ các món 'Đang làm' thành 'Đã ra món' (Đã phục vụ) không?",
                    "Xác nhận Ra tất cả món",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (opt == JOptionPane.YES_OPTION) {
                int countAll = 0;
                for (ChiTietHoaDon ct : currentItemsList) {
                    if ("DangLam".equalsIgnoreCase(ct.getTrangThaiMon()) || "ChoLam".equalsIgnoreCase(ct.getTrangThaiMon())) {
                        ct.setTrangThaiMon("DaRaMon");
                        countAll++;
                    }
                }
                if (countAll > 0) {
                    refreshOrderTable();
                    if (com.restaurant.database.DatabaseConnection.isConnected() && currentHoaDon != null) {
                        new com.restaurant.dao.HoaDonDAO().saveOrderItems(currentHoaDon.getMaHD(), currentItemsList);
                    }
                    JOptionPane.showMessageDialog(this,
                            "Đã chuyển thành công toàn bộ " + countAll + " món sang trạng thái 'Đã ra món'!",
                            "Ra món thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Hiện không có món nào đang chờ phục vụ!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            return;
        }

        // Trường hợp 2: Đã chọn 1 hoặc nhiều món (bằng cách click hoặc giữ phím Ctrl / Shift)
        int updatedCount = 0;
        StringBuilder names = new StringBuilder();

        for (int r : selectedRows) {
            if (r >= 0 && r < currentItemsList.size()) {
                ChiTietHoaDon item = currentItemsList.get(r);
                if (!"DaRaMon".equalsIgnoreCase(item.getTrangThaiMon())) {
                    item.setTrangThaiMon("DaRaMon");
                    updatedCount++;
                    if (names.length() > 0) names.append(", ");
                    names.append(item.getTenMon());
                }
            }
        }

        if (updatedCount == 0) {
            JOptionPane.showMessageDialog(this,
                    "Các món bạn chọn đều đã ở trạng thái 'Đã ra món' trước đó!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        refreshOrderTable();

        // Đồng bộ trạng thái mới xuống MySQL
        if (com.restaurant.database.DatabaseConnection.isConnected() && currentHoaDon != null) {
            new com.restaurant.dao.HoaDonDAO().saveOrderItems(currentHoaDon.getMaHD(), currentItemsList);
        }

        JOptionPane.showMessageDialog(this,
                "Đã chuyển trạng thái " + updatedCount + " món sang 'Đã ra món' thành công!\nCác món: " + names.toString(),
                "Đã ra món thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleTransferTable() {
        List<Ban> allTables = DummyDataFactory.getInstance().getDanhSachBan();
        List<String> emptyTableNames = new ArrayList<>();
        for (Ban b : allTables) {
            if (!b.isCoKhach() && b.getMaBan() != currentMaBan) {
                emptyTableNames.add(b.getTenBan());
            }
        }

        if (emptyTableNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không còn bàn trống nào để chuyển!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String target = (String) JOptionPane.showInputDialog(
                this,
                "Chọn bàn trống muốn chuyển tới:",
                "Chuyển bàn",
                JOptionPane.QUESTION_MESSAGE,
                null,
                emptyTableNames.toArray(),
                emptyTableNames.get(0)
        );

        if (target != null) {
            JOptionPane.showMessageDialog(this,
                    "Đã chuyển toàn bộ hóa đơn từ Bàn " + currentMaBan + " sang " + target + " thành công!\n(Sẽ thực thi lệnh UPDATE HoaDon SET MaBan = ... khi nối CSDL)",
                    "Chuyển bàn thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleSendOrderToKitchen() {
        if (currentItemsList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có món nào được chọn trong đơn hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int count = 0;
        for (ChiTietHoaDon ct : currentItemsList) {
            if ("ChoLam".equalsIgnoreCase(ct.getTrangThaiMon())) {
                ct.setTrangThaiMon("DangLam");
                count++;
            }
        }

        refreshOrderTable();

        if (com.restaurant.database.DatabaseConnection.isConnected() && currentHoaDon != null) {
            new com.restaurant.dao.HoaDonDAO().saveOrderItems(currentHoaDon.getMaHD(), currentItemsList);
            JOptionPane.showMessageDialog(
                    this,
                    "Đã lưu và đồng bộ order thành công xuống CSDL MySQL!\nSố món chuyển sang trạng thái Bếp đang làm: " + count,
                    "Gửi Bếp thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Đã lưu và gửi order thành công đến Bếp!\nSố món mới chuyển sang trạng thái 'Đang làm': " + count,
                    "Gửi Bếp thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
