package com.example.aethoneventsapp;

import java.util.ArrayList;

/**
 * The WaitingListView class provides methods for displaying waitlist-related information to the user,
 * such as joining or leaving the waitlist and showing selected entrants.
 */
public class WaitingListView {

    /**
     * Displays the list of entrants on the waitlist for a specific event.
     *
     * @param waitlist the WaitingList instance containing waitlisted entrants
     */
    public void showWaitlist(WaitingList waitlist) {
        System.out.println("Displaying waitlist details for event ID: " + waitlist.getEventId());
        ArrayList<String> waitlistEntries = waitlist.getWaitList();
        for (String entrantId : waitlistEntries) {
            // TODO
            System.out.println("Entrant ID: " + entrantId);
        }
    }

    /**
     * Displays the form for an entrant to join the waitlist for a specific event.
     *
     * @param event the event to join the waitlist for
     */
    public void showJoinWaitlistForm(String event) {
        // TODO
        System.out.println("Showing form to join waitlist for event: " + event);
    }

    /**
     * Shows a confirmation message for removing an entrant from the waitlist.
     *
     * @param entrantId the entrant ID to confirm removal from
     */
    public void showUnjoinWaitlistConfirmation(String entrantId) {
        // TODO
        System.out.println("Confirm removal of entrant ID: " + entrantId + " from waitlist.");
    }

    /**
     * Displays the list of selected entrants for a given event after waitlist processing.
     *
     * @param event the event for which selected entrants are displayed
     */
    public void displaySelectedEntrantsForEvent(String event) {
        // TODO
        System.out.println("Displaying selected entrants for event: " + event);
    }
}