package com.restaurant.service;

import com.restaurant.model.CaLamViec;

import java.util.List;

/**
 * Interface định nghĩa các nghiệp vụ quản lý ca làm việc, đối soát két tiền mặt và bàn giao ca
 */
public interface IShiftService {

    /**
     * Lấy thông tin ca làm việc đang mở gần nhất
     * @return Đối tượng CaLamViec đang hoạt động hoặc null nếu chưa mở ca
     */
    CaLamViec getCaDangMo();

    /**
     * Lấy thông tin ca làm việc đã đóng gần nhất (để lấy số tiền bàn giao cho ca tiếp theo)
     * @return Ca làm việc gần nhất đã hoàn tất
     */
    CaLamViec getCaTruocGanNhat();

    /**
     * Mở một ca làm việc mới
     * @param maNV Mã nhân viên bắt đầu ca
     * @param tienDauCa Số tiền mặt trong két nhận bàn giao
     * @param ghiChu Ghi chú khi mở ca
     * @return true nếu mở ca thành công
     */
    boolean moCa(int maNV, double tienDauCa, String ghiChu);

    /**
     * Bàn giao ca và đóng ca làm việc
     * @param ca Đối tượng ca làm việc chứa thông tin kiểm đếm tiền thực tế và giải trình
     * @return true nếu đóng ca thành công
     */
    boolean dongCa(CaLamViec ca);

    /**
     * Kiểm tra và tính toán độ chênh lệch giữa tiền thực tế kiểm đếm và tiền lý thuyết trong két
     * @param tienLyThuyet Tiền đầu ca + Doanh thu tiền mặt
     * @param tienThucTe Tiền mặt nhân viên thực tế đếm được trong két
     * @return Số tiền chênh lệch (dương: thừa tiền, âm: thiếu tiền, 0: khớp 100%)
     */
    double tinhChenhLechKetTien(double tienLyThuyet, double tienThucTe);

    /**
     * Lấy danh sách lịch sử toàn bộ các ca làm việc
     * @return Danh sách ca làm việc
     */
    List<CaLamViec> getLichSuCaLamViec();
}
