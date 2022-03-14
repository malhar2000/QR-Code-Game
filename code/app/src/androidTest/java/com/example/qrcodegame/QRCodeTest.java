package com.example.qrcodegame;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.qrcodegame.controllers.QRCodeController;

import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class QRCodeTest {

    @Test
    public void test_hashing_qr_code(){
        QRCodeController qrCodeController = new QRCodeController(InstrumentationRegistry.getInstrumentation().getTargetContext(), null, null);
        qrCodeController.initNewCode();
        qrCodeController.processHash("HelloWorld");
        String hashed = "872e4e50ce9990d8b041330c47c9ddd11bec6b503ae9386a99da8584e9bb12c4";
        assertEquals(qrCodeController.getCurrentQrCode().getId(), hashed);
    }

}
