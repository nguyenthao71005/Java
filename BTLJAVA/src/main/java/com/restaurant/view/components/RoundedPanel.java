package com.restaurant.view.components;

import javax.swing.*;
import java.awt.*;

/**
 * Panel có góc bo tròn hiện đại và viền nhẹ tinh tế
 */
public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;
    private boolean drawBorder;

    public RoundedPanel(int radius) {
        this(radius, Color.WHITE, new Color(226, 232, 240));
    }

    public RoundedPanel(int radius, Color bgColor) {
        this(radius, bgColor, null);
    }

    public RoundedPanel(int radius, Color bgColor, Color borderColor) {
        super();
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        this.borderColor = borderColor;
        this.drawBorder = (borderColor != null);
        setOpaque(false);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Vẽ nền bo góc
        g2.setColor(backgroundColor != null ? backgroundColor : getBackground());
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Vẽ viền nếu có
        if (drawBorder && borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
    }
}
