package com.example.aethoneventsapp;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationView {
    private Context context;
    private NotificationManagerCompat notificationManager;
    public NotificationView(Context context){
        this.context = context;

        this.notificationManager = NotificationManagerCompat.from(context);
    }
    public void displayNotification(Entrant entrant, String message) {
        // send notification using Firebase and Device ID.

        }

    }
}
