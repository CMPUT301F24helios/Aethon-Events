package com.example.aethoneventsapp;

/**
 * The WaitingListController class manages operations for handling waitlist entries,
 * including joining, leaving, selecting, and tracking entrants on the waitlist.
 */
public class WaitingListController {

    private WaitingList waitingList;

    /**
     * Constructor for WaitingListController.
     * Initializes a new WaitingList instance for managing waitlist operations.
     */
    public WaitingListController() {
        this.waitingList = new WaitingList();
    }

    /**
     * Adds an entrant to the waitlist for a given event.
     *
     * @param eventId   the ID of the event to join the waitlist for
     * @param entrantId the ID of the entrant joining the waitlist
     */
    public void joinWaitlist(String eventId, String entrantId) {
        waitingList.addEntrantToWaitlist(eventId, entrantId);
    }

    /**
     * Removes an entrant from the waitlist based on the entrant ID.
     *
     * @param entrantId the ID of the entrant to be removed from the waitlist
     */
    public void unjoinWaitlist(String entrantId) {
        waitingList.removeEntrantFromWaitlist(entrantId);
    }

    /**
     * Selects entrants from the waitlist for an event, processing them for selection.
     *
     * @param eventId the ID of the event for which entrants are to be selected
     */
    public void selectEntrantsForEvent(String eventId) {
        waitingList.manageEntrantSelection(eventId);
    }

    /**
     * Tracks and displays all entrants on the waitlist for a given event.
     *
     * @param eventId the ID of the event to track the waitlist for
     */
    public void trackEntrantsOnWaitlist(String eventId) {
        waitingList.trackWaitlistForEvent(eventId);
    }
}