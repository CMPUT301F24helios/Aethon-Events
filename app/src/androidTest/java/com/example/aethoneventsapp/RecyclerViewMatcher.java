package com.example.aethoneventsapp;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class RecyclerViewMatcher {
    private final int recyclerViewId;

    public RecyclerViewMatcher(int recyclerViewId) {
        this.recyclerViewId = recyclerViewId;
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }
    public Matcher<View> atPositionOnView(final int position, final int targetViewId) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                RecyclerView recyclerView = view.getRootView().findViewById(recyclerViewId);
                if (recyclerView == null || recyclerView.getId() != recyclerViewId) {
                    return false;
                }
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    return false;
                }
                View targetView = viewHolder.itemView.findViewById(targetViewId);
                return view == targetView;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView with ID: " + recyclerViewId +
                        " at position: " + position +
                        " with target view ID: " + targetViewId);
            }
        };
    }

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed(); // Ensure the item is displayed
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View childView = view.findViewById(id);
                if (childView != null) {
                    childView.performClick();
                }
            }
        };
    }



    public static Matcher<View> hasChildAtPosition(final int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                RecyclerView recyclerView = (RecyclerView) view;
                return recyclerView != null && recyclerView.getAdapter() != null
                        && position < recyclerView.getAdapter().getItemCount();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView does not have a child at position " + position);
            }
        };
    }



    public Matcher<View> atPosition(final int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                RecyclerView recyclerView = view.getRootView().findViewById(recyclerViewId);
                if (recyclerView == null || recyclerView.getId() != recyclerViewId) {
                    return false;
                }
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                return viewHolder != null && viewHolder.itemView == view;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView with ID: " + recyclerViewId + " at position: " + position);
            }
        };
    }
}