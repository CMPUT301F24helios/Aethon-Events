package com.example.aethoneventsapp;

/**
 * The WaitingList class represents a waitlist for an event and includes methods for managing entrants.
 */
public class WaitingList {

    private int waitlistId;
    private int eventId;
    private int entrantId;
    private String status;

    /**
     * Adds an entrant to the waitlist for a specific event.
     *
     * @param eventId  The ID of the event.
     * @param entrantId The ID of the entrant to add.
     */
    public void addEntrantToWaitlist(int eventId, int entrantId) {
        // Logic to add the entrant to the waitlist
    }

    /**
     * Removes an entrant from the waitlist using the waitlist ID.
     *
     * @param waitlistId The ID of the waitlist entry to remove.
     */
    public void removeEntrantFromWaitlist(int waitlistId) {
        // Logic to remove the entrant from the waitlist
    }

    /**
     * Manages the selection of entrants from the waitlist for a specific event.
     *
     * @param eventId The ID of the event to manage selection for.
     */
    public void manageEntrantSelection(int eventId) {
        // Logic for managing entrant selection
    }

    /**
     * Tracks the waitlist status for a specific event.
     *
     * @param eventId The ID of the event whose waitlist is to be tracked.
     */
    public void trackWaitlistForEvent(int eventId) {
        // Logic to track waitlist for the event
    }

    // Getters and setters for attributes
    public int getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(int waitlistId) {
        this.waitlistId = waitlistId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getEntrantId() {
        return entrantId;
    }

    public void setEntrantId(int entrantId) {
        this.entrantId = entrantId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
