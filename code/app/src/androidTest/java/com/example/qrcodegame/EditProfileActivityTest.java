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

@RunWith(AndroidJUnit4ClassRunner.class)
public class EditProfileActivityTest {

    private CurrentUserHelper currentUserHelper;

    private Intent mockIntentForPersonalProfile(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ViewProfileActivity.class);
        intent.putExtra("username", currentUserHelper.getUsername());
        return intent;
    }

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
     * This makes sure that the this activity launches
     */
    @Test
    public void testActivityLaunches() {
        ActivityScenario<EditProfileActivity> activityScenario = ActivityScenario.launch(EditProfileActivity.class);
        onView(withText("Profile Editing")).check(matches(isDisplayed()));
    }

    @Test
    public void testSaveButtonWorksOnChanges(){
        ActivityScenario<ViewProfileActivity> activityScenario = ActivityScenario.launch(mockIntentForPersonalProfile());
        onView(withId(R.id.buttonEditProfile))
                .perform(click());
        String time = String.format("%d", Instant.now().getEpochSecond());
        onView(withId(R.id.editPhone))
                .perform(clearText())
                .perform(typeText(time));
        onView(withId(R.id.editProfileSaveBtn))
                .perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.buttonEditProfile))
                .perform(click());
        onView(withText(time)).check(matches(isDisplayed()));
    }

    @Test
    public void testSaveButtonWorksOnNoChanges() {
        ActivityScenario<ViewProfileActivity> activityScenario = ActivityScenario.launch(mockIntentForPersonalProfile());
        onView(withId(R.id.buttonEditProfile))
                .perform(click());
        onView(withId(R.id.editProfileSaveBtn))
                .perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.buttonEditProfile))
                .check(matches(isDisplayed()))
                .perform(click());
    }

}
