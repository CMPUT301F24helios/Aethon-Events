package com.example.aethoneventsapp;

import java.util.List;

public class NotificationModel {
    public List<Entrant> getChosenEntrantsToSignup(Event event) {
        // Implement logic to retrieve chosen entrants
        return java.util.Collections.emptyList();
    }

    public List<Entrant> getCancelledEntrants(Event event) {
        // Implement logic to retrieve cancelled entrants
        return java.util.Collections.emptyList();
    }

    public List<Entrant> getAllEntrantsOnWaitlist(Event event) {
        // Implement logic to retrieve waitlisted entrants
        return java.util.Collections.emptyList();
    }

    public List<Entrant> getSelectedEntrants(Event event) {
        // Implement logic to retrieve selected entrants
        return java.util.Collections.emptyList();
    }

    // New: Stores or updates the notification preference for an entrant.
    public void updateNotificationPreference(Entrant entrant, boolean preference) {
        // Logic to update notification preference in the data source.
    }

    // New: Retrieves the notification preference of an entrant, if this data is stored in the model.
    public boolean getNotificationPreference(Entrant entrant) {
        // Logic to retrieve the preference setting, if stored in the model.
        return false;
    }
}
