package com.example.aethoneventsapp;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.auth.User;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerTestCase {
//        Performing the test for the following User Stories:
//            1. US 02.03.01
//            2. US 02.04.01
//            3. US 02.02.03
//            4. US 02.05.02
//            5. US 02.01.01
//            6. US 02.04.02
//            7. US 02.02.01
//            8. US 02.05.01
//            9. US 02.01.03

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testCreateEventFlow() {
//        Performing the test for the following User Stories:
//            1. US 02.03.01
//            2. US 02.04.01
//            3. US 02.02.03
//            4. US 02.05.02
//            5. US 02.01.01

        // Navigate to 'Create Event' page by clicking on the add
        onView(ViewMatchers.withId(R.id.button_organizer))
                .perform(ViewActions.click());

        // Fill in event details
        onView(withId(R.id.title))
                .perform(ViewActions.typeText("Dance Rooftop Session"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextLocation))
                .perform(ViewActions.typeText("Edmonton, Canada"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextEventDate))
                .perform(ViewActions.typeText("2024-01-01 7:30 PM"), ViewActions.closeSoftKeyboard());
        // Limiting the number of entrants that can join the waitlist: US 02.03.01
        onView(withId(R.id.editTextLimit))
                .perform(ViewActions.typeText("45"), ViewActions.closeSoftKeyboard());
        // organizer can enable or disable geolocation: US 02.02.03
        onView(withId(R.id.radioGroupGeolocation))
                .perform(ViewActions.click());
        // limiting the number of attendees: US 02.05.02
        onView(withId(R.id.editTextCapacity))
                .perform(ViewActions.typeText("30"), ViewActions.closeSoftKeyboard());

        // Change the photo of the Event - US 02.04.01
        onView(withId(R.id.buttonUploadImage))
                .perform(ViewActions.click());
        // Submit the event creation and view qr code: US 02.01.01
        onView(withId(R.id.buttonSubmit))
                .perform(ViewActions.click());

        // Verify QR Code is displayed
        onView(withId(R.id.imageViewQRCode)) // TODO: Need to change the view for qr code
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        // TODO: Add a back button from the create event page OR use Nav Bar
    }

    @Test
    public void testViewEntrants() {
//        Performing the test for the following User Stories:
//            1. US 02.02.01
//            2. TODO: US 02.04.02
        onView(withText("Music Rooftop Session"))
                .perform(ViewActions.click());

        // Click on 'Pool' to view entrants
        onView(withId(R.id.poolButton))
                .perform(ViewActions.click());
        // Check entrants list is displayed
        onView(withId(R.id.entrantsListView))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withId(R.id.positiveButton))
                .perform(ViewActions.click());
        // TODO: Check for updating the event poster
    }

}