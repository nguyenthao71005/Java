package com.restaurant.view.components;

import com.restaurant.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Biểu đồ kết hợp Cột Doanh Thu & Đường Mục Tiêu (Revenue & Growth Bar-Line Chart)
 * Thiết kế đồ họa UI/UX cao cấp:
 * - Cột Gradient mềm mại (Vibrant Royal Blue sang Deep Indigo) với đỉnh bo tròn
 * - Đường cong Spline Bezier mượt mà với điểm neo phát sáng (Glow nodes)
 * - Vùng diện tích mờ trong suốt (Glassmorphism Area Gradient Fill)
 * - Tooltip tương tác thông minh khi rê chuột hiển thị chính xác số tiền & % hoàn thành
 * - Nhận dữ liệu động thời gian thực từ CSDL MySQL
 */
public class RevenueBarLineChartPanel extends JPanel {

    private String[] labels = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6"};
    private double[] revenues = {14.2, 22.5, 21.8, 18.6, 24.3, 31.5}; // Triệu VND
    private double[] targets = {18.0, 20.0, 22.0, 20.0, 25.0, 32.0};  // Triệu VND

    private int hoveredCol = -1;
    private Point mousePoint = null;

    public RevenueBarLineChartPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(460, 220));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
                int col = getColumnAt(e.getX());
                if (col != hoveredCol) {
                    hoveredCol = col;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredCol = -1;
                mousePoint = null;
                repaint();
            }
        });
    }

    /**
     * Cập nhật dữ liệu biểu đồ từ CSDL
     */
    public void updateChartData(String[] newLabels, double[] newRevenues, double[] newTargets) {
        if (newLabels != null && newRevenues != null && newTargets != null && newLabels.length > 0) {
            this.labels = newLabels;
            this.revenues = newRevenues;
            this.targets = newTargets;
            repaint();
        }
    }

    private int getColumnAt(int mouseX) {
        int padLeft = 50;
        int padRight = 30;
        int chartW = getWidth() - padLeft - padRight;
        if (chartW <= 0 || labels.length == 0) return -1;

        int slotW = chartW / labels.length;
        int relX = mouseX - padLeft;
        if (relX >= 0 && relX < chartW) {
            return relX / slotW;
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

        // 1. Chú thích Legend trên đầu biểu đồ
        drawLegend(g2, w);

        int padLeft = 46;
        int padRight = 36;
        int padTop = 32;
        int padBottom = 32;

        int chartW = w - padLeft - padRight;
        int chartH = h - padTop - padBottom;

        if (chartW <= 0 || chartH <= 0 || labels.length == 0) {
            g2.dispose();
            return;
        }

        // Tìm giá trị max để tự động căn chỉnh tỷ lệ trục Y
        double maxDataVal = 10.0;
        for (double r : revenues) if (r > maxDataVal) maxDataVal = r;
        for (double t : targets) if (t > maxDataVal) maxDataVal = t;
        double maxVal = Math.ceil((maxDataVal * 1.2) / 10.0) * 10.0; // làm tròn lên bội số 10
        if (maxVal <= 0) maxVal = 50.0;

        // 2. Vẽ đường lưới ngang nét đứt (Dashed Gridlines) & Trục Y
        int gridCount = 4;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();

        Stroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f, 4.0f}, 0.0f);
        Stroke solidStroke = new BasicStroke(1.0f);

        for (int i = 0; i < gridCount; i++) {
            int y = padTop + chartH - (i * chartH / (gridCount - 1));
            double val = (maxVal * i) / (gridCount - 1);

            // Đường dóng ngang nét đứt
            g2.setStroke(dashedStroke);
            g2.setColor(new Color(230, 236, 243));
            g2.drawLine(padLeft, y, padLeft + chartW, y);

            // Nhãn trục trái (Doanh thu)
            g2.setStroke(solidStroke);
            g2.setColor(new Color(148, 163, 184));
            String lblL = String.format("%.0f tr", val);
            g2.drawString(lblL, padLeft - fm.stringWidth(lblL) - 6, y + 4);
        }

        // Đường chân trục hoành
        g2.setColor(new Color(203, 213, 225));
        g2.drawLine(padLeft, padTop + chartH, padLeft + chartW, padTop + chartH);

        int colCount = labels.length;
        int slotW = chartW / colCount;
        int barW = Math.max(18, (int) (slotW * 0.44));

        int[] lineX = new int[colCount];
        int[] lineY = new int[colCount];

        // 3. Vẽ các cột Doanh Thu (Rounded Gradient Bars)
        for (int i = 0; i < colCount; i++) {
            int centerX = padLeft + i * slotW + slotW / 2;
            int barHeight = (int) ((revenues[i] / maxVal) * chartH);
            int barX = centerX - barW / 2;
            int barY = padTop + chartH - barHeight;

            boolean isHovered = (i == hoveredCol);

            // Tô màu Gradient chuyển từ Royal Blue (#2563eb) sang Navy Indigo (#1e3a8a)
            Color topColor = isHovered ? new Color(59, 130, 246) : new Color(37, 99, 235);
            Color btmColor = isHovered ? new Color(30, 64, 175) : new Color(15, 23, 42);

            GradientPaint gp = new GradientPaint(barX, barY, topColor, barX, padTop + chartH, btmColor);
            g2.setPaint(gp);

            // Cột có đỉnh bo tròn 8px
            RoundRectangle2D roundBar = new RoundRectangle2D.Double(barX, barY, barW, barHeight + 6, 8, 8);
            Shape oldClip = g2.getClip();
            g2.clipRect(barX, barY, barW, barHeight);
            g2.fill(roundBar);
            g2.setClip(oldClip);

            // Nhãn số tiền trên đỉnh cột
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(isHovered ? UIConstants.PRIMARY_BLUE : new Color(71, 85, 105));
            String valStr = String.format("%.1ftr", revenues[i]);
            int valW = g2.getFontMetrics().stringWidth(valStr);
            g2.drawString(valStr, centerX - valW / 2, Math.max(padTop - 2, barY - 4));

            // Nhãn trục hoành (Tháng / Ngày)
            g2.setFont(new Font("Segoe UI", isHovered ? Font.BOLD : Font.PLAIN, 11));
            g2.setColor(isHovered ? new Color(15, 23, 42) : new Color(100, 116, 139));
            String colLabel = labels[i];
            int lblW = g2.getFontMetrics().stringWidth(colLabel);
            g2.drawString(colLabel, centerX - lblW / 2, padTop + chartH + 18);

            // Ghi nhớ tọa độ đường Target
            lineX[i] = centerX;
            lineY[i] = padTop + chartH - (int) ((targets[i] / maxVal) * chartH);
        }

        // 4. Vẽ đường cong Spline & Vùng diện tích mờ (Glassmorphism Area Fill)
        if (colCount > 1) {
            Path2D.Double curvePath = new Path2D.Double();
            curvePath.moveTo(lineX[0], lineY[0]);

            for (int i = 0; i < colCount - 1; i++) {
                double x1 = lineX[i];
                double y1 = lineY[i];
                double x2 = lineX[i + 1];
                double y2 = lineY[i + 1];

                double ctrlX1 = x1 + (x2 - x1) * 0.5;
                double ctrlY1 = y1;
                double ctrlX2 = x1 + (x2 - x1) * 0.5;
                double ctrlY2 = y2;

                curvePath.curveTo(ctrlX1, ctrlY1, ctrlX2, ctrlY2, x2, y2);
            }

            // Tạo vùng diện tích mờ (Area gradient fill)
            Path2D.Double areaPath = (Path2D.Double) curvePath.clone();
            areaPath.lineTo(lineX[colCount - 1], padTop + chartH);
            areaPath.lineTo(lineX[0], padTop + chartH);
            areaPath.closePath();

            GradientPaint areaGp = new GradientPaint(
                    0, padTop, new Color(6, 182, 212, 60), // Cyan 25% alpha
                    0, padTop + chartH, new Color(6, 182, 212, 0)
            );
            g2.setPaint(areaGp);
            g2.fill(areaPath);

            // Vẽ đường Line nổi bật
            g2.setColor(new Color(6, 182, 212)); // Cyan rực rỡ
            g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(curvePath);

            // Vẽ các điểm neo phát sáng (Glow nodes)
            for (int i = 0; i < colCount; i++) {
                int cx = lineX[i];
                int cy = lineY[i];

                // Vòng hào quang ngoài (Halo)
                g2.setColor(new Color(6, 182, 212, 80));
                g2.fillOval(cx - 7, cy - 7, 14, 14);

                // Vòng viền chính
                g2.setColor(new Color(6, 182, 212));
                g2.fillOval(cx - 4, cy - 4, 8, 8);

                // Chấm trắng ở tâm
                g2.setColor(Color.WHITE);
                g2.fillOval(cx - 2, cy - 2, 4, 4);
            }
        }

        // 5. Tooltip tương tác hiển thị khi Hover chuột
        if (hoveredCol >= 0 && hoveredCol < colCount) {
            drawTooltip(g2, hoveredCol, lineX[hoveredCol], lineY[hoveredCol], padTop + chartH);
        }

        g2.dispose();
    }

    private void drawLegend(Graphics2D g2, int w) {
        int legendY = 14;
        int rightMargin = 20;

        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();

        String item1 = "Doanh thu thực tế";
        String item2 = "Mục tiêu kinh doanh";

        int w2 = fm.stringWidth(item2) + 16;
        int w1 = fm.stringWidth(item1) + 16;

        int x2 = w - rightMargin - w2;
        int x1 = x2 - w1 - 16;

        // Mục 1: Doanh thu (Square/Bar)
        g2.setColor(new Color(37, 99, 235));
        g2.fillRoundRect(x1, legendY - 8, 10, 10, 3, 3);
        g2.setColor(new Color(71, 85, 105));
        g2.drawString(item1, x1 + 14, legendY);

        // Mục 2: Mục tiêu (Circle/Line)
        g2.setColor(new Color(6, 182, 212));
        g2.fillOval(x2, legendY - 8, 10, 10);
        g2.setColor(new Color(71, 85, 105));
        g2.drawString(item2, x2 + 14, legendY);
    }

    private void drawTooltip(Graphics2D g2, int col, int nodeX, int nodeY, int bottomY) {
        String title = labels[col];
        String revText = String.format("Doanh thu: %,.1f tr VND", revenues[col]);
        double target = targets[col];
        double pct = target > 0 ? (revenues[col] * 100.0 / target) : 100.0;
        String targText = String.format("Mục tiêu: %,.1f tr (%d%%)", target, (int) Math.round(pct));

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        int maxW = Math.max(fm.stringWidth(title), Math.max(fm.stringWidth(revText), fm.stringWidth(targText)));

        int tipW = maxW + 20;
        int tipH = 56;
        int tipX = nodeX - tipW / 2;
        int tipY = Math.max(10, nodeY - tipH - 12);

        // Đảm bảo không tràn lề
        if (tipX < 10) tipX = 10;
        if (tipX + tipW > getWidth() - 10) tipX = getWidth() - tipW - 10;

        // Khung nền đen Glassmorphism bo tròn
        g2.setColor(new Color(15, 23, 42, 235));
        g2.fillRoundRect(tipX, tipY, tipW, tipH, 8, 8);
        g2.setColor(new Color(255, 255, 255, 50));
        g2.drawRoundRect(tipX, tipY, tipW, tipH, 8, 8);

        // Chữ trong Tooltip
        g2.setColor(new Color(148, 163, 184));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.drawString(title, tipX + 10, tipY + 16);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.drawString(revText, tipX + 10, tipY + 32);

        g2.setColor(new Color(6, 182, 212));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.drawString(targText, tipX + 10, tipY + 46);
    }
}
