package com.example.aethoneventsapp;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminFacilityTestCase {
    @Rule
    public ActivityScenarioRule<AdminFacilityActivity> scenario =
            new ActivityScenarioRule<>(AdminFacilityActivity.class);

    @Test
    public void testEventList() throws InterruptedException {
        // Wait for the RecyclerView to be displayed
        onView(withId(R.id.recyclerViewFacilities)).check(matches(isDisplayed()));
        Thread.sleep(2000);

        // Check if the first item in the RecyclerView is displayed
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recyclerViewFacilities).atPosition(0))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteEvent() throws InterruptedException {
        // Ensure the RecyclerView is displayed
        onView(withId(R.id.recyclerViewFacilities)).check(matches(isDisplayed()));
        Thread.sleep(2000);
        // Check if the first item in the RecyclerView is displayed
        int initialCount = FacilityAdapter.facilityList.size();
        // Perform the delete action on the first item
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recyclerViewFacilities).atPositionOnView(0, R.id.deleteButton))
                .check(matches(isDisplayed())) // Ensure the delete button is visible
                .perform(click());
        Thread.sleep(2000);
        onView(withText("Yes"))
                .perform(click());
        Thread.sleep(2000);
        assertEquals(initialCount - 1, FacilityAdapter.facilityList.size());


    }
}
