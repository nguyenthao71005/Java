package com.restaurant.view.management;

import com.restaurant.util.UIConstants;
import com.restaurant.view.components.ModernTable;
import com.restaurant.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Module 5: Base Panel dùng chung cho các màn hình Quản trị (CRUD Management)
 * Cấu trúc gồm 2 phần chính:
 * 1. Form điền thông tin (Trên)
 * 2. Thanh thao tác (Add, Edit, Delete, Reset, Tìm kiếm)
 * 3. JTable hiển thị danh sách dữ liệu (Dưới)
 */
public abstract class BaseCrudPanel extends JPanel {

    protected JLabel lblTitle;
    protected JPanel formContainer;
    protected DefaultTableModel tableModel;
    protected ModernTable table;
    protected JTextField txtSearch;

    protected JButton btnAdd;
    protected JButton btnEdit;
    protected JButton btnDelete;
    protected JButton btnReset;

    public BaseCrudPanel(String titleText) {
        setLayout(new BorderLayout(0, 14));
        setBackground(UIConstants.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initBaseUI(titleText);
    }

    private void initBaseUI(String titleText) {
        // Container phía trên: Title + Form + Buttons
        JPanel topSection = new JPanel(new BorderLayout(0, 12));
        topSection.setOpaque(false);

        // 1. Tiêu đề trang
        lblTitle = new JLabel(titleText);
        lblTitle.setFont(UIConstants.FONT_PAGE_TITLE);
        lblTitle.setForeground(UIConstants.TEXT_MAIN);
        topSection.add(lblTitle, BorderLayout.NORTH);

        // 2. Form Panel (bo góc hiện đại)
        RoundedPanel formCard = new RoundedPanel(12, Color.WHITE, UIConstants.BORDER_COLOR);
        formCard.setLayout(new BorderLayout(0, 10));
        formCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        formContainer = new JPanel();
        formContainer.setOpaque(false);
        setupFormFields(formContainer);
        formCard.add(formContainer, BorderLayout.CENTER);

        // 3. Thanh nút chức năng (Add, Edit, Delete, Reset, Search)
        formCard.add(createToolbarPanel(), BorderLayout.SOUTH);

        topSection.add(formCard, BorderLayout.CENTER);
        add(topSection, BorderLayout.NORTH);

        // 4. Vùng bảng danh sách (Dưới)
        RoundedPanel tableCard = new RoundedPanel(12, Color.WHITE, UIConstants.BORDER_COLOR);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        tableModel = createTableModel();
        table = new ModernTable(tableModel);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                onTableRowSelected(table.getSelectedRow());
            }
        });

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        tableCard.add(scrollTable, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private JPanel createToolbarPanel() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(240, 243, 246)),
                new EmptyBorder(10, 0, 0, 0)
        ));

        // Bên trái: Các nút Add, Edit, Delete, Reset
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftButtons.setOpaque(false);

        btnAdd = new JButton("+ Thêm mới");
        btnAdd.setFont(UIConstants.FONT_BOLD);
        btnAdd.setBackground(UIConstants.PRIMARY_BLUE);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> onAdd());

        btnEdit = new JButton("Cập nhật");
        btnEdit.setFont(UIConstants.FONT_REGULAR);
        btnEdit.addActionListener(e -> onEdit());

        btnDelete = new JButton("Xóa");
        btnDelete.setFont(UIConstants.FONT_REGULAR);
        btnDelete.setForeground(UIConstants.DANGER_RED);
        btnDelete.addActionListener(e -> onDelete());

        btnReset = new JButton("Làm mới form");
        btnReset.setFont(UIConstants.FONT_REGULAR);
        btnReset.addActionListener(e -> onReset());

        leftButtons.add(btnAdd);
        leftButtons.add(btnEdit);
        leftButtons.add(btnDelete);
        leftButtons.add(btnReset);

        // Bên phải: Thanh tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchPanel.setOpaque(false);

        JLabel lblFind = new JLabel("Tìm kiếm:");
        lblFind.setFont(UIConstants.FONT_REGULAR);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(180, 30));
        txtSearch.setFont(UIConstants.FONT_REGULAR);
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập từ khóa...");
        txtSearch.addActionListener(e -> onSearch(txtSearch.getText().trim()));

        searchPanel.add(lblFind);
        searchPanel.add(txtSearch);

        bar.add(leftButtons, BorderLayout.WEST);
        bar.add(searchPanel, BorderLayout.EAST);
        return bar;
    }

    // Các hàm trừu tượng để lớp con triển khai
    protected abstract void setupFormFields(JPanel container);
    protected abstract DefaultTableModel createTableModel();
    protected abstract void onTableRowSelected(int selectedRow);
    protected abstract void onAdd();
    protected abstract void onEdit();
    protected abstract void onDelete();
    protected abstract void onReset();
    protected abstract void onSearch(String keyword);
}
