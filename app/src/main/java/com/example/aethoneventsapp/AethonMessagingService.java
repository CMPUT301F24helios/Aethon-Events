package com.example.aethoneventsapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// From Firebase Docs https://firebase.google.com/docs/cloud-messaging/android/client#java, Read 2024-11-06

/**
 * Aethon Events' Notification Service
 */
public class AethonMessagingService extends FirebaseMessagingService {
    // Logging Identifier
    private static final String TAG = "AMS Notifications";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Handle FCM messages here
        if (remoteMessage.getNotification() != null) {
            // Display the notification
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            // Here you can display a notification or update the UI
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            // Handle data message payload
        }
    }
}
