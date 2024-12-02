package com.example.aethoneventsapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerViewTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testViewEntrants() throws InterruptedException {
//        Performing the test for the following User Stories:
//            1. US 02.02.01
//            2. US 02.02.02
//            3. US 02.04.02
//            4. US 02.06.02
//            5. US 02.06.01
        // check long click to update photo

//        // go back to organizer view activity
        onView(withId(R.id.navigation_profile)).perform(ViewActions.click());
        onView(withId(R.id.switch_org)).perform(ViewActions.click());

        onView(withText("Dance Rooftop")).perform(ViewActions.longClick());
        onView(withText("No")).perform(ViewActions.click()); // dont upload

        onView(withText("Dance Rooftop")).perform(ViewActions.click());

        Thread.sleep(2000);

        onView(withText("WaitingList")).perform(ViewActions.click()); // Expand listview

        onView(withText("Pending")).perform(ViewActions.click()); // Expand listview

        onView(withText("Accepted")).perform(ViewActions.click()); // Expand listview

        onView(withText("Declined")).perform(ViewActions.click()); // Expand listview

        Thread.sleep(2000);

        onView(withText("Pending")).perform((ViewActions.longClick()));

        onView(withId(R.id.customMessageInput)).perform((ViewActions.typeText("Test Message")));
        onView(withId(R.id.sendButton)).perform(ViewActions.click());

        onView(withId(R.id.poolButton)).perform(ViewActions.click());
        onView(withText("Yes")).perform(ViewActions.click());

        onView(withId(R.id.QRButton)).perform(ViewActions.click());
        Espresso.pressBack();

        onView(withId(R.id.MapButton)).perform(ViewActions.click());




//        // Check if map object is visible when generate map button is pressed
//        onView(withId(R.id.MapButton)).perform(ViewActions.click());
//        onView(withId(R.id.mapView)).check(ViewAssertions.matches(isDisplayed()));
//        pressBack(); // go to the waitlist page
//        // Click on 'Pool' to view entrants
//        onView(withId(R.id.poolButton))
//                .perform(ViewActions.click());
//        // Click on waitlist
//        onView(withText("Waitlist"))
//                .perform(ViewActions.click());
//        // Click on Pending
//        onView(withText("Pending"))
//                .perform(ViewActions.click());
//        // Click on Accepted
//        onView(withText("Accepted"))
//                .perform(ViewActions.click());
//        // Click on Declined
//        onView(withText("Declined"))
//                .perform(ViewActions.click());
    }
}
