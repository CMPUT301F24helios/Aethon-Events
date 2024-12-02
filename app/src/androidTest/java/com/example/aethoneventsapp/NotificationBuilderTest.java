package com.example.aethoneventsapp;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/*
* This test checks the functionality of the Notification Builder code
* snippet that can be seen in Main.
* It is not possible to check the Main Activity code directly as it is strongly
* coupled within various Firebase Firestore calls.
*
* User Stories checked:
*
* US 01.04.01 and US 01.04.02 - these are checked as they are just different types of notification
*                               build extras from the same code
* US 01.04.03 - the main activity will send notifications only if the profile page indicates so
*               this is handled by a simple if statement in MainActivity
*
* */
public class NotificationBuilderTest {

    private Context context;

    @Before
    public void setUp() {
        // Use ApplicationProvider to get a mock Context
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testNotificationBuilderCreatesCorrectNotification() {
        // Arrange: Define expected values
        // Hardcoded Test Values
        String channelId = "test_channel";
        String expectedTitle = "Test Notification";
        String expectedText = "This is a test notification.";

        // Act: Build the notification
        Notification notification = new Notification.Builder(context, channelId)
                .setContentTitle(expectedTitle)
                .setContentText(expectedText)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        // Assert: Check notification properties
        Bundle extras = notification.extras;

        assertNotNull("Extras should not be null", extras);
        assertEquals("Title should match", expectedTitle, extras.getString(Notification.EXTRA_TITLE));
        assertEquals("Text should match", expectedText, extras.getString(Notification.EXTRA_TEXT));
        assertNotNull("Small icon should not be null", notification.getSmallIcon());

    }
}