package com.restaurant;

import com.formdev.flatlaf.FlatLightLaf;
import com.restaurant.model.Ban;
import com.restaurant.model.HoaDon;
import com.restaurant.util.DummyDataFactory;
import com.restaurant.view.MainFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

public class MainFrameTest {

    @BeforeAll
    public static void setup() {
        FlatLightLaf.setup();
    }

    @Test
    public void testMainFrameInitializationAndCardSwitching() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> {
            MainFrame frame = new MainFrame();
            assertNotNull(frame);
            assertEquals("RESTAURANT OPS - Hệ Thống Quản Lý Vận Hành Nhà Hàng", frame.getTitle());

            // Test navigation to all cards
            frame.showCard(MainFrame.CARD_DASHBOARD);
            assertNotNull(frame.getDashboardPanel());

            frame.showCard(MainFrame.CARD_TABLE_MAP);
            assertNotNull(frame.getTableMapPanel());

            frame.showCard(MainFrame.CARD_ORDER_DETAILS);
            assertNotNull(frame.getOrderPanel());

            frame.showCard(MainFrame.CARD_CHECKOUT);
            assertNotNull(frame.getCheckoutPanel());

            frame.showCard(MainFrame.CARD_USERS);
            frame.showCard(MainFrame.CARD_MENU_ITEMS);
            frame.showCard(MainFrame.CARD_CATEGORIES);
            frame.showCard(MainFrame.CARD_TABLES);
            frame.showCard(MainFrame.CARD_VOUCHERS);
            frame.showCard(MainFrame.CARD_INVOICE_HISTORY);
            assertNotNull(frame.getInvoiceHistoryPanel());
            frame.showCard(MainFrame.CARD_REVENUE_REPORT);
            assertNotNull(frame.getRevenueReportPanel());
            frame.showCard(MainFrame.CARD_TOP_DISHES);
            assertNotNull(frame.getTopDishesReportPanel());

            // Test routing to specific table
            frame.navigateToOrder(4);
            frame.navigateToCheckout(4);

            frame.dispose();
        });
    }

    @Test
    public void testDummyDataOperations() {
        DummyDataFactory factory = DummyDataFactory.getInstance();
        assertNotNull(factory.getDanhSachBan());
        assertEquals(20, factory.getDanhSachBan().size());

        assertNotNull(factory.getDanhSachDanhMuc());
        assertTrue(factory.getDanhSachDanhMuc().size() >= 5);

        assertNotNull(factory.getDanhSachMonAn());
        assertTrue(factory.getDanhSachMonAn().size() >= 10);

        // Test table opening
        factory.thanhToanBan(1, "Tiền mặt", 0, 8);
        Ban table1 = factory.timBanTheoMa(1);
        assertNotNull(table1);
        assertEquals("Trong", table1.getTrangThai());

        HoaDon hd1 = factory.moBanMoi(1);
        assertNotNull(hd1);
        assertEquals("CoKhach", table1.getTrangThai());

        // Test checkout
        factory.thanhToanBan(1, "Tiền mặt", 0, 8);
        assertEquals("Trong", table1.getTrangThai());
    }
}
