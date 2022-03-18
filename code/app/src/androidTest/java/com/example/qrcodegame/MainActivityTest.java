package com.example.qrcodegame;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

    /**
     * This makes sure that the main activity launches
     */
    @Test
    public void mainActivityLaunches() {
    }

    /**
     * This method tests whether or not we can open QR code scanner
     */
    @Test
    public void successfulQrCodeScannerOpen() {
    }

    /**
     * This method test whether or not we can save a QR code with geolocation
     */
    @Test
    public void successfulQRCodeWithGeolocation(){

    }

}