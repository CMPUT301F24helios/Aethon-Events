package com.example.aethoneventsapp;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.junit.Assert.assertEquals;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import org.hamcrest.Matcher;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.View;

@RunWith(AndroidJUnit4.class)
public class AdminUserActivityTest {


    @Rule
    public ActivityScenarioRule<AdminUserActivity> activityRule =
            new ActivityScenarioRule<>(AdminUserActivity.class);

    @Test
    public void testRecyclerViewDisplaysUsers() {
        // Verify that RecyclerView is displayed
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteEvent() throws InterruptedException {
        // Ensure the RecyclerView is displayed
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        Thread.sleep(2000);
        // Check if the first item in the RecyclerView is displayed
        int initialCount = UserAdapter.userList.size();
        // Perform the delete action on the first item
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.deleteButton))
                .check(matches(isDisplayed())) // Ensure the delete button is visible
                .perform(click());
        Thread.sleep(2000);
        onView(withText("Yes"))
                .perform(click());
        Thread.sleep(2000);
        assertEquals(initialCount - 1, UserAdapter.userList.size());


    }



    @Test
    public void testNavigationAndRemoveProfilePicture() throws InterruptedException {
        // Simulate clicking on the first item in the RecyclerView
        onView(withId(R.id.recyclerView)) // Replace with the RecyclerView ID or specific child view inside the item
                .perform(click());
        // Wait for UI transition to complete
        Thread.sleep(500); // Wait for animations to finish (adjust time if needed)

        // Verify user profile details are displayed
        onView(withId(R.id.nameTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.emailTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.phoneTextView)).check(matches(isDisplayed()));

        // Click the remove profile picture button
        onView(withId(R.id.removeProfilePictureButton)).perform(click());

        // Verify the profile picture has been updated to the default
        // Replace defaultProfilePictureId with the resource ID for the default profile picture
        onView(withId(R.id.profileImageView))
                .check(matches(isDisplayed()));
    }
    public static ViewAction clickChildViewWithId(final int recyclerViewId, final int position) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                // Ensures that the view is a RecyclerView
                return isAssignableFrom(RecyclerView.class);
            }

            @Override
            public String getDescription() {
                return "Click on item at position " + position;
            }

            @Override
            public void perform(UiController uiController, View view) {
                // Find the RecyclerView
                RecyclerView recyclerView = (RecyclerView) view;

                // Check that the position is valid
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

                // If the view holder is not null, perform a click
                if (viewHolder != null) {
                    viewHolder.itemView.performClick();
                }
            }
};}
}
