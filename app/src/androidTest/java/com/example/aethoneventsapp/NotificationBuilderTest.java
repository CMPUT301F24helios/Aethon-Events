package com.example.aethoneventsapp;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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