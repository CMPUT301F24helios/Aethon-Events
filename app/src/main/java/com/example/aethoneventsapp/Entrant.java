package com.example.aethoneventsapp;

import java.util.ArrayList;
import java.util.List;

public class Entrant {
    private int entrantId;
    private int deviceId;
    private String name;
    private String email;
    private String phoneNumber;
    private List<Integer> registeredEvents;
    private List<Integer> waitlistedEvents;
    private List<Integer> pendingInvitations;
    private String postalCode;

    public Entrant(int entrantId, int deviceId, String name, String email, String phoneNumber, String postalCode) {
        this.entrantId = entrantId;
        this.deviceId = deviceId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.registeredEvents = new ArrayList<>();
        this.waitlistedEvents = new ArrayList<>();
        this.pendingInvitations = new ArrayList<>();
        this.postalCode = postalCode;
    }

    /**
     * Get location
     */
    public double[] getLocation() {
        double[] test = new double[] {};
        return test;
        // TO DO
    }

    /**
     * Getter for entrantId
     *
     * @return entrantId
     */
    public int getEntrantId() {
        return entrantId;
    }

    /**
     * Setter for entrantId
     *
     * @param entrantId
     */
    public void setEntrantId(int entrantId) {
        this.entrantId = entrantId;
    }

    /**
     * Getter for deviceId
     *
     * @return deviceId
     */
    public int getDeviceId() {
        return deviceId;
    }

    /**
     * Setter for deviceId
     *
     * @param deviceId
     */
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Getter for name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for email
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for email
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for phoneNumber
     *
     * @return phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Setter for phoneNumber
     *
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Getter for registeredEvents
     *
     * @return registeredEvents
     */
    public List<Integer> getRegisteredEvents() {
        return registeredEvents;
    }

    /**
     * Setter for registeredEvents
     *
     * @param registeredEvents
     */
    public void setRegisteredEvents(List<Integer> registeredEvents) {
        this.registeredEvents = registeredEvents;
    }

    /**
     * Getter for waitlistedEvents
     *
     * @return waitlistedEvents
     */
    public List<Integer> getWaitlistedEvents() {
        return waitlistedEvents;
    }

    /**
     * Setter for waitlistedEvents
     *
     * @param waitlistedEvents
     */
    public void setWaitlistedEvents(List<Integer> waitlistedEvents) {
        this.waitlistedEvents = waitlistedEvents;
    }

    /**
     * Getter for pendingInvitations
     *
     * @return pendingInvitations
     */
    public List<Integer> getPendingInvitations() {
        return pendingInvitations;
    }

    /**
     * Setter for pendingInvitations
     * @param pendingInvitations
     */
    public void setPendingInvitations(List<Integer> pendingInvitations) {
        this.pendingInvitations = pendingInvitations;
    }

    /**
     * Getter for postalCode
     * @return postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Setter for postalCode
     * @param postalCode
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }


    /**
     * Method to update the entrant's information
     * @param name
     * @param email
     * @param phoneNumber
     * @param postalCode
     */
    public void updateEntrant(String name, String email, String phoneNumber, String postalCode) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.postalCode = postalCode;
        System.out.println("Entrant updated successfully");
    }

    /**
     * Method to join the waitlist for an event
     * @param eventId
     */
    public void joinWaitlist(int eventId) {
        if (!waitlistedEvents.contains(eventId)) {
            waitlistedEvents.add(eventId);
            System.out.println("You have been added to the waitlist");
        }
        else {
            System.out.println("You are already on the waitlist");
        }

    }

    /**
     * Method to leave the waitlist for an event
     * @param eventId
     */
    public void leaveWaitlist(int eventId) {
        waitlistedEvents.remove(eventId);
    }

    /**
     * Method to register for an event
     * @param eventId
     */
    public void acceptInvitation(int eventId) {
        if (pendingInvitations.contains(eventId)) {
            registeredEvents.add(eventId);
            pendingInvitations.remove(eventId);
        }
    }

    /**
     * Method to decline an invitation for an event
     * @param eventId
     */
    public void declineInvitation(int eventId) {
        pendingInvitations.remove(eventId);
    }

}
