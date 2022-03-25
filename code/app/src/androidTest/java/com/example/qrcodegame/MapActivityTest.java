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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.util.Log;

import com.example.qrcodegame.utils.CurrentUserHelper;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.ArrayList;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MapActivityTest {

    private CurrentUserHelper currentUserHelper;

    @Before
    public void setupCurrentUserHelper(){
        currentUserHelper = CurrentUserHelper.getInstance();
        currentUserHelper.setUsername("Shaishav");
        currentUserHelper.setPhone("7800000000");
        currentUserHelper.setUniqueID("f2ca80b75173362d");
        currentUserHelper.setFirebaseId("Shaishav");
        currentUserHelper.setAppInTestMode(false);
        ArrayList<Double> location = new ArrayList<>();
        location.add(53.631611);
        location.add(-113.323975);
        currentUserHelper.setCurrentLocation(location);
    }


    /**
     * This makes sure that the this activity launches
     */
    @Test
    public void testActivityLaunches() {
        ActivityScenario<MapsActivity> activityScenario = ActivityScenario.launch(MapsActivity.class);
        onView(withId(R.id.map))
                .check(matches(isDisplayed()));
    }


    /**
     * Just moving around the map to see if it crashes
     */
    @Test
    public void testActivityHandlesInteraction(){
        ActivityScenario<MapsActivity> activityScenario = ActivityScenario.launch(MapsActivity.class);
        onView(withId(R.id.map))
                .perform(swipeLeft())
                .perform(swipeRight())
                .perform(swipeUp())
                .perform(swipeDown())
                .check(matches(isDisplayed()));
    }

    // We can not test the marker clicks. Since the markers aren't in the view hierarchy.
    // As a result, that test has been skipped for right now.
    // A manual test can be conducted quite easily!

}
