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
public class SplashScreenActivityTest {

    /**
     * This makes sure that the start screen launches
     */
    @Test
    public void splashScreenLaunches() {
        ActivityScenario<SplashScreenActivity> activityScenario = ActivityScenario.launch(SplashScreenActivity.class);
        onView(withId(R.id.splashScreenActivity)).check(matches(isDisplayed()));
    }

    /*
    This checks whether or not the activity successfully goes to another page after 2 seconds.
    Whether that be the Sign up page, Main Activity, or Owner activity
     */
    @Test
    public void successfulRedirect() {
        ActivityScenario<SplashScreenActivity> activityScenario = ActivityScenario.launch(SplashScreenActivity.class);
        new Thread(() -> {
            try {
                wait(2000);
                onView(withId(R.id.splashScreenActivity)).check(doesNotExist());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}