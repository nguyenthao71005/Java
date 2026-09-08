package com.restaurant.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Biểu đồ hình phễu "Quy trình Bàn & Gọi món" (Funnel Pipeline)
 * Thiết kế đồ họa hiện đại:
 * - Gradient đa sắc (Deep Navy -> Royal Blue -> Ocean Blue -> Sky Blue -> Emerald Teal)
 * - Tỷ lệ phần trăm chuyển đổi trực quan
 * - Hiệu ứng Hover tương tác làm nổi bật tầng đang trỏ chuột
 * - Nhận dữ liệu động thời gian thực từ CSDL
 */
public class FunnelChartPanel extends JPanel {

    public static class FunnelStage {
        public String name;
        public int value;
        public Color colorStart;
        public Color colorEnd;

        public FunnelStage(String name, int value, Color colorStart, Color colorEnd) {
            this.name = name;
            this.value = value;
            this.colorStart = colorStart;
            this.colorEnd = colorEnd;
        }
    }

    private final FunnelStage[] stages = {
            new FunnelStage("Tổng số bàn", 20, new Color(15, 23, 42), new Color(30, 41, 59)),
            new FunnelStage("Bàn có khách", 6, new Color(29, 78, 216), new Color(37, 99, 235)),
            new FunnelStage("Đã gọi món", 5, new Color(2, 132, 199), new Color(14, 165, 233)),
            new FunnelStage("Đang chế biến", 8, new Color(14, 116, 144), new Color(6, 182, 212)),
            new FunnelStage("Đã ra món / Phục vụ", 4, new Color(5, 150, 105), new Color(16, 185, 129))
    };

    private int hoveredIndex = -1;

    public FunnelChartPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(340, 220));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = getStageAt(e.getY());
                if (index != hoveredIndex) {
                    hoveredIndex = index;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredIndex = -1;
                repaint();
            }
        });
    }

    /**
     * Cập nhật dữ liệu quy trình thời gian thực
     */
    public void updateData(int totalTables, int occupiedTables, int orderedTables, int cookingDishes, int servedDishes) {
        stages[0].value = Math.max(1, totalTables);
        stages[1].value = occupiedTables;
        stages[2].value = orderedTables;
        stages[3].value = cookingDishes;
        stages[4].value = servedDishes;
        repaint();
    }

    private int getStageAt(int mouseY) {
        int topPadding = 12;
        int bottomPadding = 14;
        int availableH = getHeight() - topPadding - bottomPadding;
        if (availableH <= 0) return -1;
        int stageH = availableH / stages.length;

        int index = (mouseY - topPadding) / stageH;
        if (index >= 0 && index < stages.length) {
            return index;
        }
        return -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int topPadding = 12;
        int bottomPadding = 14;
        int availableH = h - topPadding - bottomPadding;
        int stageH = availableH / stages.length;

        int maxWidth = (int) (w * 0.88);
        int minWidth = (int) (w * 0.38);
        int centerX = w / 2;

        int totalBase = Math.max(1, stages[0].value);

        for (int i = 0; i < stages.length; i++) {
            double ratioTop = (double) i / stages.length;
            double ratioBottom = (double) (i + 1) / stages.length;

            int currentTopW = (int) (maxWidth - ratioTop * (maxWidth - minWidth));
            int currentBottomW = (int) (maxWidth - ratioBottom * (maxWidth - minWidth));

            int yTop = topPadding + i * stageH;
            int yBottom = yTop + stageH - 4; // khoảng cách 4px giữa các tầng phễu

            int[] xPoints = {
                    centerX - currentTopW / 2,
                    centerX + currentTopW / 2,
                    centerX + currentBottomW / 2,
                    centerX - currentBottomW / 2
            };
            int[] yPoints = {
                    yTop,
                    yTop,
                    yBottom,
                    yBottom
            };

            boolean isHovered = (i == hoveredIndex);

            // Tô màu Gradient cho từng tầng
            Color startColor = isHovered ? stages[i].colorStart.brighter() : stages[i].colorStart;
            Color endColor = isHovered ? stages[i].colorEnd.brighter() : stages[i].colorEnd;

            GradientPaint gp = new GradientPaint(
                    centerX - currentTopW / 2, yTop, startColor,
                    centerX + currentTopW / 2, yBottom, endColor
            );
            g2.setPaint(gp);
            g2.fillPolygon(xPoints, yPoints, 4);

            // Viền phát sáng nhẹ hoặc viền hover nổi bật
            if (isHovered) {
                g2.setColor(new Color(255, 255, 255, 220));
                g2.setStroke(new BasicStroke(2.0f));
            } else {
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1.2f));
            }
            g2.drawPolygon(xPoints, yPoints, 4);

            // Nhãn chữ bên trong tầng phễu
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, isHovered ? 12 : 11));
            FontMetrics fm = g2.getFontMetrics();

            // Tính % chuyển đổi
            int pct = (int) Math.round((stages[i].value * 100.0) / totalBase);
            String label = stages[i].name + ": " + stages[i].value;
            if (i > 0 && i < 3) {
                label += " (" + pct + "%)";
            } else if (i >= 3) {
                label = stages[i].name + ": " + stages[i].value + " món";
            }

            int textW = fm.stringWidth(label);
            // Nếu chữ quá dài so với đáy phễu thì rút gọn
            if (textW > currentBottomW - 10) {
                label = stages[i].value + (i >= 3 ? " món" : " bàn");
                textW = fm.stringWidth(label);
            }

            int textX = centerX - textW / 2;
            int textY = yTop + (stageH / 2) + (fm.getAscent() / 2) - 2;

            g2.drawString(label, textX, textY);
        }

        g2.dispose();
    }
}
