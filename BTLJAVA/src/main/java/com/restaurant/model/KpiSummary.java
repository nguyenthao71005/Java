package com.restaurant.model;

/**
 * Model chứa dữ liệu tổng hợp cho màn hình Dashboard
 * Thường được tính toán qua các câu truy vấn tổng hợp (SUM, COUNT, AVG) từ CSDL
 */
public class KpiSummary {
    private double totalRevenue;        // Doanh thu (VND)
    private double revenueMomPercent;    // Tăng trưởng MoM (%)
    private int totalOrders;             // Số lượng đơn hàng
    private double ordersMomPercent;     // Tăng trưởng số đơn MoM (%)
    private double occupancyRate;        // Tỷ lệ lấp đầy bàn (%)
    private int avgTableTurnMinutes;     // Thời gian quay vòng bàn trung bình (phút)

    public KpiSummary() {
        this.totalRevenue = 450500000;
        this.revenueMomPercent = 12.4;
        this.totalOrders = 312;
        this.ordersMomPercent = 3.2;
        this.occupancyRate = 65.4;
        this.avgTableTurnMinutes = 78;
    }

    public KpiSummary(double totalRevenue, double revenueMomPercent, int totalOrders, double ordersMomPercent, double occupancyRate, int avgTableTurnMinutes) {
        this.totalRevenue = totalRevenue;
        this.revenueMomPercent = revenueMomPercent;
        this.totalOrders = totalOrders;
        this.ordersMomPercent = ordersMomPercent;
        this.occupancyRate = occupancyRate;
        this.avgTableTurnMinutes = avgTableTurnMinutes;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public double getRevenueMomPercent() {
        return revenueMomPercent;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public double getOrdersMomPercent() {
        return ordersMomPercent;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public int getAvgTableTurnMinutes() {
        return avgTableTurnMinutes;
    }
}
