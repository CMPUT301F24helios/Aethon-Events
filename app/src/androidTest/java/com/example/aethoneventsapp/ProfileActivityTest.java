package com.example.aethoneventsapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
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
 * Instrumented test, that checks the the Profile Activity works as expected.
 *
 * Tests:
 * US 01.04.03
 * US 01.03.02
 * US 01.02.02
 * US 01.03.03
 * US 01.03.01
 *
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileActivityTest {


    @Rule
    public ActivityScenarioRule<ProfileActivity> scenario =
            new ActivityScenarioRule<ProfileActivity>(ProfileActivity.class);

    @Before
    public void setUp(){
        Intents.init();
        // Changing a saved and existing profile with this data.

        onView(withId(R.id.edit_name)).perform(click());
        onView(withId(R.id.edit_name)).perform(ViewActions.replaceText("John Doe"));

        onView(withId(R.id.edit_address)).perform(click());
        onView(withId(R.id.edit_address)).perform(ViewActions.replaceText("john@doe.com"));

        onView(withId(R.id.edit_phone)).perform(click());
        onView(withId(R.id.edit_phone)).perform(ViewActions.replaceText("780-420-6969"));

    }

    @Test
    /*
    * US 01.04.03
    * US 01.02.02
    *
    * */
    public void testUpdateInfoTesting() throws InterruptedException {
        onView(withId(R.id.edit_name)).perform(click());
        onView(withId(R.id.edit_name)).perform(ViewActions.replaceText("John DoeUpdated"));

        onView(withId(R.id.edit_address)).perform(click());
        onView(withId(R.id.edit_address)).perform(ViewActions.replaceText("john@doeupdated.com"));

        onView(withId(R.id.edit_phone)).perform(click());
        onView(withId(R.id.edit_phone)).perform(ViewActions.replaceText("780-420-0000"));

        onView(withId(R.id.checkBox)).perform(click());

        onView(withId(R.id.profileImage))
                .check(matches(isDisplayed()));

        onView(withText("John DoeUpdated")).check(matches(isDisplayed()));
        onView(withText("john@doeupdated.com")).check(matches(isDisplayed()));
        onView(withText("780-420-0000")).check(matches(isDisplayed()));


        // save button fetches all the data seen above and asks Firebase to save it.
        onView(withId(R.id.save_btn)).perform(click());
    }

    @Test
    /*
    * US 01.03.02
    * US 01.03.03
    * */
    public void testRemoveButton(){
        // Remove picture button click sets profile image to a deterministically generated one.
        onView(withId(R.id.removePhoto)).perform(click());

        // Check if the ImageView is displaying the image after the Remove picture button click
        onView(withId(R.id.profileImage))
                .check(matches(isDisplayed()));
    }

    /*
    * US 01.03.01
    * */
    @Test
    public void testUploadImageButton(){

        onView(withId(R.id.changePhoto)).perform(click());

        // Verify that a chooser intent is triggered
        intended(hasAction(Intent.ACTION_CHOOSER));

        // Saving is done by firebase, therefore assuming that it will be done properly.
    }
    @After
    public void tearDown(){
        Intents.release();

    }

}
