package com.restaurant;

import com.restaurant.dao.CaLamViecDAO;
import com.restaurant.dao.KitchenDAO;
import com.restaurant.database.DatabaseConnection;
import com.restaurant.model.CaLamViec;
import com.restaurant.model.KitchenOrder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KitchenAndShiftTest {

    @Test
    public void testShiftOperations() {
        if (!DatabaseConnection.isConnected()) {
            System.out.println("MySQL Offline, skipping shift DB test");
            return;
        }

        CaLamViecDAO dao = new CaLamViecDAO();

        // 1. Kiểm tra lấy ca đang mở
        CaLamViec activeCa = dao.getCaDangMo();
        if (activeCa == null) {
            CaLamViec newCa = new CaLamViec(1, "Quản lý Hệ Thống", 2000000.0, "Ca làm việc tự động");
            dao.moCa(newCa);
            activeCa = dao.getCaDangMo();
        }
        assertNotNull(activeCa, "Phải có ca làm việc đang mở từ seed data hoặc vừa tạo");
        assertTrue(activeCa.isDangMo(), "Trạng thái ca phải là DangMo");
        assertTrue(activeCa.getTienDauCa() > 0, "Tiền đầu ca phải > 0");

        // 2. Kiểm tra lấy danh sách tất cả các ca
        List<CaLamViec> allShifts = dao.getAll();
        assertFalse(allShifts.isEmpty(), "Danh sách ca làm việc không được rỗng");

        // 3. Kiểm tra tính tiền lý thuyết
        activeCa.tinhTienLyThuyet();
        assertEquals(activeCa.getTienDauCa() + activeCa.getDoanhThuTienMat(), activeCa.getTienLyThuyet());

        // 4. Kiểm tra lấy danh sách hóa đơn theo ca
        List<?> invoices = dao.getHoaDonByMaCa(activeCa.getMaCa());
        assertNotNull(invoices);
    }

    @Test
    public void testKitchenOperations() {
        if (!DatabaseConnection.isConnected()) {
            System.out.println("MySQL Offline, skipping kitchen DB test");
            return;
        }

        KitchenDAO dao = new KitchenDAO();

        // 1. Lấy danh sách order bếp
        List<KitchenOrder> orders = dao.getKitchenOrders(null, null, null);
        assertNotNull(orders, "Danh sách order bếp không được null");
        assertFalse(orders.isEmpty(), "Seed data phải có các món đang gọi từ các bàn");

        // 2. Kiểm tra các thông tin bàn, món, số lượng
        KitchenOrder first = orders.get(0);
        assertNotNull(first.getTenMon(), "Tên món không được null");
        assertNotNull(first.getTenBan(), "Tên bàn không được null");
        assertTrue(first.getSoLuong() > 0, "Số lượng phải > 0");

        // 3. Kiểm tra lọc trạng thái
        List<KitchenOrder> cookingOrders = dao.getKitchenOrders("DangLam", null, null);
        for (KitchenOrder o : cookingOrders) {
            assertEquals("DangLam", o.getTrangThaiMon());
        }

        // 4. Kiểm tra cập nhật trạng thái món
        boolean updated = dao.updateItemStatus(first.getMaCTHD(), "DangLam");
        assertTrue(updated, "Cập nhật trạng thái món phải thành công");

        // 5. Kiểm tra cập nhật tình trạng món ăn (Còn món / Hết hàng)
        boolean dishStatusUpdated = dao.updateDishAvailability(101, true);
        assertTrue(dishStatusUpdated, "Cập nhật tình trạng món ăn phải thành công");
    }
}
