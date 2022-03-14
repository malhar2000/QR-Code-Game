//package com.example.qrcodegame;
//
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//import android.app.Application;
//import android.app.Instrumentation;
//
//import androidx.test.platform.app.InstrumentationRegistry;
//
//import com.example.qrcodegame.controllers.QRCodeController;
//
//public class QRCodeTest {
//
//
//
//    // Test to check hashing properly
//    @Test
//    public void hashing_correct() {
//        QRCodeController qrCodeController = new QRCodeController(InstrumentationRegistry.getInstrumentation().getTargetContext(), null, null);
//        qrCodeController.initNewCode();
//        qrCodeController.processHash("HelloWorld");
//        String hashed = "872e4e50ce9990d8b041330c47c9ddd11bec6b503ae9386a99da8584e9bb12c4";
//        assertEquals(qrCodeController.getCurrentQrCode().getId(), hashed);
//    }
//
//    // Test to see if switch profile detected
//
//    // Test to see if view profile detected
//
//    // Test to save code without image and location
//
//
//}
