package com.example.aethoneventsapp;

import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, that checks the the Landing Activity to ensure proper main page navigation.
 * Tests User Story US 01.07.01 given we know that firebase calls are mimicked as expected.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeviceRecognitionUITests {

    @Rule
    public ActivityScenarioRule<LandingActivity> scenario = new
            ActivityScenarioRule<LandingActivity>(LandingActivity.class);

    @Before
    public void setup() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testAdminNavigation() {
        // Simulating Firebase response that calls the method navigateToAdminMode
        scenario.getScenario().onActivity(LandingActivity::navigateToAdminMode);

        // Use Espresso to verify AdminMainActivity is launched
        Intents.intended(hasComponent(AdminMainActivity.class.getName()));
    }

    @Test
    public void testMainPageNavigation() {
        // Simulating Firebase response that calls the method navigateToAdminMode
        scenario.getScenario().onActivity(LandingActivity::navigateToMainApp);

        // Use Espresso to verify AdminMainActivity is launched
        Intents.intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testSignUpNavigation() {
        // Simulating Firebase response that calls the method navigateToAdminMode
        scenario.getScenario().onActivity(LandingActivity::navigateToProfileSetup);

        // Use Espresso to verify AdminMainActivity is launched
        Intents.intended(hasComponent(SignUpActivity.class.getName()));
    }
}