package com.example.aethoneventsapp;

import android.app.NotificationManager;

import java.util.List;

public class NotificationController {
    private NotificationModel notificationModel;
    private NotificationView notificationView;

    // Controller constructor.
    public NotificationController(NotificationModel notificationModel, NotificationView notificationView) {
        this.notificationModel = notificationModel;
        this.notificationView = notificationView;
    }

    // Pushes a notification message to a list of entrants.
    public void pushNotification(List<Entrant> entrants, String message) {
        for (Entrant entrant : entrants) {
            if (notificationModel.getNotificationPreference(entrant)) {
                notificationView.displayNotification(entrant, message);
            }
        }
    }

    // Sets the notification preference for a given entrant.
    public void setNotificationPreference(Entrant entrant, boolean preference) {
        notificationModel.updateNotificationPreference(entrant, preference);
    }
}
