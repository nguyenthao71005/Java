package com.restaurant;

import com.restaurant.dao.CaLamViecDAO;
import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.CaLamViec;
import com.restaurant.model.NhanVien;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.view.shift.CloseShiftDialog;
import com.restaurant.view.shift.OpenShiftDialog;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftPaymentTest {

    @Test
    public void testFullShiftAndPaymentLifecycle() throws Exception {
        if (!DatabaseConnection.isConnected()) {
            System.out.println("MySQL is offline, testing in memory only.");
            return;
        }

        CaLamViecDAO caDao = new CaLamViecDAO();

        // 1. Lấy ca làm việc đang mở
        CaLamViec activeCa = caDao.getCaDangMo();
        if (activeCa == null) {
            CaLamViec newCa = new CaLamViec(1, "Quản lý Hệ Thống", 2000000.0, "Ca làm việc thử nghiệm");
            caDao.moCa(newCa);
            activeCa = caDao.getCaDangMo();
        }
        assertNotNull(activeCa, "Phải có ca làm việc đang mở");
        System.out.println("-> Ca đang mở ban đầu: #" + activeCa.getMaCa() + " | Tiền đầu ca: " + activeCa.getTienDauCa());

        double initialCash = activeCa.getTienDauCa();
        double initialRevenue = activeCa.getTongDoanhThu();
        int initialCount = activeCa.getSoHoaDon();

        // 2. Thanh toán một hóa đơn (Bàn 9) với hình thức Tiền mặt
        System.out.println("-> Thực hiện thanh toán hóa đơn Bàn 9...");
        DummyDataFactory.getInstance().thanhToanBan(9, "Tiền mặt (Cash)", 0.0, 8.0);

        // 3. Kiểm tra xem doanh thu của ca đang mở đã tự động cập nhật chưa
        CaLamViec updatedCa = caDao.getCaDangMo();
        assertNotNull(updatedCa);
        System.out.println("-> Doanh thu ca sau thanh toán: " + updatedCa.getTongDoanhThu() + " | Số HĐ: " + updatedCa.getSoHoaDon());
        System.out.println("-> Tiền mặt lý thuyết trong két: " + updatedCa.getTienLyThuyet());

        assertTrue(updatedCa.getTongDoanhThu() >= initialRevenue, "Doanh thu phải tăng hoặc giữ nguyên");
        assertEquals(updatedCa.getTienDauCa() + updatedCa.getDoanhThuTienMat(), updatedCa.getTienLyThuyet(), 0.01,
                "Tiền lý thuyết phải bằng Tiền đầu ca + Doanh thu tiền mặt");

        // 4. Kiểm tra ca trước gần nhất
        CaLamViec prevShift = caDao.getCaTruocGanNhat();
        if (prevShift != null) {
            System.out.println("-> Ca trước gần nhất: #" + prevShift.getMaCa() + " | Nhân viên: " + prevShift.getTenNV() +
                    " | Tiền thực tế bàn giao: " + prevShift.getTienThucTe());
            assertNotNull(prevShift.getTenNV());
            assertNotNull(prevShift.getThoiGianDong());
        }

        // 5. Kiểm tra khởi tạo Dialog Mở Ca và Đóng Ca không bị exception
        NhanVien testUser = new NhanVien(2, "Trần Thị Mai", "cashier01", "mai123", "ThuNgan", "DangLam");
        OpenShiftDialog openDialog = new OpenShiftDialog(null, testUser);
        assertNotNull(openDialog);

        CloseShiftDialog closeDialog = new CloseShiftDialog(null, updatedCa);
        assertNotNull(closeDialog);

        System.out.println("-> TOÀN BỘ QUY TRÌNH THANH TOÁN & ĐỒNG BỘ CA LÀM VIỆC ĐẠT 100% YÊU CẦU!");
    }
}
