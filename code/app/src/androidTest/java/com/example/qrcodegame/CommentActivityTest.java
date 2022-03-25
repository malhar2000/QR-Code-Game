package com.example.qrcodegame;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static android.service.autofill.Validators.not;

import static org.hamcrest.Matchers.endsWith;

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
public class CommentActivityTest {

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

    // We open comments from the Single QR Page, or else we can't launch it multiple times within same test
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
        onView(withId(R.id.commentBtn))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.commentActivityMainLayout))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testComment(){

        ViewInteraction commentButton = onView(withId(R.id.commentBtn));
        ViewInteraction editTextComments = onView(withId(R.id.add_comments));
        ViewInteraction addButton = onView(withId(R.id.addButtonComments));

        // Launch Activity
        ActivityScenario<SingleQRActivity> activityScenario = ActivityScenario.launch(mockIntentForSingleQRCode());
        commentButton
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.commentActivityMainLayout))
                .check(matches(isDisplayed()));
        // Add Comment
        String time = String.format("%d", Instant.now().getEpochSecond());
        editTextComments
                .perform(clearText())
                .perform(typeText(time))
                .perform(closeSoftKeyboard());
        addButton
                .perform(click());
        // Exit activity
        onView(withContentDescription(("Navigate up"))).perform(click());
        // Open comments again
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        commentButton
                .check(matches(isDisplayed()))
                .perform(click());
        // Check comment exists
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        onView(withText(String.format("%s: %s", currentUserHelper.getUsername(), time))).check(matches(isDisplayed()));
    }

}
