package com.example.qrcodegame;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static android.service.autofill.Validators.not;

import android.content.Intent;
import android.util.Log;

import com.example.qrcodegame.utils.CurrentUserHelper;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class SingleQRActivityTest {

    private CurrentUserHelper currentUserHelper;

    private Intent mockIntentForSingleQRCode(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SingleQRActivity.class);
        intent.putExtra("codeID", "2dfcdc9c-003c-4ebd-a595-8c94458dce16");
        return intent;
    }

    /**
     * This makes sure that the activity launches
     */
    @Test
    public void testActivityLaunches() {
        ActivityScenario<SingleQRActivity> activityScenario = ActivityScenario.launch(mockIntentForSingleQRCode());
        onView(withId(R.id.layoutSingleQRCodeActivity))
                .check(matches(isDisplayed()));
    }

    /**
     * This makes sure that all elements are present
     */
    @Test
    public void testActivityLoadsEverything() {
        ActivityScenario<SingleQRActivity> activityScenario = ActivityScenario.launch(mockIntentForSingleQRCode());
        onView(withId(R.id.qrMap)).check(matches(isDisplayed()));
        onView(withId(R.id.surroundingImage)).check(matches(isDisplayed()));
        onView(withId(R.id.otherPlayersTxt)).check(matches(isDisplayed()));
        onView(withId(R.id.usernameList)).check(matches(isDisplayed()));
    }

    /**
     * This makes sure that the comments button works
     */
    @Test
    public void testCommentsButtonLaunchesNewActivity() {
        ActivityScenario<SingleQRActivity> activityScenario = ActivityScenario.launch(mockIntentForSingleQRCode());
        onView(withId(R.id.commentBtn))
                .check(matches(isDisplayed()))
                .perform(click());
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onView(withId(R.id.commentActivityMainLayout)).check(matches(isDisplayed()));
    }


}
