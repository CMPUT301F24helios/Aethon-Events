package com.example.aethoneventsapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavbarTest {

    @Rule
    public ActivityScenarioRule<NavActivity> activityRule = new ActivityScenarioRule<NavActivity>(NavActivity.class);

    @Test
    public void testGoProfile() {
        onView(withId(R.id.navigation_profile)).perform(click());
    }

    @Test
    public void testGoScanner() {
        onView(withId(R.id.navigation_scanner)).perform(click());
    }

    @Test
    public void testGoHome() {
        onView(withId(R.id.navigation_home)).perform(click());
    }

}
