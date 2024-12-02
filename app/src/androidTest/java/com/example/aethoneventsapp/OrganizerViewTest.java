package com.example.aethoneventsapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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
    public ActivityScenarioRule<OrganizerViewActivity> activityRule = new ActivityScenarioRule<OrganizerViewActivity>(OrganizerViewActivity.class);

    @Test
    public void testCreateEvent() {
        onView(withId(R.id.button_organizer)).perform(click());
        onView(withId(R.id.editTextName)).perform(typeText("Test Event"));
        onView(withId(R.id.editTextLocation)).perform(typeText("Test Location"));
        onView(withId(R.id.editTextCapacity)).perform(typeText("100"));
        onView(withId(R.id.editTextLimit)).perform(typeText("50"));
        onView(withId(R.id.editTextDescription)).perform(typeText("Test Description"));
        onView(withId(R.id.radioYes)).perform(click());
        onView(withId(R.id.editTextEventDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.buttonSubmit)).perform(click());
        // go back to organizer view
        onView(withId(R.id.button_organizer)).perform(click());
    }

    @Test
    public void testViewEvents() {
        onView(withId(R.id.button_organizer)).perform(click());
        onView(withId(R.id.ListViewEvents)).check(matches(isDisplayed()));

        // test long clicking each event
        onView(withId(R.id.ListViewEvents)).perform(longClick());
        onView(withText("Confirm Image Upload")).check(matches(isDisplayed()));
        onView(withText("Are you sure you want to update the image for the event?")).check(matches(isDisplayed()));
        onView(withText("No")).perform(click());
        onView(withId(R.id.ListViewEvents)).check(matches(isDisplayed()));
    }
}
