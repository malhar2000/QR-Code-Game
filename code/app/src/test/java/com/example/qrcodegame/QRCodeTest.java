package com.example.qrcodegame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import android.app.Application;
import android.app.Instrumentation;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.qrcodegame.controllers.QRCodeController;

public class QRCodeTest {

    QRCodeController qrCodeController = new QRCodeController(InstrumentationRegistry.getInstrumentation().getContext(), null, null);


    // Test to check hashing properly
    @Test
    public void hashing_correct() {
        String testStr = "HelloWorld";
        qrCodeController.initNewCode();

    }

    // Test to see if switch profile detected

    // Test to see if view profile detected

    // Test to save code without image and location


}
