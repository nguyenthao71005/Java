package com.restaurant;

import com.formdev.flatlaf.FlatLightLaf;
import com.restaurant.view.MainFrame;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CaptureUiScreenshotsTest {

    @Test
    public void captureScreenshots() throws Exception {
        FlatLightLaf.setup();

        SwingUtilities.invokeAndWait(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setSize(1366, 768);
                frame.addNotify();
                frame.validate();

                File outputDir = new File("C:\\Users\\PC\\.gemini\\antigravity-ide\\brain\\08fe2abe-31e2-4cd8-b9f9-feb54d3ac262");
                outputDir.mkdirs();

                // 1. Capture Dashboard
                frame.showCard(MainFrame.CARD_DASHBOARD);
                frame.validate();
                renderAndSave(frame.getContentPane(), new File(outputDir, "ui_dashboard.png"));

                // 2. Capture Table Map
                frame.showCard(MainFrame.CARD_TABLE_MAP);
                frame.validate();
                renderAndSave(frame.getContentPane(), new File(outputDir, "ui_table_map.png"));

                // 3. Capture Order Details
                frame.showCard(MainFrame.CARD_ORDER_DETAILS);
                frame.validate();
                renderAndSave(frame.getContentPane(), new File(outputDir, "ui_order_details.png"));

                // 4. Capture Checkout
                frame.showCard(MainFrame.CARD_CHECKOUT);
                frame.validate();
                renderAndSave(frame.getContentPane(), new File(outputDir, "ui_checkout.png"));

                // 5. Capture Users Management
                frame.showCard(MainFrame.CARD_USERS);
                frame.validate();
                renderAndSave(frame.getContentPane(), new File(outputDir, "ui_users.png"));

                // 6. Capture Menu Items Management
                frame.showCard(MainFrame.CARD_MENU_ITEMS);
                frame.validate();
                renderAndSave(frame.getContentPane(), new File(outputDir, "ui_menu_items.png"));

                // 7. Capture Vouchers Management
                frame.showCard(MainFrame.CARD_VOUCHERS);
                frame.validate();
                renderAndSave(frame.getContentPane(), new File(outputDir, "ui_vouchers.png"));

                // 8. Capture Invoice History
                frame.showCard(MainFrame.CARD_INVOICE_HISTORY);
                frame.validate();
                renderAndSave(frame.getContentPane(), new File(outputDir, "ui_invoice_history.png"));

                // 9. Capture Revenue Report
                frame.showCard(MainFrame.CARD_REVENUE_REPORT);
                frame.validate();
                renderAndSave(frame.getContentPane(), new File(outputDir, "ui_revenue_report.png"));

                // 10. Capture Top Dishes Report
                frame.showCard(MainFrame.CARD_TOP_DISHES);
                frame.validate();
                renderAndSave(frame.getContentPane(), new File(outputDir, "ui_top_dishes.png"));

                // 11. Capture Kitchen Management
                frame.showCard(MainFrame.CARD_KITCHEN);
                frame.validate();
                renderAndSave(frame.getContentPane(), new File(outputDir, "ui_kitchen_management.png"));

                // 12. Capture Shift Management
                frame.showCard(MainFrame.CARD_SHIFTS);
                frame.validate();
                renderAndSave(frame.getContentPane(), new File(outputDir, "ui_shift_management.png"));

                // 13. Capture Login Dialog
                com.restaurant.view.auth.LoginDialog login = new com.restaurant.view.auth.LoginDialog(null);
                login.addNotify();
                login.validate();
                renderAndSave(login.getContentPane(), new File(outputDir, "ui_login.png"));
                login.dispose();

                // 14. Capture Open Shift Dialog
                com.restaurant.view.shift.OpenShiftDialog openShift = new com.restaurant.view.shift.OpenShiftDialog(null, null);
                openShift.addNotify();
                openShift.validate();
                renderAndSave(openShift.getContentPane(), new File(outputDir, "ui_open_shift_dialog.png"));
                openShift.dispose();

                // 15. Capture Close Shift Dialog
                com.restaurant.model.CaLamViec sampleShift = new com.restaurant.dao.CaLamViecDAO().getCaDangMo();
                if (sampleShift == null) sampleShift = com.restaurant.util.DummyDataFactory.getInstance().getCaHienTai();
                com.restaurant.view.shift.CloseShiftDialog closeShift = new com.restaurant.view.shift.CloseShiftDialog(null, sampleShift);
                closeShift.addNotify();
                closeShift.validate();
                renderAndSave(closeShift.getContentPane(), new File(outputDir, "ui_close_shift_dialog.png"));
                closeShift.dispose();

                frame.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void renderAndSave(Container container, File file) throws Exception {
        int w = container.getWidth() > 0 ? container.getWidth() : 1366;
        int h = container.getHeight() > 0 ? container.getHeight() : 768;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        container.printAll(g2);
        g2.dispose();
        ImageIO.write(img, "png", file);
    }
}
