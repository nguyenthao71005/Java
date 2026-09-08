package com.restaurant.view.components;

import com.restaurant.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Thẻ KPI hiển thị chỉ số kinh doanh hiện đại
 * Thiết kế chuẩn UI/UX cao cấp:
 * - Icon tròn biểu trưng ở góc trên
 * - Tiêu đề rõ ràng, số liệu lớn nổi bật
 * - Badge xu hướng MoM (Pill badge bo tròn với màu xanh tăng / đỏ giảm)
 */
public class KpiCard extends RoundedPanel {

    private JLabel lblTitle;
    private JLabel lblValue;
    private JLabel lblSubtitle;
    private JPanel badgePill;
    private JLabel lblBadgeText;
    private JPanel iconBadge;

    public KpiCard(String title, String value, String badgeText, boolean isPositive, String iconSymbol, Color iconBg, Color iconColor) {
        super(14, Color.WHITE, UIConstants.BORDER_COLOR);
        setLayout(new BorderLayout(0, 4));
        setBorder(new EmptyBorder(10, 16, 10, 16));

        // Top Row: Tiêu đề bên trái, Icon tròn bên phải
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(100, 116, 139));
        topRow.add(lblTitle, BorderLayout.WEST);

        if (iconSymbol != null && !iconSymbol.isEmpty()) {
            iconBadge = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(iconBg != null ? iconBg : new Color(241, 245, 249));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            };
            iconBadge.setPreferredSize(new Dimension(32, 32));
            iconBadge.setOpaque(false);

            JLabel lblIcon = new JLabel(iconSymbol);
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
            lblIcon.setForeground(iconColor != null ? iconColor : UIConstants.PRIMARY_BLUE);
            iconBadge.add(lblIcon);

            topRow.add(iconBadge, BorderLayout.EAST);
        }

        add(topRow, BorderLayout.NORTH);

        // Center: Giá trị số lớn
        lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(new Color(15, 23, 42)); // Slate 900
        add(lblValue, BorderLayout.CENTER);

        // Bottom Row: Pill badge MoM hoặc Subtitle
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        bottomRow.setOpaque(false);

        badgePill = new RoundedPanel(12, isPositive ? new Color(236, 253, 245) : new Color(254, 242, 242),
                isPositive ? new Color(167, 243, 208) : new Color(254, 202, 202));
        badgePill.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));

        lblBadgeText = new JLabel(badgeText != null ? badgeText : "");
        lblBadgeText.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadgeText.setForeground(isPositive ? new Color(5, 150, 105) : new Color(220, 38, 38));
        badgePill.add(lblBadgeText);

        lblSubtitle = new JLabel("");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitle.setForeground(new Color(148, 163, 184));

        if (badgeText != null && !badgeText.trim().isEmpty()) {
            bottomRow.add(badgePill);
        }
        bottomRow.add(lblSubtitle);

        add(bottomRow, BorderLayout.SOUTH);
    }

    public KpiCard(String title, String value, String momText, boolean isPositive) {
        this(title, value, momText, isPositive, "📊", new Color(238, 242, 255), UIConstants.PRIMARY_BLUE);
    }

    public void setValue(String value) {
        lblValue.setText(value);
    }

    public void setTitle(String title) {
        lblTitle.setText(title);
    }

    public void setSubtitle(String subtitle) {
        lblSubtitle.setText(subtitle);
    }

    public void setBadge(String badgeText, boolean isPositive) {
        if (badgeText != null && !badgeText.trim().isEmpty()) {
            lblBadgeText.setText(badgeText);
            lblBadgeText.setForeground(isPositive ? new Color(5, 150, 105) : new Color(220, 38, 38));
            badgePill.setBackground(isPositive ? new Color(236, 253, 245) : new Color(254, 242, 242));
            badgePill.setVisible(true);
        } else {
            badgePill.setVisible(false);
        }
    }
}
