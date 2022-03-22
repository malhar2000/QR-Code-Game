package com.example.qrcodegame;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;


import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


import android.content.Intent;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;


import com.example.qrcodegame.adapters.LeaderBoardAdapter;
import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.CurrentUserHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class LeaderBoardTest {

    public Intent changeActivity(){
        return new Intent(ApplicationProvider.getApplicationContext(),
                LeaderBoardActivity.class);
    }
    @Before
    public void setUp(){
        CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
       currentUserHelper.setUsername("Shaishav");
       currentUserHelper.setAppInTestMode(false);
    }


    @Test
    public void checkIfIntentWorks(){
        ActivityScenario<LeaderBoardActivity> activityScenario = ActivityScenario.launch(changeActivity());
        onView(withId(R.id.cardViewLeaderB)).check(matches(isDisplayed()));
    }

    /**
     * I cannot check if user rank is 5 because multiple 5 exists in the View
     * I cannot check if "Your Rank By Score: 5" because 5 and Your Rank are separate TextView
     */
    @Test
    public void checkIfTheUserExistsInTheList(){
        ActivityScenario<LeaderBoardActivity> activityScenario = ActivityScenario.launch(changeActivity());
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        onView(withText("Shaishav")).check(matches(isDisplayed()));
    }

    @Test
    public void checkTheTenthPositionInTheView(){
        ActivityScenario<LeaderBoardActivity> activityScenario = ActivityScenario.launch(changeActivity());
        try{
            Thread.sleep(5000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        onView(withId(R.id.recycle_view)).perform(RecyclerViewActions.actionOnItemAtPosition
                (10, click()));
        onView(withId(R.id.viewProfileActivity)).check(matches(isDisplayed()));
        //Open a menu if we have one and then do onView(withText("xyz")).perform(click());
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        //https://stackoverflow.com/questions/23985181/click-home-icon-with-espresso/26898398#26898398
        onView(withContentDescription(("Navigate up"))).perform(click());
        onView(withId(R.id.recycle_view)).check(matches(isDisplayed()));
    }



}
