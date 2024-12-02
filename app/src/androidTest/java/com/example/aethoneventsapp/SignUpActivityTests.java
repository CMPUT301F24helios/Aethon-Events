package com.example.aethoneventsapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.Button;

import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 * Instrumented test, that checks the the Landing Activity to ensure proper main page navigation.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpActivityTests {

    @Rule
    public ActivityScenarioRule<SignUpActivity> scenarioSignUp =
            new ActivityScenarioRule<SignUpActivity>(SignUpActivity.class);

    @Before
    public void setUp(){
        Intents.init();
    }

    @After
    public void tearDown(){
        Intents.release();
    }

    @Test
    public void signUpTests(){

        // Click on Add City button
        onView(withId(R.id.add_name)).perform(click());
        onView(withId(R.id.add_name)).perform(ViewActions.typeText("John Doe"));

        onView(withId(R.id.add_address)).perform(click());
        onView(withId(R.id.add_address)).perform(ViewActions.typeText("john@doe.com"));

        onView(withId(R.id.add_phone)).perform(click());
        onView(withId(R.id.add_phone)).perform(ViewActions.typeText("780-420-6969"));

        onView(withText("John Doe")).check(matches(isDisplayed()));
        onView(withText("john@doe.com")).check(matches(isDisplayed()));
        onView(withText("780-420-6969")).check(matches(isDisplayed()));

    }

    @Test
    public void signUpButtonTest(){

        onView(withId(R.id.add_name)).perform(click());
        onView(withId(R.id.add_name)).perform(ViewActions.typeText("John Doe"));

        onView(withId(R.id.add_address)).perform(click());
        onView(withId(R.id.add_address)).perform(ViewActions.typeText("john@doe.com"));

        onView(withId(R.id.add_phone)).perform(click());
        onView(withId(R.id.add_phone)).perform(ViewActions.typeText("780-420-6969"));

        onView(withId(R.id.signup_btn)).perform(click());

        // Assuming that the code mocks the firebase call perfectly(which it does :))
        // i.e creates a user with the given details.
        // We check if we switched to the MainActivity.
        Intents.intended(hasComponent(MainActivity.class.getName()));
    }

    @Ignore
    public void testProfilePictureUpload(){
        
    }
}
