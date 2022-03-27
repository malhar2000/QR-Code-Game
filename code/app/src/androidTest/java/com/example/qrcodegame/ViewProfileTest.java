package com.example.qrcodegame;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.qrcodegame.utils.CurrentUserHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ViewProfileTest {

    private CurrentUserHelper currentUserHelper;

    private Intent mockIntentForPersonalProfile(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ViewProfileActivity.class);
        intent.putExtra("username", currentUserHelper.getUsername());
        return intent;
    }

    private Intent mockIntentForNonPersonalProfile(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ViewProfileActivity.class);
        intent.putExtra("username", "ss");
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
     * This makes sure that the main activity launches
     */
    @Test
    public void testActivityLaunch() {
        ActivityScenario<ViewProfileActivity> activityScenario = ActivityScenario.launch(mockIntentForPersonalProfile());
        onView(withId(R.id.viewProfileActivity)).check(matches(isDisplayed()));
    }

    @Test
    public void testShareProfileAvaliableOnPersonalProfile(){
        ActivityScenario<ViewProfileActivity> activityScenario = ActivityScenario.launch(mockIntentForPersonalProfile());
        onView(withId(R.id.buttonOpenShareQRCode))
                .check(matches(isDisplayed()))
                .perform(click());
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        onView(withText("Share your Profile! (Not Transfer)")).check(matches(isDisplayed()));
    }

    @Test
    public void testTransferProfileAvaliableOnPersonalProfile(){
        ActivityScenario<ViewProfileActivity> activityScenario = ActivityScenario.launch(mockIntentForPersonalProfile());
        onView(withId(R.id.profileTransferBtn))
                .check(matches(isDisplayed()))
                .perform(click());
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        onView(withText("Transfer your Profile!")).check(matches(isDisplayed()));
    }

    @Test
    public void testTransferProfileNotAvaliableOnNonPersonalProfile(){
        ActivityScenario<ViewProfileActivity> activityScenario = ActivityScenario.launch(mockIntentForNonPersonalProfile());
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        onView(withId(R.id.profileTransferBtn))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    @Test
    public void testShareProfileAvaliableOnNonPersonalProfile(){
        ActivityScenario<ViewProfileActivity> activityScenario = ActivityScenario.launch(mockIntentForNonPersonalProfile());
        onView(withId(R.id.buttonOpenShareQRCode))
                .check(matches(isDisplayed()))
                .perform(click());
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        onView(withText("Share your Profile! (Not Transfer)")).check(matches(isDisplayed()));
    }

    @Test
    public void testEditProfileBtnWorks(){
        ActivityScenario<ViewProfileActivity> activityScenario = ActivityScenario.launch(mockIntentForPersonalProfile());
        onView(withId(R.id.buttonEditProfile))
                .check(matches(isDisplayed()))
                .perform(click());
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        onView(withText("Profile Editing")).check(matches(isDisplayed()));
    }

}
