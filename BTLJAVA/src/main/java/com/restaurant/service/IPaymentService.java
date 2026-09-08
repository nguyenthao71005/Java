package com.restaurant.service;

import com.restaurant.model.HoaDon;
import com.restaurant.model.Voucher;

import java.util.Map;

/**
 * Interface định nghĩa các nghiệp vụ tính toán chi phí, chiết khấu khuyến mãi và thanh toán hóa đơn
 */
public interface IPaymentService {

    /**
     * Xác thực mã Voucher và tính toán số tiền giảm giá hợp lệ
     * @param code Mã khuyến mãi người dùng nhập
     * @param tongTienGoc Tổng tiền gốc trước chiết khấu
     * @return Đối tượng Voucher hợp lệ hoặc null nếu không hợp lệ
     */
    Voucher xacThucVaApDungVoucher(String code, double tongTienGoc);

    /**
     * Tính toán tổng quan tài chính cho hóa đơn (Tiền món, Tiền giảm, Thuế VAT, Tổng thanh toán)
     * @param hoaDon Đối tượng hóa đơn
     * @param voucher Voucher áp dụng (nếu có)
     * @param vatPercent Tỷ lệ thuế VAT (%)
     * @return Map chứa các thông số: "TienMon", "TienGiam", "TienVAT", "TongThanhToan"
     */
    Map<String, Double> tinhToanChiPhiHoaDon(HoaDon hoaDon, Voucher voucher, double vatPercent);

    /**
     * Thực hiện chốt thanh toán hóa đơn của bàn
     * @param maBan Mã bàn thanh toán
     * @param phuongThuc Hình thức thanh toán ("Tiền mặt", "Chuyển khoản QR", "Thẻ POS")
     * @param tienVoucher Số tiền giảm giá từ khuyến mãi
     * @param vatPercent Tỷ lệ thuế VAT áp dụng
     * @return true nếu thanh toán và cập nhật doanh thu ca thành công
     */
    boolean thanhToanHoaDon(int maBan, String phuongThuc, double tienVoucher, double vatPercent);

    /**
     * Tách một phần món ăn của bàn thành hóa đơn phụ độc lập để thanh toán riêng
     * @param maBan Mã bàn gốc
     * @param dsMaCTHD Danh sách mã món ăn được tách
     * @return Mã hóa đơn mới được tách
     */
    int tachHoaDon(int maBan, java.util.List<Integer> dsMaCTHD);
}
