package com.restaurant.service;

import com.restaurant.model.ChiTietHoaDon;
import com.restaurant.model.HoaDon;

import java.util.List;

/**
 * Interface định nghĩa các nghiệp vụ gọi món, quản lý chi tiết hóa đơn và bàn ăn
 */
public interface IOrderService {

    /**
     * Mở bàn và khởi tạo hóa đơn mới cho bàn nếu chưa có
     * @param maBan Mã bàn cần mở
     * @param maNV Mã nhân viên phục vụ mở bàn
     * @return Đối tượng hóa đơn mới được tạo
     */
    HoaDon moBanVaTaoHoaDon(int maBan, int maNV);

    /**
     * Thêm món ăn vào hóa đơn của bàn
     * @param maBan Mã bàn
     * @param maMon Mã món ăn
     * @param soLuong Số lượng món
     * @param ghiChu Yêu cầu đặc biệt chế biến
     * @return true nếu thêm thành công
     */
    boolean themMonVaoBan(int maBan, int maMon, int soLuong, String ghiChu);

    /**
     * Cập nhật số lượng món đã gọi trong hóa đơn
     * @param maCTHD Mã chi tiết hóa đơn
     * @param soLuongMoi Số lượng mới cần cập nhật
     * @return true nếu cập nhật thành công
     */
    boolean capNhatSoLuongMon(int maCTHD, int soLuongMoi);

    /**
     * Hủy món ăn khỏi bàn kèm lý do giải trình
     * @param maCTHD Mã chi tiết hóa đơn cần hủy
     * @param lyDo Lý do hủy món (khách đổi ý, bếp hết nguyên liệu...)
     * @return true nếu hủy thành công
     */
    boolean huyMonKhoiBan(int maCTHD, String lyDo);

    /**
     * Chuyển toàn bộ order từ bàn cũ sang bàn mới
     * @param maBanCu Mã bàn nguồn
     * @param maBanMoi Mã bàn đích
     * @return true nếu chuyển bàn thành công
     */
    boolean chuyenBan(int maBanCu, int maBanMoi);

    /**
     * Lấy danh sách các món ăn trong hóa đơn hiện tại của bàn
     * @param maBan Mã bàn cần lấy order
     * @return Danh sách chi tiết món ăn
     */
    List<ChiTietHoaDon> getOrderCuaBan(int maBan);
}
