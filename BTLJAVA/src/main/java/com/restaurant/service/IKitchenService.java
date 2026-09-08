package com.restaurant.service;

import com.restaurant.model.ChiTietHoaDon;
import com.restaurant.model.MonAn;

import java.util.List;

/**
 * Interface định nghĩa các nghiệp vụ điều hành bếp & bar, cập nhật trạng thái chế biến và kho món ăn
 */
public interface IKitchenService {

    /**
     * Lấy danh sách toàn bộ các món ăn đang cần xử lý tại bếp (Chờ nấu, Đang nấu, Đã ra món)
     * @return Danh sách chi tiết món ăn
     */
    List<ChiTietHoaDon> getDanhSachMonChoBep();

    /**
     * Chuyển trạng thái danh sách món được chọn sang "Đang nấu" (DangLam)
     * @param dsMaCTHD Danh sách mã chi tiết hóa đơn
     * @return Số lượng món được cập nhật thành công
     */
    int chuyenTrangThaiDangNau(List<Integer> dsMaCTHD);

    /**
     * Chuyển trạng thái danh sách món sang "Đã ra món" (DaRaMon)
     * @param dsMaCTHD Danh sách mã chi tiết hóa đơn
     * @return Số lượng món hoàn thành
     */
    int chuyenTrangThaiDaRaMon(List<Integer> dsMaCTHD);

    /**
     * Bếp báo hủy món ăn do hết nguyên liệu hoặc sự cố kỹ thuật
     * @param maCTHD Mã món ăn cần hủy
     * @param lyDo Lý do bếp báo hủy
     * @return true nếu hủy thành công
     */
    boolean baoHuyMonBep(int maCTHD, String lyDo);

    /**
     * Cập nhật tình trạng kho của món ăn (Còn món hoặc Hết hàng)
     * @param maMon Mã món ăn
     * @param conMon true nếu còn hàng phục vụ, false nếu tạm hết hàng
     * @return true nếu cập nhật thành công
     */
    boolean capNhatTinhTrangMon(int maMon, boolean conMon);

    /**
     * Lấy danh sách toàn bộ thực đơn kèm trạng thái còn/hết món để bếp theo dõi
     * @return Danh sách món ăn
     */
    List<MonAn> getThucDonKemTrangThai();
}
