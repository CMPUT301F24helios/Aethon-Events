package com.example.aethoneventsapp;

import java.util.ArrayList;

/**
 * The WaitingList class represents a waitlist for an event, including details
 * about the waitlist entry ID, associated event ID, entrant ID, and status.
 */
public class WaitingList {
    private String waitlistId; // Unique identifier for the waitlist entry
    private String eventId;    // Identifier of the event associated with this waitlist
    private String entrantId;  // Identifier of the entrant on the waitlist
    private String status;     // Status of the entrant in the waitlist (e.g., "waiting", "selected")
    private final ArrayList<String> waitList = new ArrayList<String>(); // The WaitingList
    /**Entrant
     * Adds an entrant to the waitlist for a specific event.
     *
     * @param eventId   the ID of the event to join the waitlist for
     * @param entrantId the ID of the entrant joining the waitlist
     */
    public void addEntrantToWaitlist(String eventId, String entrantId) {
        // adding entrant to the waiting list
        waitList.add(entrantId);
        System.out.println("Entrant " + entrantId + " added to waitlist for event " + eventId);
    }

    /**
     * Removes an entrant from the waitlist based on the waitlist ID.
     *
     * @param entrantId the entrant to be removed
     */
    public void removeEntrantFromWaitlist(String entrantId) {
        waitList.remove(entrantId);
        System.out.println("Removed entrant with entrant ID: " + entrantId);
    }

    /**
     * Manages selection of entrants from the waitlist for a specific event.
     *
     * @param eventId the ID of the event for which entrants will be selected from the waitlist
     */
    public void manageEntrantSelection(String eventId) {
        // TODO: complete the function by adding the algorithm of selection
        System.out.println("Selecting entrants for event: " + eventId);
    }

    /**
     * Tracks and displays the status of the waitlist for a specific event.
     *
     * @param eventId the ID of the event to track the waitlist for
     */
    public void trackWaitlistForEvent(String eventId) {
        // TODO: Complete this
        System.out.println("Tracking waitlist status for event: " + eventId);
    }

    // Getters and Setters for each attribute

    /**
     * Gets the waitlist ID.
     *
     * @return the waitlist ID
     */
    public String getWaitlistId() {
        return waitlistId;
    }

    /**
     * Sets the waitlist ID.
     *
     * @param waitlistId the waitlist ID to set
     */
    public void setWaitlistId(String waitlistId) {
        this.waitlistId = waitlistId;
    }

    /**
     * Gets the event ID.
     *
     * @return the event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID.
     *
     * @param eventId the event ID to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the entrant ID.
     *
     * @return the entrant ID
     */
    public String getEntrantId() {
        return entrantId;
    }

    /**
     * Sets the entrant ID.
     *
     * @param entrantId the entrant ID to set
     */
    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
    }

    /**
     * Gets the status of the entrant.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the entrant.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the list of entrants on the waitlist.
     *
     * @return the waitlist
     */
    public ArrayList<String> getWaitList() {
        return waitList;
    }
}