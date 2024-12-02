package com.example.aethoneventsapp;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anything;

import static java.util.regex.Pattern.matches;

import android.widget.DatePicker;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;


@RunWith(AndroidJUnit4.class)
public class OrganizerTestCase {

    @Rule
    public ActivityScenarioRule<OrganizerViewActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerViewActivity.class);

    @Test
    public void testCreateEventFlow() throws InterruptedException {
//        Performing the test for the following User Stories:
//            1. US 02.03.01
//            2. US 02.04.01
//            3. US 02.02.03
//            4. US 02.05.02
//            5. US 02.01.01

//        // Navigate to 'Create Event' page
//        onView(ViewMatchers.withId(R.id.navigation_profile))
//                .perform(ViewActions.click());
//        onView(ViewMatchers.withId(R.id.switch_org))
//                .perform(ViewActions.click());
        // create button
        onView(ViewMatchers.withId(R.id.button_organizer))
                .perform(ViewActions.click());


        // Fill in event details
        onView(withId(R.id.editTextName))
                .perform(ViewActions.typeText("Dance Rooftop Session"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextLocation))
                .perform(ViewActions.typeText("Edmonton, Canada"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextDescription))
                .perform(ViewActions.typeText("Dance"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextEventDate)).perform(click());
        onView(withText("OK")).perform(click());

        // Limiting the number of entrants that can join the waitlist: US 02.03.01
        onView(withId(R.id.editTextLimit))
                .perform(ViewActions.typeText("45"), ViewActions.closeSoftKeyboard());
        // organizer can enable or disable geolocation: US 02.02.03
        onView(withId(R.id.radioYes))
                .perform(ViewActions.click());

        // limiting the number of capacity: US 02.05.02
        onView(withId(R.id.editTextCapacity))
                .perform(ViewActions.typeText("30"), ViewActions.closeSoftKeyboard());


        // Submit the event creation and view qr code: US 02.01.01
        onView(withId(R.id.buttonSubmit))
                .perform(ViewActions.click());

        Espresso.pressBack();


        onView(withText("Dance Rooftop Session")).perform(ViewActions.longClick());
        onView(withText("No")).perform(ViewActions.click()); // dont upload
        Thread.sleep(2000);
        onView(withText("Dance Rooftop Session")).perform(ViewActions.click());
        Thread.sleep(2000);

//        // Verify QR Code is displayed
//        onView(withId(R.id.imageViewQRCode))
//                .check(ViewAssertions.matches(isDisplayed()));

    }

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
//        onView(withId(R.id.navigation_profile)).perform(ViewActions.click());
//        onView(withId(R.id.switch_org)).perform(ViewActions.click());

        onView(withText("Dance Rooftop Session")).perform(ViewActions.longClick());
        onView(withText("No")).perform(ViewActions.click()); // dont upload
        Thread.sleep(2000);
        onView(withText("Dance Rooftop Session")).perform(ViewActions.click());
        Thread.sleep(2000);

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
