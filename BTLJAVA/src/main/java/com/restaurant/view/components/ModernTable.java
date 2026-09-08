package com.restaurant.view.components;

import com.restaurant.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * Bảng JTable tùy biến theo phong cách hiện đại trong ảnh mẫu
 * - Header màu xanh đậm (Dark Navy), chữ trắng nổi bật
 * - Chiều cao hàng thoáng đãng (32px - 36px)
 * - Màu nền xen kẽ (Zebra stripes) và màu khi chọn chuột dịu mắt
 */
public class ModernTable extends JTable {

    public ModernTable(TableModel dm) {
        super(dm);
        initModernStyle();
    }

    private void initModernStyle() {
        setRowHeight(34);
        setFont(UIConstants.FONT_REGULAR);
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setGridColor(new Color(240, 243, 246));
        setSelectionBackground(new Color(225, 239, 254));
        setSelectionForeground(UIConstants.TEXT_MAIN);
        setFillsViewportHeight(true);

        // Tùy chỉnh Header
        JTableHeader header = getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(UIConstants.TABLE_HEADER_BG);
        header.setForeground(UIConstants.TABLE_HEADER_TEXT);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        // Renderer cho Header
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(UIConstants.TABLE_HEADER_BG);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return lbl;
            }
        });

        // Renderer mặc định cho các ô dữ liệu
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
                }
                return c;
            }
        });
    }
}
