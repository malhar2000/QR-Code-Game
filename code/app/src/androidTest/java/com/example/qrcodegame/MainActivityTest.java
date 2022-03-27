package com.example.qrcodegame;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static android.service.autofill.Validators.not;

import android.util.Log;

import com.example.qrcodegame.utils.CurrentUserHelper;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

    private CurrentUserHelper currentUserHelper;

    @Before
    public void setupCurrentUserHelper(){
        currentUserHelper = CurrentUserHelper.getInstance();
        currentUserHelper.setUsername("Shaishav");
        currentUserHelper.setPhone("7800000000");
        currentUserHelper.setUniqueID("f2ca80b75173362d");
        currentUserHelper.setFirebaseId("Shaishav");
        currentUserHelper.setAppInTestMode(false);
    }

    /**
     * This makes sure that the main activity launches
     */
    @Test
    public void mainActivityLaunches() {
        String currentUserName = currentUserHelper.getUsername();
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.welcomeText)).check(matches(withText("Welcome " + currentUserName + "!")));
    }

    /**
     * This method tests whether or not we can open QR code scanner
     */
    @Test
    public void successfulQrCodeScannerOpen() {
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.scanQRCodeBtn)).perform(click());
        onView(withText("Scanning Code")).check(matches(isDisplayed()));
    }

    /**
     * This method test whether or not we can save a QR code with geolocation
     */
    @Test
    public void successfulQRCodeWithGeolocation(){
        currentUserHelper.setAppInTestMode(true);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.saveLocationCheckBox)).perform(click());
        onView(withId(R.id.saveQRtoCloudBtn)).perform(click());
        onView(withId(R.id.saveQRtoCloudBtn)).check(matches(Matchers.not(isEnabled())));
        try {
            Thread.sleep(3000);
            onView(withId(R.id.saveQRtoCloudBtn)).check(matches(isEnabled()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Make sure player can't click "ADD QR CODE" without any code scanned
     */
    @Test
    public void testToastOnSaveIfNoQRCodeScanned(){
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.saveQRtoCloudBtn)).perform(click());
        onView(withId(R.id.saveQRtoCloudBtn)).check(matches(isEnabled()));
    }

    /**
     * Make sure player can't click "TAKE PHOTO" without any code scanned
     */
    @Test
    public void testNoPhotoIfNoQRCodeScanned(){
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.takeLocationBtn)).perform(click());
        onView(withId(R.id.takeLocationBtn)).check(matches(isEnabled()));
    }


}