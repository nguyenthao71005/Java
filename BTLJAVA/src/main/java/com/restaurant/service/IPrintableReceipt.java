package com.restaurant.service;

import com.restaurant.model.CaLamViec;
import com.restaurant.model.HoaDon;

/**
 * Interface định nghĩa việc kết xuất nội dung in ấn phiếu thu K80, hóa đơn tạm tính và biên bản bàn giao ca
 */
public interface IPrintableReceipt {

    /**
     * Định dạng và tạo chuỗi văn bản hóa đơn thanh toán K80 chuẩn
     * @param hoaDon Hóa đơn cần in
     * @param tenNhaHang Tên nhà hàng / Chi nhánh
     * @param diaChi Địa chỉ nhà hàng
     * @param hotline Số hotline liên hệ
     * @return Chuỗi định dạng hóa đơn K80 có phân cách căn giữa, bảng món và tổng kết tài chính
     */
    String taoNoiDungHoaDonK80(HoaDon hoaDon, String tenNhaHang, String diaChi, String hotline);

    /**
     * Tạo biên bản bàn giao ca làm việc chi tiết
     * @param ca Đối tượng ca làm việc đã đóng
     * @return Văn bản biên bản bàn giao có thông số tiền két, chênh lệch và chữ ký xác nhận
     */
    String taoBienBanBanGiaoCa(CaLamViec ca);

    /**
     * Gửi lệnh in trực tiếp ra máy in hóa đơn nhiệt hoặc máy in hệ thống
     * @param noiDungIn Chuỗi văn bản cần in
     * @return true nếu gửi lệnh in thành công
     */
    boolean inRaMayIn(String noiDungIn);
}
